package ca.cmpt276.walkinggroup.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The purpose of this class is to organize different
 * groups that the user is part of. A user can be part
 * of multiple groups.
 *
 * takes in a List of groups and holds them as a singleton
 *
 * returns the list of groups
 */

public class GroupCollection {
    private List<Group> groups = new ArrayList<>();
    private static GroupCollection single_instance = null;

    public List<Group> getGroups() {
        return groups;
    }

    public void addGroupToCollection(Group newGroup) {
        groups.add(newGroup);
    }

    public void removeGroupFromCollection(Group targetGroup){
        groups.remove(targetGroup);
    }

    public void setGroups(List<Group> newGroups) {
        groups = newGroups;
    }

    public boolean isGroupCollectionEmpty(){
        return groups.size() == 0;
    }

    public int countGroups() {
        return groups.size();
    }

    public Group getGroup(int index) {
        validateIndexWithException(index);
        return groups.get(index);
    }

    // singleton:
    public static GroupCollection getInstance() {
        if (single_instance == null)
            single_instance = new GroupCollection();

        return single_instance;
    }

    private void validateIndexWithException(int index) {
        if (index < 0 || index >= countGroups()) {
            throw new IllegalArgumentException();
        }
    }
}
