package com.example.go4lunch.activities.notifications;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.go4lunch.R;
import com.example.go4lunch.databinding.ActivityNotificationsBinding;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import static java.lang.Math.floor;

public class NotificationsActivity extends AppCompatActivity {

    private ActivityNotificationsBinding binding;

    private MaterialToolbar toolbar;

    public static final String PREFS= "PREFS";
    SharedPreferences prefs;

    private Calendar calendar;

    public NotificationsActivity(Calendar calendar){
        this.calendar = calendar;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getApplicationContext().getSharedPreferences(PREFS, MODE_PRIVATE);

        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);

        this.configureToolbar();


        binding.notificationsSwitch.setChecked(prefs.getBoolean("switch_checked", false));
        binding.notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            WorkManager mWorkManager = WorkManager.getInstance(this);
            if (isChecked) {

                long delay = calculateDelay(11,45, 0);

                PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(MyWorker.class,
                        1, TimeUnit.DAYS)
                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .build();

                mWorkManager.enqueue(workRequest);

                Toast.makeText(getApplicationContext(), R.string.notifications_on, Toast.LENGTH_LONG).show();
                prefs.edit().putBoolean("switch_checked", true).apply();

            } else {
                mWorkManager.cancelAllWork();

                prefs.edit().putBoolean("switch_checked", false).apply();
                Toast.makeText(getApplicationContext(), R.string.notifications_off, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void configureToolbar() {
        //Set the toolbar
        this.toolbar = binding.toolbar.toolbar;
        setSupportActionBar(toolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * <b>Calculate the delay</b>
     * <p>
     *     return the delay to senbd the first notification
     * </p>
     * @param hour
     *          choosen hour for notification
     * @param min
     *          choosen min for notification
     * @param sec
     *          choosen sec for notification
     * @return long
     *
     * @see this#calculateDelta(Calendar, Calendar)
     *
     */
    public static long calculateDelay(int hour, int min, int sec) {


        // Initialize the calendar with today and the preferred time to run the job.
        Calendar cal1 = Calendar.getInstance();
        cal1.set(Calendar.HOUR_OF_DAY, hour);
        cal1.set(Calendar.MINUTE, min);
        cal1.set(Calendar.SECOND, sec);

        // Initialize a calendar with now.
        Calendar cal2 = Calendar.getInstance();

        return calculateDelta(cal1,cal2);

    }

    /**
     * <b>Calculate the delta</b>
     * <p>
     *     Return the delta between two calendars in seconds
     * </p>
     * @param calendar1
     *          moment when notification will be sent
     * @param calendar2
     *          moment when user activate the notification
     * @return long
     *
     * @see this#calculateDelay(int, int, int)
     */
    public static long calculateDelta(Calendar calendar1, Calendar calendar2){

      long delta = Double.valueOf(floor(calendar1.getTimeInMillis()/1000.0) - floor(calendar2.getTimeInMillis()/1000.0)).longValue();

        return ((delta >= 0) ?
                delta
                : (TimeUnit.DAYS.toSeconds(1)+delta));
    }

}
