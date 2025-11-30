plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.chpark.kronos"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.chpark.kronos"
        minSdk = 35
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        signingConfigs {
            create("release") {
                storeFile = file(project.properties["KRONOS_KEYSTORE"] as String)
                storePassword = project.properties["KRONOS_STORE_PASSWORD"] as String
                keyAlias = project.properties["KRONOS_KEY_ALIAS"] as String
                keyPassword = project.properties["KRONOS_KEY_PASSWORD"] as String
            }
        }
        debug {
            applicationIdSuffix = ".dev"   //
            versionNameSuffix = "-dev"
        }
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")  // 서명 적용
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlin {
        jvmToolchain(21)
    }
    kotlinOptions {
        compileOptions {
        }

    }
    buildFeatures {
        compose = true
    }
}


tasks.register("generateKeystore") {

    val keystorePath = project.properties["KRONOS_KEYSTORE"] as String
    val keystoreFile = File(project.projectDir, keystorePath)

    val storePassword = project.properties["KRONOS_STORE_PASSWORD"] as String
    val keyPassword = project.properties["KRONOS_KEY_PASSWORD"] as String
    val keyAlias = project.properties["KRONOS_KEY_ALIAS"] as String

    doLast {
        if (keystoreFile.exists()) {
            println("Keystore already exists: ${keystoreFile.absolutePath}")
            return@doLast
        }

        println("Generating keystore at: ${keystoreFile.absolutePath}")

        val process = ProcessBuilder(
            "keytool",
            "-genkeypair",
            "-v",
            "-keystore", keystoreFile.absolutePath,
            "-storepass", storePassword,
            "-keypass", keyPassword,
            "-alias", keyAlias,
            "-keyalg", "RSA",
            "-keysize", "2048",
            "-validity", "10000",
            "-dname", "CN=Kronos, OU=Dev, O=Company, L=Seoul, S=Seoul, C=KR"
        )
            .redirectErrorStream(true)
            .start()

        val output = process.inputStream.bufferedReader().readText()
        process.waitFor()

        println(output)
        println("Keystore generated successfully.")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.androidx.compose.runtime.livedata)
    val room_version = "2.8.3"

    implementation("androidx.room:room-runtime:$room_version")

    // If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
    // See Add the KSP plugin to your project
    ksp("androidx.room:room-compiler:$room_version")

    // If this project only uses Java source, use the Java annotationProcessor
    // No additional plugins are necessary
    annotationProcessor("androidx.room:room-compiler:$room_version")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")

    // optional - RxJava2 support for Room
    implementation("androidx.room:room-rxjava2:$room_version")

    // optional - RxJava3 support for Room
    implementation("androidx.room:room-rxjava3:$room_version")

    // optional - Guava support for Room, including Optional and ListenableFuture
    implementation("androidx.room:room-guava:$room_version")

    // optional - Test helpers
    testImplementation("androidx.room:room-testing:$room_version")

    // optional - Paging 3 Integration
    implementation("androidx.room:room-paging:$room_version")
    implementation("com.cronutils:cron-utils:9.2.1")
    implementation(libs.coil.compose)
    implementation(libs.gson)
    implementation("com.google.dagger:hilt-android:2.57.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")
    ksp("com.google.dagger:hilt-android-compiler:2.57.2")
    implementation(platform("io.github.rosemoe:editor-bom:0.24.0"))
    implementation("io.github.rosemoe:editor")
    implementation("io.github.rosemoe:language-textmate")

}