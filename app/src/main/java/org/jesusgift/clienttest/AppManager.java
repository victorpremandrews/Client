package org.jesusgift.clienttest;/* *
 * Developed By : Victor Vincent
 * Created On : 08/06/16
 * victorvprem@gmail.com
 * Kliotech Pvt Ltd.
 * */

import android.app.Application;
import android.util.Log;

import com.facebook.stetho.DumperPluginsProvider;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.dumpapp.DumperPlugin;

public class AppManager extends android.app.Application {
    private static final String TAG = "Application";

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
