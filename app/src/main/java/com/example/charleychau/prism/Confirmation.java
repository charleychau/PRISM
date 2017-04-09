package com.example.charleychau.prism;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Confirmation extends AppCompatActivity {

    private Button pharmacyButton;
    private Button confirmButton;
    private EditText userid;
    private EditText dob;
    private GoogleApiClient mGoogleApiClient;
    int PLACE_PICKER_REQUEST = 1;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        pharmacyButton = (Button) findViewById(R.id.buttonChoosePharm);
        confirmButton = (Button) findViewById(R.id.buttonConfirm);
        userid = (EditText) findViewById(R.id.editTextUID);
        dob = (EditText) findViewById(R.id.editTextDOB);

        queue = Volley.newRequestQueue(this);
        responseListener = new Response.Listener<String> () {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "Response is: " + response);
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, "RESPONSE RECEIVED", duration);
                toast.show();
            }
        };

        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error is: " + error.toString());
            }
        };

        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                uid = userid.getText().toString().substring(0,6);
                pid = userid.getText().toString().substring(6,8);

                SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy");
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
                year = String.valueOf(c.get(Calendar.YEAR));

                //TODO: Send to node
                final String endpointUID = server + "/pharm/check_uid/" + uid;
                final String endpointPID = server + "/pharm/check_pid/" + uid + "/" + pid;
                StringRequest srUID = new StringRequest(Request.Method.GET, endpointUID, responseListener, errorListener);
                StringRequest srPID = new StringRequest(Request.Method.GET, endpointPID, responseListener, errorListener);
                queue.add(srUID);
                queue.add(srPID);

                /*
                //If confirmed, go to My Pills
                Intent showPills = new Intent(Confirmation.this, MyPills.class);
                startActivity(showPills);

                //If not, try again
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, "Confirmation Failed!", duration);
                toast.show();
                */
            }
        });
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
