package com.example.dotsandboxes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Play extends AppCompatActivity {

    private Board board;
    private int row, column, plyrs;
    private TextView textViewPlyrTurn, textViewPlyrscores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        Intent intent = getIntent();
        row = intent.getIntExtra(MainActivity.ROW,0);
        column = intent.getIntExtra(MainActivity.COLUMN,0);
        plyrs = intent.getIntExtra(MainActivity.PLAYERS,0);
        textViewPlyrTurn = (TextView)findViewById(R.id.textViewTurn);
        textViewPlyrscores = (TextView)findViewById(R.id.textViewPlayerScore);
        displayDetails(1,0);
        board = (Board)findViewById(R.id.boardView);
        board.setPlayActivity(this);
        board.initialise(row, column, plyrs);
        board.invalidate();
    }

    public void displayDetails(int turn, int score){
        String plyr;
        if(plyrs == 1)
            plyr = "Player's Turn";
        else
            plyr = "Player "+turn+"'s Turn";
        textViewPlyrTurn.setText(plyr);
        if(plyrs == 1)
            plyr = "Player's Score : "+score;
        else
            plyr = "Player "+turn+"'s Score : "+score;
        textViewPlyrscores.setText(plyr);
    }

    public void undoMove(View view){
         board.undo();
    }

    public void gameOver(int win){
        String winner;
        if(win==0)
            winner = "Draw!";
        else if(plyrs > 1)
            winner = "Player " + win + " Wins!";
        else{
            if(win==-1)
                winner = "Player Loses!";
            else
                winner = "Player Wins!";
        }
        Toast.makeText(getApplicationContext(), winner, Toast.LENGTH_LONG).show();
    }
}