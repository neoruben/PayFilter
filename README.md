# PayFilter — Smart Duplicate Filter for NFC Mobile Payments 💳📱

<p align="center">
  <a href="https://github.com/neoruben/PayFilter/releases/latest"><img src="https://img.shields.io/badge/⬇_Download_APK-Android-2E7D32?style=for-the-badge" alt="Download APK for Android" height="38"></a>
  &nbsp;
  <a href="https://ko-fi.com/neoruben" target="_blank">
    <img src="https://ko-fi.com/img/githubbutton_sm.svg" alt="Support me on Ko-fi" height="38">
  </a>
</p>

An open-source Android app built with **Kotlin + Jetpack Compose** that prevents duplicate notifications whenever you pay with your phone (**Google Wallet** or **Samsung Wallet**) using a card whose official banking app is also installed on your device.

---

## ✨ Key Features

1. **Automatic Detection of Installed Banks:**
   - Automatically identifies compatible banking apps installed on your device (Openbank, Santander, BBVA, CaixaBankNow, imagin, ING, Revolut, N26, Sabadell, Bankinter, Unicaja, ABANCA, B100, Kutxabank, Ibercaja, EVO Banco, MyInvestor, Trade Republic, Wise, PayPal, Curve, Cetelem, WiZink, Cajamar, Laboral Kutxa, and more).
   - Easily toggle filters on or off per bank.
2. **Clean & Accessible Navigation Drawer:**
   - Compact interface without excessive scrolling: manage enabled banks directly from the home screen and easily access notification policies and filtered history via the side drawer.
3. **Exact Amount Matching in Euros:**
   - Parses and normalizes transaction amounts (`12,50 €`, `12.50 EUR`, `€12.50`, `1.250,45 €`...).
   - Only suppresses the duplicate notification if both the NFC wallet and the bank report the **exact same amount** within a 60-second window.
   - Physical card payments, web transactions, wire transfers, and Bizum payments will always notify normally.
4. **Non-Destructive Filtering:**
   - Only dismisses the repetitive pop-up from your status bar to prevent spam. All original notifications remain intact in **Android's Notification History** and in the app's local log.
5. **100% Private & Fully Offline:**
   - Does not request the `INTERNET` permission in `AndroidManifest.xml`. No data ever leaves your device.

---

## 📱 Installation

