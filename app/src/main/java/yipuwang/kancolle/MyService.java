package yipuwang.kancolle;

/**
 * Created by Yipu Wang on 7/27/2016.
 */
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class MyService extends Service {
    private String TAG = "In MyService";
    private int[] rid;
    private static boolean first = true;
    private AlarmManager manager;
    private PendingIntent pi;
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not Binding");
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(first){
            rid = intent.getIntArrayExtra("hour_voice_rid");
        }
        Log.d(TAG, "Executing mainFunctionality");
        hourVoice();
        if(first)
            first = false;
        return START_NOT_STICKY;
    }
    private void hourVoice(){
//        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        Calendar cal = new GregorianCalendar();
        Log.d("In MyService", "Current time: "+cal.get(Calendar.HOUR_OF_DAY)+": "+cal.get(Calendar.MINUTE)+": "+ cal.get(Calendar.SECOND));
        Calendar next = new GregorianCalendar(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),cal.get(Calendar.DATE),cal.get(Calendar.HOUR_OF_DAY)+1,0);
        if(!first)
            new KancolleThread(this,rid[cal.get(Calendar.HOUR_OF_DAY)+1]).run();

        //--------------- for debugging purpose -----------------------
//        Calendar debug = new GregorianCalendar(cal.get(Calendar.YEAR),
//                cal.get(Calendar.MONTH),cal.get(Calendar.DATE),cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE)+2);
//        int id = rid[cal.get(Calendar.HOUR_OF_DAY)];
//        int prefID = settings.getInt(""+cal.get(Calendar.HOUR_OF_DAY),0);
//        if (id != prefID)
//            Log.d(TAG,"ID = "+id+", prefID = "+prefID);
//        new KancolleThread(this,rid[cal.get(Calendar.HOUR_OF_DAY)]).run();
//        Log.d(TAG,"HOUR: "+cal.get(Calendar.HOUR)+" HOUR_OF_DAY: "+cal.get(Calendar.HOUR_OF_DAY));
        //-------------------------------------------------------------

        manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = new Intent(this,KancolleAlarmReceiver.class);
        pi = PendingIntent.getBroadcast(this, 0, i, 0);


        manager.set(AlarmManager.RTC_WAKEUP,next.getTimeInMillis(),pi);

        //--------------- for debugging purpose -----------------------
//        manager.set(AlarmManager.RTC_WAKEUP,debug.getTimeInMillis(),pi);
        //-------------------------------------------------------------

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