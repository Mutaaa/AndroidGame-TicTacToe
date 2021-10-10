package com.example.tictactoe_androidjava;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView playerXScore, playerOScore;
    private Button [] buttons = new Button[9];
    private Button btn_restart;

    private int playerXScoreCount, playerOScoreCount, roundCount;
    boolean activePlayer;

    /*
    player X -> 0
    player O -> 1
    active button -> 2
    */
    int[] gameState = {2,2,2,2,2,2,2,2,2};

    int[][] winningPositions = {
            {0,1,2}, {3,4,5}, {6,7,8},
            {0,3,6}, {1,4,7}, {2,5,8},
            {0,4,8}, {2,4,6}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerXScore = (TextView) findViewById(R.id.playerXScore);
        playerOScore = (TextView) findViewById(R.id.playerOScore);

        btn_restart = (Button) findViewById(R.id.btn_restart);

        for(int i = 0 ; i < buttons.length ; i++){
            String buttonID = "btn_" + i;
            int resourceID = getResources().getIdentifier(buttonID, "id", getPackageName());
            buttons[i] = (Button) findViewById(resourceID);
            buttons[i].setOnClickListener(this);
        }

        roundCount = 0;
        playerXScoreCount = 0;
        playerOScoreCount = 0;
        activePlayer = true;
    }

    @Override
    public void onClick(View v) {
        //Log.i("test", "button is clicked");
        if(!((Button)v).getText().toString().equals("")){
            return;
        }
        String buttonID = v.getResources().getResourceEntryName(v.getId());
        int gameStatePointer = Integer.parseInt(buttonID.substring(buttonID.length()-1, buttonID.length()));

        if(activePlayer){
            ((Button) v).setText("X");
            ((Button) v).setTextColor(Color.parseColor("#009FFD"));
            gameState[gameStatePointer] = 0;
        }else{
            ((Button) v).setText("O");
            ((Button) v).setTextColor(Color.parseColor("#FF575A"));
            gameState[gameStatePointer] = 1;
        }
        roundCount++;

        if(checkWinner()){
            if(activePlayer){
                playerXScoreCount++;
                updatePlayerScore();
                Toast.makeText(this, "Player X Won", Toast.LENGTH_SHORT).show();
                playAgain();
            } else {
                playerOScoreCount++;
                updatePlayerScore();
                Toast.makeText(this, "Player O Won", Toast.LENGTH_SHORT).show();
                playAgain();
            }
        } else if(roundCount == 9){
            playAgain();
            Toast.makeText(this, "No winner!", Toast.LENGTH_SHORT).show();
        } else {
            activePlayer = !activePlayer;
        }

        btn_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                playAgain();
                playerXScoreCount = 0;
                playerOScoreCount = 0;
                updatePlayerScore();
            }
        });
    }

    public boolean checkWinner(){
        boolean winnerResult = false;
        for(int[] winningPosition : winningPositions){
            if(
                    gameState[winningPosition[0]] == gameState[winningPosition[1]] &&
                    gameState[winningPosition[1]] == gameState[winningPosition[2]] &&
                            gameState[winningPosition[0]] != 2){
                winnerResult = true;
            }
        }
        return  winnerResult;
    }

    public void updatePlayerScore(){
        playerXScore.setText(Integer.toString(playerXScoreCount));
        playerOScore.setText(Integer.toString(playerOScoreCount));
    }

    public void playAgain(){
        roundCount = 0;
        activePlayer = true;

        for(int i = 0 ; i < buttons.length ; i++){
            gameState[i] = 2;
            buttons[i].setText("");
        }
    }
}