# NexusKit Proguard / R8 Optimization Rules

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# Hilt & Dagger
-keep class * extends dagger.hilt.internal.GeneratedComponent {}
-keep class * extends dagger.hilt.internal.TestSingletonComponent {}
-keep class androidx.hilt.navigation.compose.** { *; }

# Room
-keep class androidx.room.RoomDatabase { *; }
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Kotlinx Serialization
-keepattributes *Annotation*,InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# Exp4j Math Parser
-keep class net.objecthunter.exp4j.** { *; }

# ZXing QR Code
-keep class com.google.zxing.** { *; }

# Material Color Utilities
-keep class studio.lunabee.compose.** { *; }
