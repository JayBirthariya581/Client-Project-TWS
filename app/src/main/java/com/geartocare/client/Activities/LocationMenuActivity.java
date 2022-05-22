package com.geartocare.client.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.geartocare.client.Helpers.LocationHelper;
import com.geartocare.client.Helpers.CustomProgressDialog;
import com.geartocare.client.R;
import com.geartocare.client.SessionManager;
import com.geartocare.client.databinding.ActivityLocationMenuBinding;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class LocationMenuActivity extends AppCompatActivity {
    ActivityLocationMenuBinding binding;
    SessionManager sessionManager;


    String locality = null;
    String country = null;
    String state;
    String sub_admin;
    String city;
    String pincode;
    String locality_city;
    String sub_localoty;
    String country_code;
    CustomProgressDialog dialog;

    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLocationMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


       sessionManager = new SessionManager(LocationMenuActivity.this);

         dialog = new CustomProgressDialog(LocationMenuActivity.this);






        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        //Toast.makeText(LocationMenuActivity.this, sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID), Toast.LENGTH_SHORT).show();
        ActivityResultLauncher<Intent> lau = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                   dialog.show();
                    Intent data = result.getData();
                    if (data.getStringExtra("requestType").equals("currentLocation")) {
                        double latitude = Double.valueOf(data.getStringExtra("lat"));
                        double longitude = Double.valueOf(data.getStringExtra("lng"));

                        Geocoder geocoder = new Geocoder(LocationMenuActivity.this);

                        try {
                            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                            if (addressList != null && addressList.size() > 0) {
                                locality = addressList.get(0).getAddressLine(0);
                                country = addressList.get(0).getCountryName();
                                state = addressList.get(0).getAdminArea();
                                sub_admin = addressList.get(0).getSubAdminArea();
                                city = addressList.get(0).getFeatureName();
                                pincode = addressList.get(0).getPostalCode();
                                locality_city = addressList.get(0).getLocality();
                                sub_localoty = addressList.get(0).getSubLocality();
                                country_code = addressList.get(0).getCountryCode();
                                if (locality != null && country != null) {

                                    LocationHelper location = new LocationHelper(String.valueOf(latitude), String.valueOf(longitude), locality);

                                    FirebaseDatabase.getInstance().getReference("Users").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID)).child("location")
                                            .setValue(location).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                SessionManager sessionManager = new SessionManager(LocationMenuActivity.this);

                                                SharedPreferences.Editor editor = sessionManager.getEditor();

                                                editor.putString(SessionManager.KEY_LOCATION_Lat, String.valueOf(latitude));
                                                editor.putString(SessionManager.KEY_LOCATION_Lng, String.valueOf(longitude));
                                                editor.putString(SessionManager.KEY_LOCATION_Txt, locality);


                                                editor.commit();
                                                dialog.dismiss();

                                                startActivity(new Intent(LocationMenuActivity.this, MainActivity.class));
                                                finish();


                                            }


                                        }
                                    });


                                } else {
                                    //resutText.setText("Location could not be fetched...");

                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    } else {
                        PlacesClient placesClient = Places.createClient(LocationMenuActivity.this);


                        String placeId = String.valueOf(data.getStringExtra("placeId"));

                        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
                        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
                        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                            @Override
                            public void onSuccess(FetchPlaceResponse response) {


                                Place place = response.getPlace();

                                try {
                                    LocationHelper location = new LocationHelper(String.valueOf(place.getLatLng().latitude), String.valueOf(place.getLatLng().longitude), place.getAddress());

                                    FirebaseDatabase.getInstance().getReference("Users").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID)).child("location")
                                            .setValue(location).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {


                                                SessionManager sessionManager = new SessionManager(LocationMenuActivity.this);

                                                SharedPreferences.Editor editor = sessionManager.getEditor();

                                                editor.putString(SessionManager.KEY_LOCATION_Lat, String.valueOf(place.getLatLng().latitude));
                                                editor.putString(SessionManager.KEY_LOCATION_Lng, String.valueOf(place.getLatLng().longitude));
                                                editor.putString(SessionManager.KEY_LOCATION_Txt, place.getAddress());


                                                editor.commit();

                                                startActivity(new Intent(LocationMenuActivity.this, MainActivity.class));
                                                dialog.dismiss();
                                                finish();


                                            }


                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                if (exception instanceof ApiException) {
                                    Toast.makeText(LocationMenuActivity.this, exception.getMessage() + "", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }


                }
            }
        });


        binding.currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                getCurrentLocation();


            }
        });


        binding.otherLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lau.launch(new Intent(LocationMenuActivity.this, SelectLocationActivity.class));
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    getCurrentLocation();

                } else {

                    turnOnGPS();
                }
            }
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {

                getCurrentLocation();
            }
        }
    }


    private void getCurrentLocation() {
       dialog.show();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(LocationMenuActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    LocationServices.getFusedLocationProviderClient(LocationMenuActivity.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(LocationMenuActivity.this)
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() > 0) {

                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();


                                        //Fetching the place
                                        Geocoder geocoder = new Geocoder(LocationMenuActivity.this);

                                        try {
                                            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                                            if (addressList != null && addressList.size() > 0) {
                                                locality = addressList.get(0).getAddressLine(0);
                                                country = addressList.get(0).getCountryName();
                                                state = addressList.get(0).getAdminArea();
                                                sub_admin = addressList.get(0).getSubAdminArea();
                                                city = addressList.get(0).getFeatureName();
                                                pincode = addressList.get(0).getPostalCode();
                                                locality_city = addressList.get(0).getLocality();
                                                sub_localoty = addressList.get(0).getSubLocality();
                                                country_code = addressList.get(0).getCountryCode();
                                                if (locality != null && country != null) {

                                                    LocationHelper location = new LocationHelper(String.valueOf(latitude), String.valueOf(longitude), locality);

                                                    FirebaseDatabase.getInstance().getReference("Users").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID)).child("location")
                                                            .setValue(location).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful()) {

                                                                SessionManager sessionManager = new SessionManager(LocationMenuActivity.this);

                                                                SharedPreferences.Editor editor = sessionManager.getEditor();

                                                                editor.putString(SessionManager.KEY_LOCATION_Lat, String.valueOf(latitude));
                                                                editor.putString(SessionManager.KEY_LOCATION_Lng, String.valueOf(longitude));
                                                                editor.putString(SessionManager.KEY_LOCATION_Txt, locality);


                                                                editor.commit();
                                                                dialog.dismiss();
                                                                startActivity(new Intent(LocationMenuActivity.this, MainActivity.class));
                                                                finish();


                                                            }


                                                        }
                                                    });


                                                } else {
                                                    //resutText.setText("Location could not be fetched...");

                                                }
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }


                                        //Toast.makeText(LocationMenuActivity.this, latitude +" "+longitude, Toast.LENGTH_SHORT).show();;
                                    }
                                }
                            }, Looper.getMainLooper());

                } else {
                    dialog.dismiss();
                    turnOnGPS();
                }

            } else {
                dialog.dismiss();
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }


    private void turnOnGPS() {


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(LocationMenuActivity.this, "GPS is already turned on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(LocationMenuActivity.this, 2);
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

}