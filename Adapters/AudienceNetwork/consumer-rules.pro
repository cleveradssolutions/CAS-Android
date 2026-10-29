-keeppackagenames com.facebook.ads
-keep public class com.cleveradssolutions.adapters.AudienceNetworkSettings { public *; }
-keep class androidx.recyclerview.widget.RecyclerView { *; }
-keep class androidx.appcompat.app.AppCompatActivity { *; }
-keeppackagenames com.facebook.*
-keep class com.facebook.ads.BuildConfig {
    public static final java.lang.String VERSION_NAME;
}