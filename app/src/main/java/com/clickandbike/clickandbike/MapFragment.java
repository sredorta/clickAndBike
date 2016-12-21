package com.clickandbike.clickandbike;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

/**
 * Created by sredorta on 11/8/2016.
 */
public class MapFragment extends SupportMapFragment implements GoogleMap.OnMarkerClickListener {
    // Create client for google API
    private GoogleApiClient mClient;
    private Location mCurrentLocation;
    private GoogleMap mMap;
    public JsonItem mStation = new JsonItem();
    public Marker stationMarker;
    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Declare the client
        //mClient = new GoogleApiClient.Builder(getActivity()).addApi(LocationServices.API).build();
        mClient = new GoogleApiClient.Builder(getActivity()).addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        //Invalideate options menu when callback says connected so that button is updated
                        findMyLocation();
                        updateUI();

                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .build();
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

            }
        });
    }



    //Connect the Google API client during onStart
    @Override
    public void onStart() {
        super.onStart();
        mClient.connect();
        Log.i("SERGI", "Connected to mClient  !");
    }

    //Disconnect the Google API client during onStop
    @Override
    public void onStop() {
        super.onStop();
        mClient.disconnect();
    }


    private void findMyLocation() {
        final LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setFastestInterval(10000);
        request.setInterval(10000);
        request.setSmallestDisplacement(300);

        //request.setExpirationDuration(4000);
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi
                .requestLocationUpdates(mClient, request, new LocationListener() {

                    @Override
                    public void onLocationChanged(Location location) {
                        //Toast.makeText(getActivity(),"Got current location:" + location,Toast.LENGTH_LONG).show();
                        new SearchTask().execute(location);
                        //LocationServices.FusedLocationApi.removeLocationUpdates(mClient,this);
                    }
                });
    }

    private void updateUI() {
        Log.i("SERGI","Updating map");
        if (mMap == null || mStation.getLatitude() == null || mStation.getLongitude() == null) {
            return;
        }
        if (mStation.getLatitude().equals("not_available") || mStation.getLongitude().equals("not_available")) {
            Log.i("SERGI", "Station2 has no coords !");
            return;
        }
        if (mStation.getTimeDelta() > 10000) {

            if (stationMarker != null) {

                    stationMarker.setVisible(false);
                if (stationMarker.isVisible()) {
                    Toast.makeText(getActivity(), "Station has been disconnected !", Toast.LENGTH_SHORT).show();
                }
            }
            return;
        }
        if (stationMarker != null) {
                stationMarker.setVisible(true);
            if (!stationMarker.isVisible()) {
                Toast.makeText(getActivity(), "Station has been reconnected !", Toast.LENGTH_SHORT).show();
            }
        }
        mMap.setOnMarkerClickListener(this);
        LatLng myPoint   = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());

//        Toast.makeText(getActivity(),"item \nlat: " + mMapItem.getLat() + "\nlon: " + mMapItem.getLon(), Toast.LENGTH_LONG).show();
//        Toast.makeText(getActivity(),"curr\n lat: " + mCurrentLocation.getLatitude() + "\nlon: " + mCurrentLocation.getLongitude(), Toast.LENGTH_LONG).show();
        //Add markers on the two points
        LatLng itemPoint = new LatLng(Double.valueOf(mStation.getLatitude()), Double.valueOf(mStation.getLongitude()));
        BitmapDescriptor itemBitmap = BitmapDescriptorFactory.fromResource(R.drawable.bike_icon);
        MarkerOptions itemMarker = new MarkerOptions()
                .position(itemPoint)
                .title(mStation.getName())
                .icon(itemBitmap)
                .zIndex(10);

        MarkerOptions myMarker = new MarkerOptions()
                .position(myPoint);
        mMap.clear();
        stationMarker = mMap.addMarker(itemMarker);
        mMap.addMarker(myMarker);
        LatLngBounds bounds = new LatLngBounds.Builder()
               .include(itemPoint)
                .include(myPoint)
                .build();
        //int margin = getResources().getDimensionPixelSize(R.dimen.map_intersect_margin);
        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds,0);
        mMap.animateCamera(update);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
         if (marker.equals(stationMarker)) {
             Toast.makeText(getActivity(),"clicked !",Toast.LENGTH_SHORT).show();
             Intent i = new Intent(getActivity(), ButtonActivity.class);
             getActivity().startActivity(i);
            Log.i("SERGI","marker");
        }
        return false;
    }

    private class SearchTask extends AsyncTask<Location,Void,Void> {
 //       private GalleryItem mGalleryItem;
        private Bitmap mBitmap;
        private Location mLocation;


        @Override
        protected Void doInBackground(Location... params) {
            mLocation = params[0];
            CloudFetchr fetchr = new CloudFetchr();
            JsonItem item = fetchr.getStation();
            mStation = item;
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
/*            mMapImage = mBitmap;
            mMapItem = mGalleryItem;*/
            mCurrentLocation = mLocation;
            updateUI();
        }
    }

}
