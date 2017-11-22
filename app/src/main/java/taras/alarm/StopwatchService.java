package taras.alarm;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class StopwatchService extends Service {

    NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;

    Notification notification;

    private long startTime;
    private long millisecondTime;
    private long timeBuff;
    private long updateTime;

    Intent broadcastIntent;
    Handler handler;

    private boolean isEnd = false;
    private boolean isPaused = false;

    public final static String UPDATE_TIME_LONG = "service_update_time";
    public final static String START_TIME_LONG = "service_start_time";
    public final static String TIME_BUFF_LONG = "service_time_buff";


    public final static String STOPWATCH_PAUSED = "service_stopwatch_paused";
    public final static String STOPWATCH_END = "service_stopwatch_end";
    public final static String STOPWATCH_CLEAR = "service_stopwatch_clear";

    public final static int SERVICE_ID = 101;


    @Override
    public void onCreate() {
        super.onCreate();

        broadcastIntent = new Intent();
        broadcastIntent.setAction(MainActivity.BROADCAST_ACTION);

        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.ic_notification_clear_all)
                .setContentTitle("Stopwatch Service")
                .setTicker("Stopwatch");

        notification = mBuilder.build();
        startForeground(SERVICE_ID, notification);

        startTime = 0L;
        millisecondTime = 0L;
        timeBuff = 0L;
        updateTime = 0L;

        handler = new Handler();
        handler.postDelayed(runnable, 0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null){
            return Service.START_NOT_STICKY;
        }

        isEnd = intent.getBooleanExtra(STOPWATCH_END, false);
        isPaused = intent.getBooleanExtra(STOPWATCH_PAUSED, false);

        if (intent.getBooleanExtra(STOPWATCH_CLEAR, false)){
            startTime = 0L;
            millisecondTime = 0L;
            timeBuff = 0L;
            updateTime = 0L;
        }


        if (isPaused){
            timeBuff += millisecondTime;
            handler.removeCallbacks(runnable);

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(MainActivity.BROADCAST_ACTION);
            broadcastIntent.putExtra(STOPWATCH_PAUSED, isPaused);
            broadcastIntent.putExtra(STOPWATCH_END, isEnd);
            broadcastIntent.putExtra(UPDATE_TIME_LONG, updateTime);
            sendBroadcast(broadcastIntent);

            if (isEnd){
                onDestroy();
            }
        } else {
            //addNotification();
            startTime = SystemClock.uptimeMillis();
            handler.postDelayed(runnable, 0);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (isEnd){
            handler.removeCallbacks(runnable);
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(MainActivity.BROADCAST_ACTION);
            broadcastIntent.putExtra(UPDATE_TIME_LONG, 0);
            broadcastIntent.putExtra(STOPWATCH_PAUSED, isPaused);
            broadcastIntent.putExtra(STOPWATCH_END, isEnd);
            sendBroadcast(broadcastIntent);
            startTime = 0L;
            millisecondTime = 0L;
            timeBuff = 0L;
            updateTime = 0L;
            stopForeground(true);
            super.onDestroy();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }


    public Runnable runnable = new Runnable() {
        @Override
        public void run() {

            millisecondTime = SystemClock.uptimeMillis() - startTime; // прошедшее время на текущий момент
            updateTime = timeBuff + millisecondTime;

            int seconds = (int) (updateTime / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            int milliSeconds = (int) (updateTime % 1000);

            String time = String.format("%02d:%02d:%02d", minutes, seconds, milliSeconds);


            mBuilder.setContentText(time);

            notification = mBuilder.build();
            startForeground(SERVICE_ID, notification);

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(MainActivity.BROADCAST_ACTION);
            broadcastIntent.putExtra(UPDATE_TIME_LONG, updateTime);
            broadcastIntent.putExtra(STOPWATCH_PAUSED, false);
            broadcastIntent.putExtra(STOPWATCH_END, false);
            sendBroadcast(broadcastIntent);

            handler.postDelayed(this, 0);
        }
    };
}
