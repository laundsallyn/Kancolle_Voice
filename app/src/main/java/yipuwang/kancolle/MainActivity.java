package yipuwang.kancolle;
import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener {
    public static final String PREFS_NAME = "MyPrefsFile";
    private static String TAG = "In MainActivity";
    private int[] hour_voice_rid;
    private int[] touch_voice_rid;
    private Button start,stop,exit;
    private Character name;
    private ImageView img;
    private int startRid, stopRid,restRid,portraitID;
    private SharedPreferences settings;

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hour_voice_rid = new int[24];
        touch_voice_rid = new int[3];
        setContentView(R.layout.activity_main);
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
        exit = (Button) findViewById(R.id.exit);
        start.setOnClickListener(new StartListener(this));
        stop.setOnClickListener(new StopListener(this));
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Kanmusu, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_layout);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        img = (ImageView) findViewById(R.id.imageView);
        settings = getSharedPreferences(PREFS_NAME, 0);
        restoreSetting();
        stop.setEnabled(false);
        super.onCreate(savedInstanceState);

    }
    public void restoreSetting(){
        portraitID = settings.getInt("portraitID", 0);
        startRid = settings.getInt("startRid", 0);
        stopRid = settings.getInt("startRid", 0);
        restRid = settings.getInt("startRid", 0);
        for(int i = 0; i < 24; i++)
            hour_voice_rid[i] = settings.getInt(""+i,0);
        for(int i = 0; i < 3; i++)
            touch_voice_rid[i] = settings.getInt("t"+i,0);
    }

    private int[] getResourceID (Character name){
        int[] rid = new int[24];
        SharedPreferences.Editor editor = settings.edit();
        String pat = name.name().toLowerCase();
        Pattern hour_pattern, start_pattern,stop_pattern,rest_pattern,touch_pattern;
        hour_pattern = Pattern.compile("("+ pat+ ")(\\d*)");
        start_pattern = Pattern.compile(pat+"_set");
        stop_pattern = Pattern.compile(pat +"_stop_service");
        rest_pattern = Pattern.compile(pat+ "_idle");
        touch_pattern = Pattern.compile("("+ pat+ ")_touch_(\\d*)");
        switch(name){
            case HARUNA:
                portraitID = R.drawable.haruna_portrait;
                break;
            case KONGO:
                portraitID = R.drawable.kongo_portrait;
                break;
            case AYANAMI:
                portraitID = R.drawable.ayanami_portrait;
                break;
            case WARSPITE:
                portraitID = R.drawable.warspite_portrait;
                break;
            case AMATSUKAZE:
                portraitID = R.drawable.amatsukaze_portrait;
                break;
            case GRAFZEPPELIN:
                portraitID = R.drawable.grafzeppelin_portrait;
                break;
            default:
                portraitID = R.drawable.blank;
        }
        Class raw = R.raw.class;
        Field[] fields = raw.getFields();
        for (Field f : fields) {
            try {
                String fileName = f.getName();
                Matcher matcher_hour = hour_pattern.matcher(fileName);
                Matcher start = start_pattern.matcher(fileName);
                Matcher stop = stop_pattern.matcher(fileName);
                Matcher rest = rest_pattern.matcher(fileName);
                Matcher touch = touch_pattern.matcher(fileName);
                // Log.d(TAG, " @@@@" + matcher.group(2));
                if (matcher_hour.matches()) {
                    int id = f.getInt(null);
                    int i = Integer.parseInt(matcher_hour.group(2));
                    rid[i] = id;
                    editor.putInt(""+i,id);
                } else if (start.matches()){
                    startRid = f.getInt(null);
                    editor.putInt("startRid",startRid);
                } else if (stop.matches()){
                    stopRid = f.getInt(null);
                    editor.putInt("stopRid",stopRid);
                } else if (rest.matches()){
                    restRid = f.getInt(null);
                    editor.putInt("restRid",restRid);
                }else if(touch.matches()){
                    int id = f.getInt(null);
                    int i = Integer.parseInt(touch.group(2));
                    touch_voice_rid[i-1] = id;
                    editor.putInt("t"+(i-1),id);
                }
            } catch (Exception e) {
                continue;
            }
        }
        editor.apply();
        return rid;
    }
    private class StartListener implements View.OnClickListener {
        private SoundPool mSound;
        Context con;
        public StartListener(Context con){
            this.con = con;
            mSound = new SoundPool(2, AudioManager.STREAM_MUSIC,0);
        }
        public void onClick(View view){
            hour_voice_rid = getResourceID(name);
            img.setImageResource(portraitID);
            img.setEnabled(true);
            int st = mSound.load(con,startRid,1);
            try{
                Thread.sleep(1000);
                mSound.play(st, 1, 1, 1, 0, 1);

            }catch(Exception e){
                Log.d("IN MainActivity", e.toString());
            }
            Intent hour_service = new Intent(con, MyService.class);
//            for (int i = 0; i < hour_voice_rid.length; i++)
//                Log.d(TAG,"----"+hour_voice_rid[i]+"----");
            hour_service.putExtra("hour_voice_rid", hour_voice_rid);
            startService(hour_service);

            showNotification();
            Intent rest_service = new Intent(con, IdleService.class);
            rest_service.putExtra("restRid", restRid);
            startService(rest_service);
            start.setEnabled(false);
            exit.setEnabled(false);
            stop.setEnabled(true);
        }
    }

    private class StopListener implements View.OnClickListener {
        private SoundPool mSound;
        private NotificationManager man;
        Context con;
        public StopListener( Context con){
            man = (NotificationManager) con.getSystemService(Context.NOTIFICATION_SERVICE);
            this.con = con;
            mSound = new SoundPool(2, AudioManager.STREAM_MUSIC,0);
        }
        public void onClick(View view){
            Log.d(TAG, "stopRid is: "+ stopRid+" kongo stop service is: "+R.raw.kongo_stop_service);
            int st = mSound.load(con,stopRid,1);
            try{
                Thread.sleep(1000);
                mSound.play(st, 1, 1, 1, 0, 1);

            }catch(Exception e){
                Log.d("IN MyService", e.toString());
            }
            start.setEnabled(true);
            stop.setEnabled(false);
            exit.setEnabled(true);
            img.setImageResource(R.drawable.blank);
            img.setEnabled(false);
            stopService(new Intent(con, MyService.class));
            stopService(new Intent(con, IdleService.class));
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            man.cancel(12345);
            editor.clear();
        }
    }
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        String select = ((String) parent.getSelectedItem()).toUpperCase();
        Toast.makeText(this, "You selected: "+ select, Toast.LENGTH_LONG).show();
        name = Character.valueOf(select);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
    public void touchVoice(View view){
        Random rand = new Random();
        int i = rand.nextInt(3);
        MediaPlayer mp = MediaPlayer.create(this,touch_voice_rid[i]);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        mp.start();
    }

    public void quit(View view){
        FragmentManager fm = getFragmentManager();
        QuitDialogFragment quitDialogFragment = new QuitDialogFragment();
        quitDialogFragment.show(fm, "quit");
    }

    public void showNotification(){
        new MyNotification(this,name);
    }
}
