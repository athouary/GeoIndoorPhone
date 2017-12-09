package fr.ece.athouary.geoindoorphone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

public class MapActivity extends AppCompatActivity {

    private TextView locationText;
    private Button backButton;
    private MapView mapView;

    private Double Lat = 0.0;
    private Double Lng = 0.0;

    private String location;

    private MapboxMap mapboxMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, this.getResources().getString(R.string.mapboxToken));
        setContentView(R.layout.activity_map);

        Log.v("DebugToggle", "start Map Activity");

        locationText = (TextView) findViewById(R.id.location_view);
        backButton = (Button) findViewById(R.id.back_button);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        //récupération et affichage des coordonnées dans l'intent
        Intent intentCoord = getIntent();
        location = intentCoord.getStringExtra(MessageReceiver.EXTRA_MAP_MESSAGE);
        locationText.setText(location);

        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent goBacktoMain = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(goBacktoMain);
                finish();
            }
        });

        /*Bundle b = getIntent().getExtras();
        String location = ""; // or other values
        if(b != null)
            location = b.getString("phoneNum");*/

        if (location != null){
            Log.d("Help","StartParsing");
            Lat = Double.parseDouble(location.split(",")[0]);
            Lng = Double.parseDouble(location.split(",")[1]);
            Log.d("Help_parsing","EndParsing : " + Lat + "|" + Lng);
        }

        // Managers initialization
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mbMap) {
                Log.d("Help_parsing","start displaying");

                Icon icon = IconFactory.getInstance(MapActivity.this).fromResource(R.drawable.marker);
                Log.d("Help_parsing","stop1");
                LatLng latLng = new LatLng(Lat, Lng);
                Log.d("Help_parsing","stop2");

                // marker view using all the different options available
                mbMap.addMarker(new MarkerViewOptions()
                        .position(latLng)
                        .icon(icon));
                Log.d("Help_parsing","stop3");
                mbMap.setCameraPosition(new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(13.0)
                        .build());
                Log.d("Help_parsing","end displaying");
            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        Log.d("Help","onDestroy");
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
