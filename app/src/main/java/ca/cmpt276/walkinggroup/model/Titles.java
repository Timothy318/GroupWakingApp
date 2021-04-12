package ca.cmpt276.walkinggroup.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A class used for the customization of the information
 * regarding every users' bought titles. This class will
 * communicate with the customJson to pass the server the
 * necessary getters and setters for retrieval and setting
 * the information respectively.
 *
 * takes in a list of titles and their associated prices
 * outputs the same
 */

public class Titles {
    private static Titles single_instance = null;
    private List<String> lockedTitlesArr = new ArrayList<>();
    private List<Integer> lockedScoreList = new ArrayList<>();
    private List<String> unlockedTitles = new ArrayList<>();

    public Titles(){
        if(single_instance == null){
            lockedTitlesArr.add("Noob");
            lockedTitlesArr.add("Active");
            lockedTitlesArr.add("Master");
            lockedTitlesArr.add("Bravest");
            lockedTitlesArr.add("King Kong");

            lockedScoreList.add(5);
            lockedScoreList.add(10);
            lockedScoreList.add(20);
            lockedScoreList.add(35);
            lockedScoreList.add(50);
        }
    }

    // ............. Unlocked Titles ............... //
    public void addUnlockedTitle(String unlockedTitle){unlockedTitles.add(unlockedTitle);}
    public void setUnlockedTitles(List<String> unlockedTitles){this.unlockedTitles = unlockedTitles;}
    public List<String> getUnlockedTitles(){return unlockedTitles;}
    // ............................................. //

    // .............. Locked Titles ................ //
    public void remakeLockedTitlesList(){
        // Titles' Name:
        lockedTitlesArr.clear();
        lockedTitlesArr.add("Noob");
        lockedTitlesArr.add("Active");
        lockedTitlesArr.add("Master");
        lockedTitlesArr.add("Bravest");
        lockedTitlesArr.add("King Kong");

        // Titles' Score:
        lockedScoreList.clear();
        lockedScoreList.add(5);
        lockedScoreList.add(10);
        lockedScoreList.add(20);
        lockedScoreList.add(35);
        lockedScoreList.add(50);
    }
    public void removeLockedTitle(int position){lockedTitlesArr.remove(position);}
    public void setLockedTitles(List<String> lockedTitles) {this.lockedTitlesArr = lockedTitles;}
    public List<String> getLockedTitles() {return lockedTitlesArr;}
    public void setLockedScoreList(List<Integer> lockedScoreList){this.lockedScoreList = lockedScoreList;}
    public void removeLockedScore(int position){lockedScoreList.remove(position);}
    public List<Integer> getLockedScoreList(){return lockedScoreList;}
    // ............................................. //

    // ........... Singleton Instance ........... //
    public static Titles getInstance() {
        if (single_instance == null) {single_instance = new Titles();}

        return single_instance;
    }

    public static void setInstance(Titles newSingleton) {single_instance = newSingleton;}
    // .......................................... //
}
