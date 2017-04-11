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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.places.ui.PlacePicker;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;
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
    private String namespace;
    private String instance;
    private Button addButton;
    private Button remindButton;
    private boolean pillExists;
    private boolean pillExists2;
    private boolean filtered = false;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pills);

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

        //user = (User) getIntent().getSerializableExtra("USER");
        pill = (Pill) getIntent().getSerializableExtra("PILL");
        pillsList = (ListView) findViewById(R.id.listViewPills);
        addButton = (Button) findViewById(R.id.buttonAdd);
        remindButton = (Button) findViewById(R.id.buttonRemind);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("RESULT_ARRAY", pillsArray);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
        remindButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                for (int i = 0; i < pillsArray.size(); i++) {
                    if (pillsArray.get(i).getNamespace().equals("2F234454F4911BA9FFA6")  &&
                            pillsArray.get(i).getInstance().equals("000000000003")) {
                        filteredArray.add(pillsArray.get(i));
                    }
                }
                adapter.clear();
                adapter.addAll(filteredArray);
                filtered = true;
                //TODO: Can put text to speech here

                String phrase = "Hello User. Your pills that you need to take include the following: ";
                speak(tts, phrase);
            }
        });
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
        pillsArray = (ArrayList<Pill>) getIntent().getSerializableExtra("PILLS_ARRAY");
        //userArray.add(user);

        Pill pill2 = new Pill("99", "Hydrocodone", "10", "1", "1", "2F234454F4911BA9FFA6", "000000000002", "Tom", "1");
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
        // Regions can also be defined in terms of their bleutooth MAC address.
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
            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void showPillInfo(int pos, ArrayList<Pill> array){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        final Pill pill = array.get(pos);

        // Setting Dialog Title
        alertDialog.setTitle(pill.getName());

        // Setting Dialog Message
        alertDialog.setMessage("Pill Owner: " + pill.getOwner() + "\nPill ID: " + pill.getPid() + "\nPill Duration: " +
                pill.getDuration() + "\nPill Amount: " + pill.getAmount() + "\nPill Times/Day: " + pill.getTimes());

        alertDialog.setNegativeButton("Refill", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, "Refill Request Sent!", duration);
                toast.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        Context context = getApplicationContext();
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, pill.getName() + " Refill Ready For Pickup!", duration);
                        toast.show();
                    }

                }, 7500); // 5000ms delay

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
}
