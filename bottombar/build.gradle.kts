plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.maven.publish)
}

group = providers.gradleProperty("GROUP").get()
version = providers.gradleProperty("VERSION_NAME").get()

android {
    namespace = "com.nameisjayant.bottombar"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("src/main/keepRules/consumer-rules.keep")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

kotlin {
    // Every public declaration must state its visibility, so nothing leaks into the API by accident.
    explicitApi()
}

dependencies {
    // Exposed as api: bottom bar composables take Modifier, Color, ImageVector/Painter and TextStyle.
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.graphics)
    api(libs.androidx.compose.ui.text)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.ui.tooling.preview)

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

mavenPublishing {
    // Uploads to the Central Portal and releases once validation passes. Sonatype credentials and
    // the signing key come from ~/.gradle/gradle.properties, never from this repo.
    publishToMavenCentral(automaticRelease = true)
    // Central rejects unsigned uploads, so skipping this without a key only affects publishToMavenLocal.
    if (providers.gradleProperty("signingInMemoryKey").isPresent) {
        signAllPublications()
    }

    coordinates(group.toString(), "compose-bottombar", version.toString())

    pom {
        name.set("Compose Bottombar")
        description.set("A collection of beautiful, animated, customizable bottom bars for Jetpack Compose.")
        inceptionYear.set("2026")
        url.set("https://github.com/nameisjayant/compose-bottombar")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id.set("nameisjayant")
                name.set("Jayant Kumar")
                url.set("https://github.com/nameisjayant")
            }
        }
        scm {
            url.set("https://github.com/nameisjayant/compose-bottombar")
            connection.set("scm:git:git://github.com/nameisjayant/compose-bottombar.git")
            developerConnection.set("scm:git:ssh://git@github.com/nameisjayant/compose-bottombar.git")
        }
    }
}
