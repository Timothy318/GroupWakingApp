package com.cmpt276walkinggroupproject.walkinggroup;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.model.Message;
import ca.cmpt276.walkinggroup.model.MessageCollection;
import ca.cmpt276.walkinggroup.model.User;
import ca.cmpt276.walkinggroup.model.UserCollection;

import static org.junit.Assert.assertEquals;

/**
 * Created by timothywaikinlam on 2018-03-28.
 */
@RunWith(AndroidJUnit4.class)
public class MessageCollectionTest {

@Test
    public void MessageCollectionTest() throws Exception{
//    Message message= new Message();
//    MessageCollection collection =  new MessageCollection();
//    assertEquals(collection.getMessageList().size(), 0);
//
//    List<Message> messagesList = new ArrayList<>();
//    messagesList.add(message);
//
//    collection.setMessageList(messagesList);
//    assertEquals(collection.getMessageList().size(), 1);

    }

    private User makeUser(){
        User user = new User();
        user.setName("Steve");
        user.setEmail("CompSci@awesome.com");
        user.setPassword("1");
        return user;
    }


}
