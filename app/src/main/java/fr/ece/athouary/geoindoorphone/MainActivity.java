package fr.ece.athouary.geoindoorphone;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.lize.oledcomm.camera_lifisdk_android.ILiFiPosition;
import com.lize.oledcomm.camera_lifisdk_android.LiFiSdkManager;
import com.lize.oledcomm.camera_lifisdk_android.V1.LiFiCamera;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.os.Vibrator;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "fr.ece.athouary.geoindoorphone.MESSAGE";
    public static final String EXTRA_MODE = "fr.ece.athouary.geoindoorphone.MODE";
    public static final String EXTRA_TEXT = "fr.ece.athouary.geoindoorphone.TEXT";
    public static final String EXTRA_VIBRATOR = "fr.ece.athouary.geoindoorphone.VIBRATOR";
    public static final String EXTRA_FLASH = "fr.ece.athouary.geoindoorphone.FLASH";
    public static final String LIFI_REQUESTED = "Use Lifi";
    public static final String STANDARD_REQUESTED = "Use standard geolocalisation";

    private static String selectedMode;
    private static String textToPrint;
    private static boolean vibrator;
    private static boolean flash;

    // Views declaration
    private EditText phoneNum;
    private Button findButton;
    private Spinner modeSpinner;
    private EditText textToPrintView;
    private ToggleButton vibratorToggle;
    private ToggleButton flashToggle;
    private Button saveTextButton;
    private Spinner contactSpinner;

    private List<String> arraySpinner = new ArrayList<String>();

    private ArrayList<String> listOfContactNames;
    private ArrayList<String> listOfContactNums;

    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.v("debugMap", "MainActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //preferences
        settings = getPreferences(0);
        selectedMode = settings.getString("selectedMode", STANDARD_REQUESTED);
        textToPrint = settings.getString("textToPrint", "I'm here !");
        vibrator = settings.getBoolean("vibrator", true);
        flash = settings.getBoolean("flash", false);

        // Views initialization
        phoneNum = (EditText) findViewById(R.id.phone_num);
        findButton = (Button) findViewById(R.id.button_find);
        modeSpinner = (Spinner) findViewById(R.id.mode_spinner);
        textToPrintView = (EditText) findViewById(R.id.textToPrint);
        vibratorToggle = (ToggleButton) findViewById(R.id.vibrate);
        flashToggle = (ToggleButton) findViewById(R.id.flash);
        saveTextButton = (Button) findViewById(R.id.buttonSaveText);
        contactSpinner = (Spinner) findViewById(R.id.contact_spinner);

        // Button management
        findButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String number = phoneNum.getText().toString();
                goToLoading(number);
            }
        });

        //Mode spinner
        arraySpinner = new ArrayList<String>();
        arraySpinner.add(STANDARD_REQUESTED);
        arraySpinner.add(LIFI_REQUESTED);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        modeSpinner.setAdapter(adapter);

        //set default webviews values
        if(selectedMode.equals(STANDARD_REQUESTED))
            modeSpinner.setSelection(0);
        else
            modeSpinner.setSelection(1);
        textToPrintView.setText(textToPrint);
        vibratorToggle.setChecked(vibrator);
        flashToggle.setChecked(flash);

        //WebView listeners
        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMode = modeSpinner.getSelectedItem().toString();
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("selectedMode", selectedMode);
                editor.apply();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        contactSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                phoneNum.setText(listOfContactNums.get(contactSpinner.getSelectedItemPosition()));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        saveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToPrint = textToPrintView.getText().toString();
                textToPrint = textToPrint.replace(";"," ");
                textToPrintView.setText(textToPrint);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("textToPrint", textToPrint);
                editor.apply();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        vibratorToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator = vibratorToggle.isChecked();
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("vibrator", vibrator);
                editor.apply();
            }
        });

        flashToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flash = flashToggle.isChecked();
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("flash", flash);
                editor.apply();
            }
        });

        // Permission management
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 0);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, 0);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 0);
        }

        getContactList();
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listOfContactNames);
        contactSpinner.setAdapter(adapter);
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
            sendSMS(number, MessageReceiver.LOCATION_REQUEST_MESSAGE + ";"
                    + selectedMode + ";"
                    + textToPrint + ";"
                    + vibrator + ";"
                    + flash + ";");
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

    private void getContactList() {
        ArrayList<String> tempList = new ArrayList<>();
        listOfContactNames = new ArrayList<>();
        listOfContactNums = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        tempList.add(name + "%_%" + phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }


        Collections.sort(tempList);
        for(int i=0; i<tempList.size(); i++){
            listOfContactNames.add(tempList.get(i).split("%_%")[0]);
            listOfContactNums.add(tempList.get(i).split("%_%")[1]);
        }
    }
}
