package com.example.dotsandboxes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public static final String ROW = "NUMBER OF ROWS";
    public static final String COLUMN = "NUMBER OF COLUMNS";
    public static final String PLAYERS = "NUMBER OF PLAYERS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this,ChoosePlayers.class);
        startActivity(intent);
        }

    }