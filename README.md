# PayDedupe — Filtro inteligente de pagos NFC duplicados

<p align="center">
  <a href="https://github.com/neoruben/PayDedupe/releases/latest"><img src="https://img.shields.io/badge/⬇_Descargar_APK-Android-2E7D32?style=for-the-badge" alt="Descargar APK para Android" height="44"></a>
  &nbsp;
  <a href="https://ko-fi.com/neoruben"><img src="https://img.shields.io/badge/☕_Invítame_a_un_café-Ko--fi-f5c518?style=for-the-badge&labelColor=0e1726" alt="Invítame a un café en Ko-fi" height="44"></a>
</p>

Aplicación Android en **Kotlin + Jetpack Compose** que evita las notificaciones duplicadas cuando pagas con el móvil (**Google Wallet** o **Samsung Wallet**) usando una tarjeta bancaria cuya aplicación oficial también tienes instalada en el teléfono.

---

## ✨ Características principales

1. **Detección automática de tus bancos instalados:**
   - Detecta qué aplicaciones bancarias tienes instaladas en el móvil (Openbank, Santander, BBVA, CaixaBankNow, imagin, ING, Revolut, N26, Sabadell, Bankinter, Unicaja, ABANCA, B100, Kutxabank, Ibercaja, EVO Banco, MyInvestor, Trade Republic, Wise, PayPal, Curve, Cetelem, WiZink, Cajamar y Laboral Kutxa).
   - Permite activar o desactivar el filtro de forma individual para cada banco con un interruptor.
2. **Comparación exacta por importe en euros:**
   - Extrae el importe de la notificación (`12,50 €`, `12.50 EUR`, `€12,50`, `1.250,45 €`...).
   - Solo oculta la notificación repetida si tanto la cartera NFC como el banco notifican **exactamente el mismo importe** dentro de una ventana de 60 segundos.
   - Si pagas con la **tarjeta física**, en una web o recibes un Bizum/transferencia, la notificación del banco se muestra con total normalidad.
3. **No elimina nada del historial:**
   - Oculta el aviso duplicado de la barra de estado superior para que no moleste dos veces, pero la notificación permanece íntegra en el **Historial de notificaciones de Android** y en el registro interno de la propia aplicación.
4. **100% Privada y Offline:**
   - No solicita el permiso `INTERNET` en el `AndroidManifest.xml`. Ningún dato sale jamás de tu dispositivo.

---

## 📲 Instalación

1. Descarga el archivo **`PayDedupe-v1.1.0.apk`** desde la sección **[Releases](https://github.com/neoruben/PayDedupe/releases/latest)**.
2. Instálalo en tu dispositivo Android (Android 10 / API 29 o superior).
3. Abre **PayDedupe**, concede el permiso de **Acceso a notificaciones** y elige qué bancos quieres filtrar.

---

## ☕ Apoya el proyecto

Si esta aplicación te resulta útil, puedes apoyar su desarrollo en **[Ko-fi](https://ko-fi.com/neoruben)**.

> **Aviso legal:** Aplicación independiente no afiliada, patrocinada ni respaldada por Google ni por ninguna entidad bancaria. Los nombres comerciales se utilizan únicamente para indicar compatibilidad.
