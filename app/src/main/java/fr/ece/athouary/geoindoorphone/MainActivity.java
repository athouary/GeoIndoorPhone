package fr.ece.athouary.geoindoorphone;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.net.Uri;
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
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "fr.ece.athouary.geoindoorphone.MESSAGE";

    // Views declaration
    private EditText phoneNum;
    private Button findButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.v("debugMap", "MainActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Views initialization
        phoneNum = (EditText) findViewById(R.id.phone_num);
        findButton = (Button) findViewById(R.id.button_find);

        // Button management
        findButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String number = phoneNum.getText().toString();
                goToLoading(number);
            }
        });

        // Permission management
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 1);
        }
    }

    protected void makeToast(String txt, int length) {
        Toast.makeText(this, txt, length).show();
    }

    protected void goToLoading(String numberToSend) {
        Log.v("debug", "goToLoading start");
        if (sendLocationRequestMessage(numberToSend)) {
            Log.v("debug", "ok");
            Intent intent = new Intent(this, LoadingActivity.class);
            intent.putExtra(EXTRA_MESSAGE, numberToSend);
            startActivity(intent);
        }
        else {
            makeToast("Couldn't load", Toast.LENGTH_LONG);
        }
    }

    protected boolean sendLocationRequestMessage(String number) {
        if(!number.equals("")){
            sendSMS(number, MessageReceiver.LOCATION_REQUEST_MESSAGE);
            return true;
        }
        else {
            return true;
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
}
