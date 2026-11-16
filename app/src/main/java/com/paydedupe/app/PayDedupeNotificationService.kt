package com.paydedupe.app

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class PayDedupeNotificationService : NotificationListenerService() {

    private enum class SourceKind {
        WALLET,
        BANK
    }

    private data class SourceClassification(
        val kind: SourceKind,
        val displayName: String
    )

    private data class SeenPaymentNotification(
        val key: String,
        val classification: SourceClassification,
        val amountCents: Long,
        val timestampMillis: Long,
        val title: String,
        val text: String
    )

    private val recentPayments = mutableListOf<SeenPaymentNotification>()

    override fun onCreate() {
        super.onCreate()
        AppPreferences.init(applicationContext)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return

        if (!AppPreferences.isFilterEnabled.value) return

        val classification = classifyPackage(sbn) ?: return

        if (sbn.isOngoing) return
        val notification = sbn.notification ?: return
        if ((notification.flags and Notification.FLAG_GROUP_SUMMARY) != 0) return

        val extras = notification.extras ?: return
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty().trim()
        val text = (extras.getCharSequence(Notification.EXTRA_BIG_TEXT)
            ?: extras.getCharSequence(Notification.EXTRA_TEXT))?.toString().orEmpty().trim()

        val combinedText = "$title $text".trim()
        if (combinedText.isEmpty()) return

        val amountCents = AmountExtractor.extractEuroCents(combinedText) ?: return

        val now = System.currentTimeMillis()
        val windowMillis = AppPreferences.windowSeconds.value * 1000L

        synchronized(recentPayments) {
            recentPayments.removeAll { now - it.timestampMillis > windowMillis }

            if (recentPayments.any { it.key == sbn.key && it.amountCents == amountCents }) {
                return
            }

            val matchingOtherApp = recentPayments.firstOrNull {
                it.classification.kind != classification.kind && it.amountCents == amountCents
            }

            val currentEntry = SeenPaymentNotification(
                key = sbn.key,
                classification = classification,
                amountCents = amountCents,
                timestampMillis = now,
                title = title,
                text = text
            )

            if (matchingOtherApp != null) {
                recentPayments.remove(matchingOtherApp)

                val policy = AppPreferences.hidePolicy.value
                val targetToHide: SeenPaymentNotification = when (policy) {
                    HidePolicy.HIDE_OPENBANK -> {
                        if (currentEntry.classification.kind == SourceKind.BANK) currentEntry else matchingOtherApp
                    }
                    HidePolicy.HIDE_GOOGLE_WALLET -> {
                        if (currentEntry.classification.kind == SourceKind.WALLET) currentEntry else matchingOtherApp
                    }
                    HidePolicy.HIDE_SECOND_ARRIVING -> currentEntry
                }

                cancelNotification(targetToHide.key)

                AppPreferences.addHiddenRecord(
                    amountCents = amountCents,
                    hiddenPackageLabel = targetToHide.classification.displayName,
                    title = targetToHide.title,
                    text = targetToHide.text
                )
            } else {
                recentPayments.add(currentEntry)
            }
        }
    }

    private fun classifyPackage(sbn: StatusBarNotification): SourceClassification? {
        val pkg = sbn.packageName ?: return null
        val enabledBanks = AppPreferences.enabledBankPackages.value

        return when {
            pkg in enabledBanks -> {
                SourceClassification(
                    kind = SourceKind.BANK,
                    displayName = BankCatalog.getNameForPackage(pkg)
                )
            }
            pkg == "com.google.android.apps.walletnfcrel" -> {
                SourceClassification(
                    kind = SourceKind.WALLET,
                    displayName = "Google Wallet"
                )
            }
            pkg == "com.samsung.android.spay" -> {
                SourceClassification(
                    kind = SourceKind.WALLET,
                    displayName = "Samsung Wallet"
                )
            }
            pkg == "com.google.android.gms" -> {
                val channelId = sbn.notification?.channelId.orEmpty().lowercase()
                if (channelId.contains("wallet") || channelId.contains("pay") || channelId.contains("tapandpay")) {
                    SourceClassification(
                        kind = SourceKind.WALLET,
                        displayName = "Google Wallet"
                    )
                } else {
                    null
                }
            }
            else -> null
        }
    }
}
