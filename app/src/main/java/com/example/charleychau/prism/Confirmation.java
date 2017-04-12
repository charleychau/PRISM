package com.example.charleychau.prism;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class Confirmation extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private Button pharmacyButton;
    private Button confirmButton;
    private EditText userid;
    private EditText dob;
    private TextView name;
    private TextView address;
    private TextView number;
    private String chosenPharm;
    private String chosenAddress;
    private String chosenNumber;
    private GoogleApiClient mGoogleApiClient;
    int PLACE_PICKER_REQUEST = 1;
    int PILL_REQUEST = 2;
    private RequestQueue queue;
    private String server = "http://10.0.1.5:8080";
    private Response.Listener<String> responseListener;
    private Response.ErrorListener errorListener;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    protected static final String TAG = "Confirmation";
    private String uid;
    private String pid;
    private String dayOfMonth;
    private String month;
    private String year;
    private ArrayList<User> userList;
    private ArrayList<Pill> pillsArray = new ArrayList<Pill>();
    private boolean pillExists = false;
    private boolean pillExists2 = false;
    JSONObject obj = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        // Initialize views
        name = (TextView) findViewById(R.id.textViewPharmName);
        address = (TextView) findViewById(R.id.textViewPharmAddress);
        number = (TextView) findViewById(R.id.textViewPharmNumber);
        pharmacyButton = (Button) findViewById(R.id.buttonChoosePharm);
        confirmButton = (Button) findViewById(R.id.buttonConfirm);
        userid = (EditText) findViewById(R.id.editTextUID);
        dob = (EditText) findViewById(R.id.editTextDOB);

        // Initialize Google API Client
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        // Picks pharmacy info
        pharmacyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(Confirmation.this), PLACE_PICKER_REQUEST);
                }
                catch (Exception ex) {
                }
            }
        });

        // Handle Volley
        queue = Volley.newRequestQueue(this);
        responseListener = new Response.Listener<String> () {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "Response is: " + response);

                String str = response;
                /*try {
                    try {
                        obj = new JSONObject(str);
                    } catch (JSONException e) {
                        Log.e("MYAPP", "unexpected JSON exception", e);
                    }
                    String pid = obj.getString("pid");
                    String pillname = obj.getString("name");
                    String username = obj.getString("uname");
                    String refills = obj.getString("refills");
                    String quantity = obj.getString("quantity");
                    String pillPerUse = obj.getString("pillPerUse");
                    String start = obj.getString("start");
                    String times = obj.getString("time");

                    Pill pill = new Pill(pid, pillname, "2F234454F4911BA9FFA6", "000000000003", username, refills, quantity, pillPerUse, start, times);
                    //User user = new User("John", "123456", "2F234454F4911BA9FFA6", "000000000003");

                    Intent addPills = new Intent(Confirmation.this, MyPills.class);
                    addPills.putExtra("PILL", pill);
                    addPills.putExtra("PILLS_ARRAY", pillsArray);
                    startActivityForResult(addPills, PILL_REQUEST);
                }
                catch (JSONException e){
                }*/
            }
        };

        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error is: " + error.toString());
            }
        };

        // Manually adds a pill to the pill list
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Use 78945645 as UID for Tom and Adderall
                // Use 12345678 as UID for Michael and Abilify
                uid = userid.getText().toString().substring(0,6);
                pid = userid.getText().toString().substring(6,8);

                //Date code for user authentication
                /*SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy");
                Date myDate;
                String bday = dob.getText().toString();
                Calendar c = Calendar.getInstance();
                try {
                    myDate = df.parse(bday);
                    c.setTime(myDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dayOfMonth = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
                month = String.valueOf(c.get(Calendar.MONTH) + 1);
                year = String.valueOf(c.get(Calendar.YEAR));*/

                /*final String requestRefill = server + "/pharm/refill/" + uid + "/" + pid;
                final String checkRefill = server + "/pharm/refill_ready/" + uid + "/" + pid;
                StringRequest srRequestRefill = new StringRequest(Request.Method.POST, requestRefill, responseListener, errorListener);
                StringRequest srCheckRefill = new StringRequest(Request.Method.GET, checkRefill, responseListener, errorListener);
                queue.add(srRequestRefill);
                queue.add(srCheckRefill);*/

                //Following code is for implementation without node
                if (pid.equals("45") && uid.equals("789456")) {
                    Pill pill = new Pill(pid, "Adderall", "2F234454F4911BA9FFA6", "000000000003", "Tom", "3", "30", "1", "2", "3", uid);
                    Intent addPills = new Intent(Confirmation.this, MyPills.class);
                    addPills.putExtra("PILL", pill);
                    addPills.putExtra("PILLS_ARRAY", pillsArray);
                    addPills.putExtra("FROM_MAIN", false);
                    startActivityForResult(addPills, PILL_REQUEST);
                }
                if (pid.equals("78") && uid.equals("123456")) {
                    Pill pill = new Pill(pid, "Abilify", "2F234454F4911BA9FFA6", "000000000002", "Michael", "2", "60", "1", "1", "21", uid);
                    Intent addPills = new Intent(Confirmation.this, MyPills.class);
                    addPills.putExtra("PILL", pill);
                    addPills.putExtra("PILLS_ARRAY", pillsArray);
                    addPills.putExtra("FROM_MAIN", false);
                    startActivityForResult(addPills, PILL_REQUEST);
                }
            }
        });
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
            }
        }
        else if (requestCode == PILL_REQUEST) {
            if (resultCode == RESULT_OK) {
                pillsArray = (ArrayList<Pill>) data.getSerializableExtra("RESULT_ARRAY");
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently
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
