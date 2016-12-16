package cl.ucn.disc.dam.proyecto.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import cl.ucn.disc.dam.proyecto.BuildConfig;

/**
 * Created by durrutia on 20-Oct-16.
 */

public class PackageUtils {

    /**
     * Return true if Debug build. false otherwise.
     */
    public static boolean isDebugBuild() {
        return BuildConfig.DEBUG;
    }

    public static PackageInfo getPackageInfo(final Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            return manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    /**
     * Return version code, or 0 if it can't be read
     */
    public static int getVersionCode(final Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        if (packageInfo != null) {
            return packageInfo.versionCode;
        }
        return 0;
    }

    /**
     * Return version name, or the string "0" if it can't be read
     */
    public static String getVersionName(final Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        if (packageInfo != null) {
            return packageInfo.versionName;
        }
        return "0";
    }
}
