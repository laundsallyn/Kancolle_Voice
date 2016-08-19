package yipuwang.kancolle;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by Yipu Wang on 8/16/2016.
 */
public class KancolleThread extends Thread {
    private MediaPlayer mp;
    private static Set<MediaPlayer> activePlayers= new HashSet<MediaPlayer>();;
    public KancolleThread(Context con, int rid){
        mp = MediaPlayer.create(con,rid);
        activePlayers.add(mp);

    }
    MediaPlayer.OnCompletionListener releaseOnFinishListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mp) {
            mp.release();
            activePlayers.remove(mp);
        }
    };
    @Override
    public void run (){
        mp.setOnCompletionListener(releaseOnFinishListener);
        mp.start();

    }
}
