package org.jesusgift.clienttest;/* *
 * Developed By : Victor Vincent
 * Created On : 10/06/16
 * victorvprem@gmail.com
 * Kliotech Pvt Ltd.
 * */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.jesusgift.clienttest.Helpers.AppConfig;

public class AppReceiver extends BroadcastReceiver {
    private static final String PHONE_REBOOT_COMPLETE = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1= new Intent(context, ClientService.class);
        context.startService(intent1);

        switch (intent.getAction()) {
            case AppConfig.SERVICE_STOP_BROADCAST:
                break;

            case PHONE_REBOOT_COMPLETE:
                break;
        }
    }
}
