plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("maven-publish")
}

android {
    namespace = "dora.cache.room"
    compileSdk = 32

    defaultConfig {
        minSdk = 21
        targetSdk = 32
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")

    api("com.github.dora4:dcache-android:1.7.6")
    val kotlin_coroutine_version = "1.6.1"
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlin_coroutine_version")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlin_coroutine_version")
    val room_version = "2.2.0-rc01"
    api("androidx.room:room-runtime:$room_version")
    api("androidx.room:room-ktx:$room_version")
//    annotationProcessor("androidx.room:room-compiler:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
}

afterEvaluate {
    publishing {
        publications {
            register("release", MavenPublication::class) {
                from(components["release"])
                groupId = "com.github.dora4"
                artifactId = "dcache-room-support"
                version = "1.1"
            }
        }
    }
}