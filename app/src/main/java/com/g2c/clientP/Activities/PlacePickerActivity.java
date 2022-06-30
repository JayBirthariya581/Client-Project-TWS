package com.g2c.clientP.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Toast;

import com.g2c.clientP.Helpers.CustomProgressDialog;
import com.g2c.clientP.Helpers.LocationChecker;
import com.g2c.clientP.Helpers.LocationClass;
import com.g2c.clientP.Helpers.NetworkChangeListener;
import com.g2c.clientP.Helpers.ServiceDataContainer;
import com.g2c.clientP.databinding.ActivityPlacePickerBinding;
import com.g2c.clientP.model.ModelLocation;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;

import com.g2c.clientP.BuildConfig;

import com.g2c.clientP.R;
import com.g2c.clientP.SessionManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PlacePickerActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, LocationListener, Animation.AnimationListener, GoogleApiClient.ConnectionCallbacks {


    ActivityPlacePickerBinding binding;
    LocationClass locationObject;
    FusedLocationProviderClient mFusedLocationProviderClient;//for gps
    LocationChecker locationChecker;
    GoogleMap mMap;
    GoogleMap.OnCameraIdleListener onCameraIdleListener;
    GoogleMap.OnCameraMoveCanceledListener onCameraMoveCanceledListener;
    Context context;
    CustomProgressDialog dialog;
    LocationRequest locationRequest;
    LocationCallback mLocationCallback;
    SessionManager sessionManager;
    HashMap<String, String> serviceDetails;
    ServiceDataContainer serviceData;
    LatLng latLng;
    ProgressDialog progressDialog;
    List<Address> addressList;
    ActivityResultLauncher<String> requestPermissionLauncher;
    ActivityResultLauncher<Intent> checkAvailableAreaLauncher;

     NetworkChangeListener networkChangeListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkChangeListener = new NetworkChangeListener(PlacePickerActivity.this);
        binding = ActivityPlacePickerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //normal shared preference
        sessionManager = new SessionManager(PlacePickerActivity.this);
        dialog = new CustomProgressDialog(PlacePickerActivity.this);

        locationChecker = new LocationChecker(PlacePickerActivity.this);
        locationChecker.fetchAreaList();


        checkAvailableAreaLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                progressDialog.show();
                if(result.getResultCode()==Activity.RESULT_OK){

                    Intent data = result.getData();
                    ModelLocation aDetails = (ModelLocation) data.getSerializableExtra("areaDetails");
                    if(aDetails!=null){
                        moveCamera(new LatLng(Double.valueOf(aDetails.getLat()),Double.valueOf(aDetails.getLng())),19f);
                    }

                }
                progressDialog.dismiss();
            }
        });


        locationChecker.getUvBinding().seeAvLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationChecker.getUnavailableDialog().dismiss();
                checkAvailableAreaLauncher.launch(new Intent(PlacePickerActivity.this,AvailableAreaActivity.class));

            }
        });

        if (((Class<?>) getIntent().getSerializableExtra("Activity")) == ServiceDetailActivity.class) {

            serviceData = (ServiceDataContainer) getIntent().getSerializableExtra("serviceDetails");
        } else {

            //Receive service details from previous activity
            serviceDetails = (HashMap<String, String>) getIntent().getSerializableExtra("serviceDetails");

        }


        //for storing all location related data
        locationObject = new LocationClass();

        //progress dialog
        progressDialog = new ProgressDialog(PlacePickerActivity.this);
        progressDialog.setMessage("Turning ON GPS");
        progressDialog.setCancelable(false);

        //for gps
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        //TODO: UI updates.
                    }
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.getFusedLocationProviderClient(PlacePickerActivity.this).requestLocationUpdates(locationRequest, mLocationCallback, null);
        }


        //Call SelectLocationActivity when a other location is required
        ActivityResultLauncher<Intent> otherLocationLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();


                    if (data.getStringExtra("requestType").equals("currentLocation")) {
                        double lati = Double.valueOf(data.getStringExtra("lat"));
                        double longi = Double.valueOf(data.getStringExtra("lng"));

                        moveCamera(new LatLng(lati, longi), 19f);


                    } else {


                        PlacesClient placesClient = Places.createClient(PlacePickerActivity.this);


                        String placeId = String.valueOf(data.getStringExtra("placeId"));

                        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
                        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
                        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                            @Override
                            public void onSuccess(FetchPlaceResponse response) {
                                Place place = response.getPlace();


                                moveCamera(new LatLng(place.getLatLng().latitude, place.getLatLng().longitude), 19f);


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                if (exception instanceof ApiException) {
                                    Toast.makeText(PlacePickerActivity.this, exception.getMessage() + "", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }


                }
            }
        });


        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);


                if (isGPSEnabled()) {

                    Task location = LocationServices.getFusedLocationProviderClient(context).getLastLocation();
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Location currentLocation = (Location) task.getResult();
                                if (currentLocation != null) {
                                    moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 19f);
                                }
                            }
                        }
                    });

                } else {
                    pointUserLocation();

                }


            } else {
                Toast.makeText(context, "Please enable location", Toast.LENGTH_SHORT).show();
                openSettings();
                //requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        });


        try {
            initializeViews();
            configureCameraIdle();

            binding.proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    try {

                        if (((Class<?>) getIntent().getSerializableExtra("Activity")) == ServiceDetailActivity.class) {//for two wheeler service booking

                            serviceData.getServiceData().getLocation().setLat(locationObject.getLatitude().toString());
                            serviceData.getServiceData().getLocation().setLng(locationObject.getLongitude().toString());
                            serviceData.getServiceData().getLocation().setTxt(binding.result.getText().toString());

                            Intent i = new Intent(PlacePickerActivity.this, (Class<?>) getIntent().getSerializableExtra("Activity"));
                            i.putExtra("serviceDetails", serviceData);
                            startActivity(i);

                        } else {//for other than tws
                            serviceDetails.put("lat", locationObject.getLatitude().toString());
                            serviceDetails.put("lng", locationObject.getLongitude().toString());
                            serviceDetails.put("location_txt", binding.result.getText().toString());

                            Intent i = new Intent(PlacePickerActivity.this, (Class<?>) getIntent().getSerializableExtra("Activity"));
                            i.putExtra("serviceDetails", serviceDetails);
                            startActivity(i);
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            binding.back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });


            binding.otherLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(PlacePickerActivity.this, SelectLocationActivity.class);
                    otherLocationLauncher.launch(i);

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void pointUserLocation() {


        locationObject.setLatitude(Double.valueOf(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_LOCATION_Lat)));
        locationObject.setLongitude(Double.valueOf(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_LOCATION_Lng)));
        moveCamera(new LatLng(locationObject.getLatitude(), locationObject.getLongitude()), 19f);


    }


    private void configureCameraIdle() {


        onCameraIdleListener = new GoogleMap.OnCameraIdleListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCameraIdle() {
                try {

                    binding.slider.setVisibility(View.VISIBLE);
                    LatLng latLng = mMap.getCameraPosition().target;


                    locationObject.setLatitude(latLng.latitude);
                    locationObject.setLongitude(latLng.longitude);

                    if (!locationChecker.verifyLocation(latLng.latitude, latLng.longitude)) {
                        latLng = locationChecker.getDefaultLocation();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationChecker.getDefaultLocation(), 19f));
                        locationChecker.getUnavailableDialog().show();
                    }


                    Geocoder geocoder = new Geocoder(PlacePickerActivity.this);
                    binding.result.setText("Loading...");

                    try {
                        addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        if (addressList != null && addressList.size() > 0) {
                            locationObject.setLocality(addressList.get(0).getAddressLine(0));
                            locationObject.setCountry(addressList.get(0).getCountryName());
                            locationObject.setState(addressList.get(0).getAdminArea());
                            locationObject.setSub_admin(addressList.get(0).getSubAdminArea());
                            locationObject.setCity(addressList.get(0).getFeatureName());
                            locationObject.setPincode(addressList.get(0).getPostalCode());
                            locationObject.setLocality_city(addressList.get(0).getLocality());
                            locationObject.setSub_localoty(addressList.get(0).getSubLocality());
                            locationObject.setCountry_code(addressList.get(0).getCountryCode());

                            if (locationObject.getLocality() != null && locationObject.getCountry() != null) {
                                binding.result.setText(locationObject.getLocality() + "");
                            } else {
                                binding.result.setText("Location could not be fetched...");
                            }
                            binding.slider.setVisibility(View.GONE);
                            addressList.clear();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } catch (
                        Exception e) {
                    e.printStackTrace();
                }
            }
        }

        ;


    }


    private void startLocationButtonClick() {
        // Requesting ACCESS_FINE_LOCATION using Dexter library
        try {


            if (ContextCompat.checkSelfPermission(PlacePickerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                if (isGPSEnabled()) {

                    Task location = LocationServices.getFusedLocationProviderClient(context).getLastLocation();
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Location currentLocation = (Location) task.getResult();
                                if (currentLocation != null) {
                                    moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 19f);
                                }
                            }
                        }
                    });
                } else {
                    pointUserLocation();

                }

            } else {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initializeViews() {
        try {
            context = PlacePickerActivity.this;


            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
            binding.content.startRippleAnimation();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openSettings() {
        try {
            Intent intent = new Intent();
            intent.setAction(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package",
                    BuildConfig.APPLICATION_ID, null);
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void moveCamera(LatLng latLng, float zoom) {
        try {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        latLng = new LatLng(location.getLatitude(), location.getLongitude());

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Task location = LocationServices.getFusedLocationProviderClient(context).requestLocationUpdates(locationRequest, mLocationCallback, null);
        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Location currentLocation = (Location) task.getResult();
                    if (currentLocation != null) {
                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 19f);
                    }
                }
            }
        });

    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public boolean onMyLocationButtonClick() {
        if (isGPSEnabled()) {
            return false;
        } else {

            turnOnGPS();
            return true;
        }


    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        latLng = new LatLng(location.getLatitude(), location.getLongitude());

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            mMap.setOnCameraIdleListener(onCameraIdleListener);
            mMap.setTrafficEnabled(true);
            mMap.setLatLngBoundsForCameraTarget(new LatLngBounds(new LatLng(21.027723664312575, 78.86553301878368), new LatLng(21.256168917951413, 79.18395253376026)));

            mMap.setIndoorEnabled(true);
            mMap.animateCamera(CameraUpdateFactory.zoomTo(100), 2000, null);
            mMap.setBuildingsEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.resetMinMaxZoomPreference();
            mMap.getUiSettings().setMapToolbarEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
            mMap.isIndoorEnabled();
            mMap.isBuildingsEnabled();

            startLocationButtonClick();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }


    private void turnOnGPS() {


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);


                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(PlacePickerActivity.this, 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }


            }
        });

    }


    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;

    }


    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {


                } else {

                    turnOnGPS();
                }
            }
        }


    }*/

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);

    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}