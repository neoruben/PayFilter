package com.paydedupe.app

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class HidePolicy(val label: String, val description: String) {
    HIDE_OPENBANK(
        label = "Ocultar la del Banco",
        description = "Cuando pagas con el móvil, te deja visible la de Google Wallet y oculta la duplicada del banco."
    ),
    HIDE_GOOGLE_WALLET(
        label = "Ocultar la de Google Wallet",
        description = "Cuando pagas con el móvil, te deja visible la del banco y oculta la de Google Wallet."
    ),
    HIDE_SECOND_ARRIVING(
        label = "Ocultar la que llegue segunda",
        description = "Te muestra la primera que entre y oculta automáticamente la segunda del mismo importe."
    )
}

data class HiddenNotificationEntry(
    val timestampMillis: Long,
    val formattedTime: String,
    val amountCents: Long,
    val hiddenPackageLabel: String,
    val originalTitle: String,
    val originalText: String
)

object AppPreferences {
    private const val PREFS_NAME = "pay_dedupe_prefs"
    private const val KEY_ENABLED = "filter_enabled"
    private const val KEY_HIDE_POLICY = "hide_policy"
    private const val KEY_WINDOW_SECONDS = "window_seconds"
    private const val KEY_ENABLED_BANKS = "enabled_bank_packages"
    private const val KEY_HISTORY_JSON = "history_json"
    private const val KEY_INTRO_DISMISSED = "intro_dismissed"
    private const val MAX_HISTORY_ITEMS = 50

    private lateinit var prefs: SharedPreferences

    private val _isFilterEnabled = MutableStateFlow(true)
    val isFilterEnabled: StateFlow<Boolean> = _isFilterEnabled.asStateFlow()

    private val _isIntroDismissed = MutableStateFlow(false)
    val isIntroDismissed: StateFlow<Boolean> = _isIntroDismissed.asStateFlow()

    private val _hidePolicy = MutableStateFlow(HidePolicy.HIDE_OPENBANK)
    val hidePolicy: StateFlow<HidePolicy> = _hidePolicy.asStateFlow()

    private val _windowSeconds = MutableStateFlow(60)
    val windowSeconds: StateFlow<Int> = _windowSeconds.asStateFlow()

    private val _enabledBankPackages = MutableStateFlow<Set<String>>(setOf("es.openbank.mobile"))
    val enabledBankPackages: StateFlow<Set<String>> = _enabledBankPackages.asStateFlow()

    private val _history = MutableStateFlow<List<HiddenNotificationEntry>>(emptyList())
    val history: StateFlow<List<HiddenNotificationEntry>> = _history.asStateFlow()

    fun init(context: Context) {
        if (::prefs.isInitialized) return
        val appContext = context.applicationContext
        prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _isFilterEnabled.value = prefs.getBoolean(KEY_ENABLED, true)
        _hidePolicy.value = runCatching {
            HidePolicy.valueOf(
                prefs.getString(KEY_HIDE_POLICY, HidePolicy.HIDE_OPENBANK.name)
                    ?: HidePolicy.HIDE_OPENBANK.name
            )
        }.getOrDefault(HidePolicy.HIDE_OPENBANK)
        _windowSeconds.value = prefs.getInt(KEY_WINDOW_SECONDS, 60)
        _isIntroDismissed.value = prefs.getBoolean(KEY_INTRO_DISMISSED, false)

        val savedBanks = prefs.getStringSet(KEY_ENABLED_BANKS, null)
        if (savedBanks != null) {
            _enabledBankPackages.value = savedBanks.toSet()
        } else {
            val installedDefaults = BankCatalog.loadBanksStatus(appContext)
                .filter { it.isInstalled }
                .map { it.packageName }
                .toSet()
            val initialSet = if (installedDefaults.isNotEmpty()) {
                installedDefaults
            } else {
                setOf("es.openbank.mobile")
            }
            prefs.edit().putStringSet(KEY_ENABLED_BANKS, initialSet).apply()
            _enabledBankPackages.value = initialSet
        }

        _history.value = loadHistoryFromPrefs()
    }

    fun setFilterEnabled(enabled: Boolean) {
        if (!::prefs.isInitialized) return
        prefs.edit().putBoolean(KEY_ENABLED, enabled).apply()
        _isFilterEnabled.value = enabled
    }

    fun setIntroDismissed(dismissed: Boolean) {
        if (!::prefs.isInitialized) return
        prefs.edit().putBoolean(KEY_INTRO_DISMISSED, dismissed).apply()
        _isIntroDismissed.value = dismissed
    }

    fun setHidePolicy(policy: HidePolicy) {
        if (!::prefs.isInitialized) return
        prefs.edit().putString(KEY_HIDE_POLICY, policy.name).apply()
        _hidePolicy.value = policy
    }

    fun setBankEnabled(packageName: String, enabled: Boolean) {
        if (!::prefs.isInitialized) return
        val updated = _enabledBankPackages.value.toMutableSet().apply {
            if (enabled) add(packageName) else remove(packageName)
        }
        prefs.edit().putStringSet(KEY_ENABLED_BANKS, updated).apply()
        _enabledBankPackages.value = updated
    }

    fun addHiddenRecord(
        amountCents: Long,
        hiddenPackageLabel: String,
        title: String,
        text: String
    ) {
        if (!::prefs.isInitialized) return
        val now = System.currentTimeMillis()
        val formatter = SimpleDateFormat("dd/MM HH:mm:ss", Locale("es", "ES"))
        val newEntry = HiddenNotificationEntry(
            timestampMillis = now,
            formattedTime = formatter.format(Date(now)),
            amountCents = amountCents,
            hiddenPackageLabel = hiddenPackageLabel,
            originalTitle = title,
            originalText = text
        )
        val updated = (listOf(newEntry) + _history.value).take(MAX_HISTORY_ITEMS)
        _history.value = updated
        saveHistoryToPrefs(updated)
    }

    fun clearHistory() {
        if (!::prefs.isInitialized) return
        prefs.edit().remove(KEY_HISTORY_JSON).apply()
        _history.value = emptyList()
    }

    private fun loadHistoryFromPrefs(): List<HiddenNotificationEntry> {
        val raw = prefs.getString(KEY_HISTORY_JSON, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    add(
                        HiddenNotificationEntry(
                            timestampMillis = obj.optLong("ts"),
                            formattedTime = obj.optString("time"),
                            amountCents = obj.optLong("cents"),
                            hiddenPackageLabel = obj.optString("pkg"),
                            originalTitle = obj.optString("title"),
                            originalText = obj.optString("text")
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun saveHistoryToPrefs(entries: List<HiddenNotificationEntry>) {
        val array = JSONArray()
        for (entry in entries) {
            val obj = JSONObject().apply {
                put("ts", entry.timestampMillis)
                put("time", entry.formattedTime)
                put("cents", entry.amountCents)
                put("pkg", entry.hiddenPackageLabel)
                put("title", entry.originalTitle)
                put("text", entry.originalText)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_HISTORY_JSON, array.toString()).apply()
    }
}
