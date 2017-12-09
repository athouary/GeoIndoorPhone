package fr.ece.athouary.geoindoorphone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/* Classe permettant de gérer la reception d'un SMS*/
public class MessageReceiver extends BroadcastReceiver {

    public static final String LOCATION_REQUEST_MESSAGE = "GIB_LOCATEPHONE";
    public static final String LOCATION_MESSAGE = "GIB_LOCATION";
    public static final String EXTRA_REQUEST_NUMERO = "tel_info";
    public static final String EXTRA_MAP_MESSAGE = "location_info";

    private static final String TAG = MessageReceiver.class.getSimpleName();

    public static final String SMS_BUNDLE = "pdus";

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";

            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[0]);

            String smsBody = smsMessage.getMessageBody().toString();
            String numero = smsMessage.getOriginatingAddress();

            if (smsBody.contains(LOCATION_REQUEST_MESSAGE)) {
                String[] splitedSms = smsBody.split(";");
                String mode = splitedSms[1];
                String textToPrint = splitedSms[2];
                boolean vibrator = Boolean.parseBoolean(splitedSms[3]);
                boolean flash = Boolean.parseBoolean(splitedSms[4]);

                Intent lostIntent = new Intent(context, LostActivity.class);
                lostIntent.putExtra(EXTRA_REQUEST_NUMERO, numero);
                lostIntent.putExtra(MainActivity.EXTRA_TEXT, textToPrint);
                lostIntent.putExtra(MainActivity.EXTRA_MODE, mode);
                lostIntent.putExtra(MainActivity.EXTRA_VIBRATOR, vibrator);
                lostIntent.putExtra(MainActivity.EXTRA_FLASH, flash);
                context.startActivity(lostIntent);
            }
            else if (smsBody.contains(LOCATION_MESSAGE)) {
                Log.v("DebugToggle", "test1");
                String location = smsBody;
                location = location.replace(LOCATION_MESSAGE+"\n", "");
                Log.v("DebugToggle", "test2");
                //location.substring(LOCATION_MESSAGE.length()+2);
                Intent mapIntent = new Intent(context, MapActivity.class);
                Log.v("DebugToggle", "test3|" +location);
                mapIntent.putExtra(EXTRA_MAP_MESSAGE, location);
                Log.v("DebugToggle", "test4");
                context.startActivity(mapIntent);
            }
        }
    }

}