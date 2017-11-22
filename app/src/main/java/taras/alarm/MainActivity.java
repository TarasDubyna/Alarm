package taras.alarm;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    NotificationManager mNotificationManager;


    TextView mTextTime;

    Button mBtnStart;
    Button mBtnStop;
    Button mBtnClear;
    Button mBtnLoop;

    public final static String FILE_NAME = "filename";

    public final static String BROADCAST_ACTION = "StopwatchServiceBroadcast";

    long updateTime;
    long startTime;
    long timeBuff;

    private boolean isStopwatchPaused;
    private boolean isStopwatchStopped;

    IntentFilter mIntentFilter;
    ServiceConnection serviceConnection;
    boolean bound = false;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(BROADCAST_ACTION)){
                updateTime = intent.getLongExtra(StopwatchService.UPDATE_TIME_LONG, 0);
                timeBuff = intent.getLongExtra(StopwatchService.TIME_BUFF_LONG, 0);

                isStopwatchPaused = intent.getBooleanExtra(StopwatchService.STOPWATCH_PAUSED, false);
                isStopwatchStopped = intent.getBooleanExtra(StopwatchService.STOPWATCH_END, false);

                updateTime(updateTime);

                if (isStopwatchPaused){
                    mBtnStart.setVisibility(View.VISIBLE);
                    mBtnClear.setVisibility(View.VISIBLE);
                    mBtnLoop.setVisibility(View.GONE);
                    mBtnStop.setVisibility(View.GONE);
                } else {
                    mBtnStart.setVisibility(View.GONE);
                    mBtnClear.setVisibility(View.GONE);
                    mBtnLoop.setVisibility(View.VISIBLE);
                    mBtnStop.setVisibility(View.VISIBLE);
                }
                if (isStopwatchStopped){
                    mBtnStart.setVisibility(View.VISIBLE);
                    mBtnClear.setVisibility(View.GONE);
                    mBtnLoop.setVisibility(View.GONE);
                    mBtnStop.setVisibility(View.GONE);
                }
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.initViews();
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.d("MyLogs", "MainActivity onStopwatchService connected");
                bound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.d("MyLogs", "MainActivity onStopwatchService connected");
                bound = false;
            }
        };

        if (isMyServiceRunning(StopwatchService.class)){
            Intent intent = new Intent(this, StopwatchService.class);
            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isMyServiceRunning(StopwatchService.class)) {
            mTextTime.setText((CharSequence) getFromSharedPreference("last_time"));
            mBtnClear.setVisibility(View.VISIBLE);
        } else {
            mTextTime.setText("00:00:000");
        }
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BROADCAST_ACTION);
        registerReceiver(broadcastReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        addToSharedPreference("last_time", mTextTime.getText().toString());
        addToSharedPreference("is_paused", isStopwatchPaused);
        addToSharedPreference("is_stopped", isStopwatchStopped);
        super.onDestroy();
    }

    private void initViews(){

        mTextTime = (TextView) findViewById(R.id.textview_time);

        mBtnStart = (Button) findViewById(R.id.btn_start);
        mBtnStop = (Button) findViewById(R.id.btn_stop);
        mBtnLoop = (Button) findViewById(R.id.btn_loop);
        mBtnClear = (Button) findViewById(R.id.btn_clear);

        mBtnStart.setOnClickListener(this);
        mBtnClear.setOnClickListener(this);
        mBtnLoop.setOnClickListener(this);
        mBtnStop.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_start:
                mBtnStart.setVisibility(View.GONE);
                mBtnClear.setVisibility(View.GONE);
                mBtnStop.setVisibility(View.VISIBLE);
                mBtnLoop.setVisibility(View.VISIBLE);
                Intent startService = new Intent(this, StopwatchService.class);
                if (timeBuff != 0){
                    startService.putExtra(StopwatchService.TIME_BUFF_LONG, startTime);
                }
                if (!isMyServiceRunning(StopwatchService.class)){
                    startService(startService);
                } else {
                    startService.putExtra(StopwatchService.STOPWATCH_PAUSED, false);
                    startService(startService);
                }
                isStopwatchPaused = false;
                isStopwatchStopped = false;
                break;
            case R.id.btn_stop:
                addToSharedPreference("last_value", mTextTime.getText().toString());
                Intent pauseService = new Intent(this, StopwatchService.class);
                pauseService.putExtra(StopwatchService.STOPWATCH_PAUSED, true);
                if (mTextTime.getText().equals(getResources().getString(R.string.default_time))){
                    pauseService.putExtra(StopwatchService.STOPWATCH_END, true);
                }
                startService(pauseService);

                isStopwatchStopped = false;
                isStopwatchPaused = true;
                break;
            case R.id.btn_loop:
                Toast.makeText(this, "Take loop!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_clear:
                mBtnClear.setVisibility(GONE);
                mTextTime.setText(getResources().getString(R.string.default_time));
                Intent clearService = new Intent(this, StopwatchService.class);
                isStopwatchPaused = true;
                isStopwatchStopped = true;
                clearService.putExtra(StopwatchService.STOPWATCH_CLEAR, true);
                clearService.putExtra(StopwatchService.STOPWATCH_PAUSED, isStopwatchPaused);
                clearService.putExtra(StopwatchService.STOPWATCH_END, isStopwatchStopped);
                stopService(clearService);
                break;
        }
    }

    public Object getFromSharedPreference(String type){
        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        Object result = null;
        if (type.equals("last_time")){
            result = sharedPreferences.getString(type,"");
            return result;
        } else if (type.equals("is_running")){
            result = sharedPreferences.getBoolean(type,false);
            return result;
        } else {
            return result;
        }
    }
    public void addToSharedPreference(String type, Object value){
        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        switch (value.getClass().getName()){
            case "java.lang.String":
                String text = (String) value;
                editor.putString(type, (String) value);
                editor.apply();
                break;
            case "java.lang.Long":
                editor.putLong(type, (Long) value);
                editor.apply();
                break;
            case "java.lang.Boolean":
                editor.putBoolean(type, (Boolean) value);
                editor.apply();
                break;
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void updateTime(long updateTime){
        String time;
        if (updateTime == 0){
            time = getResources().getString(R.string.default_time);
        } else {
            int seconds = (int) (updateTime / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            int milliSeconds = (int) (updateTime % 1000);
            time = String.format("%02d:%02d:%02d", minutes, seconds, milliSeconds);
        }
        mTextTime.setText(time);
    }

}
