package com.anupam.cabapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import com.google.android.gms.location.LocationServices;
import com.google.maps.android.SphericalUtil;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener {

    double latitude, longitude;

    private final double lat = 12.95414087;
    private final double lng = 77.70075416;

    private GoogleMap mMap;

    private GoogleApiClient googleApiClient;

    private Circle mCircle;

    private LatLng hbackcar1 = new LatLng(12.94869544, 77.7021532);
    private LatLng hbackcar2 = new LatLng(12.95526173, 77.71009254);
    private LatLng hbackcar3 = new LatLng(12.95400704, 77.70043659);
    private LatLng hbackcar4 = new LatLng(12.9548435, 77.69082355);

    private LatLng sedancar1 = new LatLng(12.94622781, 77.69846248);
    private LatLng sedancar2 = new LatLng(12.95626548, 77.70060825);
    private LatLng sedancar3 = new LatLng(12.94572592, 77.70944881);
    private LatLng sedancar4 = new LatLng(12.95304511, 77.69648838);

    private LatLng suvcar1 = new LatLng(12.95856573, 77.7012949);
    private LatLng suvcar2 = new LatLng(12.9550108, 77.69820499);
    private LatLng suvcar3 = new LatLng(12.94886274, 77.69670296);
    private LatLng suvcar4 = new LatLng(12.94154343, 77.69648838);

    private int hatchbackicon = R.drawable.ic_color_hatchbag;
    private int sedanicon = R.drawable.ic_color_sedan;
    private int suvicon = R.drawable.ic_color_suv;

    ImageView hatchback, sedan, suv;

    TextView bookButton;

    LatLng latLng, compLatLng1, compLatLng2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        bookButton = (TextView) findViewById(R.id.btn_book);
        bookButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                compLatLng1 = new LatLng(lat, lng);

                int distance = (int) Math.round(distanceBetween(compLatLng1, compLatLng2));

                if(distance > 1500) {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.default_message), Toast.LENGTH_LONG).show();
                }else if(distance <= 1500){

                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.custom_dialog);
                    dialog.setCancelable(true);
                    dialog.show();
                }

            }
        });
    }

    @Override
    public void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    private void getCurrentLocation() {
        mMap.clear();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {

            longitude = location.getLongitude();
            latitude = location.getLatitude();

            moveMap();
        }
    }

    private void moveMap() {

        latLng = new LatLng(latitude, longitude);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(14).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        markerRadius(latLng);

        cabMarkers(latLng);
    }

    @Override
    public void onResume() {
        sedan = (ImageView) findViewById(R.id.sedan_icon);
        sedan.setBackgroundResource(0);
        suv = (ImageView) findViewById(R.id.suv_icon);
        suv.setBackgroundResource(0);
        super.onResume();
    }

    public void markerRadius(LatLng latLng) {
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker))
                .title("Pick up Location"));

        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        double radiusInMeters = 1000.0;
        int strokeColor = Color.parseColor("#55009688");
        int shadeColor = Color.parseColor("#30009688");


        CircleOptions circleOptions = new CircleOptions().center(latLng)
                .radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(2);
        mCircle = mMap.addCircle(circleOptions);

        compLatLng2 = latLng;
    }

    public void createMarker(LatLng latlng, int icon) {

        mMap.addMarker(new MarkerOptions()
                .position(latlng)
                .icon(BitmapDescriptorFactory.fromResource(icon)));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng1) {
                mMap.clear();

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));

                markerRadius(latLng1);

                cabMarkers(latLng1);
            }
        });

    }

    public void cabMarkers(final LatLng latLng){
        ArrayList<LatLng> latlngArray = new ArrayList<LatLng>();
        latlngArray.add(hbackcar1);
        latlngArray.add(hbackcar2);
        latlngArray.add(hbackcar3);
        latlngArray.add(hbackcar4);

        for(int i = 0 ; i < latlngArray.size() ; i++ ) {

            createMarker(latlngArray.get(i),hatchbackicon);
        }


        hatchback = (ImageView) findViewById(R.id.hatchback_icon);
        hatchback.setBackgroundResource(R.drawable.round_background);
        sedan.setBackgroundResource(0);
        suv.setBackgroundResource(0);
        hatchback.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                hatchback.setBackgroundResource(R.drawable.round_background);
                sedan.setBackgroundResource(0);
                suv.setBackgroundResource(0);

                mMap.clear();
                markerRadius(latLng);
                ArrayList<LatLng> latlngArray = new ArrayList<LatLng>();
                latlngArray.add(hbackcar1);
                latlngArray.add(hbackcar2);
                latlngArray.add(hbackcar3);
                latlngArray.add(hbackcar4);

                for(int i = 0 ; i < latlngArray.size() ; i++ ) {

                    createMarker(latlngArray.get(i),hatchbackicon);
                }
            }
        });
        sedan = (ImageView) findViewById(R.id.sedan_icon);
        sedan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                sedan.setBackgroundResource(R.drawable.round_background);
                suv.setBackgroundResource(0);
                hatchback.setBackgroundResource(0);

                mMap.clear();
                markerRadius(latLng);
                ArrayList<LatLng> latlngArray = new ArrayList<LatLng>();
                latlngArray.add(sedancar1);
                latlngArray.add(sedancar2);
                latlngArray.add(sedancar3);
                latlngArray.add(sedancar4);

                for(int i = 0 ; i < latlngArray.size() ; i++ ) {

                    createMarker(latlngArray.get(i),sedanicon);
                }

            }
        });
        suv = (ImageView) findViewById(R.id.suv_icon);
        suv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                suv.setBackgroundResource(R.drawable.round_background);
                sedan.setBackgroundResource(0);
                hatchback.setBackgroundResource(0);

                mMap.clear();
                markerRadius(latLng);
                ArrayList<LatLng> latlngArray = new ArrayList<LatLng>();
                latlngArray.add(suvcar1);
                latlngArray.add(suvcar2);
                latlngArray.add(suvcar3);
                latlngArray.add(suvcar4);

                for(int i = 0 ; i < latlngArray.size() ; i++ ) {

                    createMarker(latlngArray.get(i),suvicon);
                }
            }
        });
    }

    public static Double distanceBetween(LatLng point1, LatLng point2) {
        if (point1 == null || point2 == null) {
            return null;
        }

        return SphericalUtil.computeDistanceBetween(point1, point2);
    }

    @Override
    public void onConnected(Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.location_message))
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}
