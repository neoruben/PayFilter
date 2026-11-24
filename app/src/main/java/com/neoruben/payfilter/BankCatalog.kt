package com.neoruben.payfilter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

data class BankCatalogEntry(
    val packageName: String,
    val defaultName: String
)

data class DetectedBankApp(
    val packageName: String,
    val displayName: String,
    val isInstalled: Boolean,
    val iconBitmap: ImageBitmap?
)

object BankCatalog {

    val SUPPORTED_BANKS: List<BankCatalogEntry> = listOf(
        BankCatalogEntry("es.openbank.mobile", "Openbank"),
        BankCatalogEntry("es.bancosantander.apps", "Banco Santander"),
        BankCatalogEntry("com.bbva.bbvacontigo", "BBVA"),
        BankCatalogEntry("es.caixabank.caixabanknow", "CaixaBankNow"),
        BankCatalogEntry("es.lacaixa.mobile.android.newwapicon", "CaixaBank (Clásica)"),
        BankCatalogEntry("com.imaginbank.app", "imagin"),
        BankCatalogEntry("www.ingdirect.nativeframe", "ING España"),
        BankCatalogEntry("com.revolut.revolut", "Revolut"),
        BankCatalogEntry("de.number26.android", "N26"),
        BankCatalogEntry("net.inverline.bancosabadell.officelocator.android", "Banco Sabadell"),
        BankCatalogEntry("com.bankinter.launcher", "Bankinter"),
        BankCatalogEntry("es.unicajabanco.app", "Unicaja"),
        BankCatalogEntry("com.abanca.bancamovil", "ABANCA"),
        BankCatalogEntry("com.abanca.b100", "B100 (ABANCA)"),
        BankCatalogEntry("com.kutxabank.android", "Kutxabank"),
        BankCatalogEntry("es.ibercaja.ibercajaapp", "Ibercaja"),
        BankCatalogEntry("es.evobanco.bancamovil", "EVO Banco"),
        BankCatalogEntry("es.myinvestor.app", "MyInvestor"),
        BankCatalogEntry("de.traderepublic.app", "Trade Republic"),
        BankCatalogEntry("com.transferwise.android", "Wise"),
        BankCatalogEntry("com.paypal.android.p2pmobile", "PayPal"),
        BankCatalogEntry("com.imaginecurve.curve.prd", "Curve"),
        BankCatalogEntry("es.cetelem.mi.espacio.cliente", "Cetelem"),
        BankCatalogEntry("es.wizink.mobile", "WiZink"),
        BankCatalogEntry("com.grupocooperativocajamar.wefferent", "Grupo Cajamar"),
        BankCatalogEntry("com.laboralkutxa.bancamovil", "Laboral Kutxa")
    )

    fun getNameForPackage(packageName: String): String {
        return SUPPORTED_BANKS.firstOrNull { it.packageName == packageName }?.defaultName
            ?: packageName
    }

    fun loadBanksStatus(context: Context): List<DetectedBankApp> {
        val pm = context.packageManager
        return SUPPORTED_BANKS.map { entry ->
            val appInfo = runCatching {
                pm.getApplicationInfo(entry.packageName, 0)
            }.getOrNull()

            if (appInfo != null) {
                val label = runCatching {
                    pm.getApplicationLabel(appInfo).toString()
                }.getOrDefault(entry.defaultName)

                val icon = runCatching {
                    drawableToImageBitmap(pm.getApplicationIcon(appInfo))
                }.getOrNull()

                DetectedBankApp(
                    packageName = entry.packageName,
                    displayName = label,
                    isInstalled = true,
                    iconBitmap = icon
                )
            } else {
                DetectedBankApp(
                    packageName = entry.packageName,
                    displayName = entry.defaultName,
                    isInstalled = false,
                    iconBitmap = null
                )
            }
        }
    }

    private fun drawableToImageBitmap(drawable: Drawable): ImageBitmap {
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            return drawable.bitmap.asImageBitmap()
        }
        val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 96
        val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 96
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap.asImageBitmap()
    }
}
