package com.cmpt276walkinggroupproject.walkinggroup;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import ca.cmpt276.walkinggroup.model.Group;
import ca.cmpt276.walkinggroup.model.Message;
import ca.cmpt276.walkinggroup.model.User;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 */
@RunWith(AndroidJUnit4.class)
public class MessageTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.cmpt276walkinggroupproject.walkinggroup", appContext.getPackageName());
    }

    @Test
    public void MessageIdTest() throws Exception {
        Long testLong = 5L;
        Long testLong2 = null;

        Message mez = new Message();
        assertEquals(mez.getId(), null);
        mez.setId(testLong);
        assertEquals(mez.getId(), testLong);
        mez.setId(testLong2);
        assertEquals(mez.getId(), null);

    }

    @Test
    public void MessageTextTest() throws Exception {
        String goodTextTest = "He who debugs passes";
        String badTextTest = "";

        Message mez = new Message();
        assertEquals(mez.getText(), null);
        mez.setText(goodTextTest);
        assertEquals(mez.getText(), goodTextTest);
        mez.setText(badTextTest);
        assertEquals(mez.getText(), badTextTest);
    }

    @Test
    public void MessageTimestampTest() throws Exception {
        Long timestamp = new Long( -1);

        Message mez = new Message();
        assertEquals(mez.getTimestamp(), null);
        timestamp = new Long(3255);
        mez.setTimestamp(timestamp);
        assertEquals(mez.getTimestamp(), timestamp);
        timestamp = new Long(3232125);
        mez.setTimestamp(timestamp);
        assertEquals(mez.getTimestamp(), timestamp);
    }

    @Test
    public void MessageHrefTest() throws Exception {
        String goodTextTest = "He who debugs passes";
        String badTextTest = "";

        Message mez = new Message();
        assertEquals(mez.getHref(), null);
        mez.setHref(goodTextTest);
        assertEquals(mez.getHref(), goodTextTest);
        mez.setHref(badTextTest);
        assertEquals(mez.getHref(), badTextTest);
    }

    @Test
    public void MessageUserTest() throws Exception {
        User he = makeUser();

        Message mez = new Message();
        assertEquals(mez.getFromUser(), null);
        mez.setFromUser(he);
        assertEquals(mez.getFromUser(), he);
        mez.setFromUser(null);
        assertEquals(mez.getFromUser(), null);
    }

    @Test
    public void MessageEmergencyTest() throws Exception {
        User he = makeUser();

        Message mez = new Message();
        mez.setEmergency(false);
        assertEquals(mez.isEmergency(), false);
        mez.setEmergency(true);
        assertEquals(mez.isEmergency(), true);
        mez.setEmergency(false);
        assertEquals(mez.isEmergency(), false);
    }

    @Test
    public void MessageGroupTest() throws Exception {
        Group them = new Group();

        Group us = new Group();

        Message mez = new Message();
        mez.setToGroup(us);
        assertEquals(mez.getToGroup(), us);
        mez.setToGroup(them);
        assertEquals(mez.getToGroup(), them);
        mez.setToGroup(us);
        assertEquals(mez.getToGroup(), us);
    }


    private User makeUser(){
        User user = new User();
        user.setName("Steve");
        user.setEmail("CompSci@awesome.com");
        user.setPassword("1");
        return user;
    }

}

