package fr.ece.athouary.geoindoorphone;

import android.app.PendingIntent;
import android.content.Intent;
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
    String numeroChercheur = "";

    // Managers declaration
    private LiFiSdkManager liFiSdkManager;

    // Other attributes declaration
    private HashMap<String, String> lampLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.v("debugMap", "LostActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost);

        //récupération du numéro du tel chercheur
        Intent intentNum = getIntent();
        numeroChercheur = intentNum.getStringExtra(MessageReceiver.EXTRA_MESSAGE);

        popUp = (TextView) findViewById(R.id.pop_up);

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
        String position = getPosition();
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
        popUp.setText(getString(R.string.here_message));
        Vibrator vibrator = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
        vibrator.vibrate(500);
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

    private String getPosition() {
        return "10,20";
    }
}
