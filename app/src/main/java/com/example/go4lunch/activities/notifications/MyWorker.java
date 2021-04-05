package com.example.go4lunch.activities.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import com.example.go4lunch.R;
import com.example.go4lunch.firestore.CurrentUser;
import com.example.go4lunch.firestore.UserHelper;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import io.reactivex.disposables.Disposable;

public class MyWorker extends Worker {

    private Context context;

    private String restId = "";

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context=context;

    }

    @NonNull
    @Override
    public Result doWork() {

        executeHttpRequest();


        return Result.success();

    }

    public void executeHttpRequest() {

        restId = CurrentUser.getInstance().getRestId();
        String message;
        if (!restId.isEmpty()) {


            message = context.getResources().getString(R.string.notification_message_with_restId) + CurrentUser.getInstance().getRestName();
            UserHelper.updateRestId("", CurrentUser.getInstance().getUid());
            UserHelper.updateRestName("", CurrentUser.getInstance().getUid());
        }else {
            message = context.getResources().getString(R.string.notification_message_without_restId);

        }

        sendNotif(message);

    }



    private void sendNotif(String message){

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("chanel_id","my chanel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableLights(true);

            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notify=new NotificationCompat.Builder(getApplicationContext(),"chanel_id");
        notify.setContentTitle("Go4Lunch");
        notify.setContentText(message);
        notify.setSmallIcon(R.drawable.ic_launcher_foreground);
        notify.setPriority(NotificationCompat.PRIORITY_MAX);


        Notification notif = notify.build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(101,notif);



    }
}
