package fr.ece.athouary.geoindoorphone;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.widget.TextView;

import com.lize.oledcomm.camera_lifisdk_android.ILiFiPosition;
import com.lize.oledcomm.camera_lifisdk_android.LiFiSdkManager;
import com.lize.oledcomm.camera_lifisdk_android.V1.LiFiCamera;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    // Views
    private TextView txtOutput;

    // Managers
    private LiFiSdkManager liFiSdkManager;

    // Other attributes
    private HashMap<String, String> lampLocations;
    private static final int PERMISSIONS_REQUEST_CAMERA = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Views initialization
        txtOutput = (TextView) findViewById(R.id.txt_output);

        // Managers initialization
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_CAMERA);
        }
        liFiSdkManager = new LiFiSdkManager(this, LiFiSdkManager.CAMERA_LIB_VERSION_0_1, "token", "user", new ILiFiPosition() {
            @Override
            public void onLiFiPositionUpdate(String lamp) {
                // TODO: Refresh location
                // lamp will contain the tag (eg. 10101010) if decoding was successful.
                // If there was an error, lamp could contain the text "No lamp detected" or "Weak signal".
                txtOutput.setText(lampLocations.get(lamp));
            }
        });
        liFiSdkManager.setLocationRequestMode(LiFiSdkManager.LOCATION_REQUEST_OFFLINE_MODE);
        liFiSdkManager.init(R.id.cam_layout, LiFiCamera.BACK_CAMERA);
        liFiSdkManager.start();

        // Other initializations
        lampLocations = new HashMap<>();
        lampLocations.put("11000011","51.43393,7.97437");
        lampLocations.put("00111100","51.43455,7.97437");
        lampLocations.put("10101010","51.49577,7.97437");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (liFiSdkManager != null && liFiSdkManager.isStarted()) {
            liFiSdkManager.stop();
            liFiSdkManager.release();
            liFiSdkManager = null;
        }
    }
}
