package com.cmpt276walkinggroupproject.walkinggroup;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.model.GPS;
import ca.cmpt276.walkinggroup.model.Group;
import ca.cmpt276.walkinggroup.model.Message;
import ca.cmpt276.walkinggroup.model.User;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class UserTest {
    @Test
    public void UserBirthYearTest() throws Exception {
        Integer birthYear = 1960;
        Integer birthMonth = 1;
        User user = makeUser();
        user.setBirthYear(birthYear);
        assertEquals(user.getBirthYear(), birthYear);

        birthYear = 1963;
        user.setBirthYear(birthYear);
        assertEquals(user.getBirthYear(), birthYear);

        birthYear = 1955;
        user.setBirthYear(birthYear);
        assertEquals(user.getBirthYear(), birthYear);
    }

    @Test
    public void UserBirthMonthTest() throws Exception {
        Integer birthMonth = 1;

        User user = makeUser();
        user.setBirthMonth(birthMonth);
        assertEquals(user.getBirthMonth(), birthMonth);
        birthMonth = 3;
        user.setBirthMonth(birthMonth);
        assertEquals(user.getBirthMonth(), birthMonth);
        birthMonth = 5;
        user.setBirthMonth(birthMonth);
        assertEquals(user.getBirthMonth(), (Integer) 5);
    }

    @Test
    public void UserAddressTest() throws Exception {
        String goodTextTest = "He who debugs passes";
        String badTextTest = null;

        User user = makeUser();
        assertEquals(user.getAddress(), badTextTest);
        user.setAddress(goodTextTest);
        assertEquals(user.getAddress(), goodTextTest);
        user.setAddress(badTextTest);
        assertEquals(user.getAddress(), badTextTest);
    }

    @Test
    public void UserCellPhoneTest() throws Exception {
        String goodTextTest = "He who debugs passes";
        String badTextTest = null;

        User user = makeUser();
        assertEquals(user.getCellPhone(), badTextTest);
        user.setCellPhone(goodTextTest);
        assertEquals(user.getCellPhone(), goodTextTest);
        user.setCellPhone(badTextTest);
        assertEquals(user.getCellPhone(), badTextTest);
    }

    @Test
    public void UserHomePhoneTest() throws Exception {
        String goodTextTest = "He who debugs passes";
        String badTextTest = null;

        User user = makeUser();
        assertEquals(user.getHomePhone(), badTextTest);
        user.setHomePhone(goodTextTest);
        assertEquals(user.getHomePhone(), goodTextTest);
        user.setHomePhone(badTextTest);
        assertEquals(user.getHomePhone(), badTextTest);
    }

    @Test
    public void UserGradeTest() throws Exception {
        String goodTextTest = "He who debugs passes";
        String badTextTest = null;

        User user = makeUser();
        assertEquals(user.getGrade(), badTextTest);
        user.setGrade(goodTextTest);
        assertEquals(user.getGrade(), goodTextTest);
        user.setGrade(badTextTest);
        assertEquals(user.getGrade(), badTextTest);
    }

    @Test
    public void UserTeacherTest() throws Exception {
        String goodTextTest = "He who debugs passes";
        String badTextTest = null;

        User user = makeUser();
        assertEquals(user.getTeacherName(), badTextTest);
        user.setTeacherName(goodTextTest);
        assertEquals(user.getTeacherName(), goodTextTest);
        user.setTeacherName(badTextTest);
        assertEquals(user.getTeacherName(), badTextTest);
    }

    @Test
    public void UserEmergencyContactInfoTest() throws Exception {
        String goodTextTest = "She who Tests well passes";
        String badTextTest = null;

        User user = makeUser();
        assertEquals(user.getEmergencyContactInfo(), badTextTest);
        user.setEmergencyContactInfo(goodTextTest);
        assertEquals(user.getEmergencyContactInfo(), goodTextTest);
        user.setEmergencyContactInfo(badTextTest);
        assertEquals(user.getEmergencyContactInfo(), badTextTest);
    }

    @Test
    public void testOutput(){
        User user = makeComplexUser();
        List<User> users = new ArrayList<>();
        users.add(makeUser());
        user.setMonitoredByUsers(users);
        assertEquals(user.toString(), "User{id=666, name='Steve', email='CompSci@awesome.com', password='1', monitoredByUsers=[User{id=null, name='Steve', email='CompSci@awesome.com', password='1', monitoredByUsers=[], monitorsUsers=[], walkingGroups=[]}], monitorsUsers=[], walkingGroups=[]}");
    }

    @Test
    public void UserReadMessagesTest(){
        User user = makeComplexUser();
        Message message = new Message();
        message.setText("Message1");
        Message message2 = new Message();
        message2.setText("Message2");
        List<Message> messages = new ArrayList<>();
        messages.add(message);
        messages.add(message2);
        user.setReadMessages(messages);
        assertEquals(user.getReadMessages().get(0).getText(),"Message1");
        assertEquals(user.getReadMessages().get(1).getText(),"Message2");
    }

    @Test
    public void UserUnreadMessagesTest(){
        User user = makeComplexUser();
        Message message = new Message();
        message.setText("Message1");
        Message message2 = new Message();
        message2.setText("Message2");
        List<Message> messages = new ArrayList<>();
        messages.add(message);
        messages.add(message2);
        user.setUnreadMessages(messages);
        assertEquals(user.getUnreadMessages().get(0).getText(),"Message1");
        assertEquals(user.getUnreadMessages().get(1).getText(),"Message2");
    }

    @Test
    public void UserGpsTest(){
        GPS newGPS = new GPS();
        User user = makeUser();
        assertEquals(user.getLastGpsLocation(), null);
        user.setLastGpsLocation(newGPS);
        assertEquals(user.getLastGpsLocation(), newGPS);
        user.setLastGpsLocation(null);
        assertEquals(user.getLastGpsLocation(), null);
    }
    @Test

    public void UserNameTest() throws Exception {
        String goodTextTest = "He who debugs passes";
        String badTextTest = "DEFAULT";

        User user = makeUser();
        assertEquals(user.getName(), "Steve");
        user.setName(goodTextTest);
        assertEquals(user.getName(), goodTextTest);
        user.setName(badTextTest);
        assertEquals(user.getName(), badTextTest);
    }

    @Test
    public void UserEmailTest() throws Exception {
        String goodTextTest = "He who debugs passes";
        String badTextTest = "DEFAULT";

        User user = makeUser();
        assertEquals(user.getEmail(), "CompSci@awesome.com");
        user.setEmail(goodTextTest);
        assertEquals(user.getEmail(), goodTextTest);
        user.setEmail(badTextTest);
        assertEquals(user.getEmail(), badTextTest);
    }

    @Test
    public void UserPasswordTest() throws Exception {
        String goodTextTest = "He who debugs passes";
        String badTextTest = "DEFAULT";

        User user = makeUser();
        assertEquals(user.getPassword(), "1");
        user.setPassword(goodTextTest);
        assertEquals(user.getPassword(), goodTextTest);
        user.setPassword(badTextTest);
        assertEquals(user.getPassword(), badTextTest);
    }

    @Test
    public void UserIdTest() throws Exception {

        Long testLong1 = (long)93;
        Long testLong2 = (long)3;
        User user = makeUser();
        assertEquals(user.getId(), null);
        user.setId(testLong1);
        assertEquals(user.getId(), testLong1);
        user.setId(testLong2);
        assertEquals(user.getId(), testLong2);
    }

    @Test
    public void UserHrefTest() throws Exception {
        String goodTextTest = "He who debugs passes";
        String badTextTest = null;

        User user = makeUser();
        assertEquals(user.getHref(), badTextTest);
        user.setHref(goodTextTest);
        assertEquals(user.getHref(), goodTextTest);
        user.setHref(badTextTest);
        assertEquals(user.getHref(), badTextTest);
    }

    @Test
    public void UserMonitoredByTest() throws Exception {
        User user = makeComplexUser();
        User usedOne = makeUser();
        assertEquals(user.getMonitoredByUsers().size(), 0);
        List<User> users = new ArrayList<>();
        users.add(usedOne);
        user.setMonitoredByUsers(users);
        assertEquals(user.getMonitoredByUsers().size(), 1);
        assertEquals(user.getMonitoredByUsers().get(0), usedOne);
        users.add(makeUser());
        assertEquals(user.getMonitoredByUsers().size(), 2);

    }

    @Test
    public void UserMonitoringTest() throws Exception {
        User user = makeComplexUser();
        User usedOne = makeUser();
        assertEquals(user.getMonitorsUsers().size(), 0);
        List<User> users = new ArrayList<>();
        users.add(usedOne);
        user.setMonitorsUsers(users);
        assertEquals(user.getMonitorsUsers().size(), 1);
        assertEquals(user.getMonitorsUsers().get(0), usedOne);
        users.add(makeUser());
        assertEquals(user.getMonitorsUsers().size(), 2);

    }

    @Test
    public void UserWalkingGroupAllTest() throws Exception {
        User user = makeComplexUser();
        User usedOne = makeUser();
        Group inThis = new Group();
        List<Group> myGroups = new ArrayList<>();
        assertEquals(user.getWalkingGroups().size(), 0);
        myGroups.add(inThis);
        user.setWalkingGroups(myGroups);
        assertEquals(user.getWalkingGroups().size(), 1);
        assertEquals(user.getWalkingGroups().get(0), inThis);
        user.getWalkingGroups().add(new Group());
        assertEquals(user.getWalkingGroups().size(), 2);


    }

    @Test
    public void UserWalkingGroupMemberTest() throws Exception {
        User user = makeComplexUser();
        Group inThis = new Group();
        List<Group> myGroups = new ArrayList<>();
        assertEquals(user.getMemberOfGroups().size(), 0);
        myGroups.add(inThis);
        user.setMemberOfGroups(myGroups);
        assertEquals(user.getMemberOfGroups().size(), 1);
        assertEquals(user.getMemberOfGroups().get(0), inThis);
        user.getMemberOfGroups().add(new Group());
        assertEquals(user.getMemberOfGroups().size(), 2);


    }

    @Test
    public void UserWalkingGroupLeaderTest() throws Exception {
        User user = makeComplexUser();
        User usedOne = makeUser();
        Group inThis = new Group();
        List<Group> myGroups = new ArrayList<>();
        assertEquals(user.getLeadsGroups().size(), 0);
        myGroups.add(inThis);
        user.setLeadsGroups(myGroups);
        assertEquals(user.getLeadsGroups().size(), 1);
        assertEquals(user.getLeadsGroups().get(0), inThis);
        user.getLeadsGroups().add(new Group());
        assertEquals(user.getLeadsGroups().size(), 2);


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

