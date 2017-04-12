package com.example.charleychau.prism;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.RemoteException;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

import android.os.Handler;

public class MyPills extends AppCompatActivity implements BeaconConsumer{
    protected static final String TAG = "Beacon";
    private BeaconManager beaconManager;
    private ImageButton addPillsButton;
    private ListView pillsList;
    private ArrayList<Pill> pillsArray = new ArrayList<>();
    private ArrayList<Pill> filteredArray = new ArrayList<>();
    private ArrayList<User> userArray = new ArrayList<>();
    private ArrayAdapter<Pill> adapter;
    private String uid;
    private String pid;
    private User user;
    private Pill pill;
    private Pill currPill;
    private String namespace;
    private String instance;
    private Button addButton;
    private Button remindButton;
    private boolean pillExists;
    private boolean pillExists2;
    private boolean pillExists3;
    private boolean filtered = false;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_SEND_SMS = 1;
    private TextToSpeech tts;
    private SmsManager sms;
    private RequestQueue queue;
    private Response.Listener<String> responseListener;
    private Response.ErrorListener errorListener;
    private String server = "http://10.0.1.5:8080";
    private String refillPill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pills);

        // Text to speech
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e(TAG, "This language is not supported");
                    }
                }
            }
        });
        // SMS
        sms = SmsManager.getDefault();
        //requestPermissions(new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SEND_SMS);

        // Handle Volley
        queue = Volley.newRequestQueue(this);
        responseListener = new Response.Listener<String> () {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "Response is: " + response);
                String str = response;
                Boolean ready = null;
                try {
                    JSONObject obj = new JSONObject(str);
                    ready = obj.getBoolean("ready");
                    if (ready) {
                        text("8133896108", "Your refill request for " + refillPill + " is ready.");
                        Log.d("debug", "refill request ready received");
                        Toast.makeText(MyPills.this, "Your refill request for " + refillPill + " is ready.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                catch (JSONException e){
                }

            }
        };
        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error is: " + error.toString());
            }
        };

        // Initialize views
        pillsList = (ListView) findViewById(R.id.listViewPills);
        addButton = (Button) findViewById(R.id.buttonAdd);
        remindButton = (Button) findViewById(R.id.buttonRemind);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Add Button sends user back to confirmation screen
                Intent resultIntent = new Intent();
                resultIntent.putExtra("RESULT_ARRAY", pillsArray);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
        remindButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Filters pills for beacon with instance of 3
                for (int i = 0; i < pillsArray.size(); i++) {
                    if (pillsArray.get(i).getNamespace().equals("2F234454F4911BA9FFA6")  &&
                            pillsArray.get(i).getInstance().equals("000000000003")) {
                        filteredArray.add(pillsArray.get(i));
                    }
                }
                adapter.clear();
                adapter.addAll(filteredArray);
                filtered = true;

                // Handle audio feedback
                String phrase = "Hello User. Your pills that you need to take include the following: You need to take ";
                speak(tts, phrase);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        for (int i = 0; i < filteredArray.size(); i++) {
                            currPill = filteredArray.get(i);
                            String reminder = currPill.getPillPerUse() + " " + currPill.getName() + " ";
                            speak(tts, reminder);
                            try {
                                synchronized (this) {
                                    wait(1500);
                                }
                            } catch (InterruptedException ex) {}
                        }
                    }

                }, 5000);

                // If the user is in range, assume the pills were taken
                for (int i = 0; i < filteredArray.size(); i++) {
                    filteredArray.get(i).setQuantity(Integer.toString(Integer.parseInt(filteredArray.get(i).getQuantity()) -
                            Integer.parseInt(filteredArray.get(i).getPillPerUse())));
                }

                Log.d("debug", "reached after setQuantity");

                // Goes through personal pill list and checks if any pills need to be refilled
                for (int i = 0; i < filteredArray.size(); i++) {
                    if (Integer.parseInt(filteredArray.get(i).getQuantity()) <= 5) {
                        final String uid = filteredArray.get(i).getUid();
                        final String pid = filteredArray.get(i).getPid();

                        final String requestRefill = server + "/pharm/refill/" + uid + "/" + pid;
                        StringRequest srRequestRefill = new StringRequest(Request.Method.POST, requestRefill, responseListener, errorListener);
                        queue.add(srRequestRefill);
                        refillPill = filteredArray.get(i).getName();
                        //text("8133896108", "A refill request for " + filteredArray.get(i).getName() + " was sent.");
                        Toast.makeText(MyPills.this, "A refil request for " + filteredArray.get(i).getName() + " was sent.",
                                Toast.LENGTH_SHORT).show();
                        Log.d("debug", "refill request sent");

                        final int finalI = i;
                        handler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                final String checkRefill = server + "/pharm/refill_ready/" + uid + "/" + pid;
                                StringRequest srCheckRefill = new StringRequest(Request.Method.GET, checkRefill, responseListener, errorListener);
                                queue.add(srCheckRefill);
                                Log.d("debug", "refill request ready sent");
                            }

                        }, 10000);
                    }
                }
            }
        });

        // Set up adapter to populate listView
        adapter = new AdapterPill(this, R.layout.list_pill_layout);
        pillsList.setAdapter(adapter);
        pillsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (filtered == true) {
                    showPillInfo(position, filteredArray);
                }
                else {
                    showPillInfo(position, pillsArray);
                }
            }
        });

        // Retrieve pill from intent
        pill = (Pill) getIntent().getSerializableExtra("PILL");
        // Retrieve pill array from intent
        pillsArray = (ArrayList<Pill>) getIntent().getSerializableExtra("PILLS_ARRAY");

        // Default pill2
        Pill pill2 = new Pill("99", "Hydrocodone", "2F234454F4911BA9FFA6", "000000000003", "Tom", "1", "6", "1", "1", "1", "789456");
        // Go through pill array and add the default pill if not already in the list
        for (int x = 0; x < pillsArray.size(); x++) {
            if(pillsArray.size() == 0) {
                pillsArray.add(pill2);
                pillExists2 = true;
            }
            else if(pillsArray.get(x).getName().equals(pill2.getName())) {
                pillExists2 = true;
            }
        }
        if (pillExists2 == false) {
            pillsArray.add(pill2);
            pillExists2 = true;
        }

        // Default pill3
        Pill pill3 = new Pill("78", "Humira", "2F234454F4911BA9FFA6", "000000000004", "Julian", "1", "10", "1", "2", "3", "486521");
        // Go through pill array and add the default pill if not already in the list
        for (int x = 0; x < pillsArray.size(); x++) {
            if(pillsArray.size() == 0) {
                pillsArray.add(pill3);
                pillExists3 = true;
            }
            else if(pillsArray.get(x).getName().equals(pill3.getName())) {
                pillExists3 = true;
            }
        }
        if (pillExists3 == false) {
            pillsArray.add(pill3);
            pillExists3 = true;
        }

        // Add pill from intent to list if not already in list
        for (int x = 0; x < pillsArray.size(); x++) {
            if(pillsArray.size() == 0) {
                pillsArray.add(pill);
                pillExists = true;
            }
            else if(pillsArray.get(x).getName().equals(pill.getName())) {
                pillExists = true;
            }
        }
        if (pillExists == false) {
            pillsArray.add(pill);
            pillExists = true;

        }
        adapter.clear();
        adapter.addAll(pillsArray);

        // Handle beacon location access
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check 
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        //requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        beaconManager.bind(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
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

    @Override
    protected void onDestroy() {
        beaconManager.unbind(this);
        super.onDestroy();
    }

    @Override
    public void onBeaconServiceConnect() {
        // Regions can also be defined in terms of their bluetooth MAC address.
        Region region = new Region("John", Identifier.parse("2F234454F4911BA9FFA6"), Identifier.parse("000000000003"), null);
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(final Region region) {
                if (region.getId1() == null || region.getId2() == null) return;
                Log.i(TAG, "Beacon found: " + region.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (region.getUniqueId() == "John"){
                            Toast.makeText(MyPills.this, "Reached Beacon Code from didEnterRegion", Toast.LENGTH_SHORT).show();
                            //TODO: Get reminders for pills and filter array
                            for (int i = 0; i < pillsArray.size(); i++) {
                                if (pillsArray.get(i).getNamespace().equals("2F234454F4911BA9FFA6")  &&
                                        pillsArray.get(i).getInstance().equals("000000000003")) {
                                    filteredArray.add(pillsArray.get(i));
                                }
                            }
                            adapter.clear();
                            adapter.addAll(filteredArray);
                            filtered = true;
                            pillsList.invalidateViews();
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            }

            @Override
            public void didExitRegion(Region region) {
                if (region.getId1() == null || region.getId2() == null) return;
                Log.i(TAG, "Beacon lost: " + region.toString());
            }

            @Override
            public void didDetermineStateForRegion(int state, final Region region) {
                if (region.getId1() == null || region.getId2() == null) return;

                if (state == MonitorNotifier.OUTSIDE) {
                    Log.i(TAG, "I have left the beacon region: " + region.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            /*StringRequest sr = new StringRequest(Request.Method.DELETE, endpoint, responseListener, errorListener);
                            queue.add(sr);*/
                        }
                    });
                }
                else if (state == MonitorNotifier.INSIDE){
                    Log.i(TAG, "I have entered the beacon region: " + region.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (region.getUniqueId() == "John"){
                                Toast.makeText(MyPills.this, "Reached Beacon Code from didDetermineStateForRegion", Toast.LENGTH_SHORT).show();
                                for (int i = 0; i < pillsArray.size(); i++) {
                                    if (pillsArray.get(i).getNamespace().equals("2F234454F4911BA9FFA6")  &&
                                            pillsArray.get(i).getInstance().equals("000000000003")) {
                                        filteredArray.add(pillsArray.get(i));
                                    }
                                }
                                adapter.clear();
                                adapter.addAll(filteredArray);
                                filtered = true;
                                pillsList.invalidateViews();
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    // Shows info about a pill in a dialog pop-up window
    public void showPillInfo(int pos, ArrayList<Pill> array){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        final Pill pill = array.get(pos);

        // Setting Dialog Title
        alertDialog.setTitle(pill.getName());

        // Setting Dialog Message
        alertDialog.setMessage("Pill Owner: " + pill.getUname() + "\nPill ID: " + pill.getPid() + "\nRefills left: " +
                pill.getRefills() + "\nPill Quantity: " + pill.getQuantity() + "\nPills Per Use: " + pill.getPillPerUse());

        alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private void speak(TextToSpeech tts, String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private void text(String phoneNumber, String smsContent) {
        sms.sendTextMessage(phoneNumber, null, smsContent, null, null);

    }
}
