package com.example.charleychau.prism;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.altbeacon.beacon.Identifier;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Prescriptions extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private Button pharmacyButton;
    private RadioGroup radioNotificationGroup;
    private RadioButton radioNotificationButton;
    private Button sendButton;
    private GoogleApiClient mGoogleApiClient;
    int PLACE_PICKER_REQUEST = 1;
    private TextView name;
    private TextView address;
    private TextView number;
    private Bitmap placeBitmap;
    private RequestQueue queue;
    private String server = "";
    private Response.Listener<String> responseListener;
    private Response.ErrorListener errorListener;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    protected static final String TAG = "Prescription";
    private String chosenPharm;
    private String chosenAddress;
    private String chosenNumber;
    private String notification;
    private Bitmap prescription;
    PlacePhotoMetadataBuffer photoMetadataBuffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescriptions);

        pharmacyButton = (Button) findViewById(R.id.buttonPharmacy);
        radioNotificationGroup = (RadioGroup) findViewById(R.id.radioNotification);
        sendButton = (Button) findViewById(R.id.buttonSend);
        name = (TextView) findViewById(R.id.textViewName);
        address = (TextView) findViewById(R.id.textViewAddress);
        number = (TextView) findViewById(R.id.textViewNumber);

        Bundle extras = getIntent().getExtras();
        prescription = (Bitmap) extras.getParcelable("prescription");

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        pharmacyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(Prescriptions.this), PLACE_PICKER_REQUEST);
                }
                catch (Exception ex) {

                }
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int selectedId = radioNotificationGroup.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                radioNotificationButton = (RadioButton) findViewById(selectedId);
                notification = radioNotificationButton.getText().toString();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                prescription.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                byte[] b = baos.toByteArray();
                String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                //TODO: send info to node
                final String endpointPharm = server + "/" + chosenPharm;
                final String endpointNotification = server + "/" + notification;
                final String endpointImage = server + "/" + encodedImage;
                //TODO: send bitmap to node final String endpointBitmap = server + "/" + prescription;
                StringRequest srPharm = new StringRequest(Request.Method.POST, endpointPharm, responseListener, errorListener);
                StringRequest srNotification = new StringRequest(Request.Method.POST, endpointNotification, responseListener, errorListener);
                queue.add(srPharm);
                queue.add(srNotification);
            }
        });

        queue = Volley.newRequestQueue(this);
        responseListener = new Response.Listener<String> () {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "Response is: " + response);

                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, "Prescription Sent!", duration);
                toast.show();

                Intent returnMain = new Intent(Prescriptions.this, MainActivity.class);
                returnMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(returnMain);
            }
        };

        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error is: " + error.toString());
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                chosenPharm = place.getName().toString();
                chosenAddress = place.getAddress().toString();
                chosenNumber = place.getPhoneNumber().toString();

                name.setText(chosenPharm);
                address.setText(chosenAddress);
                number.setText(chosenNumber);

                //TODO: Display map of location
                // Get a PlacePhotoMetadataResult containing metadata for the first 10 photos.
                /*String placeId = place.getId();
                PlacePhotoMetadataResult result = Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeId).await();
                // Get a PhotoMetadataBuffer instance containing a list of photos (PhotoMetadata).
                if (result != null && result.getStatus().isSuccess()) {
                    photoMetadataBuffer = result.getPhotoMetadata();
                }
                // Get the first photo in the list.
                PlacePhotoMetadata photo = photoMetadataBuffer.get(0);
                // Get a full-size bitmap for the photo.
                Bitmap image = photo.getPhoto(mGoogleApiClient).await().getBitmap();
                // Get the attribution text.
                CharSequence attribution = photo.getAttributions();
                placeImage.setImageBitmap(image);*/
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently

        // ...
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }


}
