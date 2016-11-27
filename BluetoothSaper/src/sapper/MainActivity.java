package ru.example.michael.saper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import ru.example.michael.saper.gui.GUIAction;
import ru.example.michael.saper.gui.GUIBoard;
import ru.example.michael.saper.gui.GUICell;
import ru.example.michael.saper.logics.Easy;
import java.util.Random;

public class MainActivity extends Activity implements OnClickListener{
    public static Context context;
    public static ImageButton btnInit;
    public static ImageButton btnMine;
    public static TextView tvMine;
    public static boolean stopGame=false;
    public static int countBomb;
    public static boolean chbMine=false;
    public static int PADDING = 120;
    public static String prefCountBomb;

    public static SharedPreferences sharedPref;
    public static ImageButton btnMenu, btnSend, btnbluemenu;
    private GUIBoard board;
    private Cell[][] cells;
    final Random random = new Random();
    private final int BOARD_POST = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("myLogs", "MainActivity onCreate ");
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        board = (GUIBoard) findViewById(R.id.guiBoard);
        btnMenu = (ImageButton) findViewById(R.id.btnMenu);
        btnInit = (ImageButton) findViewById(R.id.btnInit);
        btnMine = (ImageButton) findViewById(R.id.btnMine);
        if(chbMine){
            btnMine.setImageResource(R.drawable.chbmine_checked);
        } else{
            btnMine.setImageResource(R.drawable.chbmine_notchecked);
        }
        tvMine = (TextView) findViewById(R.id.tvMine);
        btnSend = (ImageButton) findViewById(R.id.btnSend);
        btnSend.setVisibility(View.INVISIBLE);
        btnbluemenu = (ImageButton) findViewById(R.id.btnbluemenu);
        btnbluemenu.setVisibility(View.INVISIBLE);
        context = getBaseContext();

        //*** Set array for measuring the working field ***
        cells = new Cell[1][1];
        //*** Removing the cell's size from Preference Activity ***
        int size = sharedPref.getInt("keySize", 70);
        PADDING = size;

        //*** Removing difficulty of the game from Preference Activity ***
        prefCountBomb = sharedPref.getString("keyDifficulty", "");
        Log.d("myLogs", "MainActivity size: " + size);

        //*** Measure the working field ***
        board.post(new Runnable() {
            @Override
            public void run() {
                handle.sendEmptyMessage(BOARD_POST);
            }
        });
    }

    Handler handle = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case BOARD_POST:
                    calcCell();
                    beginGame();
                    break;
            }
        }
    };

    //*** Calculating the number of cells of the playing field ***
    private void calcCell(){
        Log.d("myLogs", "calcCell() ");
        Log.d("myLogs", "board.getWidth() " + board.getWidth());
        Log.d("myLogs", "board.getHeight() " + board.getHeight());
        if (PADDING==0){
            PADDING = 70;
        }
        int cntX =  board.getWidth() / PADDING;
        int cntY =  board.getHeight() / PADDING;
        Log.d("myLogs", "cntX: in board.post " + cntX);
        Log.d("myLogs", "cntY: in board.post " + cntY);
        if (cntY==0 && cntX==0){
            cntY = 1;
            cntX = 1;
        }
        cells = new Cell[cntY][cntX];
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d("myLogs", "MainActivity onStart ");
        /*** Removing the Number of players from Preference Activity ***/
        int cntPlayer = Integer.valueOf(sharedPref.getString("keyPlayers", ""));
        if (cntPlayer==1) {
            /*** Removing the cell's size from Preference Activity ***/
            Toast.makeText(this, R.string.gameModeSingle,Toast.LENGTH_SHORT).show();
            // Check changing the settings of "The difficulty level" or "Cell size"
            if(PADDING!=sharedPref.getInt("keySize", 70) || !prefCountBomb.equals(sharedPref.getString("keyDifficulty", ""))){
                PADDING = sharedPref.getInt("keySize", 70);
                calcCell();
                this.beginGame();
            }
        } else if (cntPlayer==2){
            Toast.makeText(this, R.string.gameModeTwo,Toast.LENGTH_SHORT).show();
            /*** Launch Activity of the Multiplayer game ***/
            Intent intent = new Intent(this, Multiplayer.class);
            startActivity(intent);
            finish();
        }
}
    @Override
    protected void onResume() {
        Log.d("myLogs", "MainActivity onResume ");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.d("myLogs", "MainActivity onDestroy ");
        super.onDestroy();
    }


    public void beginGame() {
        new GUIAction(
                new Easy(), board,
                new GeneratorBoard() {
                    public Cell[][] generate() {
                        Log.d("myLogs", "generate() Array's Size: " + cells.length + "  " + cells[0].length);
                        for (int i = 0; i < cells.length; i++) {
                            for (int j = 0; j < cells[0].length; j++) {
                                cells[i][j] = new GUICell(context, false);
                            }
                        }

                        /***  We realize the calculation of the amount mines, depending on the "Difficulty level" ***/
                        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                        prefCountBomb = sharedPref.getString("keyDifficulty", "");
                        switch(Integer.parseInt(prefCountBomb)){
                            case 1:
                                countBomb = (cells.length * cells[0].length)/ 8;
                                break;
                            case 2:
                                countBomb = (cells.length * cells[0].length)/ 4;
                                break;
                            case 3:
                                countBomb = (int) ((cells.length * cells[0].length)/ (2.8));
                                break;
                        }
                        tvMine.setText("0" + countBomb);

                        /*** Arrange the mines ***/
                        int count = 0;
                        int i, j;
                        while(count<countBomb){
                            i = random.nextInt(cells.length);
                            j = random.nextInt(cells[0].length);
                            if(!cells[i][j].isBomb()) {
                                cells[i][j].generateBomb(true);
                                count++;
                            }
                        }
                        return cells;
                    }
                });
    }

    @Override
    public void onClick(View v) {
        Log.d("myLogs", "onClick() ");
        switch(v.getId()){
            case R.id.btnMenu:
                /*** Launch Preference Activity ***/
                Intent intent = new Intent(this, PrefActivity.class);
                startActivity(intent);
            break;
            case R.id.btnInit:
                /*** Start new game ***/
                this.beginGame();
            break;
            case R.id.btnMine:
                if(chbMine){
                    chbMine = false;
                    /*** Set checking empty cells ***/
                    MainActivity.btnMine.setImageResource(R.drawable.chbmine_notchecked);
                }
                else {
                    chbMine = true;
                    /*** Set checking mines ***/
                    MainActivity.btnMine.setImageResource(R.drawable.chbmine_checked);
                }
                break;
        }
    }
}
