package com.example.charleychau.prism;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

public class MyPills extends AppCompatActivity {
    private ImageButton addPillsButton;
    private ListView pillsList;
    //private ArrayList<PillInfo> pillsArray = new ArrayList<>();
    //private ArrayAdapter<PillInfo> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pills);


    }
}
