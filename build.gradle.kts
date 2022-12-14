plugins {
    kotlin("multiplatform") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
    id("com.android.library")
    id("maven-publish")
    signing
}

group = "com.metaplex"
version = "development"

repositories {
    google()
    jcenter()
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }
    android {
        publishLibraryVariants("release")
    }
//    js {
//        moduleName = "kborsh"
//        browser()
//        nodejs()
//    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            kotlin.srcDir("src/commonJvmAndroid/kotlin")
        }
        val androidMain by getting {
            kotlin.srcDir("src/commonJvmAndroid/kotlin")
            dependencies {
                implementation("androidx.core:core-ktx:1.9.0")
            }
        }
        val androidTest by getting {
            kotlin.srcDir("src/jvmTest/kotlin")
            kotlin.srcDir("src/androidTest/kotlin")
        }
        val jvmTest by getting
//        val jsMain by getting {
//            dependencies {
//                implementation("org.jetbrains.kotlinx:kotlinx-nodejs:0.0.7")
//            }
//        }
//        val jsTest by getting
    }
}

android {
    compileSdk = 31
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 31
    }
    namespace = "$group.${rootProject.name}"
}

System.getenv("GITHUB_REPOSITORY")?.let {

    val publishedGroupId = "com.metaplex"
    val branchName = System.getenv("GITHUB_HEAD_REF")
        ?: System.getenv("GITHUB_REF_NAME")?.split('/')?.last()
    val libraryVersion =
        if (System.getenv("GITHUB_REF_TYPE") == "tag")
            System.getenv("GITHUB_REF_NAME")
        else
            "$branchName-SNAPSHOT"

    println("PUBLISHING LIBRARY: $publishedGroupId:$libraryVersion")

    project.group = publishedGroupId
    project.version = libraryVersion

    publishing {
        publications.withType(MavenPublication::class) {
            groupId = publishedGroupId
            version = libraryVersion
        }

        repositories {
            maven {
                name = "github"
                url = uri("https://maven.pkg.github.com/$it")
                credentials(PasswordCredentials::class)
            }
        }
    }
}