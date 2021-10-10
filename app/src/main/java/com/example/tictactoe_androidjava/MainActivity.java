package com.example.tictactoe_androidjava;
/** Emilia Hepolehto
 * m.hepolehto@gmail.com
**/

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button [] buttons = new Button[9];
    private Button btn_restart;
    private Button btn_share;
    private TextView txt_result;
    private int roundCount;
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

        btn_restart = (Button) findViewById(R.id.btn_restart);
        btn_share = (Button) findViewById(R.id.btn_share);
        txt_result = (TextView) findViewById(R.id.txt_result);

        for(int i = 0 ; i < buttons.length ; i++){
            String buttonID = "btn_" + i;
            int resourceID = getResources().getIdentifier(buttonID, "id", getPackageName());
            buttons[i] = (Button) findViewById(resourceID);
            buttons[i].setOnClickListener(this);
        }

        roundCount = 0;
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
                txt_result.setText("Player X Won");
                Toast.makeText(this, "Player X Won", Toast.LENGTH_SHORT).show();
            } else {
                txt_result.setText("Player O Won");
                Toast.makeText(this, "Player O Won", Toast.LENGTH_SHORT).show();
            }
        } else if(roundCount == 9){
            txt_result.setText("No winner!");
            Toast.makeText(this, "No winner!", Toast.LENGTH_SHORT).show();
        } else {
            activePlayer = !activePlayer;
        }

        btn_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                playAgain();
            }
        });
        verifyStoragePermission(MainActivity.this);
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                takeScreenShot(getWindow().getDecorView());
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

    public void playAgain(){
        roundCount = 0;
        activePlayer = true;
        txt_result.setText("");

        for(int i = 0 ; i < buttons.length ; i++){
            gameState[i] = 2;
            buttons[i].setText("");
        }
    }

    //Screenshot and Share methods reference -> https://trendoceans.com/how-to-take-and-share-screenshot-programmatically-in-android-studio/
    private void takeScreenShot(View view) {

        //This is used to provide file name with Date a format
        Date date = new Date();
        CharSequence format = DateFormat.format("MM-dd-yyyy_hh:mm:ss", date);

        //It will make sure to store file to given below Directory and If the file Directory dosen't exist then it will create it.
        try {
            File mainDir = new File(
                    this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "FilShare");
            if (!mainDir.exists()) {
                boolean mkdir = mainDir.mkdir();
            }

            //Providing file name along with Bitmap to capture screenview
            String path = mainDir + "/" + "TrendOceans" + "-" + format + ".jpeg";
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);

        //This logic is used to save file at given location with the given filename and compress the Image Quality.
            File imageFile = new File(path);
            FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

        //Create New Method to take ScreenShot with the imageFile.
            shareScreenShot(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Share ScreenShot
    private void shareScreenShot(File imageFile) {
        //Using sub-class of Content provider
        Uri uri = FileProvider.getUriForFile(
                this,
                BuildConfig.APPLICATION_ID + "." + getLocalClassName() + ".provider",
                imageFile);

        //Explicit intent
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "Look my Tic Tac Toe score! :)");
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        //It will show the application which are available to share Image; else Toast message will throw.
        try {
            this.startActivity(Intent.createChooser(intent, "Share With"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No App Available", Toast.LENGTH_SHORT).show();
        }
    }

    //Permissions Check
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSION_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    public static void verifyStoragePermission(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSION_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
}