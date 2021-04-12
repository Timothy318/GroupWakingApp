package com.cmpt276walkinggroupproject.walkinggroup;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import ca.cmpt276.walkinggroup.model.GPS;
import ca.cmpt276.walkinggroup.model.Group;
import ca.cmpt276.walkinggroup.model.Message;
import ca.cmpt276.walkinggroup.model.User;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 */
@RunWith(AndroidJUnit4.class)
public class GPSTest {



    @Test
    public void GPSLatTest() throws Exception {
        // Context of the app under test.
        double LAT = 0.0;
        GPS gps = new GPS();
        gps.setLat(LAT);
        assert(gps.getLat() == LAT);
        LAT = 8342.324;
        gps.setLat(LAT);
        assert(gps.getLat() == LAT);
        LAT = 0.3213244;
        gps.setLat(LAT);
        assert(gps.getLat() == LAT);
    }

    @Test
    public void GPSLngTest() throws Exception {
        // Context of the app under test.
        double LAT = 0.0;
        GPS gps = new GPS();
        gps.setLng(LAT);
        assert(gps.getLng() == LAT);
        LAT = 8342.324;
        gps.setLng(LAT);
        assert(gps.getLng() == LAT);
        LAT = 0.3213244;
        gps.setLng(LAT);
        assert(gps.getLng() == LAT);
    }

    @Test
    public void GPSTimestampTest() throws Exception {
        Date timestamp = null;
        GPS mez = new GPS();
        assertEquals(mez.getTimestamp(), timestamp);
        timestamp = new Date(2);
        mez.setTimestamp(timestamp);
        assertEquals(mez.getTimestamp(), timestamp);
        timestamp = new Date(4543);
        mez.setTimestamp(timestamp);
        assertEquals(mez.getTimestamp(), timestamp);
    }


}

