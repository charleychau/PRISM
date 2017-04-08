package com.example.charleychau.prism;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button uidButton;
    private Button prescriptionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uidButton = (Button) findViewById(R.id.buttonUID);
        prescriptionButton = (Button) findViewById(R.id.buttonPrescription);

        prescriptionButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent addPrescription = new Intent(MainActivity.this, Prescriptions.class);
                startActivity(addPrescription);
            }
        });
    }
}
