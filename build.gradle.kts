buildscript {
    repositories {
        google()
        jcenter()
    }

    val koin_version = "2.2.2"
    dependencies {
        classpath("com.android.tools.build:gradle:4.0.0")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.2.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.72")
        classpath("org.koin:koin-gradle-plugin:$koin_version")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

