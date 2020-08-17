package com.example.dotsandboxes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ChoosePlayers extends AppCompatActivity {

    private int row,column,plyrs;
    private EditText editTextPlyr, editTextRow, editTextColumn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_players);
        editTextPlyr = (EditText)findViewById(R.id.editTextPlyrs);
        editTextRow = (EditText)findViewById(R.id.editTextRow);
        editTextColumn = (EditText)findViewById(R.id.editTextColumn);
        Intent intent = getIntent();
        row = intent.getIntExtra(MainActivity.ROW,0);
        column = intent.getIntExtra(MainActivity.COLUMN,0);
    }

    public void play(View view){
        if(TextUtils.isEmpty(editTextPlyr.getText().toString().trim()) ||
                TextUtils.isEmpty(editTextRow.getText().toString().trim()) ||
                TextUtils.isEmpty(editTextColumn.getText().toString().trim())){
            String msg = "Enter all the fields!";
            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
        }
        else {
            int flag = 0;
            plyrs = Integer.parseInt(editTextPlyr.getText().toString());
            row = Integer.parseInt(editTextRow.getText().toString());
            column = Integer.parseInt(editTextColumn.getText().toString());
            if (plyrs < 1 || plyrs > 9) {
                flag++;
                String msg = "Number of players should be between 1 and 10!";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
            if (row < 3 || row > 15) {
                flag++;
                String msg = "Number of rows should be between 3 and 15!";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
            if (column < 3 || column > 15) {
                flag++;
                String msg = "Number of columns should be between 3 and 15!";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
            if (flag == 0) {
                Intent intent = new Intent(this, Play.class);
                intent.putExtra(MainActivity.ROW,row);
                intent.putExtra(MainActivity.COLUMN,column);
                intent.putExtra(MainActivity.PLAYERS,plyrs);
                startActivity(intent);
                finish();
            }
        }
    }

}