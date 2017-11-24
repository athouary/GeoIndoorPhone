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

    private MapboxMap mapboxMap;
    private boolean mapBoxReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, this.getResources().getString(R.string.mapboxToken));
        setContentView(R.layout.activity_map);

        locationText = (TextView) findViewById(R.id.location_view);
        backButton = (Button) findViewById(R.id.back_button);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent goBacktoMain = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(goBacktoMain);
                finish();
            }
        });

        Bundle b = getIntent().getExtras();
        String location = ""; // or other values
        if(b != null)
            location = b.getString("phoneNum");

        if (location != null){
            locationText.setText("Location: " + location);
            Log.d("Help","StartParsing");
            Double Lat = Double.parseDouble(location.split(",")[0]);
            Double Lng = Double.parseDouble(location.split(",")[1]);
            Log.d("Help","EndParsing");

            Icon icon = IconFactory.getInstance(MapActivity.this).fromResource(R.drawable.marker);
            LatLng latLng = new LatLng(Lat, Lng);
            if (mapBoxReady) {
                // marker view using all the different options available
                mapboxMap.addMarker(new MarkerViewOptions()
                        .position(latLng)
                        .icon(icon));
                mapboxMap.setCameraPosition(new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(13.0)
                        .build());
            }
        }

        // Managers initialization
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mbMap) {
                Log.d("Help","MapReadyBegin");
                mapboxMap = mbMap;
                mapBoxReady = true;
                Log.d("Help","MapReadyFinish");
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
