package com.example.wchen.trackpack;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.UUID;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Button btnONOFF = (Button) findViewById(R.id.btnONOFF);

        btnEnableDisable_Discoverable = (Button) findViewById(R.id.btnDiscoverable_on_off);
        lvNewDevices = (ListView) findViewById(R.id.lvNewDevices);
        mBTDevices = new ArrayList<>();

        btnStartConnection = (Button) findViewById(R.id.btnStartConnection);
        //btnSend = (Button) findViewById(R.id.btnSend);
        etSend = (EditText) findViewById(R.id.editText);

        //Broadcasts when bond state changes (ie:pairing)
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        lvNewDevices.setOnItemClickListener((AdapterView.OnItemClickListener) MainActivity.this);


        btnONOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: enabling/disabling bluetooth.");
                enableDisableBT();
            }
        });

        btnStartConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startConnection();
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getFragmentManager();

        if (id == R.id.nav_my_schedule) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame
                            , new MySchedule())
                    .commit();
        } else if (id == R.id.nav_connections) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame
                            , new Connections())
                    .commit();
        } else if (id == R.id.nav_tags) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame
                            , new Tags())
                    .commit();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /////
    private static final String TAG = "MainActivity";
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mBTDevice;
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;
    ListView lvNewDevices;
    Button btnEnableDisable_Discoverable;
    Button btnStartConnection;
    EditText etSend;
    BluetoothConnectionService mBluetoothConnection;
    private final UUID MY_UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    public void btnEnableDisable_Discoverable(View view) {
        if(view.getId()  == R.id.btnDiscoverable_on_off) {
            Log.d(TAG, "btnEnableDisable_Discoverable is clicked.");
            Log.d(TAG, "btnEnableDisable_Discoverable: Making device discoverable for 300 seconds.");

            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);

            IntentFilter intentFilter = new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
            registerReceiver(mBroadcastReceiver2, intentFilter);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void btnDiscover(View view) {
        if(view.getId()  == R.id.btnFindUnpairedDevices) {
            Log.d(TAG, "btnDiscover is clicked.");
            Log.d(TAG, "btnDiscover: Looking for unpaired devices.");

            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
                Log.d(TAG, "btnDiscover: Canceling discovery.");

                //check BT permissions in manifest
                checkBTPermissions();

                mBluetoothAdapter.startDiscovery();
                IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
            }
            if (!mBluetoothAdapter.isDiscovering()) {

                //check BT permissions in manifest
                checkBTPermissions();

                mBluetoothAdapter.startDiscovery();
                IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
            }
        }
    }

    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };


    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }

            }
        }
    };

    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                lvNewDevices.setAdapter(mDeviceListAdapter);
            }
        }
    };


    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                    //inside BroadcastReceiver4
                    mBTDevice = mDevice;
                }
                //case2: creating a bone
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        } else {
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

//    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
//    @Override


    public void enableDisableBT() {
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "enableDisableBT: enabling BT.");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
        if (mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "enableDisableBT: disabling BT.");
            mBluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }

    }

    public void startConnection() {
        startBTConnection(mBTDevice, MY_UUID_INSECURE);
    }

    public void startBTConnection(BluetoothDevice device, UUID uuid) {
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection.");

        mBluetoothConnection.startClient(device, uuid);
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //first cancel discovery because its very memory intensive.
        mBluetoothAdapter.cancelDiscovery();

        Log.d(TAG, "onItemClick: You Clicked on a device.");
        String deviceName = mBTDevices.get(i).getName();
        String deviceAddress = mBTDevices.get(i).getAddress();

        Log.d(TAG, "onItemClick: deviceName = " + deviceName);
        Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

        //create the bond.
        //NOTE: Requires API 17+? I think this is JellyBean
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Log.d(TAG, "Trying to pair with " + deviceName);
            mBTDevices.get(i).createBond();

            mBTDevice = mBTDevices.get(i);
            mBluetoothConnection = new BluetoothConnectionService(MainActivity.this);
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);
        unregisterReceiver(mBroadcastReceiver2);
        unregisterReceiver(mBroadcastReceiver3);
        unregisterReceiver(mBroadcastReceiver4);
        //mBluetoothAdapter.cancelDiscovery();
    }


//////


