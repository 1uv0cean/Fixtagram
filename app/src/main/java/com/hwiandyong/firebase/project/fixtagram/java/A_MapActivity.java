package com.hwiandyong.firebase.project.fixtagram.java;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.hwiandyong.firebase.project.fixtagram.R;


public class A_MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_a_map, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return true;
        }else if(i == R.id.action_post) {
            startActivity(new Intent(this, A_MainActivity.class));
            finish();
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Seoul and move the camera


        LatLng office1 = new LatLng(37.452787, 126.657387);
        LatLng office2 = new LatLng(37.448418, 126.661733);
        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.position(office1);
        markerOptions1.title("Fixtagram 1호점");
        markerOptions1.snippet("용현동 195-23 3층");
        mMap.addMarker(markerOptions1);
        MarkerOptions markerOptions2 = new MarkerOptions();
        markerOptions2.position(office2);
        markerOptions2.title("Fixtagram 2호점");
        markerOptions2.snippet("용현동 84-34 2층");
        mMap.addMarker(markerOptions2);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(office1, 16.0f));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));


    }
}
