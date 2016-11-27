package ru.example.michael.saper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import ru.example.michael.saper.gui.GUIAction;
import ru.example.michael.saper.gui.GUIBoard;
import ru.example.michael.saper.gui.GUICell;
import ru.example.michael.saper.logics.Easy;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by michael on 16.10.2016.
 */
public class Multiplayer extends Activity implements OnClickListener{
    // Debugging
    private static final String TAG = "myLogs";
    private static final int DIALOG_CHOICE = 1;
    private static final int DIALOG_MINER_LOSS = 2;
    private static final int DIALOG_MINER_WON = 3;
    private static final int DIALOG_BLUETOOTH_MENU = 4;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    private final  int BOARD_POST = 6;
    public static final int MESSAGE_SAPPER_LOSS = 7;
    public static final int MESSAGE_SAPPER_WIN = 8;

    public static final int SOCKET_SERVER = 4;
    public static final int SOCKET_CLIENT = 5;

    private int socketView;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    public static final int REQUEST_CONNECT_DEVICE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    int cellFromBluetooth[][]; //An array of field cells obtained by Bluetooth

    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private MultiplayerService mChatService = null;


    private GUIBoard board;
    public Context context;
    private Cell[][] cells;

    @Override
    public void onCreate(Bundle multiplayer) {
        super.onCreate(multiplayer);
        Log.d("myLogs", "MultiplayerActivity onCreate");

        setContentView(R.layout.activity_main);
        board = (GUIBoard) findViewById(R.id.guiBoard);
        MainActivity.btnMenu = (ImageButton) findViewById(R.id.btnMenu);
        MainActivity.btnInit = (ImageButton) findViewById(R.id.btnInit);
        MainActivity.btnInit.setEnabled(false);
        MainActivity.btnMine = (ImageButton) findViewById(R.id.btnMine);
        if(MainActivity.chbMine){
            MainActivity.btnMine.setImageResource(R.drawable.chbmine_checked);
        } else {
            MainActivity.btnMine.setImageResource(R.drawable.chbmine_notchecked);
        }
        MainActivity.btnSend = (ImageButton) findViewById(R.id.btnSend);
        MainActivity.btnbluemenu = (ImageButton) findViewById(R.id.btnbluemenu);
        MainActivity.btnbluemenu.setVisibility(View.VISIBLE);


        MainActivity.tvMine = (TextView) findViewById(R.id.tvMine);
        context = getBaseContext();

        //*** The ban on the change screen orientation ***
        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
        }

        /*** Removing difficulty of the game from Preference Activity ***/
        MainActivity.prefCountBomb = MainActivity.sharedPref.getString("keyDifficulty", "");
        cells = new Cell[1][1];
        /*** Measure cell's size from Preference Activity ***/
        {int size = MainActivity.sharedPref.getInt("keySize", 70);
            if (size==0){MainActivity.PADDING=70;}
            MainActivity.PADDING = size;
            Log.d("myLogs", "MainActivity size: " + size);}