//    class Main extends AppCompatActivity implements AdapterView.OnItemClickListener {
//        private static final String TAG = "Main";
//
//        BluetoothAdapter mBluetoothAdapter;
//
//
//        Button btnEnableDisable_Discoverable;
//
//
//        BluetoothConnectionService mBluetoothConnection;
//
//        Button btnStartConnection;
//        //Button btnSend;
//
//        EditText etSend;
//
//        private final UUID MY_UUID_INSECURE =
//                UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//
//        BluetoothDevice mBTDevice;
//
//        public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
//
//        public DeviceListAdapter mDeviceListAdapter;
//
//        ListView lvNewDevices;


        // Create a BroadcastReceiver for ACTION_FOUND
//        private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
//            public void onReceive(Context context, Intent intent) {
//                String action = intent.getAction();
//                // When discovery finds a device
//                if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
//                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);
//
//                    switch (state) {
//                        case BluetoothAdapter.STATE_OFF:
//                            Log.d(TAG, "onReceive: STATE OFF");
//                            break;
//                        case BluetoothAdapter.STATE_TURNING_OFF:
//                            Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
//                            break;
//                        case BluetoothAdapter.STATE_ON:
//                            Log.d(TAG, "mBroadcastReceiver1: STATE ON");
//                            break;
//                        case BluetoothAdapter.STATE_TURNING_ON:
//                            Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
//                            break;
//                    }
//                }
//            }
//        };

        /**
         * Broadcast Receiver for changes made to bluetooth states such as:
         * 1) Discoverability mode on/off or expire.
         */
//        private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {
//
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                final String action = intent.getAction();
//
//                if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
//
//                    int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);
//
//                    switch (mode) {
//                        //Device is in Discoverable Mode
//                        case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
//                            Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
//                            break;
//                        //Device not in discoverable mode
//                        case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
//                            Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
//                            break;
//                        case BluetoothAdapter.SCAN_MODE_NONE:
//                            Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
//                            break;
//                        case BluetoothAdapter.STATE_CONNECTING:
//                            Log.d(TAG, "mBroadcastReceiver2: Connecting....");
//                            break;
//                        case BluetoothAdapter.STATE_CONNECTED:
//                            Log.d(TAG, "mBroadcastReceiver2: Connected.");
//                            break;
//                    }
//
//                }
//            }
//        };


        /**
         * Broadcast Receiver for listing devices that are not yet paired
         * -Executed by btnDiscover() method.
         */
//        private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
//            @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                final String action = intent.getAction();
//                Log.d(TAG, "onReceive: ACTION FOUND.");
//
//                if (action.equals(BluetoothDevice.ACTION_FOUND)) {
//                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                    mBTDevices.add(device);
//                    Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
//                    mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
//                    lvNewDevices.setAdapter(mDeviceListAdapter);
//                }
//            }
//        };

        /**
         * Broadcast Receiver that detects bond state changes (Pairing status changes)
         */
//        private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
//            @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                final String action = intent.getAction();
//
//                if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
//                    BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                    //3 cases:
//                    //case1: bonded already
//                    if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
//                        Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
//                        //inside BroadcastReceiver4
//                        mBTDevice = mDevice;
//                    }
//                    //case2: creating a bone
//                    if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
//                        Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
//                    }
//                    //case3: breaking a bond
//                    if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
//                        Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
//                    }
//                }
//            }
//        };


//        @Override
//        protected void onDestroy() {
//            Log.d(TAG, "onDestroy: called.");
//            super.onDestroy();
//            unregisterReceiver(mBroadcastReceiver1);
//            unregisterReceiver(mBroadcastReceiver2);
//            unregisterReceiver(mBroadcastReceiver3);
//            unregisterReceiver(mBroadcastReceiver4);
//            //mBluetoothAdapter.cancelDiscovery();
//        }


