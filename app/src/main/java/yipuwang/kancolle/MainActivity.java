package yipuwang.kancolle;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener {
    public static final String PREFS_NAME = "MyPrefsFile";
    private static String TAG = "In MainActivity";
    private int[] hour_voice_rid;
    private Button start;
    private Button stop;
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
        setContentView(R.layout.activity_main);
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
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
    }

    private int[] getResourceID (Character name){
        int[] rid = new int[24];
        SharedPreferences.Editor editor = settings.edit();

        Pattern hour_pattern, start_pattern,stop_pattern,rest_pattern;
        switch(name){
            case HARUNA:
                hour_pattern = Pattern.compile("(haruna)(\\d*)");
                start_pattern = Pattern.compile("haruna_set");
                stop_pattern = Pattern.compile("haruna_stop_service");
                rest_pattern = Pattern.compile("haruna_rest");

                portraitID = R.drawable.haruna_portrait;
                break;
            case KONGO:
                hour_pattern = Pattern.compile("(kongo)(\\d*)");
                start_pattern = Pattern.compile("kongo_set");
                stop_pattern = Pattern.compile("kongo_stop_service");
                rest_pattern = Pattern.compile("kongo_rest");
                portraitID = R.drawable.kongo_portrait;
                break;
            default:
                hour_pattern = Pattern.compile("(haruna)(\\d*)");
                start_pattern = Pattern.compile("haruna_set");
                stop_pattern = Pattern.compile("haruna_stop_service");
                rest_pattern = Pattern.compile("haruna_rest");

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

                boolean matchHour = matcher_hour.matches();
                boolean matchStart = start.matches();
                boolean matchStop = stop.matches();
                boolean matchRest = rest.matches();

                // Log.d(TAG, " @@@@" + matcher.group(2));
                if (matchHour) {
                    int id = f.getInt(null);
                    int i = Integer.parseInt(matcher_hour.group(2));
                    rid[i] = id;
                    editor.putInt(""+i,id);
                } else if (matchStart){
                    startRid = f.getInt(null);
                    editor.putInt("startRid",startRid);
                } else if (matchStop){
                    stopRid = f.getInt(null);
                    editor.putInt("stopRid",stopRid);
                } else if (matchRest){
                    restRid = f.getInt(null);
                    editor.putInt("restRid",restRid);
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


//            Intent rest_service = new Intent(con, RestService.class);
//            rest_service.putExtra("restRid", restRid);
//            startService(rest_service);
            start.setEnabled(false);
            stop.setEnabled(true);
        }
    }

    private class StopListener implements View.OnClickListener {
        private SoundPool mSound;
        Context con;
        public StopListener( Context con){
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
            img.setImageResource(R.drawable.blank);
            stopService(new Intent(con, MyService.class));
//            stopService(new Intent(con, RestService.class));
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
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
}
