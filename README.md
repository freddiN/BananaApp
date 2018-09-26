# BananaApp
Sourcecode for the BananaApp: https://play.google.com/store/apps/details?id=de.freddi.bananaapp

To build this you will need to add a build.gradle, which I unfortunately cannot share since it contains my keystore password ... it looks something like this:
```
apply plugin: 'com.android.application'

android {
    signingConfigs {
        config {
            storeFile file('nope')
            storePassword 'nahhhh'
            keyPassword 'you-wish'
        }
    }
    compileSdkVersion 28
    buildToolsVersion '28.0.3'
    defaultConfig {
        applicationId 'de.freddi.bananaapp'
        minSdkVersion 21
        targetSdkVersion 28
        versionName '2.0.11'
        signingConfig signingConfigs.config
        versionCode 54
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            signingConfig signingConfigs.config
        }
    }
    productFlavors {
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}

repositories {
    jcenter()
    maven {
        url "https://maven.google.com"
    }
}

dependencies {
    implementation fileTree(include: ['*.jar'], dir: 'libs')
    implementation 'com.android.support:appcompat-v7:28.0.0'
    implementation 'com.android.support:design:28.0.0'
    implementation 'com.android.support:support-v4:28.0.0'
    implementation 'com.android.support:support-compat:28.0.0'
    implementation 'com.android.support.constraint:constraint-layout:1.1.3'
    implementation 'com.journeyapps:zxing-android-embedded:3.6.0'
    implementation 'com.google.firebase:firebase-core:16.0.3'
    implementation 'com.google.firebase:firebase-messaging:17.3.2'
    implementation 'android.arch.persistence.room:runtime:1.1.1'
    implementation 'org.apache.commons:commons-lang3:3.7'
    implementation 'commons-io:commons-io:2.6'
    implementation 'com.squareup.okhttp3:okhttp:3.11.0'
    implementation 'android.arch.lifecycle:livedata:1.1.1'
    annotationProcessor "android.arch.persistence.room:compiler:1.1.1"
}

apply plugin: 'com.google.gms.google-services'
```

I also git-ignored my google-services.json since it contains my firebase token, but everything else should be here.
