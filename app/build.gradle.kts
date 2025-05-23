plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.studynote"             // 应用包名
    compileSdk = 35                                 // 编译 SDK 版本

    defaultConfig {
        applicationId = "com.example.studynote"    // 安装包名
        minSdk = 24                                  // 支持最低 SDK 版本
        targetSdk = 35                               // 运行目标 SDK
        versionCode = 1                              // 版本号（内部识别）
        versionName = "1.0"                        // 版本名称（对外显示）

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"  // Android 测试运行器
    }

    buildTypes {
        release {
            isMinifyEnabled = false                  // 发布包是否启用混淆
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )                                        // 混淆规则文件
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11  // Java 版本兼容性
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"                            // Kotlin 编译目标
    }

    buildFeatures {
        compose = true                                // 启用 Jetpack Compose
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"  // Compose Kotlin 编译器扩展版本
    }
}

dependencies {
    // Kotlin 扩展与生命周期
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Compose 核心库
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Jetpack Navigation for Compose
    implementation("androidx.navigation:navigation-compose:2.5.3")

    //
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Retrofit + OkHttp + Gson 用于网络请求
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // 协程支持
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

    // 图片加载（Compose）
    implementation("io.coil-kt:coil-compose:2.3.0")

    //
    implementation("androidx.compose.foundation:foundation:1.5.1")
    implementation("androidx.compose.foundation:foundation-layout:1.5.1")
    implementation("androidx.compose.material:material-icons-extended:1.5.1")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.30.1")

    // 单元测试和 Android 测试
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // 调试工具
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
