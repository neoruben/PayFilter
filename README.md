# PayFilter

<p align="center">
  <a href="https://github.com/neoruben/PayFilter/releases/latest"><img src="https://img.shields.io/badge/⬇_Descargar_APK-Android-2E7D32?style=for-the-badge" alt="Descargar APK para Android" height="38"></a>
  &nbsp;
  <a href="https://ko-fi.com/neoruben" target="_blank">
    <img src="https://ko-fi.com/img/githubbutton_sm.svg" alt="Support me on Ko-fi" height="38">
  </a>
</p>

*[English version below](#english)*

Cuando pagas con el móvil (**Google Wallet** o **Samsung Wallet**) y también tienes instalada la app de tu banco, te llegan **dos notificaciones del mismo pago**: una de la cartera y otra del banco. **PayFilter oculta la repetida** para que cada pago te avise una sola vez.

- Solo oculta el aviso si la cartera y el banco notifican **el mismo importe en menos de un minuto**. Los pagos con la tarjeta física, las compras por internet, los Bizum y las transferencias te siguen avisando como siempre.
- Tú eliges qué bancos filtrar y qué aviso prefieres ver, el de la cartera o el del banco.
- No borra nada: la notificación ocultada sigue en el **historial de notificaciones de Android** y en el registro de la propia app.
- **Privada:** no tiene acceso a internet, así que ningún dato sale de tu móvil.

Funciona con Openbank, Santander, BBVA, CaixaBank, imagin, ING, Revolut, N26, Sabadell, Bankinter, Unicaja, ABANCA, B100, Kutxabank, Ibercaja, EVO Banco, MyInvestor, Trade Republic, Wise, PayPal, Curve, Cetelem, WiZink, Cajamar y Laboral Kutxa.

## 📱 Instalación

Necesita Android 10 o superior.

1. Descarga **`PayFilter-v1.2.5.apk`** desde **[Releases](https://github.com/neoruben/PayFilter/releases/latest)**.
   Si tienes una versión anterior a la 1.2.5, desinstálala antes: desde la 1.2.5 la app es otra para Android y las dos estarían filtrando a la vez.
2. **Pausa Play Protect mientras la instalas.** Play Protect puede bloquear o borrar las apps que no vienen de Google Play. Abre **Play Store → tu foto de perfil → Play Protect → ⚙ (arriba a la derecha)** y desactiva **Analizar apps con Play Protect**.
3. Abre el APK e instálalo (si te lo pide, permite que tu navegador o tu gestor de archivos instale apps).
4. Vuelve a activar **Analizar apps con Play Protect**.
5. **Da acceso a las notificaciones.** Abre **PayFilter** y pulsa el botón para dar el acceso. Android abre **Notificaciones de dispositivos y aplicaciones** (en algunos móviles, *Lectura, respuesta y control de notificaciones*). Toca **PayFilter**, activa **Permitir acceso a las notificaciones** y pulsa **Permitir** en el aviso que sale.
6. De vuelta en PayFilter, elige qué bancos quieres filtrar.

#### Si el interruptor de PayFilter sale en gris y no se puede activar

Android bloquea este permiso a las apps instaladas fuera de Google Play (*ajuste restringido*). Desbloquéalo así:

1. Toca **PayFilter** aunque salga en gris. Sale el aviso *Ajuste restringido*: pulsa **Aceptar**. Este paso hace falta; si no, la opción del paso 3 no aparece.
2. Ve a **Ajustes → Aplicaciones → PayFilter** (*Información de la aplicación*).
3. Toca **⋮ (arriba a la derecha) → Permitir ajustes restringidos** y confírmalo con tu PIN, patrón o huella.
4. Vuelve a **Ajustes → Notificaciones → Notificaciones de dispositivos y aplicaciones**, toca **PayFilter** y activa **Permitir acceso a las notificaciones**. Ahora el interruptor ya funciona: pulsa **Permitir** en el aviso.

Si en el paso 3 no aparece el menú **⋮**, repite el paso 1 y vuelve a probar. Con el móvil conectado por ADB también puedes desbloquearlo con un solo comando:
`adb shell appops set com.neoruben.payfilter ACCESS_RESTRICTED_SETTINGS allow`

## ☕ Apoya el proyecto

Si PayFilter te resulta útil, puedes invitarme a un café en **[Ko-fi](https://ko-fi.com/neoruben)**.

> **Aviso legal:** aplicación independiente, no afiliada, patrocinada ni respaldada por Google, Samsung ni ninguna entidad bancaria. Los nombres comerciales se usan solo para indicar compatibilidad.

---

<a id="english"></a>

## 🇬🇧 English

<p align="center">
  <a href="https://github.com/neoruben/PayFilter/releases/latest"><img src="https://img.shields.io/badge/⬇_Download_APK-Android-2E7D32?style=for-the-badge" alt="Download APK for Android" height="38"></a>
</p>

When you pay with your phone (**Google Wallet** or **Samsung Wallet**) and also have your bank's app installed, you get **two notifications for the same payment**: one from the wallet and one from the bank. **PayFilter hides the duplicate** so each payment notifies you only once.

- It only hides a notification when the wallet and the bank report **the same amount within a minute**. Physical card payments, online purchases, Bizum and transfers still notify you as usual.
- You choose which banks to filter and which notification you'd rather see: the wallet's or the bank's.
- Nothing is deleted: the hidden notification stays in **Android's notification history** and in the app's own log.
- **Private:** it has no internet access, so no data ever leaves your phone.

Works with Openbank, Santander, BBVA, CaixaBank, imagin, ING, Revolut, N26, Sabadell, Bankinter, Unicaja, ABANCA, B100, Kutxabank, Ibercaja, EVO Banco, MyInvestor, Trade Republic, Wise, PayPal, Curve, Cetelem, WiZink, Cajamar and Laboral Kutxa.

### 📱 Installation

Requires Android 10 or newer.

1. Download **`PayFilter-v1.2.5.apk`** from **[Releases](https://github.com/neoruben/PayFilter/releases/latest)**.
   If you have a version older than 1.2.5, uninstall it first: from 1.2.5 on, Android sees it as a different app, and both would be filtering at once.
2. **Pause Play Protect while you install it.** Play Protect may block or delete apps that don't come from Google Play. Open the **Play Store → your profile picture → Play Protect → ⚙ (top right)** and turn off **Scan apps with Play Protect**.
3. Open the APK and install it (if asked, allow your browser or file manager to install apps).
4. Turn **Scan apps with Play Protect** back on.
5. **Grant notification access.** Open **PayFilter** and tap the button to grant access. Android opens **Device & app notifications** (on some phones, *Notification read, reply & control*). Tap **PayFilter**, turn on **Allow notification access** and tap **Allow** in the confirmation dialog.
6. Back in PayFilter, choose which bank notifications you want to filter.

#### If PayFilter's switch is greyed out and can't be turned on

Android blocks this permission for apps installed outside Google Play (*restricted setting*). Unlock it like this:

1. Tap the greyed-out **PayFilter** entry anyway. A *Restricted setting* message appears: tap **OK**. This step is needed, otherwise the option in step 3 won't show up.
2. Go to **Settings → Apps → PayFilter** (*App info*).
3. Tap **⋮ (top right) → Allow restricted settings** and confirm with your PIN, pattern or fingerprint.
4. Go back to **Settings → Notifications → Device & app notifications**, tap **PayFilter** and turn on **Allow notification access**. The switch now works: tap **Allow** in the dialog.

If the **⋮** menu doesn't appear in step 3, repeat step 1 and try again. With the phone connected over ADB you can also unlock it with one command:
`adb shell appops set com.neoruben.payfilter ACCESS_RESTRICTED_SETTINGS allow`

### ☕ Support the project

If PayFilter is useful to you, you can buy me a coffee on **[Ko-fi](https://ko-fi.com/neoruben)**.

> **Disclaimer:** independent project, not affiliated with, sponsored or endorsed by Google, Samsung or any bank. Trademarks are used only to indicate compatibility.
