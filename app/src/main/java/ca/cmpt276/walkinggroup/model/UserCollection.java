package ca.cmpt276.walkinggroup.model;
import java.util.ArrayList;
import java.util.List;


/**
 * UserCollection class stores and handles user information
 * which later can be retrieved if required.
 *
 * Refers to the data held by the currently logged in user
 *
 * a singelton to hold the monitored and monitoring lists pertaining to the current user
 */

public class UserCollection {
    // user collection:
    private List<User> monitoringList = new ArrayList<>();
    private List<User> monitoredByList = new ArrayList<>();
    private static UserCollection single_instance = null;

    public void addUserToMonitoringList(User newUser){
        monitoringList.add(newUser);
    }

    public void addUserToMonitoredByList(User newUser){
        monitoredByList.add(newUser);
    }

    public void setUsersToMonitoringList(List<User> users) {
        monitoringList = users;
    }

    public void setUsersToMonitoredByList(List<User> users) {
        monitoredByList = users;
    }

    public List<User> getMonitoredByList(){
        return monitoredByList;
    }
    public List<User> getMonitoringList() {return monitoringList;}

    // singleton
    public static UserCollection getInstance() {
        if (single_instance == null)
            single_instance = new UserCollection();

        return single_instance;
    }
}
