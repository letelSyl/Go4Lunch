package com.example.go4lunch;

import com.example.go4lunch.activities.notifications.NotificationsActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * NearbySearch local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class NotificationsActivityTests {

    private NotificationsActivity notificationsActivityTests;


    @Test
    public void NotificationsActivityCalculateDelta() {


       Calendar cal1 = Calendar.getInstance();
       cal1.set(2021,3,23,11,45,0);

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2021,3,23,9,5,17);


        Calendar cal3 = Calendar.getInstance();
        cal3.set(2021,3,23,16,38,24);

        Calendar cal4 = Calendar.getInstance();
        cal4.set(2021,3,23,11,45,0);

        assertNotEquals(NotificationsActivity.calculateDelta(cal1,cal2),125000);
        assertNotEquals(NotificationsActivity.calculateDelta(cal1,cal3),125000);

        assertEquals(NotificationsActivity.calculateDelta(cal1,cal4), 0);

        assertEquals(NotificationsActivity.calculateDelta(cal1,cal2), 9583);
        assertEquals(NotificationsActivity.calculateDelta(cal1,cal3), 68796);

    }
}