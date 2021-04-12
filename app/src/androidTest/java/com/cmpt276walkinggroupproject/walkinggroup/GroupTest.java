package com.cmpt276walkinggroupproject.walkinggroup;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.model.Group;
import ca.cmpt276.walkinggroup.model.GroupCollection;
import ca.cmpt276.walkinggroup.model.User;
import ca.cmpt276.walkinggroup.model.UserCollection;

import static org.junit.Assert.assertEquals;


/**
 * Created by timothywaikinlam on 2018-03-27.
 */
@RunWith(AndroidJUnit4.class)


public class GroupTest {
    @Test
    public void GroupIdTest() throws Exception {
    long testid = (long)666;
    Group group = makeGroup();
    group.setId(testid);
    assertEquals(group.getId(),testid,1);
}
    @Test
    public void GroupDescriptionTest() throws Exception {
        String goodTextTest = "goodDescriptionText";
        String badTextTest = "badDescriptionText";

        Group group = makeGroup();
        //assertEquals(group.getGroupDescription(), badTextTest);
        group.setGroupDescription(goodTextTest);
        assertEquals(group.getGroupDescription(), goodTextTest);
        group.setGroupDescription(badTextTest);
        assertEquals(group.getGroupDescription(), badTextTest);
    }

    @Test
    public void GroupleaderTest(){
        User user = new User();
        Group group = makeGroup();
        //assertEquals(group.getLeader(), null);
        group.setLeader(user);
        assertEquals(group.getLeader(), user);
        group.setLeader(null);
        assertEquals(group.getLeader(), null);
    }



    private Group makeGroup() {
        Group group = new Group();
        User user =new User();
        group.setLeader(user);
        group.setGroupDescription("setGroupDescription");
        group.setId((long)666);
        return group;

    }

}
