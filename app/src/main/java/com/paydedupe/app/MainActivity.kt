package com.paydedupe.app

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.launch

private const val KOFI_URL = "https://ko-fi.com/neoruben"
private const val APP_VERSION = "v1.2.0"

enum class AppDestination(val title: String) {
    BANKS("PayFilter"),
    HISTORY("Notificaciones ocultadas")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppPreferences.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                PayFilterApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayFilterApp() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    val isFilterEnabled by AppPreferences.isFilterEnabled.collectAsState()
    val hidePolicy by AppPreferences.hidePolicy.collectAsState()
    val enabledBanks by AppPreferences.enabledBankPackages.collectAsState()
    val history by AppPreferences.history.collectAsState()

    var currentDestination by remember { mutableStateOf(AppDestination.BANKS) }
    var showPolicyDialog by remember { mutableStateOf(false) }
    var showDisclosureDialog by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var hasNotificationPermission by remember {
        mutableStateOf(isNotificationServiceEnabled(context))
    }
    var detectedBanks by remember {
        mutableStateOf(BankCatalog.loadBanksStatus(context))
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasNotificationPermission = isNotificationServiceEnabled(context)
                detectedBanks = BankCatalog.loadBanksStatus(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    BackHandler(enabled = currentDestination == AppDestination.HISTORY || drawerState.isOpen) {
        if (drawerState.isOpen) {
            scope.launch { drawerState.close() }
        } else {
            currentDestination = AppDestination.BANKS
        }
    }

    if (showDisclosureDialog) {
        ProminentDisclosureDialog(
            onDismiss = { showDisclosureDialog = false },
            onConfirm = {
                showDisclosureDialog = false
                context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            }
        )
    }

    if (showPolicyDialog) {
        PolicySelectionDialog(
            currentPolicy = hidePolicy,
            onSelect = {
                AppPreferences.setHidePolicy(it)
                showPolicyDialog = false
            },
            onDismiss = { showPolicyDialog = false }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(320.dp)
            ) {
                DrawerHeader()

                Spacer(modifier = Modifier.height(12.dp))

                NavigationDrawerItem(
                    label = { Text("Mis bancos y filtro", fontWeight = FontWeight.SemiBold) },
                    selected = (currentDestination == AppDestination.BANKS),
                    onClick = {
                        currentDestination = AppDestination.BANKS
                        scope.launch { drawerState.close() }
                    },
                    icon = { Text("🏦", fontSize = 18.sp) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = {
                        Column {
                            Text("Notificación a mostrar", fontWeight = FontWeight.SemiBold)
                            Text(
                                text = hidePolicy.label,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            showPolicyDialog = true
                        }
                    },
                    icon = { Text("⚙️", fontSize = 18.sp) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Notificaciones ocultadas", fontWeight = FontWeight.SemiBold) },
                    selected = (currentDestination == AppDestination.HISTORY),
                    onClick = {
                        currentDestination = AppDestination.HISTORY
                        scope.launch { drawerState.close() }
                    },
                    icon = { Text("📋", fontSize = 18.sp) },
                    badge = {
                        if (history.isNotEmpty()) {
                            Badge {
                                Text(history.size.toString())
                            }
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(8.dp))

                NavigationDrawerItem(
                    label = { Text("Historial de Android") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        openAndroidNotificationHistory(context)
                    },
                    icon = { Text("📱", fontSize = 18.sp) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Invitar a un café en Ko-fi", fontWeight = FontWeight.SemiBold)
                        }
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        openExternalUrl(context, KOFI_URL)
                    },
                    icon = { Text("☕", fontSize = 18.sp) },
                    badge = {
                        Surface(
                            color = Color(0xFFF5C518),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Apoyo",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0E1726),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "PayFilter $APP_VERSION\n100% Privado y Offline · Sin Internet",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        if (currentDestination == AppDestination.BANKS) {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Abrir panel lateral"
                                )
                            }
                        } else {
                            IconButton(onClick = { currentDestination = AppDestination.BANKS }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Volver a bancos"
                                )
                            }
                        }
                    },
                    title = {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (currentDestination == AppDestination.BANKS) "PayFilter" else "Ocultadas",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = APP_VERSION,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (currentDestination == AppDestination.BANKS) {
                                    "Filtro inteligente de pagos NFC"
                                } else {
                                    "${history.size} pago(s) filtrado(s)"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    actions = {
                        Button(
                            onClick = { openExternalUrl(context, KOFI_URL) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF5C518),
                                contentColor = Color(0xFF0E1726)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = "☕ Ko-fi",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentDestination) {
                    AppDestination.BANKS -> {
                        BanksScreenContent(
                            hasPermission = hasNotificationPermission,
                            isFilterEnabled = isFilterEnabled,
                            activeBankCount = enabledBanks.size,
                            currentPolicy = hidePolicy,
                            allBanks = detectedBanks,
                            enabledBanks = enabledBanks,
                            onRequestPermission = { showDisclosureDialog = true },
                            onToggleMain = { AppPreferences.setFilterEnabled(it) },
                            onOpenPolicyDialog = { showPolicyDialog = true },
                            onToggleBank = { pkg, enabled ->
                                AppPreferences.setBankEnabled(pkg, enabled)
                            },
                            onOpenKofi = { openExternalUrl(context, KOFI_URL) }
                        )
                    }
                    AppDestination.HISTORY -> {
                        HistoryScreenContent(
                            history = history,
                            onOpenAndroidHistory = { openAndroidNotificationHistory(context) },
                            onClearHistory = { AppPreferences.clearHistory() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawerHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E3A8A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("💳", fontSize = 22.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "PayFilter",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = APP_VERSION,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = "100% Offline & Privado",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Filtro de avisos repetidos para pagos móviles y bancos.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun BanksScreenContent(
    hasPermission: Boolean,
    isFilterEnabled: Boolean,
    activeBankCount: Int,
    currentPolicy: HidePolicy,
    allBanks: List<DetectedBankApp>,
    enabledBanks: Set<String>,
    onRequestPermission: () -> Unit,
    onToggleMain: (Boolean) -> Unit,
    onOpenPolicyDialog: () -> Unit,
    onToggleBank: (String, Boolean) -> Unit,
    onOpenKofi: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(2.dp)) }

        if (!hasPermission) {
            item {
                PermissionWarningCard(onRequestPermission = onRequestPermission)
            }
        }

        item {
            MainToggleCard(
                isEnabled = isFilterEnabled,
                hasPermission = hasPermission,
                activeBankCount = activeBankCount,
                onToggle = onToggleMain
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenPolicyDialog() },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Aviso a mostrar: ${currentPolicy.label}",
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Toca aquí o abre el menú lateral ☰ para cambiarlo",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(onClick = onOpenPolicyDialog) {
                        Text("Cambiar")
                    }
                }
            }
        }

        item {
            InstalledBanksCard(
                allBanks = allBanks,
                enabledPackages = enabledBanks,
                onToggleBank = onToggleBank
            )
        }

        item {
            KofiSupportCard(onOpenKofi = onOpenKofi)
        }

        item {
            Text(
                text = "PayFilter $APP_VERSION · 100% Privado y Offline\nEsta aplicación no tiene permiso de acceso a Internet.\nAplicación independiente no afiliada a ninguna entidad bancaria.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )
        }
    }
}

@Composable
private fun KofiSupportCard(onOpenKofi: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenKofi() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16233A))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "☕ ¿Te resulta útil PayFilter?",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Apoya el proyecto invitándome a un café en Ko-fi (se abre en tu navegador).",
                    color = Color(0xFF95A6BF),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = onOpenKofi,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF5C518),
                    contentColor = Color(0xFF0E1726)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Invitar ☕",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun HistoryScreenContent(
    history: List<HiddenNotificationEntry>,
    onOpenAndroidHistory: () -> Unit,
    onClearHistory: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(2.dp)) }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Registro local de pagos duplicados",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (history.isNotEmpty()) {
                    TextButton(onClick = onClearHistory) {
                        Text("Limpiar lista")
                    }
                }
            }
        }

        item {
            OutlinedButton(
                onClick = onOpenAndroidHistory,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("📱 Abrir Historial de notificaciones de Android")
            }
        }

        if (history.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🎉", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Aún no se ha ocultado ningún aviso duplicado.",
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Cuando pagues en una tienda con el móvil y coincida el importe exacto entre Google Wallet y tu banco, la notificación duplicada se filtrará y quedará registrada aquí.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(history) { item ->
                HiddenNotificationItemCard(item)
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PolicySelectionDialog(
    currentPolicy: HidePolicy,
    onSelect: (HidePolicy) -> Unit,
    onDismiss: () -> Unit
) {
    var selected by remember { mutableStateOf(currentPolicy) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "¿Qué notificación quieres ver al pagar?",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Elige cuál de las dos notificaciones prefieres mantener visible en tu barra de estado cuando pagues con el móvil:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                HidePolicy.entries.forEachIndexed { index, policy ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selected = policy }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        RadioButton(
                            selected = (policy == selected),
                            onClick = { selected = policy }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = policy.label,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = policy.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    if (index < HidePolicy.entries.lastIndex) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSelect(selected) }) {
                Text("Guardar preferencia")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun ProminentDisclosureDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Uso del permiso de notificaciones",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "PayFilter necesita acceso a las notificaciones únicamente para detectar " +
                    "cuándo Google Wallet y la app de tu banco muestran el mismo importe de pago " +
                    "al mismo tiempo y ocultar el aviso duplicado.\n\n" +
                    "• Ignora cualquier otra aplicación (WhatsApp, correo, etc.).\n" +
                    "• Todo se procesa localmente en tu dispositivo.\n" +
                    "• Esta app no tiene permiso de Internet: ningún dato sale de tu teléfono."
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Entendido, abrir Ajustes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun PermissionWarningCard(onRequestPermission: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF3E0)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Permiso necesario: Acceso a notificaciones",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE65100),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Para detectar cuándo llegan a la vez la notificación de Google Wallet y la de tu banco, activa \"PayFilter\" en los ajustes de Android.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF4E342E)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onRequestPermission,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
            ) {
                Text("Conceder permiso en Ajustes")
            }
        }
    }
}

@Composable
private fun MainToggleCard(
    isEnabled: Boolean,
    hasPermission: Boolean,
    activeBankCount: Int,
    onToggle: (Boolean) -> Unit
) {
    val active = isEnabled && hasPermission
    val containerColor = if (active) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant
    val dotColor = if (active) Color(0xFF2E7D32) else Color(0xFF757575)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isEnabled) "Filtro ACTIVADO" else "Filtro DESACTIVADO",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (!hasPermission) {
                                "Pendiente de permiso de notificaciones"
                            } else if (isEnabled) {
                                "Filtrando pagos duplicados en $activeBankCount banco(s) activo(s)"
                            } else {
                                "Se mostrarán todas las notificaciones sin filtrar"
                            },
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Switch(
                    checked = isEnabled,
                    onCheckedChange = onToggle
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = { onToggle(!isEnabled) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isEnabled) Color(0xFFC62828) else Color(0xFF2E7D32)
                )
            ) {
                Text(
                    text = if (isEnabled) "Desactivar aplicación" else "Activar aplicación",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun InstalledBanksCard(
    allBanks: List<DetectedBankApp>,
    enabledPackages: Set<String>,
    onToggleBank: (String, Boolean) -> Unit
) {
    val installedBanks = allBanks.filter { it.isInstalled }
    val notInstalledBanks = allBanks.filter { !it.isInstalled }
    var showNotInstalled by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Mis aplicaciones de banco",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Activa los bancos cuyas notificaciones quieras filtrar al pagar con el móvil:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (installedBanks.isEmpty()) {
                Text(
                    text = "No se ha detectado automáticamente ninguna app bancaria de la lista en este dispositivo. Puedes activar cualquiera manualmente debajo:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            } else {
                installedBanks.forEachIndexed { index, bank ->
                    BankToggleRow(
                        bank = bank,
                        isEnabled = bank.packageName in enabledPackages,
                        onToggle = { onToggleBank(bank.packageName, it) }
                    )
                    if (index < installedBanks.lastIndex) {
                        HorizontalDivider()
                    }
                }
            }

            if (notInstalledBanks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = { showNotInstalled = !showNotInstalled },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (showNotInstalled) {
                            "Ocultar otros bancos compatibles"
                        } else {
                            "Ver otros bancos compatibles (${notInstalledBanks.size})"
                        }
                    )
                }

                if (showNotInstalled) {
                    HorizontalDivider()
                    notInstalledBanks.forEachIndexed { index, bank ->
                        BankToggleRow(
                            bank = bank,
                            isEnabled = bank.packageName in enabledPackages,
                            onToggle = { onToggleBank(bank.packageName, it) }
                        )
                        if (index < notInstalledBanks.lastIndex) {
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BankToggleRow(
    bank: DetectedBankApp,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!isEnabled) }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (bank.iconBitmap != null) {
                Image(
                    bitmap = bank.iconBitmap,
                    contentDescription = bank.displayName,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = bank.displayName.take(2).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = bank.displayName,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (bank.isInstalled) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Instalada",
                            fontSize = 11.sp,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Text(
                    text = if (isEnabled) "Filtro activo para este banco" else "Sin filtrar",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle
        )
    }
}

@Composable
private fun HiddenNotificationItemCard(item: HiddenNotificationEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${item.hiddenPackageLabel} (${AmountExtractor.formatCents(item.amountCents)})",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = item.formattedTime,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (item.originalTitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.originalTitle,
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (item.originalText.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.originalText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun isNotificationServiceEnabled(context: Context): Boolean {
    val expectedComponent = ComponentName(context, PayDedupeNotificationService::class.java)
    val enabledListeners = Settings.Secure.getString(
        context.contentResolver,
        "enabled_notification_listeners"
    ) ?: return false

    return enabledListeners.split(":").any {
        ComponentName.unflattenFromString(it) == expectedComponent
    }
}

private fun openAndroidNotificationHistory(context: Context) {
    val historyIntent = Intent("android.settings.NOTIFICATION_HISTORY")
    runCatching {
        context.startActivity(historyIntent)
    }.onFailure {
        context.startActivity(Intent(Settings.ACTION_SETTINGS))
    }
}

private fun openExternalUrl(context: Context, url: String) {
    runCatching {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
