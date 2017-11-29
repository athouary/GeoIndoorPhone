package fr.ece.athouary.geoindoorphone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import android.util.Log;

/* Classe permettant de gérer la reception d'un SMS*/
public class MessageReceiver extends BroadcastReceiver {

    public static final String LOCATION_REQUEST_MESSAGE = "GIB_LOCATEPHONE";
    public static final String LOCATION_MESSAGE = "GIB_LOCATION";
    public static final String EXTRA_MESSAGE = "fr.ece.athouary.geoindoorphone.MESSAGE";

    private static final String TAG = MessageReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.v("debugMap", "MessageReceived");

        Cursor c = context.getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
        c.moveToFirst();
        //On récupère le corps du message
        String smsBody = c.getString(12);
        //On récupère le numéro de l'expéditeur
        String num = c.getString(2);

        Log.v("debugMap", smsBody);

        if (smsBody.equals(LOCATION_REQUEST_MESSAGE)) {
            Intent lostIntent = new Intent(context, LostActivity.class);
            lostIntent.putExtra(EXTRA_MESSAGE, num);
            context.startActivity(lostIntent);
        } else if (smsBody.contains(LOCATION_MESSAGE)) {
            String location = smsBody;
            location.replace(LOCATION_MESSAGE +"\n", "");
            Intent mapIntent = new Intent(context, MapActivity.class);
            mapIntent.putExtra(EXTRA_MESSAGE, location);
            Log.v("debugMap", location);
            context.startActivity(mapIntent);
        }
    }

}