package com.geartocare.client.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.geartocare.client.R;

import java.util.Arrays;
import java.util.List;

public class PlacesAutoActivity extends AppCompatActivity {

    EditText editText;
    TextView tv1,tv2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_auto);

        editText = findViewById(R.id.edit_text);
        tv1= findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);

        Places.initialize(PlacesAutoActivity.this,"AIzaSyAQtEk43RaJ2Jp-6EnqK2qXz58OuJZKZCE");

        editText.setFocusable(false);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG,Place.Field.NAME);


                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY
                        ,fieldList).build(PlacesAutoActivity.this);

                startActivityForResult(intent,100);

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode==100 && resultCode == RESULT_OK){

            Place place = Autocomplete.getPlaceFromIntent(data);

            editText.setText(place.getAddress());

            tv1.setText(String.format("Location Name : %s",place.getName()));

            tv2.setText(String.valueOf(place.getLatLng()));

        }else if(resultCode == AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);

            Toast.makeText(PlacesAutoActivity.this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}