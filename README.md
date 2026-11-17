# PayFilter — Filtro inteligente de pagos NFC duplicados

<p align="center">
  <a href="https://github.com/neoruben/PayFilter/releases/latest"><img src="https://img.shields.io/badge/⬇_Descargar_APK-Android-2E7D32?style=for-the-badge" alt="Descargar APK para Android" height="44"></a>
  &nbsp;
  <a href="https://ko-fi.com/neoruben"><img src="https://img.shields.io/badge/☕_Invítame_a_un_café-Ko--fi-f5c518?style=for-the-badge&labelColor=0e1726" alt="Invítame a un café en Ko-fi" height="44"></a>
</p>

Aplicación Android en **Kotlin + Jetpack Compose** que evita las notificaciones duplicadas cuando pagas con el móvil (**Google Wallet** o **Samsung Wallet**) usando una tarjeta bancaria cuya aplicación oficial también tienes instalada en el teléfono.

---

## ✨ Características principales

1. **Detección automática de tus bancos instalados:**
   - Detecta qué aplicaciones bancarias tienes instaladas en el móvil (Openbank, Santander, BBVA, CaixaBankNow, imagin, ING, Revolut, N26, Sabadell, Bankinter, Unicaja, ABANCA, B100, Kutxabank, Ibercaja, EVO Banco, MyInvestor, Trade Republic, Wise, PayPal, Curve, Cetelem, WiZink, Cajamar y Laboral Kutxa).
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

## 📲 Instalación

1. Descarga el archivo **`PayFilter-v1.2.3.apk`** desde la sección **[Releases](https://github.com/neoruben/PayFilter/releases/latest)**.
2. Instálalo en tu dispositivo Android (Android 10 / API 29 o superior).
3. Abre **PayFilter**, concede el permiso de **Acceso a notificaciones** y elige qué bancos quieres filtrar.

---

## ☕ Apoya el proyecto

Si esta aplicación te resulta útil, puedes apoyar su desarrollo en **[Ko-fi](https://ko-fi.com/neoruben)**.

> **Aviso legal:** Aplicación independiente no afiliada, patrocinada ni respaldada por Google ni por ninguna entidad bancaria. Los nombres comerciales se utilizan únicamente para indicar compatibilidad.

---

## 🛠️ Arquitectura y detalles técnicos

- **Servicio de notificaciones (`PayDedupeNotificationService.kt`):** Extiende `NotificationListenerService`. Escucha las notificaciones de las carteras NFC (Google Wallet / Samsung Wallet) y de los bancos activados, y oculta la notificación duplicada (`cancelNotification`) cuando coincide exactamente el importe en una ventana de 60 segundos.
- **Extractor de importes (`AmountExtractor.kt`):** Extrae y normaliza importes en euros a céntimos (`12,50 €`, `12.50 EUR`, `1.250,45 €`, etc.).
- **Catálogo de bancos (`BankCatalog.kt`):** Lista de aplicaciones bancarias compatibles y detección de las que están instaladas en el dispositivo mediante `PackageManager`.
- **Preferencias e historial (`AppPreferences.kt`):** Persistencia local en `SharedPreferences` expuesta mediante `StateFlow` (estado del filtro, bancos habilitados, política `HidePolicy`, estado de cierre del aviso introductorio e historial de notificaciones ocultadas).
- **Interfaz (`MainActivity.kt`):** Jetpack Compose + Material 3 (`v1.2.3`, `versionCode = 6`). Menú lateral (`ModalNavigationDrawer`) ordenado en:
  1. **Mis bancos y filtro** (pantalla principal con toggle global y lista de bancos).
  2. **Notificaciones a mostrar** (selección inline de `HidePolicy` con el mismo estilo tipográfico `labelLarge` / `SemiBold`).
  3. **Notificaciones ocultadas** (historial local con contador).
  4. Acceso rápido a **Historial de Android** y **Ko-fi**.
- **Nota de instalación (Android 13–16):** Al instalar un APK fuera de Google Play, Android restringe el permiso de acceso a notificaciones como *Ajuste restringido*. Se desbloquea en *Info de la app → Permitir ajustes restringidos* (tras intentar activar el permiso) o por ADB con `adb shell appops set com.paydedupe.app ACCESS_RESTRICTED_SETTINGS allow`.
