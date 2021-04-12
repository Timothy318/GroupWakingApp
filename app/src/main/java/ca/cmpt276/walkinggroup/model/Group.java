package ca.cmpt276.walkinggroup.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a single group that the user is part of.
 * It contains all the necessary information about this
 * class only.
 *
 * takes in a long id a string description and a user
 * takes in 2 lists which contain map coordiates for the path
 * takes in a list of users
 *
 * returns a Group object
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Group {
    @SerializedName("id")
    private Long id;
    @SerializedName("groupDescription")
    private String groupDescription;
    @SerializedName("leader")
    private User leader;
    @SerializedName("routeLatArray")
    private List<Double> routeLatArray = new ArrayList<>();
    @SerializedName("routeLngArray")
    private List<Double> routeLngArray = new ArrayList<>();
    @SerializedName("memberUsers")
    private List<User> memberUsers = new ArrayList<>();

    public Group() {
        this.id = 43l;
        this.groupDescription = "DEFAULT";
        this.leader = new User();
    }

    // constructor
    public Group(String name, User owner) {
        this.groupDescription = name;
        this.leader = owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String name) {
        this.groupDescription = name;
    }

    public User getLeader() {
        return leader;
    }

    public void setLeader(User leader) {
        this.leader = leader;
    }

    public void addRouteLat(Double lat) {
        routeLatArray.add(lat);
    }

    public void addRouteLng(Double lng) {
        routeLngArray.add(lng);
    }

    public List<Double> getRouteLatArray() {
        return routeLatArray;
    }

    public List<Double> getRouteLngArray() {
        return routeLngArray;
    }

    public void clearRoutes() {
        routeLatArray = new ArrayList<>();
        routeLngArray = new ArrayList<>();
    }

    public boolean isGroupEmpty(){
        return memberUsers.size() == 0;
    }

    public List<User> getMemberUsers() {
        return memberUsers;
    }

    public void setGroupUser(User newUser) {
        this.memberUsers.add(newUser);
    }

    public void setMemberList(List<User> users) {
        this.memberUsers = users;
    }

    public void removeGroupUser(User targetUser) {
        this.memberUsers.remove(targetUser);
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", name='" + groupDescription + '\'' +
                ", leader=" + leader;
    }
}
