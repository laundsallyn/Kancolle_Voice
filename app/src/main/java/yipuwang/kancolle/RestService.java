package yipuwang.kancolle;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

public class RestService extends Service {
    private static boolean first = true;
    private int restRid;
    private String TAG = "In RestService";
    private AlarmManager manager;
    private PendingIntent pi;


    @Override
    public void onCreate() {
        Log.d(TAG,"RestService Fired");
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not Binding");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (first)
            restRid = intent.getIntExtra("restRid",0);
        Log.d(TAG, "Executing restVoice");
        restVoice();
        if(first)
            first = false;
        return START_NOT_STICKY;
    }
    private void restVoice(){
        Calendar cal = new GregorianCalendar();
        Log.d("In RestService", "Current time: "+cal.get(Calendar.HOUR_OF_DAY)+": "+cal.get(Calendar.MINUTE)+": "+ cal.get(Calendar.SECOND));
        Random ran = new Random();
        Calendar c = new GregorianCalendar(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),cal.get(Calendar.DATE),cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE)+10);
        if(!first)
            new KancolleThread(this,restRid).run();
        manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = new Intent(this,RestVoiceReceiver.class);
        pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.RTC_WAKEUP,c.getTimeInMillis() ,pi);

    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        try {
            manager.cancel(pi);
        } catch (Exception e) {
            Log.e(TAG, "AlarmManager update was not canceled. " + e.toString());
        }
        first = true;
        super.onDestroy();
    }
}
