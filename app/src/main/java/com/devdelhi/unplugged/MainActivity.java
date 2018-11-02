package com.devdelhi.unplugged;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private View mainLayout;
    private boolean usbSwitchStatus, headsetSwitchStatus = false;
    private Switch headsetSwitch, usbSwitch;
    private BroadcastReceiver myHeadsetReceiver, myUSBReceiver;
    private TextView USBTextStatus, headsetTextStatus, mainTextView;
    private MediaPlayer mediaPlayer;
    private String TAG = "DEEJAY";
    private Vibrator mVibrator;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainTextView = findViewById(R.id.mainText);
        headsetSwitch = findViewById(R.id.headsetSwitch);
        usbSwitch = findViewById(R.id.usbSwitch);
        USBTextStatus = findViewById(R.id.usbTextStatus);
        headsetTextStatus =  findViewById(R.id.headsetTextStatus);
        mainLayout = findViewById(R.id.mainLayout);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        sharedPreferences = getSharedPreferences("ShaPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean firstTime = sharedPreferences.getBoolean("first", true);
        if (firstTime) {
            editor.putBoolean("first", false);
            editor.commit();
            Intent intent = new Intent(MainActivity.this, TutorialActivity.class);
            startActivity(intent);
            finish();
        }

        checkLayoutColor();

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        myUSBReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

                if (plugged == BatteryManager.BATTERY_PLUGGED_AC) {
                    acChargerPluggedIn();
                    //CHARGER PLUGGED IN
                    stopAlarmSound();

                } else if (plugged == BatteryManager.BATTERY_PLUGGED_USB) {
                    usbPluggedIn();
                    //USB PLUGGED IN
                    stopAlarmSound();

                } else if (plugged == 0) {
                    onBattery();
                    //ON BATTERY

                } else {
                    cannotDetect();
                }
            }
        };
        final IntentFilter myUSBFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        final IntentFilter myHeadsetFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);

        myHeadsetReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                    int state = intent.getIntExtra("state", -1);

                    switch (state) {
                        case 0:
                            headsetPluggedOut();
                            break;
                        case 1:
                            headsetPluggedIn();
                            break;
                    }
                }
            }
        };

        usbSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean status) {
                if (status) {
                    USBTextStatus.setAlpha(1);
                    usbSwitchStatus = true;
                    USBTextStatus.setVisibility(View.VISIBLE);
                    if (isConnected(getApplicationContext())) {
                        ActivateUSBAlarm(myUSBFilter);
                    } else {
                        status = false;
                        usbSwitchStatus = false;
                        usbSwitch.setChecked(false);
                        USBTextStatus.setVisibility(View.VISIBLE);
                        USBTextStatus.setAlpha(1);
                        USBTextStatus.setText("Connect Your Device To USB First");
                        USBTextStatus.setBackgroundColor(Color.parseColor("#AAF62A00"));
                        USBTextStatus.setTextColor(Color.parseColor("#FFFFFF"));
                        USBTextStatus.postDelayed(new Runnable() {
                            public void run() {
                                USBTextStatus.
                                        animate()
                                        .alpha(0.0f)
                                        .setDuration(1000);
                            }
                        }, 3000);
                    }
                } else {
                    deActiviateUSBAlarm();
                    usbSwitchStatus = false;
                    if(headsetSwitchStatus) {
                        USBTextStatus.setVisibility(View.INVISIBLE);
                    }
                    else {
                        USBTextStatus.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        USBTextStatus.setTextColor(Color.parseColor("#FFFFFF"));
                        USBTextStatus.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        headsetSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean status) {
                if (status) {
                    headsetSwitchStatus = true;
                    headsetTextStatus.setAlpha(1);
                    headsetTextStatus.setVisibility(View.VISIBLE);
                    //TODO CHECK IF HEADPHONE IS CONNECTED FIRST
                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    if (audioManager.isWiredHeadsetOn()) {
                        activateHeadsetAlarm(myHeadsetFilter);
                    } else {
                        status = false;
                        headsetSwitchStatus = false;
                        headsetSwitch.setChecked(false);
                        headsetTextStatus.setVisibility(View.VISIBLE);
                        headsetTextStatus.setAlpha(1);
                        headsetTextStatus.setText("Connect Your Earphones First");
                        headsetTextStatus.setBackgroundColor(Color.parseColor("#AAF62A00"));
                        headsetTextStatus.setTextColor(Color.parseColor("#FFFFFF"));
                        headsetTextStatus.postDelayed(new Runnable() {
                            public void run() {
                                headsetTextStatus.
                                        animate()
                                        .alpha(0.0f)
                                        .setDuration(1000);
                            }
                        }, 3000);
                    }
                } else {
                    deActivateHeadsetAlarm();
                    headsetSwitchStatus = false;
                    if(usbSwitchStatus) {
                        headsetTextStatus.setVisibility(View.INVISIBLE);
                    }
                    else {
                        headsetTextStatus.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        headsetTextStatus.setTextColor(Color.parseColor("#FFFFFF"));
                        headsetTextStatus.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    private void deActivateHeadsetAlarm() {
        headsetSwitchStatus = false;
        unRegisterHeadset(myHeadsetReceiver);
        headsetTextStatus.setText("");
        checkLayoutColor();
    }

    private void activateHeadsetAlarm(IntentFilter myHeadsetFilter) {
        headsetSwitchStatus = true;
        registerHeadset(myHeadsetReceiver, myHeadsetFilter);
    }

    private void deActiviateUSBAlarm() {
        usbSwitchStatus = false;
        unRegisterUSB(myUSBReceiver);
        USBTextStatus.setText("");
        checkLayoutColor();
    }

    private void ActivateUSBAlarm(IntentFilter myUSBFilter) {
        usbSwitchStatus = true;
        registerUSB(myUSBReceiver, myUSBFilter);
    }

    public static boolean isConnected(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
    }

    private void checkLayoutColor() {
        if ((!usbSwitchStatus) && (!headsetSwitchStatus)) {
            changeTextColorToBlack();
            mainLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            USBTextStatus.setVisibility(View.INVISIBLE);
            headsetTextStatus.setVisibility(View.INVISIBLE);
        }
    }

    //FUNCTIONALITY METHODS

    private void changeTextColorToBlack() {
        USBTextStatus.setTextColor(getResources().getColor(R.color.blackColor));
        usbSwitch.setTextColor(getResources().getColor(R.color.blackColor));
        headsetSwitch.setTextColor(getResources().getColor(R.color.blackColor));
        mainTextView.setTextColor(getResources().getColor(R.color.blackColor));
        headsetTextStatus.setTextColor(getResources().getColor(R.color.blackColor));
    }

    private void changeTextColorToWhite() {
        USBTextStatus.setTextColor(getResources().getColor(R.color.whiteColor));
        usbSwitch.setTextColor(getResources().getColor(R.color.whiteColor));
        headsetSwitch.setTextColor(getResources().getColor(R.color.whiteColor));
        mainTextView.setTextColor(getResources().getColor(R.color.whiteColor));
        headsetTextStatus.setTextColor(getResources().getColor(R.color.whiteColor));
    }

    private void onBattery() {
        changeTextColorToWhite();
        mainLayout.setBackgroundColor(Color.parseColor("#AAF62A00"));
        USBTextStatus.setText("USB Plugged Out");
        USBTextStatus.setBackgroundColor(Color.parseColor("#AAF62A00"));
        Log.v(TAG, "USB Plugged OUT");
        startAlarmSound();
    }

    private void usbPluggedIn() {
        changeTextColorToWhite();
        mainLayout.setBackgroundColor(Color.parseColor("#AA6AB187"));
        USBTextStatus.setText("USB Plugged In");
        USBTextStatus.setBackgroundColor(Color.parseColor("#AA6AB187"));
        Log.v(TAG, "USB Plugged IN");
        stopAlarmSound();
    }

    private void acChargerPluggedIn() {
        changeTextColorToWhite();
        mainLayout.setBackgroundColor(Color.parseColor("#AA6AB187"));
        USBTextStatus.setText("Charger Plugged In");
        USBTextStatus.setBackgroundColor(Color.parseColor("#AA6AB187"));
        Log.v(TAG, "Charger Plugged IN");
        stopAlarmSound();
    }

    private void cannotDetect() {
        USBTextStatus.setText("Cannot Detect Status");
        changeTextColorToBlack();
        USBTextStatus.setBackgroundColor(Color.parseColor("#FFFFFF"));
        headsetTextStatus.setBackgroundColor(Color.parseColor("#FFFFFF"));
    }

    private void headsetPluggedIn() {
        changeTextColorToWhite();
        mainLayout.setBackgroundColor(Color.parseColor("#AA6AB187"));
        headsetTextStatus.setBackgroundColor(Color.parseColor("#AA6AB187"));
        headsetTextStatus.setText("Headsets Plugged In");
        Log.v(TAG, "Headsets Plugged IN");
        stopAlarmSound();
    }

    private void headsetPluggedOut() {
        changeTextColorToWhite();
        mainLayout.setBackgroundColor(Color.parseColor("#AAF62A00"));
        headsetTextStatus.setText("Headsets Plugged Out");
        headsetTextStatus.setBackgroundColor(Color.parseColor("#AAF62A00"));
        headsetTextStatus.setTextColor(Color.parseColor("#FFFFFF"));
        Log.v(TAG, "Headsets Plugged OUT");
        startAlarmSound();
    }

    //STARTING and STOPPING ALARM

    private void startAlarmSound() {

        mVibrator.vibrate(10 * 60 * 1000);

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        //Maximising Volume
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        //Play From LoudSpeaker
        am.setSpeakerphoneOn(true);
        if (am.isWiredHeadsetOn()) {
            am.setWiredHeadsetOn(false);
            am.setRouting(AudioManager.MODE_CURRENT, AudioManager.ROUTE_SPEAKER, AudioManager.ROUTE_ALL);
            am.setMode(AudioManager.MODE_CURRENT);
            am.setSpeakerphoneOn(true);
        }

        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.alarm);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    private void stopAlarmSound() {
        mVibrator.cancel();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    //BROADCAST RECEIVERS

    private void registerUSB(BroadcastReceiver receiver, IntentFilter filter) {
        registerReceiver(receiver, filter);
    }

    private void unRegisterUSB(BroadcastReceiver receiver) {
        unregisterReceiver(receiver);
    }

    private void registerHeadset(BroadcastReceiver receiver, IntentFilter filter) {
        registerReceiver(receiver, filter);
    }

    private void unRegisterHeadset(BroadcastReceiver receiver) {
        unregisterReceiver(receiver);
    }

    //OTHER METHODS
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_share) {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Share This App", Snackbar.LENGTH_LONG);
            snackbar.show();

        } else if (id == R.id.nav_send) {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Opening Your Mail Service", Snackbar.LENGTH_LONG);
            snackbar.show();
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:delhidev10@gmail.com"));
            startActivity(emailIntent);
        } else if (id == R.id.nav_website) {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Opening Your Browser", Snackbar.LENGTH_LONG);
            snackbar.show();
            Intent websiteIntent = new Intent(Intent.ACTION_VIEW);
            websiteIntent.setData(Uri.parse("http://www.devdelhi.com"));
            startActivity(websiteIntent);
        } else if (id == R.id.nav_rate) {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Rate This App", Snackbar.LENGTH_LONG);
            snackbar.show();
        } else if (id == R.id.nav_tutorial) {
            startActivity(new Intent(MainActivity.this, TutorialActivity.class));
        }
        else if (id == R.id.nav_tips) {
                startActivity(new Intent(MainActivity.this, MoreTipsActivity.class));
        } else if (id == R.id.nav_terms) {
            startActivity(new Intent(MainActivity.this, TermsActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
