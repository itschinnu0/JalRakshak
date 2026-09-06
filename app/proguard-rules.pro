# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Chinnu0\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include line and the file paths to match your setup.

# Keep Kotlinx Serialization metadata if needed
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Keep serializable classes
-keepclassmembers class * implements kotlinx.serialization.KSerializer {
    *** INSTANCE;
}
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}
