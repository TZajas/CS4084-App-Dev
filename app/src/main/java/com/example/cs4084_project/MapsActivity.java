//Created By Dennis Kolomiyets 20250762
package com.example.cs4084_project;

import static android.content.ContentValues.TAG;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private GoogleMap mGoogleMap;
    Button backBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    //on creation the following code allows the map to be displayed
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mapView = findViewById(R.id.map_view); //map
        backBtn = findViewById(R.id.backButton); //back button

        
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.onDestroy();
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });
        
    }

    //method that checks whether permission is granted, which is called when you first launch the app. 
    // @param requestCode The request code passed in
    // android.app.Activity, String[], int)}
    // @param permissions The requested permissions. Never null.
    // @param grantResults The grant results for the corresponding permissions
    //     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
    //     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_GRANTED:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    // the feature requires a permission that the user has denied.
                    Log.e(TAG, "Location permissions denied");
                }
                return;
        }
    }



    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(true);
        
        //Set location and name for security marker
        LatLng security = new LatLng(52.673355392017655, -8.567490135746874);
        mGoogleMap.addMarker(new MarkerOptions().position(security).title("UL Security"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(security));

    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
