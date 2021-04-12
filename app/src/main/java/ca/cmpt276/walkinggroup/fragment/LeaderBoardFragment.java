package ca.cmpt276.walkinggroup.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.cmpt276walkinggroupproject.walkinggroup.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import ca.cmpt276.walkinggroup.handler.RewardHandler;
import ca.cmpt276.walkinggroup.handler.ServerHandler;
import ca.cmpt276.walkinggroup.model.User;

/**
 * This class shows the list of top 100 users that
 * are present inside the app and it sorts each user
 * according to the total points that they have earned.
 *
 * takes in every user in a sorted list from the server
 * displays the top 100 users in a list to the screen
 */

public class LeaderBoardFragment extends Fragment{
    private static final int TOP_100_USER = 100;
    private static final int STORE_FRAGMENT_CODE = 77291;
    private static final String ERROR_DESCRIPTION = "ERROR RETRIEVING DESCRIPTION";
    private static final String TAG = "LeaderBoardFragment";


    private ListView leaderBoardListView;
    private LinkedHashSet<String> userNameList = new LinkedHashSet<>();
    private LinkedHashSet<User> userLinkedHashSet = new LinkedHashSet<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            // TODO: Add code here to ensure we don't override saved state
        }
    }

    @Override
    public void onStart() {
        super.onStart();


        retrieveUserListInfo();
        setupListView();
        updateUi();
    }


    private void setupListView(){
        leaderBoardListView = this.getView().findViewById(R.id.leaderBoardListViewId);
    }

    private void updateUi(){
        updateList();
    }

    private void retrieveUserListInfo(){
        ServerHandler.getAllUsers(getActivity(), this::userListInfo);

    }

    private void userListInfo(List<User> userList) {
        userLinkedHashSet = new LinkedHashSet<>();
        userLinkedHashSet.addAll(userList);
        populateListView();
    }

    private void updateList() {
        populateListView();
    }

    private void populateListView(){
        populateNameList();
//        getToastMessage();        // debugging
        List<String> boardListContent = new ArrayList<>();
        for(int i = 0; i < getSortedUserListByPoints().size(); i++){
            if(i < TOP_100_USER){
                User user = getSortedUserListByPoints().get(i);
                boardListContent = getContentForLeaderBoard(user, boardListContent, (i+1));
            }
        }
        setAdapterContent(boardListContent);
    }

    // used for debugging:
    private void getToastMessage(){
        Toast.makeText(getActivity(), "The size of name list is: "
                + userNameList.size(), Toast.LENGTH_SHORT).show();
    }

    private List<User> getSortedUserListByPoints(){
        List<User> allUserListCast = new ArrayList<>();
        for(User user : userLinkedHashSet){
            validateUserTotalPoints(user);
            allUserListCast.add(user);
        }
        Collections.sort(allUserListCast);
        return allUserListCast;
    }

    private void validateUserTotalPoints(User user){
        if(user.getTotalPointsEarned() == null){
            RewardHandler.getInstance().setUserTotalPointsInServer(user, 0);
        }
    }

    private List<String> getContentForLeaderBoard(User user,
                                                  List<String> boardListContent,
                                                  int rankNum){

        String[] str = user.getName().split(" ");
        boardListContent.add("Rank: " + rankNum + ". " + str[0] + " "
                + String.valueOf(str[1].charAt(0))
                + "\t\t points: "
                + user.getTotalPointsEarned());
        return boardListContent;
    }

    private void setAdapterContent(List<String> nameListCast){
        ArrayAdapter adapter = new ArrayAdapter<>(
                getActivity(), R.layout.list_of_groups, nameListCast);

        leaderBoardListView.setAdapter(adapter);
    }

    private void populateNameList(){
        for(User user : userLinkedHashSet){
            userNameList.add(user.getName());
        }
    }

    // ..................................................................................... //
    public static Intent makeIntentForStoreFragment(Context context) {
        return new Intent(context, LeaderBoardFragment.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.leader_board_fragment, container, false);
    }
}
