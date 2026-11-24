import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

// Firma de la versión publicada: datos en keystore.properties (nunca se sube a git).
// Se busca en la raíz del proyecto y, si no está, en Documentos/claves (compartido con otras apps).
// Sin ese archivo, la versión release sale sin firmar.
val signingPropsFile: File? = listOf(
    rootProject.file("keystore.properties"),
    File(System.getProperty("user.home"), "Documents/claves/keystore.properties"),
).firstOrNull { it.isFile }
val signingProps: Properties? = signingPropsFile?.let { f ->
    Properties().apply { f.reader(Charsets.UTF_8).use { load(it) } }
}
// Si la ruta de storeFile no existe (p. ej. escrita con otro nombre de usuario), se busca junto a keystore.properties.
val signingStoreFile: File? = signingProps?.getProperty("storeFile")?.trim()?.let { path ->
    File(path).takeIf { it.isFile } ?: File(signingPropsFile!!.parentFile, File(path).name)
}

android {
    namespace = "com.neoruben.payfilter"
    compileSdk = 35

    signingConfigs {
        if (signingProps != null && signingStoreFile != null) create("release") {
            storeFile = signingStoreFile
            storePassword = signingProps.getProperty("storePassword")
            keyAlias = signingProps.getProperty("keyAlias")
            keyPassword = signingProps.getProperty("keyPassword")
        }
    }

    defaultConfig {
        applicationId = "com.neoruben.payfilter"
        minSdk = 29
        targetSdk = 35
        versionCode = 8
        versionName = "1.2.5"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.findByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.core)

    testImplementation(libs.junit)
    debugImplementation(libs.androidx.ui.tooling)
}
