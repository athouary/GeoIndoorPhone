package fr.ece.athouary.geoindoorphone;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import android.util.Log;

/* Classe permettant de gérer la reception d'un SMS*/
public class MessageReceiver extends BroadcastReceiver {
    private static final String LOCATION_REQUEST_MESSAGE  = "GIB_LOCATEPHONE";
    private static final String LOCATION_MESSAGE  = "GIB_LOCATION";
    Activity mainActivity;

    @Override
    public MessageReceiver(MainActivity activity) {
        super();
        mainActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
        cursor.moveToFirst();
        //On récupère le corps du message
        String sms = cursor.getString(12);
        //On récupère le numéro de l'expéditeur
        String num = cursor.getString(2);
        // Toast.makeText(context, "SMS RECEIVED:", Toast.LENGTH_LONG).show();
        // Toast.makeText(context, smsBody, Toast.LENGTH_LONG).show();
        //Toast.makeText(context, num, Toast.LENGTH_LONG).show();

        //Si le message correspond à un message du téléphone cherchant alors on ouvre l'activité du téléphone perdu
        if (sms.equals(LOCATION_REQUEST_MESSAGE)) {
            mainActivity.showPresence();
            //Si le message contient les données de géolocalisation, on ouvre une google maps avec les coordonnées envoyées
        } else if (sms.startsWith(LOCATION_MESSAGE)) {

        }
    }

}

