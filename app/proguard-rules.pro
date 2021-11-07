-dontwarn com.google.errorprone.annotations.Immutable
-optimizationpasses 5
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}
-assumenosideeffects class com.android.volley.VolleyLog {
    public static void d(...);
    public static void e(...);
    public static void v(...);
    public static void wtf(...);
}