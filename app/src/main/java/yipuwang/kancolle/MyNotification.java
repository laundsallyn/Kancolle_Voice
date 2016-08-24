package yipuwang.kancolle;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

/**
 * Created by Yipu Wang on 8/23/2016.
 */
public class MyNotification {
    public MyNotification (Context con, Character name){
        NotificationCompat.Builder bud = new NotificationCompat.Builder(con);
        bud.setSmallIcon(R.mipmap.haruna_icon);
        bud.setContentTitle("Mobile Secretary Ship");
        bud.setContentText("Current secretary ship: "+ name.name());
        Intent resultIntent = new Intent(con, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        con,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_NO_CREATE
                );
//        bud.setContentIntent(resultPendingIntent);
        Notification noti = bud.build();
        NotificationManager man = (NotificationManager) con.getSystemService(Context.NOTIFICATION_SERVICE);
//        RemoteViews cView =new RemoteViews(con.getPackageName(), R.layout.notification_layout);
//        setListeners(cView);
//        noti.contentView = cView;
//        noti.flags |= Notification.FLAG_ONGOING_EVENT;
        man.notify(12345,noti);

    }

}
