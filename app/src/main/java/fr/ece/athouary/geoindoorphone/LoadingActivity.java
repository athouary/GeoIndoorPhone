package fr.ece.athouary.geoindoorphone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.v("debugMap", "LoadingActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        TextView textView = (TextView)findViewById(R.id.pop_up);
        textView.setText(textView.getText() + "\n" + message);

        Log.v("debug", "onCreate LoadingAcvtivity end");
    }
}
