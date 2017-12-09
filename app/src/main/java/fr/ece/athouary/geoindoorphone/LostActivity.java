package fr.ece.athouary.geoindoorphone;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.TextView;

import com.lize.oledcomm.camera_lifisdk_android.ILiFiPosition;
import com.lize.oledcomm.camera_lifisdk_android.LiFiSdkManager;
import com.lize.oledcomm.camera_lifisdk_android.V1.LiFiCamera;

import java.util.HashMap;

public class LostActivity extends AppCompatActivity {
    private TextView popUp;
    private static String numeroChercheur = "";
    private static String selectedMode;
    private static String textToPrint;
    private static boolean vibrator;
    private static boolean led;
    private static boolean flash;

    // Managers declaration
    private LiFiSdkManager liFiSdkManager;

    // Other attributes declaration
    private HashMap<String, String> lampLocations;

    //ON GOING : stop vibrate + back when lostActivity is lost

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost);

        popUp = (TextView) findViewById(R.id.pop_up);


        //récupération du numéro du tel chercheur
        Intent intentNum = getIntent();
        numeroChercheur = intentNum.getStringExtra(MessageReceiver.EXTRA_REQUEST_NUMERO);
        textToPrint = intentNum.getStringExtra(MainActivity.EXTRA_TEXT);
        selectedMode = intentNum.getStringExtra(MainActivity.EXTRA_MODE);
        vibrator = intentNum.getBooleanExtra(MainActivity.EXTRA_VIBRATOR, true);
        flash = intentNum.getBooleanExtra(MainActivity.EXTRA_FLASH, false);

        //TODO
        /* Lifi à réimplémenter
        // Lifi Management
        liFiSdkManager = new LiFiSdkManager(this, LiFiSdkManager.CAMERA_LIB_VERSION_0_1, "token", "user", new ILiFiPosition() {
            @Override
            public void onLiFiPositionUpdate(String lamp) {
                // lamp will contain the tag (eg. 10101010) if decoding was successful.
                // If there was an error, lamp could contain the text "No lamp detected" or "Weak signal".
                String location = lampLocations.get(lamp);
                // TODO: send location
            }
        });
        liFiSdkManager.setLocationRequestMode(LiFiSdkManager.LOCATION_REQUEST_OFFLINE_MODE);
        liFiSdkManager.init(R.id.cam_layout, LiFiCamera.BACK_CAMERA);
        liFiSdkManager.start();

        // Other initializations
        lampLocations = new HashMap<>();
        lampLocations.put("11000011", "51.43393,7.97437");
        lampLocations.put("00111100", "51.43455,7.97437");
        lampLocations.put("10101010", "51.49577,7.97437");*/

        showPresence();

        //TODO
        //à virer, utiliser les lampes
        String position="0,0";
        if(selectedMode == MainActivity.STANDARD_REQUESTED)
            position = getStandardPosition();
        else if(selectedMode == MainActivity.LIFI_REQUESTED)
            position = getLifiPosition();

        sendSMS(numeroChercheur, MessageReceiver.LOCATION_MESSAGE + "\n" + position);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Free Managers
        if (liFiSdkManager != null && liFiSdkManager.isStarted()) {
            liFiSdkManager.stop();
            liFiSdkManager.release();
            liFiSdkManager = null;
        }
    }

    public void showPresence() {
        Log.v("DebugToggle", "textToPrint : " + textToPrint);
        popUp.setText(textToPrint);
        if(vibrator){
            vibrate();
        }
        if(led) {
            Log.v("DebugToggle", "led");
        }
        if(flash) {
            Log.v("DebugToggle", "flash");
        }
    }

    private void sendSMS(String number, String message) {
        SmsManager sms = SmsManager.getDefault();
        PendingIntent sentPI;
        String SENT = "SMS_SENT";
        sentPI = PendingIntent.getBroadcast(this, 0,new Intent(SENT), 0);
        try{
            sms.sendTextMessage(number, null, message, sentPI, null);
        }
        catch(Exception e)
        {
            Log.v("debug", e.toString());
        }
    }

    public void vibrate(){
        // Vibrate for 150 milliseconds

        if (Build.VERSION.SDK_INT >= 26) {
            long[] thrice = { 0, 100, 400, 100, 400, 100 };
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createWaveform(thrice, 1));
        } else {
            long[] pattern = { 0, 100, 400, 100, 400, 100, 400};
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(pattern,0);
        }
    }

    private String getLifiPosition() {
        return "48.8473075,2.2875811";
    }

    private String getStandardPosition() {
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        return ""+latitude+","+longitude;
    }


}