//        @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
//        @Override
//        protected void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            setContentView(R.layout.activity_main);
//            Button btnONOFF = (Button) findViewById(R.id.btnONOFF);
//
//            btnEnableDisable_Discoverable = (Button) findViewById(R.id.btnDiscoverable_on_off);
//            lvNewDevices = (ListView) findViewById(R.id.lvNewDevices);
//            mBTDevices = new ArrayList<>();
//
//            btnStartConnection = (Button) findViewById(R.id.btnStartConnection);
//            //btnSend = (Button) findViewById(R.id.btnSend);
//            etSend = (EditText) findViewById(R.id.editText);
//
//            //Broadcasts when bond state changes (ie:pairing)
//            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
//            registerReceiver(mBroadcastReceiver4, filter);
//
//            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
//            lvNewDevices.setOnItemClickListener(MainActivity.Main.this);
//
//
//            btnONOFF.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Log.d(TAG, "onClick: enabling/disabling bluetooth.");
//                    enableDisableBT();
//                }
//            });
//
//            btnStartConnection.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    startConnection();
//                }
//            });
//
////            btnSend.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View view) {
////                    byte[] bytes = etSend.getText().toString().getBytes(Charset.defaultCharset());
////                    mBluetoothConnection.write(bytes);
////                }
////            });
//        }

        //create method for starting connection
//***remember the conncction will fail and app will crash if you haven't paired first
//        public void startConnection() {
//            startBTConnection(mBTDevice, MY_UUID_INSECURE);
//        }

        /**
         * starting chat service method
         */
//        public void startBTConnection(BluetoothDevice device, UUID uuid) {
//            Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection.");
//
//            mBluetoothConnection.startClient(device, uuid);
//        }


//        public void enableDisableBT() {
//            if (mBluetoothAdapter == null) {
//                Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
//            }
//            if (!mBluetoothAdapter.isEnabled()) {
//                Log.d(TAG, "enableDisableBT: enabling BT.");
//                Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivity(enableBTIntent);
//
//                IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
//                registerReceiver(mBroadcastReceiver1, BTIntent);
//            }
//            if (mBluetoothAdapter.isEnabled()) {
//                Log.d(TAG, "enableDisableBT: disabling BT.");
//                mBluetoothAdapter.disable();
//
//                IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
//                registerReceiver(mBroadcastReceiver1, BTIntent);
//            }
//
//        }

//        @RequiresApi(api = Build.VERSION_CODES.M)
//        public void btnDiscover(View view) {
//            Log.d(TAG, "btnDiscover: Looking for unpaired devices.");
//
//            if (mBluetoothAdapter.isDiscovering()) {
//                mBluetoothAdapter.cancelDiscovery();
//                Log.d(TAG, "btnDiscover: Canceling discovery.");
//
//                //check BT permissions in manifest
//                checkBTPermissions();
//
//                mBluetoothAdapter.startDiscovery();
//                IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//                registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
//            }
//            if (!mBluetoothAdapter.isDiscovering()) {
//
//                //check BT permissions in manifest
//                checkBTPermissions();
//
//                mBluetoothAdapter.startDiscovery();
//                IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//                registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
//            }
//        }

        /**
         * This method is required for all devices running API23+
         * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
         * in the manifest is not enough.
         * <p>
         * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
         */
//        @RequiresApi(api = Build.VERSION_CODES.M)
//        private void checkBTPermissions() {
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
//                int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
//                permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
//                if (permissionCheck != 0) {
//
//                    this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
//                }
//            } else {
//                Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
//            }
//        }

//        @Override
//        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//            //first cancel discovery because its very memory intensive.
//            mBluetoothAdapter.cancelDiscovery();
//
//            Log.d(TAG, "onItemClick: You Clicked on a device.");
//            String deviceName = mBTDevices.get(i).getName();
//            String deviceAddress = mBTDevices.get(i).getAddress();
//
//            Log.d(TAG, "onItemClick: deviceName = " + deviceName);
//            Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);
//
//            //create the bond.
//            //NOTE: Requires API 17+? I think this is JellyBean
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
//                Log.d(TAG, "Trying to pair with " + deviceName);
//                mBTDevices.get(i).createBond();
//
//                mBTDevice = mBTDevices.get(i);
//                mBluetoothConnection = new BluetoothConnectionService(MainActivity.Main.this);
//            }
//        }

//        public void btnEnableDisable_Discoverable(View view) {
//            if(view.getId()  == R.id.btnDiscoverable_on_off) {
//                Log.d(TAG, "btnEnableDisable_Discoverable is clicked.");
//                Log.d(TAG, "btnEnableDisable_Discoverable: Making device discoverable for 300 seconds.");
//
//                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//                startActivity(discoverableIntent);
//
//                IntentFilter intentFilter = new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
//                registerReceiver(mBroadcastReceiver2, intentFilter);
//            }
//        }
//    }



//////////


}


