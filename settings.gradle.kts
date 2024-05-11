pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            // Do not change the username below. It should always be "mapbox" (not your username).
            credentials.username = "mapbox"
            // Use the secret token stored in gradle.properties as the password
            credentials.password = "sk.eyJ1IjoiYXJpdHJhOTc4OCIsImEiOiJjbHcwYmlvOXUzNjI1MmtxejloNjF1cXR2In0.KD_BmL5xAn7_SCqsR_i8Tg"
            authentication.create<BasicAuthentication>("basic")
        }
    }
}

rootProject.name = "RakShak-AccidentSafetyApp"
include(":app")
 