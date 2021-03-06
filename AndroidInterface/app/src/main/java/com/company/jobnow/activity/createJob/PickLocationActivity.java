package com.company.jobnow.activity.createJob;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.company.jobnow.R;
import com.company.jobnow.common.Constant;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class PickLocationActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "PickLocationActivity";

    private boolean mLocationPermisionGranted = false;
    private GoogleMap map;
    private MarkerOptions markerJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_locations);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getLocationPermission();
        init();
    }

    public void init() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(getApplicationContext(), permissions[1]) == PackageManager.PERMISSION_GRANTED)) {
            mLocationPermisionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, permissions, Constant.RequestCode.LOCATION_PERMISSION);
        }
    }

    public void getDeviceLocation() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermisionGranted) {
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.e(TAG, "onComplete: Found Location");
                            Location currentLocation = (Location) task.getResult();
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), Constant.Numeric.DEFAULT_ZOOM));
                        } else {
                            Log.e(TAG, "onComplete: Location not found");
                        }
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "getDeviceLocation: Exception :" + e.getMessage());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Log.e(TAG, "onMapClick: " + latLng.latitude + " " + latLng.longitude);
                map.clear();
                markerJob = new MarkerOptions().position(new LatLng(latLng.latitude, latLng.longitude));
                map.addMarker(markerJob);
                try {
                    ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100);
                } catch (NullPointerException e) {
                    Log.e(TAG, "onMapLongClick: Exception: " + e.getMessage());
                }
            }
        });

        if (mLocationPermisionGranted) {
            getDeviceLocation();
            map.setMyLocationEnabled(true);
        } else {
            Toast.makeText(this, getString(R.string.error_cannot_find_location), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constant.RequestCode.LOCATION_PERMISSION) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermisionGranted = false;
                    return;
                }
                mLocationPermisionGranted = true;
            }
        }
    }

    public void userPicksLocation(View view) {
        Intent data = new Intent();
        if (markerJob == null) {
            Toast.makeText(this, getString(R.string.error_no_marker_on_map), Toast.LENGTH_LONG).show();
            return;
        }
        data.putExtra(Constant.Key.LATITUDE, markerJob.getPosition().latitude);
        data.putExtra(Constant.Key.LONGITUDE, markerJob.getPosition().longitude);
        setResult(RESULT_OK, data);
        finish();
    }
}
