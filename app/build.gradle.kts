plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.yoiberdev.uberclone"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.yoiberdev.uberclone"
        minSdk = 34
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resValue("string", "WEB_CLIENT_ID", project.findProperty("WEB_CLIENT_ID")?.toString() ?: "DEFAULT_WEB_CLIENT_ID")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Dependencias generales de AndroidX y Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)

    // Firebase BOM (único)
    implementation(platform(libs.firebase.bom))

    // Dependencias de Firebase
    implementation(libs.firebase.auth)
    implementation(libs.google.firebase.auth) // Si realmente necesitas ambos, verifica que no sean duplicados
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.analytics)

    // Firebase App Check (mantén solo un conjunto, en este caso usamos "appcheck")
    implementation(libs.firebase.appcheck.ktx)
    implementation(libs.firebase.appcheck.playintegrity)

    // Dependencias de Maps Compose
    val mapsComposeVersion = "6.4.4"
    implementation("com.google.maps.android:maps-compose:$mapsComposeVersion")
    implementation("com.google.maps.android:maps-compose-utils:$mapsComposeVersion")
    implementation("com.google.maps.android:maps-compose-widgets:$mapsComposeVersion")

    // Otras dependencias
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.play.services.auth)
    implementation(libs.koin.androidx.compose)

    // Dependencias para pruebas
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

// Configuración del plugin de Secrets para manejar claves de forma segura.
secrets {
    propertiesFileName = "secrets.properties"
    defaultPropertiesFileName = "local.defaults.properties"
}
