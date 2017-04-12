package com.example.charleychau.prism;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class Snapshot extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_CAMERA_PERMISSION = 2;
    static final int PICTURE_RESULT = 3;
    static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private Button prescriptionButton;
    private Button nextButton;
    private Bitmap imageBitmap;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snapshot);

        // Initialize views
        prescriptionButton = (Button) findViewById(R.id.buttonPrescription);
        imageView = (ImageView) findViewById(R.id.imageView);
        nextButton = (Button) findViewById(R.id.buttonNext);

        verifyStoragePermissions(this);

        startIntent();

        // Starts camera to take picture of prescription
        prescriptionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //takePicture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, PICTURE_RESULT);

                /*Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }*/
            }
        });

        // Progress to the next activity and send picture through intent
        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent addPrescription = new Intent(Snapshot.this, Prescriptions.class);
                Bundle extras = new Bundle();
                extras.putParcelable("prescription", imageBitmap);
                addPrescription.putExtras(extras);
                startActivity(addPrescription);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICTURE_RESULT){
            if (resultCode == RESULT_OK) {
                try {
                    Log.d("yeeeeeeeeeee", "activity result " + imageUri);
                    imageBitmap = (Bitmap) data.getExtras().get("data");
                    //imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    imageView.setImageBitmap(imageBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static void verifyStoragePermissions(Activity activity) {

        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CAMERA_PERMISSION
            );
        }
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(MainActivity.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }*/

    private void startIntent(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        if(ActivityCompat.checkSelfPermission(Snapshot.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            Log.d("yeeeeeeeeeee", "not granted");
        }
        else {
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Log.d("yeeeeeeeeeee", "not granted" + imageUri);
        }
    }
}