1. Download **`PayFilter-v1.2.4.apk`** from the **[Releases](https://github.com/neoruben/PayFilter/releases/latest)** section.
2. Install it on your Android device (Android 10 / API 29 or higher).
3. Open **PayFilter**, grant **Notification Access**, and select which bank notifications you want to filter.

> **Note for Android 13–16:** When sideloading an APK outside Google Play, Android restricts notification listener permissions under *Restricted settings*. You can unlock it by tapping *App Info → three dots (top right) → Allow restricted settings* (after trying to enable notification access once), or via ADB:  
> `adb shell appops set com.paydedupe.app ACCESS_RESTRICTED_SETTINGS allow`

---

## ☕ Support the Project

If **PayFilter** saves you from annoying notification spam, you can support its independent development by inviting me to a coffee:

<p align="center">
  <a href="https://ko-fi.com/neoruben" target="_blank">
    <img src="https://ko-fi.com/img/githubbutton_sm.svg" alt="Support me on Ko-fi" height="38">
  </a>
</p>

> **Disclaimer:** Independent project not affiliated with, sponsored, or endorsed by Google, Samsung, or any financial institution. Trademarks are used solely to indicate compatibility.

---

## 🛠️ Architecture & Technical Details

- **Notification Service (`PayDedupeNotificationService.kt`):** Extends `NotificationListenerService`. Listens for notifications from NFC wallets (Google Wallet / Samsung Wallet) and active banks, canceling duplicate alerts (`cancelNotification`) when exact euro amounts match within 60 seconds.
- **Amount Extractor (`AmountExtractor.kt`):** Extracts and normalizes transaction amounts in euros to cents (`12,50 €`, `12.50 EUR`, `1.250,45 €`, etc.).
- **Bank Catalog (`BankCatalog.kt`):** Registry of compatible banking apps with detection via `PackageManager`.
- **Preferences & History (`AppPreferences.kt`):** Local `SharedPreferences` exposed via `StateFlow` (global filter state, bank toggles, `HidePolicy`, dismiss state for intro card, and filtered history log).
- **UI (`MainActivity.kt`):** Jetpack Compose + Material 3. Side drawer navigation organized into *My Banks*, *Notifications to show*, *Filtered history*, and quick access to *Android History* and *Ko-fi*.

<br>

---
---

<br>

## 🇪🇸 Versión en español

# PayFilter — Filtro inteligente de pagos NFC duplicados

<p align="center">
  <a href="https://github.com/neoruben/PayFilter/releases/latest"><img src="https://img.shields.io/badge/⬇_Descargar_APK-Android-2E7D32?style=for-the-badge" alt="Descargar APK para Android" height="38"></a>
  &nbsp;
  <a href="https://ko-fi.com/neoruben" target="_blank">
    <img src="https://ko-fi.com/img/githubbutton_sm.svg" alt="Support me on Ko-fi" height="38">
  </a>
</p>

Aplicación Android en **Kotlin + Jetpack Compose** que evita las notificaciones duplicadas cuando pagas con el móvil (**Google Wallet** o **Samsung Wallet**) usando una tarjeta bancaria cuya aplicación oficial también tienes instalada en el teléfono.

---

### ✨ Características principales

1. **Detección automática de tus bancos instalados:**
   - Detecta qué aplicaciones bancarias tienes instaladas en el móvil (Openbank, Santander, BBVA, CaixaBankNow, imagin, ING, Revolut, N26, Sabadell, Bankinter, Unicaja, ABANCA, B100, Kutxabank, Ibercaja, EVO Banco, MyInvestor, Trade Republic, Wise, PayPal, Curve, Cetelem, WiZink, Cajamar, Laboral Kutxa, etc.).
   - Permite activar o desactivar el filtro de forma individual para cada banco con un interruptor.
2. **Panel lateral limpio y accesible:**
   - Interfaz compacta sin desplazamientos excesivos: gestiona tus bancos directamente en la pantalla de inicio y accede a tus preferencias de notificación y al historial de alertas filtradas cómodamente desde el panel lateral.
3. **Comparación exacta por importe en euros:**
   - Extrae el importe de la notificación (`12,50 €`, `12.50 EUR`, `€12,50`, `1.250,45 €`...).
   - Solo oculta la notificación repetida si tanto la cartera NFC como el banco notifican **exactamente el mismo importe** dentro de una ventana de 60 segundos.
   - Si pagas con la **tarjeta física**, en una web o recibes un Bizum/transferencia, la notificación del banco se muestra con total normalidad.
4. **No elimina nada del historial:**
   - Oculta el aviso duplicado de la barra de estado superior para que no moleste dos veces, pero la notificación permanece íntegra en el **Historial de notificaciones de Android** y en el registro interno de la propia aplicación.
5. **100% Privada y Offline:**
   - No solicita el permiso `INTERNET` en el `AndroidManifest.xml`. Ningún dato sale jamás de tu dispositivo.

---

### 📱 Instalación

1. Descarga el archivo **`PayFilter-v1.2.4.apk`** desde la sección **[Releases](https://github.com/neoruben/PayFilter/releases/latest)**.
2. Instálalo en tu dispositivo Android (Android 10 / API 29 o superior).
3. Abre **PayFilter**, concede el permiso de **Acceso a notificaciones** y elige qué bancos quieres filtrar.

---

### ☕ Apoya el proyecto

Si esta aplicación te resulta útil, puedes apoyar su desarrollo invitándome a un café en **[Ko-fi](https://ko-fi.com/neoruben)**:

<p align="center">
  <a href="https://ko-fi.com/neoruben" target="_blank">
    <img src="https://ko-fi.com/img/githubbutton_sm.svg" alt="Support me on Ko-fi" height="38">
  </a>
</p>

> **Aviso legal:** Aplicación independiente no afiliada, patrocinada ni respaldada por Google ni por ninguna entidad bancaria. Los nombres comerciales se utilizan únicamente para indicar compatibilidad.
