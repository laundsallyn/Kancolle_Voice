package yipuwang.kancolle;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
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
        bud.setContentText("Your current secretary ship is "+ name.name());
        Notification noti = bud.build();

        NotificationManager man = (NotificationManager) con.getSystemService(Context.NOTIFICATION_SERVICE);
        RemoteViews cView =new RemoteViews(con.getPackageName(), R.layout.notification_layout);
//        setListeners(cView);
        noti.contentView = cView;
        noti.flags |= Notification.FLAG_ONGOING_EVENT;
        man.notify(12345,noti);

    }

    public void setListeners(RemoteViews view) {

    }
}
