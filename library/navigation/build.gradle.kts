plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.huyhieu.library.navigation"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    api(project(":feature:photo-list"))
    api(project(":feature:photo-search"))
    //api(project(":library:ui"))

//    libs.apply {
//        api(platform(androidx.compose.bom))
//        api(libs.bundles.navigation)
//        api(bundles.design)
//        api(bundles.coil)
//
//        testImplementation(junit)
//        androidTestImplementation(androidx.junit)
//        androidTestImplementation(androidx.espresso.core)
//    }

}