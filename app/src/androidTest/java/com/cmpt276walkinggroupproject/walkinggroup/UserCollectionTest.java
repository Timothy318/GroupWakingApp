package com.cmpt276walkinggroupproject.walkinggroup;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.model.GPS;
import ca.cmpt276.walkinggroup.model.User;
import ca.cmpt276.walkinggroup.model.UserCollection;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 */
@RunWith(AndroidJUnit4.class)
public class UserCollectionTest {


    @Test
    public void UserCollectionMonitoringListTest() throws Exception {
        UserCollection collection =  new UserCollection();
        assertEquals(collection.getMonitoringList().size(), 0);

        List<User> monitoringList = new ArrayList<>();
        monitoringList.add(makeUser());

        collection.setUsersToMonitoringList(monitoringList);
        assertEquals(collection.getMonitoringList().size(), 1);

        collection.addUserToMonitoringList(makeComplexUser());
        assertEquals(collection.getMonitoringList().size(), 2);

    }

    @Test
    public void UserCollectionMonitoredByListTest() throws Exception {
        UserCollection collection =  new UserCollection();
        assertEquals(collection.getMonitoredByList().size(), 0);

        List<User> monitoredByList = new ArrayList<>();
        monitoredByList.add(makeUser());

        collection.setUsersToMonitoredByList(monitoredByList);
        assertEquals(collection.getMonitoredByList().size(), 1);

        collection.addUserToMonitoredByList(makeComplexUser());
        assertEquals(collection.getMonitoredByList().size(), 2);

    }


    private User makeUser() {
        User user = new User();
        user.setName("Steve");
        user.setEmail("CompSci@awesome.com");
        user.setPassword("1");
        return user;

    }

    private User makeComplexUser(){
        User user = new User();
        user.setName("Steve");
        user.setEmail("CompSci@awesome.com");
        user.setPassword("1");
        user.setBirthMonth(1);
        user.setBirthYear(666);
        user.setAddress("Hell");
        user.setCellPhone("666-666-6666");
        user.setEmergencyContactInfo("I am your emergency");
        user.setGrade("NULL is my life");
        user.setHomePhone("214-342-3432");
        user.setHref("I don't know what this is for");
        user.setId((long) 666);
        user.setTeacherName("Nightmare");
        return user;
    }


}

