package yipuwang.kancolle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Yipu Wang on 8/18/2016.
 */
public class RestVoiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context,RestService.class);
        context.startService(i);
    }
}
