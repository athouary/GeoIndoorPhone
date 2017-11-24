package fr.ece.athouary.geoindoorphone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lize.oledcomm.camera_lifisdk_android.ILiFiPosition;
import com.lize.oledcomm.camera_lifisdk_android.LiFiSdkManager;
import com.lize.oledcomm.camera_lifisdk_android.V1.LiFiCamera;

import java.util.HashMap;

import android.os.Vibrator;

public class MainActivity extends AppCompatActivity {
    // Views declaration
    private TextView popUp;
    private EditText phoneNum;
    private Button findButton;

    // Managers declaration
    private LiFiSdkManager liFiSdkManager;

    // Other attributes declaration
    private HashMap<String, String> lampLocations;
    private static final int PERMISSIONS_REQUEST_CAMERA = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Help", "OnCreateBegin");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Views initialization
        popUp = (TextView) findViewById(R.id.pop_up);
        phoneNum = (EditText) findViewById(R.id.phone_num);
        findButton = (Button) findViewById(R.id.button_find);

        // Button management
        findButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String number = phoneNum.getText().toString();

                if (sendLocationRequestMessage(number)) {
                    Intent goToNextActivity = new Intent(getApplicationContext(), LoadingActivity.class);
                    startActivity(goToNextActivity);
                    finish();
                }

            }
        });

        // MessageReceiver instantiation


        // Permission management
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Help", "Requesting permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CAMERA);
        }

        // Lifi Management
        liFiSdkManager = new LiFiSdkManager(this, LiFiSdkManager.CAMERA_LIB_VERSION_0_1, "token", "user", new ILiFiPosition() {
            @Override
            public void onLiFiPositionUpdate(String lamp) {
                // lamp will contain the tag (eg. 10101010) if decoding was successful.
                // If there was an error, lamp could contain the text "No lamp detected" or "Weak signal".
                Log.d("Help", "PositionUpdateBegin");
                String location = lampLocations.get(lamp);
                Log.d("Help", "PositionUpdateFinish");
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
        lampLocations.put("10101010", "51.49577,7.97437");

        Log.d("Help", "OnCreateFinish");
    }

    @Override
    protected void onDestroy() {
        Log.d("Help", "onDestroy");
        super.onDestroy();

        // Free Managers
        if (liFiSdkManager != null && liFiSdkManager.isStarted()) {
            liFiSdkManager.stop();
            liFiSdkManager.release();
            liFiSdkManager = null;
        }
    }

    protected boolean sendLocationRequestMessage(String number) {
        if(!number.equals("")){
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(number, null, getString(R.string.location_message), null, null);
            return true;
        }
        else
            return false;
    }

    public void showPresence() {
        popUp.setText(getString(R.string.here_message));
        Vibrator vibrator = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
        vibrator.vibrate(500);
    }
}
