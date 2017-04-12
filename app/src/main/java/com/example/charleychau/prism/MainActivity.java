package com.example.charleychau.prism;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button hubButton;
    private Button uidButton;
    private Button prescriptionButton;
    private Button pillsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        hubButton = (Button) findViewById(R.id.buttonHub);
        uidButton = (Button) findViewById(R.id.buttonUID);
        prescriptionButton = (Button) findViewById(R.id.buttonPrescription);
        pillsButton = (Button) findViewById(R.id.buttonPills);

        hubButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent goHub = new Intent(MainActivity.this, MyCameraActivity.class);
                startActivity(goHub);
            }
        });

        prescriptionButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent addPrescription = new Intent(MainActivity.this, Snapshot.class);
                startActivity(addPrescription);
            }
        });

        uidButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent goConfirm = new Intent(MainActivity.this, Confirmation.class);
                startActivity(goConfirm);
            }
        });

        pillsButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent goMyPills = new Intent(MainActivity.this, MyPills.class);
                goMyPills.putExtra("FROM_MAIN", true);
                startActivity(goMyPills);
            }
        });







    }



}
