package com.clickandbike.clickandbike.Fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.clickandbike.clickandbike.Activity.ButtonActivity;
import com.clickandbike.clickandbike.DAO.CloudFetchr;
import com.clickandbike.clickandbike.DAO.JsonItem;
import com.clickandbike.clickandbike.R;
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
    private static Handler handler = new Handler();
    private LatLng itemPoint;
    private LatLng myPoint;
    private Boolean isFirstTime = true;


    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.equals(stationMarker)) {
            Toast.makeText(getActivity(),"clicked !",Toast.LENGTH_SHORT).show();
        }
        return false;
    }


/*
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
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        handler.removeCallbacks(sendData);
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

    @Override
    public void onResume() {
        super.onResume();
        Log.i("SERGI","onResume");
        if (mMap == null) {
            return;
        }
        //Create a listehenr
        //mMap.setOnMarkerClickListener(this);
        // Add a marker with my location
        myPoint   = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
        MarkerOptions myMarker = new MarkerOptions()
                .position(myPoint);
        mMap.clear();
//        stationMarker = mMap.addMarker(itemMarker);
        mMap.addMarker(myMarker);
    }

    private void findMyLocation() {
        final LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(10000);
        request.setFastestInterval(5000);
        request.setInterval(5000);
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
                        //new SearchTask().execute(location);
                        mCurrentLocation = location;
                        //Initial UI update
                        updateUI();
                        //Start polling to see if there are station changes
                        handler.postDelayed(sendData, 5000);
                        //LocationServices.FusedLocationApi.removeLocationUpdates(mClient,this);
                    }
                });
    }

    private void updateUI() {
        Log.i("SERGI","Updating map");
        if (mMap == null) {
            return;
        }
        //Create a listehenr
        mMap.setOnMarkerClickListener(this);
        // Add a marker with my location
        myPoint   = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
        MarkerOptions myMarker = new MarkerOptions()
                .position(myPoint);
        mMap.clear();
//        stationMarker = mMap.addMarker(itemMarker);
        mMap.addMarker(myMarker);

        if (itemPoint != null) {
            BitmapDescriptor itemBitmap = BitmapDescriptorFactory.fromResource(R.drawable.bike_icon);
            MarkerOptions itemMarker = new MarkerOptions()
                    .position(itemPoint)
                    .title(mStation.getName())
                    .icon(itemBitmap)
                    .zIndex(10);

            itemMarker.visible(true);
            stationMarker = mMap.addMarker(itemMarker);
            if (isFirstTime) {
                LatLngBounds bounds = new LatLngBounds.Builder()
                        .include(itemPoint)
                        .include(myPoint)
                        .build();
                //int margin = getResources().getDimensionPixelSize(R.dimen.map_intersect_margin);
                CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, 10);
                mMap.animateCamera(update);
            }
        } else {
            if (isFirstTime) {
                //Toast.makeText(getActivity(), "No station found !", Toast.LENGTH_SHORT).show();
                LatLngBounds bounds = new LatLngBounds.Builder()
                        .include(myPoint)
                        .build();
                //int margin = getResources().getDimensionPixelSize(R.dimen.map_intersect_margin);
                CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, 10);
                mMap.animateCamera(update);
            }
        }
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

    //We check every 5s what is the status of the stations and if there are changes we update UI
    private final Runnable sendData = new Runnable() {
        @Override
        public void run() {
            //Do something after POLL_INTERVAL
            CloudTask task = new CloudTask();
            task.execute();
            handler.postDelayed(this, 5000);
        }};

    private class CloudTask extends AsyncTask<Location,Void,Void> {
       @Override
        protected Void doInBackground(Location... params) {
            CloudFetchr fetchr = new CloudFetchr();
            JsonItem item = fetchr.getStation();
            mStation = item;
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (mStation.getTimeDelta() > 5) {
                //Station should not be visible
                if (stationMarker != null) {
                    if (stationMarker.isVisible()) {
                        stationMarker.remove();
                        stationMarker = null;
                        itemPoint = null;
                        updateUI();
                    }
                } else {
                    itemPoint = null;
                }
            } else {
                if (stationMarker != null) {
                    if (!stationMarker.isVisible()) {
                        updateUI();
                    }
                } else {
                    itemPoint = new LatLng(Double.valueOf(mStation.getLatitude()), Double.valueOf(mStation.getLongitude()));
                    updateUI();
                }
            }
            isFirstTime = false;
        }
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
            mCurrentLocation = mLocation;
            updateUI();
        }
    }
*/
}
