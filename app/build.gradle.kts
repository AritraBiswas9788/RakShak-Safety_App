plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.rakshak_accidentsafetyapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.rakshak_accidentsafetyapp"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        mlModelBinding = true
    }
}

dependencies {


    // Barcode model
    implementation ("com.google.mlkit:barcode-scanning:17.2.0")
    // Or comment the dependency above and uncomment the dependency below to
    // use unbundled model that depends on Google Play Services
    // implementation 'com.google.android.gms:play-services-mlkit-barcode-scanning:18.3.0'

    // Object detection feature with bundled default classifier
    implementation ("com.google.mlkit:object-detection:17.0.1")

    // Object detection feature with custom classifier support
    implementation ("com.google.mlkit:object-detection-custom:17.0.1")

    // Face features
    implementation ("com.google.mlkit:face-detection:16.1.6")
    // Or comment the dependency above and uncomment the dependency below to
    // use unbundled model that depends on Google Play Services
    // implementation 'com.google.android.gms:play-services-mlkit-face-detection:17.1.0'

    // Text features
    implementation ("com.google.mlkit:text-recognition:16.0.0")
    // Or comment the dependency above and uncomment the dependency below to
    // use unbundled model that depends on Google Play Services
    // implementation 'com.google.android.gms:play-services-mlkit-text-recognition:19.0.0'
    implementation ("com.google.mlkit:text-recognition-chinese:16.0.0")
    // Or comment the dependency above and uncomment the dependency below to
    // use unbundled model that depends on Google Play Services
    // implementation 'com.google.android.gms:play-services-mlkit-text-recognition-chinese:16.0.0'
    implementation ("com.google.mlkit:text-recognition-devanagari:16.0.0")
    // Or comment the dependency above and uncomment the dependency below to
    // use unbundled model that depends on Google Play Services
    // implementation 'com.google.android.gms:play-services-mlkit-text-recognition-devanagari:16.0.0'
    implementation ("com.google.mlkit:text-recognition-japanese:16.0.0")
    // Or comment the dependency above and uncomment the dependency below to
    // use unbundled model that depends on Google Play Services
    // implementation 'com.google.android.gms:play-services-mlkit-text-recognition-japanese:16.0.0'
    implementation ("com.google.mlkit:text-recognition-korean:16.0.0")
    // Or comment the dependency above and uncomment the dependency below to
    // use unbundled model that depends on Google Play Services
    // implementation 'com.google.android.gms:play-services-mlkit-text-recognition-korean:16.0.0'

    // Image labeling
    implementation ("com.google.mlkit:image-labeling:17.0.8")
    // Or comment the dependency above and uncomment the dependency below to
    // use unbundled model that depends on Google Play Services
    // implementation 'com.google.android.gms:play-services-mlkit-image-labeling:16.0.8'

    // Image labeling custom
    implementation ("com.google.mlkit:image-labeling-custom:17.0.2")
    // Or comment the dependency above and uncomment the dependency below to
    // use unbundled model that depends on Google Play Services
    // implementation 'com.google.android.gms:play-services-mlkit-image-labeling-custom:16.0.0-beta5'

    // Pose detection with default models
    implementation ("com.google.mlkit:pose-detection:18.0.0-beta4")
    // Pose detection with accurate models
    implementation ("com.google.mlkit:pose-detection-accurate:18.0.0-beta4")

    // Selfie segmentation
    implementation ("com.google.mlkit:segmentation-selfie:16.0.0-beta5")

    implementation ("com.google.mlkit:camera:16.0.0-beta3")

    // Face Mesh Detection
    implementation ("com.google.mlkit:face-mesh-detection:16.0.0-beta2")

    // Subject Segmentation
    implementation ("com.google.android.gms:play-services-mlkit-subject-segmentation:16.0.0-beta1")

    // ViewModel and LiveData
    implementation ("androidx.lifecycle:lifecycle-livedata:2.3.1")
    implementation ("androidx.lifecycle:lifecycle-viewmodel:2.3.1")

    implementation ("androidx.appcompat:appcompat:1.2.0")
    implementation ("androidx.annotation:annotation:1.2.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.0.4")

    // CameraX
    implementation ("androidx.camera:camera-camera2:1.0.0-SNAPSHOT")
    implementation ("androidx.camera:camera-lifecycle:1.0.0-SNAPSHOT")
    implementation ("androidx.camera:camera-view:1.0.0-SNAPSHOT")

    // On Device Machine Learnings
    implementation ("com.google.android.odml:image:1.0.0-beta1")

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("org.tensorflow:tensorflow-lite-support:0.1.0")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.1.0")
    implementation("org.tensorflow:tensorflow-lite-gpu:2.3.0")
    implementation("com.google.firebase:firebase-messaging-ktx:24.0.0")
    implementation("com.google.firebase:firebase-storage-ktx:21.0.0")
    testImplementation("junit:junit:4.13.2")

    implementation ("com.ncorti:slidetoact:0.11.0")
    //lottie files
    implementation("com.airbnb.android:lottie:6.1.0")

    // navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.4")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.4")
    implementation ("com.jpardogo.googleprogressbar:library:1.2.0")

    implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-inappmessaging-display")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-storage")

    implementation("org.greenrobot:eventbus:3.3.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("org.tensorflow:tensorflow-lite-support:0.1.0")
    implementation ("org.tensorflow:tensorflow-lite-metadata:0.1.0")


    implementation("com.mapbox.maps:android:10.16.0")
    implementation ("com.mapbox.search:discover:1.2.0")
    implementation("com.google.ai.client.generativeai:generativeai:0.4.0")

    implementation ("com.firebase:geofire-android:3.2.0")

    // GeoFire utililty functions for Cloud Firestore users who
    // want to implement their own geo solution, see:
    // https://firebase.google.com/docs/firestore/solutions/geoqueries
    implementation ("com.firebase:geofire-android-common:3.2.0")
    implementation ("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
}