package taras.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Chronometer.OnChronometerTickListener {

    NotificationManager mNotificationManager;



    private static final int uniqueID = 45612;

    TextView mTextTime;
    Chronometer mChronometer;

    Button mBtnStart;
    Button mBtnStop;
    Button mBtnClear;

    public final static String FILE_NAME = "filename";

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.initViews();
    }

    private void initViews(){

        mChronometer = (Chronometer) findViewById(R.id.textview_time);
        mChronometer.setOnChronometerTickListener(this);

        mTextTime = (TextView) findViewById(R.id.textview_time);
        mBtnStart = (Button) findViewById(R.id.btn_start);
        mBtnClear = (Button) findViewById(R.id.btn_clear);
        mBtnStop = (Button) findViewById(R.id.btn_stop);

        mBtnStart.setOnClickListener(this);
        mBtnClear.setOnClickListener(this);
        mBtnStop.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_start:
                mChronometer.setBase(SystemClock.elapsedRealtime());
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
                addNotification();
                break;
            case R.id.btn_clear:
                mChronometer.setBase(SystemClock.elapsedRealtime());
                break;
            case R.id.btn_stop:
                mChronometer.stop();
                mNotificationManager.cancel(0);
                break;
        }
    }

    @Override
    public void onChronometerTick(Chronometer chronometer) {
        long elapsedMillis = SystemClock.elapsedRealtime()
                - mChronometer.getBase();

        if (elapsedMillis > 5000) {
            String strElapsedMillis = "Прошло больше 5 секунд";
            Toast.makeText(getApplicationContext(),
                    strElapsedMillis, Toast.LENGTH_SHORT)
                    .show();
        }
    }


    private void addNotification(){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.ic_notification_clear_all)
                .setContentTitle("My notification")
                .setContentText("Hello World!")
                .setAutoCancel(false);

        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.getApplicationContext().startActivity(resultIntent);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

}
