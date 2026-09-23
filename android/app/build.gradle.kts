plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }
android {
 namespace="com.kylow.mobile"; compileSdk=35
 defaultConfig { applicationId="com.kylow.mobile"; minSdk=26; targetSdk=35; versionCode=12; versionName="0.9.1"; ndk { abiFilters += listOf("arm64-v8a", "x86_64") }; externalNativeBuild { cmake { cppFlags += "-std=c++17" } } }
 signingConfigs {
  create("release") {
   storeFile=file(System.getenv("KYLOW_KEYSTORE_PATH") ?: "kylow-release.jks")
   storePassword=System.getenv("KYLOW_KEYSTORE_PASSWORD")
   keyAlias=System.getenv("KYLOW_KEY_ALIAS")
   keyPassword=System.getenv("KYLOW_KEY_PASSWORD")
  }
 }
 buildTypes { getByName("release") { isMinifyEnabled=false; signingConfig=signingConfigs.getByName("release") } }
 externalNativeBuild { cmake { path=file("src/main/cpp/CMakeLists.txt"); version="3.22.1" } }
 compileOptions { sourceCompatibility=JavaVersion.VERSION_17; targetCompatibility=JavaVersion.VERSION_17 }
}
kotlin { jvmToolchain(17) }
dependencies { implementation("androidx.core:core-ktx:1.15.0"); implementation("androidx.appcompat:appcompat:1.7.0"); testImplementation("junit:junit:4.13.2") }