package com.example.charleychau.prism;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Prescriptions extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageView;
    private Button prescriptionButton;
    private Button pharmacyButton;
    private RadioGroup radioNotificationGroup;
    private RadioButton radioNotificationButton;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescriptions);

        prescriptionButton = (Button) findViewById(R.id.buttonPrescription);
        imageView = (ImageView) findViewById(R.id.imageView);
        pharmacyButton = (Button) findViewById(R.id.buttonPharmacy);
        radioNotificationGroup = (RadioGroup) findViewById(R.id.radioNotification);
        sendButton = (Button) findViewById(R.id.buttonSend);

        prescriptionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO: bring up snapshot taker
                //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(cameraIntent, CAMERA_REQUEST);
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        pharmacyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO: bring up list of pharms
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
        }*/
        if (requestCode == REQUEST_IMAGE_CAPTURE){
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imageView.setImageBitmap(imageBitmap);
            }
            else if (resultCode == RESULT_CANCELED){
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, "GOT HERE", duration);
                toast.show();
            }
        }
    }

}