        //*** Measure the working field ***
        board.post(new Runnable() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(BOARD_POST);
            }
        });

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.bluetoothNotAvailable,
                    Toast.LENGTH_LONG).show();
            /*** Start the MainActivity - Activity of single player ***/
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    //*** Calculating the number of cells of the playing field ***
    private void calcCell(){
        Log.d("myLogs", "calcCell() ");
        Log.d("myLogs", "board.getWidth() " + board.getWidth());
        Log.d("myLogs", "board.getHeight() " + board.getHeight());
        int cntX, cntY;
        try{
            cntX =  board.getWidth() / MainActivity.PADDING;
            cntY =  board.getHeight() / MainActivity.PADDING;}
        catch(ArithmeticException e){
            MainActivity.PADDING = 70;
            cntX =  board.getWidth() / MainActivity.PADDING;
            cntY =  board.getHeight() / MainActivity.PADDING;
        }
        Log.d("myLogs", "cntX: in board.post " + cntX);
        Log.d("myLogs", "cntY: in board.post " + cntY);
        if (cntY==0 & cntX==0){cntY = 1; cntX = 1;}
        cells = new Cell[cntY][cntX];
    }

    //*** Calculating sell's size depending on the size of working field ***
    private void calcCellForBluetooth(){
        Log.d("myLogs", "calcCellForBluetooth() ");
        Log.d("myLogs", "board.getWidth() " + board.getWidth());
        Log.d("myLogs", "board.getHeight() " + board.getHeight());
        int cntX = cellFromBluetooth[0].length;
        int cntY = cellFromBluetooth.length;
        try{
            if(board.getWidth()/cntX < board.getHeight()/cntY){
                MainActivity.PADDING = board.getWidth()/cntX;
            } else {
                MainActivity.PADDING = board.getHeight()/cntY;
            }
        }
        catch(ArithmeticException e){
            MainActivity.PADDING = 70;
        }
        Log.d("myLogs", "cntX: in board.post " + cntX);
        Log.d("myLogs", "cntY: in board.post " + cntY);
        if (cntY==0 & cntX==0){cntY = 1; cntX = 1;}
        cells = new Cell[cntY][cntX];
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d("myLogs", "MultiplayerActivity onStart");

        /*** Removing the Number of players from Preference Activity ***/
        int cntPlayer = Integer.valueOf(MainActivity.sharedPref.getString("keyPlayers", ""));
        // Checks setting Number of players = 2 and whether the user miner
        if (cntPlayer==2 &&  MainActivity.btnSend.getVisibility()== View.VISIBLE) {

            // Check changing the settings of "The difficulty level" or "Cell size"
            if( MainActivity.PADDING!=MainActivity.sharedPref.getInt("keySize", 70) ||
                    !MainActivity.prefCountBomb.equals(MainActivity.sharedPref.getString("keyDifficulty", ""))){
                MainActivity.PADDING = MainActivity.sharedPref.getInt("keySize", 70);
                calcCell();
                this.beginGame();
            }
        } else if (cntPlayer==1){
            /*** Start the MainActivity - Activity of single player ***/
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            if (mChatService == null){
                setupChat();
            }
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        Log.d("myLogs", "MultiplayerActivity onResume");
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity
        // returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't
            // started already
            if (mChatService.getState() == MultiplayerService.STATE_NONE) {
                // Start the Bluetooth chat services
                socketView = SOCKET_SERVER;
                mChatService.start();
            }
        }
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new MultiplayerService(this, mHandler);
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        Log.d("myLogs", "MultiplayerActivity onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("myLogs", "MultiplayerActivity onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null)
            mChatService.stop();
        Log.d("myLogs", "MultiplayerActivity onDestroy");
        // Set "Number of players" equal 1
        SharedPreferences.Editor editor = MainActivity.sharedPref.edit();
        editor.putString("keyPlayers", "1");
        editor.apply();
        MainActivity.btnInit.setEnabled(true);
        MainActivity.btnMine.setEnabled(true);
        MainActivity.chbMine = false;
        MainActivity.btnSend.setVisibility(View.INVISIBLE);
        MainActivity.btnbluemenu.setVisibility(View.INVISIBLE);
    }

    protected void ensureDiscoverable() {
        Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(
                    BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BOARD_POST:
                    calcCell();
                    break;

                case MESSAGE_SAPPER_LOSS:
                    sendMessages("Miner won");
                    break;

                case MESSAGE_SAPPER_WIN:
                    sendMessages("Sapper won");
                    break;

                case MESSAGE_STATE_CHANGE:
                    Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        //*** Once the connection is established  ***
                        case MultiplayerService.STATE_CONNECTED:
                            if(socketView==SOCKET_CLIENT){
                                showDialog(DIALOG_CHOICE);
                                Log.d("myLogs", "I am SOCKET_CLIENT");
                            } else if (socketView==SOCKET_SERVER){
                                Log.d("myLogs", "I am SOCKET_SERVER");
                            }
                            break;
                        case MultiplayerService.STATE_CONNECTING:
                            break;
                        case MultiplayerService.STATE_LISTEN:
                        case MultiplayerService.STATE_NONE:
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    break;
                case MESSAGE_READ:
                    //Read received JSON
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    switch (readMessage) {

                        case "Sapper won":
                            showDialog(DIALOG_MINER_LOSS);
                            break;
                        case "Miner won":
                            showDialog(DIALOG_MINER_WON);
                            break;
                        case "You are miner":
                            Toast.makeText(getBaseContext(), R.string.youAreMiner, Toast.LENGTH_LONG).show();
                            MainActivity.btnSend.setVisibility(View.VISIBLE);
                            MainActivity.chbMine = true;
                            MainActivity.btnMine.setEnabled(false);
                            MainActivity.btnInit.setEnabled(true);
                            beginGame();
                            break;
                        default:
                            MainActivity.btnSend.setVisibility(View.INVISIBLE);
                            JSONArray jsonArr;

                            try {
                                jsonArr = new JSONArray(readMessage);
                                cellFromBluetooth = new int[jsonArr.length()][jsonArr.getJSONArray(0).length()];
                                for (int i = 0; i < jsonArr.length(); i++) {
                                    JSONArray jsonArrInside = jsonArr.getJSONArray(i);
                                    for (int j = 0; j < jsonArrInside.length(); j++) {
                                        cellFromBluetooth[i][j] = jsonArrInside.getInt(j);
                                    }
                                }

                            } catch (JSONException e) {
                                cellFromBluetooth = new int[5][5];
                                e.printStackTrace();
                            }
                            for (int i = 0; i < cellFromBluetooth.length; i++) {
                                String tmp = "";
                                for (int j = 0; j < cellFromBluetooth[i].length; j++) {
                                    tmp = tmp + cellFromBluetooth[i][j];
                                }
                                Log.d("myLogs", "Parce received, cellFromBluetooth[" + i + "]: " + tmp); //Read received JSON
                            }
                            calcCellForBluetooth();
                            beginGameFromBluetooth();
                            break;
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    Toast.makeText(getApplicationContext(), R.string.connectTo + msg.getData().getString(DEVICE_NAME),
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(),
                            msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    /**
     * Sends a message.
     *
     * @param message
     *            A string of text to send.
     */
    private void sendMessages(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != MultiplayerService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);
        }
    }

    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        switch (id) {
            case DIALOG_CHOICE:
                adb.setTitle(R.string.choose);
                String[] items = new String[] {getString(R.string.chooseMiner),getString(R.string.chooseSapper)};
                adb.setSingleChoiceItems(items, 0, myClickListener);
                adb.setPositiveButton("OK", myClickListener);
                break;

            case DIALOG_MINER_LOSS:
                adb.setTitle(R.string.loss);
                adb.setPositiveButton("OK", null);
                break;

            case DIALOG_MINER_WON:
                adb.setTitle(R.string.victory);
                adb.setPositiveButton("OK", null);
                break;

            case DIALOG_BLUETOOTH_MENU:
                adb.setTitle("");
                final String[] data = new String[]{getString(R.string.connect), getString(R.string.discoverable)};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                        android.R.layout.select_dialog_item, data);
                adb.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                Intent serverIntent;
                                // Launch the DeviceListActivity to see devices and do scan
                                serverIntent = new Intent(context, DeviceListActivity.class);
                                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                                break;
                            case 1:
                                // Ensure this device is discoverable by others
                                ensureDiscoverable();
                                break;
                        }
                    }
                });
                adb.setNegativeButton(R.string.Cancel, null);
                break;
        }

        return adb.create();
    }

    //*** Handler clicking on the item list or dialog button ***
    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            ListView lv = ((AlertDialog) dialog).getListView();
            if (which == Dialog.BUTTON_POSITIVE){
                // Write to Log position
                Log.d("myLogs", "pos = " + lv.getCheckedItemPosition());
                switch (lv.getCheckedItemPosition()) {
                    case 0: // This user has chosen to be miner - to place mines
                        Toast.makeText(getBaseContext(), R.string.youMiner, Toast.LENGTH_LONG).show();
                        MainActivity.chbMine = true;
                        MainActivity.btnMine.setEnabled(false);
                        MainActivity.btnSend.setVisibility(View.VISIBLE);
                        MainActivity.btnInit.setEnabled(true);
                        beginGame();
                        break;
                    case 1: // This user has chosen to be a sapper - defuse mines
                        Toast.makeText(getBaseContext(), R.string.youSapper, Toast.LENGTH_LONG).show();
                        MainActivity.btnInit.setEnabled(false);
                        sendMessages("You are miner");
                        break;
                }
            }
        }
    };

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            Log.d(TAG, "onActivityResult " + resultCode);
            switch (requestCode) {
                case REQUEST_CONNECT_DEVICE:
                    // When DeviceListActivity returns with a device to connect
                    if (resultCode == Activity.RESULT_OK) {
                        connectDeviceFromMenu(data);
                    }
                    break;
                case REQUEST_ENABLE_BT:
                    // When the request to enable Bluetooth returns
                    if (resultCode == Activity.RESULT_OK) {
                        // Bluetooth is now enabled, so set up a chat session
                        setupChat();
                    } else {
                        // User did not enable Bluetooth or an error occurred
                        Log.d(TAG, "BT not enabled");
                        Toast.makeText(context, R.string.bt_not_enabled_leaving,
                                Toast.LENGTH_SHORT).show();
                        /*** Start MainActivity ***/
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
            }
        }


        private void connectDeviceFromMenu(Intent data) {
            // Get the device MAC address
            String address = data.getExtras().getString(
                    DeviceListActivity.EXTRA_DEVICE_ADDRESS);
            // Get the BluetoothDevice object
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            // Attempt to connect to the device
            socketView = SOCKET_CLIENT;
            mChatService.connect(device);
        }

    private void connectDeviceFromPrefActivity(String address) {
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        socketView = SOCKET_CLIENT;
        mChatService.connect(device);
    }


    public void beginGame(){
            Log.d("myLogs", "generate1() ");
            new GUIAction(
                    new Easy(), board,
                    new GeneratorBoard() {
                        public Cell[][] generate() {
                            Log.d("myLogs", "generate() ");
                            Log.d("myLogs", "generate() Array's Size: " + cells.length + "  " + cells[0].length);
                            for (int i = 0; i < cells.length; i++) {
                                for (int j = 0; j < cells[0].length; j++) {
                                    cells[i][j] = new GUICell(context, false);
                                }
                            }
                            /***  Calculate count of mines, depends from difficulty game ***/
                            MainActivity.sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                            MainActivity.prefCountBomb = MainActivity.sharedPref.getString("keyDifficulty", "");
                           // MainActivity.prefCountBomb = MainActivity.sharedPref.getString("keyDifficulty", "");
                            switch (Integer.parseInt(MainActivity.prefCountBomb)) {
                                case 1:
                                    MainActivity.countBomb = (cells.length * cells[0].length) / 8;
                                    break;
                                case 2:
                                    MainActivity.countBomb = (cells.length * cells[0].length) / 4;
                                    break;
                                case 3:
                                    MainActivity.countBomb = (int) ((cells.length * cells[0].length)/ (2.8));
                                    break;
                            }
                            MainActivity.tvMine.setText("0" + MainActivity.countBomb);
                            return cells;
                        }
                    });
        }

    //*** Start Game on field from Bluetooth***
    public void beginGameFromBluetooth() {
        Log.d("myLogs", "generate1FromBluetooth() ");

        new GUIAction(
                new Easy(), board,
                new GeneratorBoard() {
                    public Cell[][] generate() {
                        board.setHandler(mHandler);
                        Log.d("myLogs", "generateFromBluetooth() Array's Size: " + cells.length + "  " + cells[0].length);
                        MainActivity.countBomb = 0;
                        cells = new Cell [cellFromBluetooth.length] [cellFromBluetooth[0].length];
                        for (int i = 0; i < cells.length; i++) {
                            for (int j = 0; j < cells[0].length; j++) {
                                if(cellFromBluetooth[i][j] == 0) {
                                    cells[i][j] = new GUICell(context, false);
                                } else {
                                    cells[i][j] = new GUICell(context, true);
                                    MainActivity.countBomb++;
                                }
                            }
                        }
                        MainActivity.tvMine.setText("0" + MainActivity.countBomb);
                        return cells;
                    }
                });
    }

        @Override
        public void onClick(View v) {
            Log.d("myLogs", "onClick() ");

            switch (v.getId()) {
                case R.id.btnMenu:
                    /*** Launch Preference Activity ***/
                    Intent intent = new Intent(context, PrefActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btnInit:
                    /*** Start new game ***/
                    this.beginGame();
                    break;
                case R.id.btnMine:
                    if (MainActivity.chbMine) {
                        MainActivity.chbMine = false;
                        /*** Set checking mines ***/
                        MainActivity.btnMine.setImageResource(R.drawable.chbmine_notchecked);
                    } else {
                        MainActivity.chbMine = true;
                        /*** Set checking empty cells ***/
                        MainActivity.btnMine.setImageResource(R.drawable.chbmine_checked);
                    }
                    break;
                case R.id.btnSend:
                    JSONArray cellArray;
                    String sendArray = "[";
                    for (int i = 0; i < cells.length; i++) {
                        cellArray = new JSONArray();
                        for (int j = 0; j < cells[0].length; j++) {
                            if (cells[i][j].isSuggestBomb()){
                                cellArray.put(1);
                            } else {
                                cellArray.put(0);
                            }
                        }
                        if (i!= cells.length-1){
                            sendArray = sendArray + cellArray.toString() + ",";
                        } else {
                            sendArray = sendArray + cellArray.toString() + "]";
                        }
                        Log.d("myLogs", "cellArray" + i + " " + cellArray.toString());
                    }
                    sendMessages(sendArray);
                    break;

                case R.id.btnbluemenu:
                    showDialog(DIALOG_BLUETOOTH_MENU);
                    break;
            }
        }
}