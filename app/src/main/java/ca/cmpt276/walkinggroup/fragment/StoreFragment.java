package ca.cmpt276.walkinggroup.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmpt276walkinggroupproject.walkinggroup.R;

import ca.cmpt276.walkinggroup.dialog.StorePurchaseDialog;
import ca.cmpt276.walkinggroup.dialog.StoreSetUserNicknameDialog;
import ca.cmpt276.walkinggroup.handler.PreferenceHandler;
import ca.cmpt276.walkinggroup.handler.RewardHandler;
import ca.cmpt276.walkinggroup.handler.ServerHandler;
import ca.cmpt276.walkinggroup.model.Titles;
import ca.cmpt276.walkinggroup.model.User;

/**
 * This class provides implements the Gamification aspect
 * of the application. It provides the user to be able to
 * use in app points to buy titles to change their nickname
 * from blank/none to an interesting name.
 *
 * takes in a User's titles as system input
 * displays owned titles and titles available for purchase
 */

public class StoreFragment extends Fragment
        implements StorePurchaseDialog.onInputSelected, StoreSetUserNicknameDialog.onInputSelected {

    private static final int STORE_FRAGMENT_CODE = 45762;
    private static final String ERROR_DESCRIPTION = "ERROR RETRIEVING DESCRIPTION";
    private static final String TAG = "StoreFragment";

    private ListView lockedStoreListView;
    private ListView unlockedStoreListView;
    private static User loggedUser = new User();
    private StorePurchaseDialog dialogObj = new StorePurchaseDialog();

    private boolean isTitlePositionSet = false;
    private Integer globalTitlePosition;

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

        updateUi();                     // deal with list views etc
        setOnClickListener();           // ask to buy title
        initListLongClickToSetTitle();  // ask to set title for user
    }

    private void updateUi() {
        initLoggedUser();
        updateList();
    }

    private void initLoggedUser() {
        ServerHandler.getUserWithEmail(
                getActivity(), this::receiveUserInfo,
                PreferenceHandler.getInstance().getLoggedInUserEmail(getActivity()));
    }

    private void receiveUserInfo(User user) {
        loggedUser = user;
        Titles.setInstance(loggedUser.getDataFromCustomJson(
                user.getTitles(),
                user.getCustomJson()));

        populateListView();
        // ................. testing .................. //
        if (loggedUser.getCurrentPoints() == null)
        RewardHandler.getInstance().setUserCurrentPointsInServer(loggedUser, 80);

        if (loggedUser.getTotalPointsEarned() == null)
            RewardHandler.getInstance().setUserTotalPointsInServer(loggedUser, 80);
        // ............................................ //

        updateTextViewForCurrentPoints();
    }

    @SuppressLint("SetTextI18n")
    private void updateTextViewForCurrentPoints() {
        TextView currentPointsTV = getView().findViewById(R.id.CurrentPointsTvId);
        currentPointsTV.setText("Current points: " + String.valueOf(loggedUser.getCurrentPoints()));
    }

    private void updateList() {
        populateListView();
    }

    private void populateListView() {
        // ................ set up adapter for locked list view ................ //
        lockedStoreListView = getView().findViewById(R.id.listViewlockedItemsId);

        ArrayAdapter lockedAdapter = new ArrayAdapter<>(
                getActivity(), R.layout.list_of_monitors, Titles.getInstance().getLockedTitles());

        lockedStoreListView.setAdapter(lockedAdapter);

        // ................ set up adapter for unlocked list view................ //
        unlockedStoreListView = getView().findViewById(R.id.listViewUnlockedItemsId);

        ArrayAdapter unlockedAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_of_monitors,
                Titles.getInstance().getUnlockedTitles());

        unlockedStoreListView.setAdapter(unlockedAdapter);
    }

    private void setOnClickListener() {
        lockedStoreListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                askToBuyDialog(position);
            }
        });
    }

    private void askToBuyDialog(int position) {
        dialogObj.setDescriptionPosition(position);
        dialogObj.setTargetFragment(StoreFragment.this, STORE_FRAGMENT_CODE);
        dialogObj.setCancelable(false);
        dialogObj.show(getFragmentManager(), StorePurchaseDialog.getDialogTag());
    }

    // ..................................................................................... //
    public static Intent makeIntentForStoreFragment(Context context) {
        return new Intent(context, StoreFragment.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.store_fragment, container, false);
    }
    // ..................................................................................... //

    @SuppressLint("SetTextI18n")
    @Override
    public void setDeleteInputPosition(int positionOfTitle) {
        // test
        updateLockedListView();
        updateUnlockedListView();

        if(positionOfTitle <= 0
                && (Titles.getInstance().getLockedTitles().size() == 0
                        || Titles.getInstance().getLockedScoreList().size() == 0)){
            return;
        }
        // ............ Compare User points vs Title cost ............ //
        boolean doesUserPointMeetsCost =
                ((loggedUser.getCurrentPoints()
                        - Titles.getInstance().getLockedScoreList().get(positionOfTitle)) >= 0);

        if (!doesUserPointMeetsCost) {
            // ............... Warn User ............... //
            toastUnsuccessfulMsg();
        } else {
            // ............ Congratulate User ............ //
            toastSuccessfulMsg(positionOfTitle);
            // ............... Unlock Title .............. //
            unlockTitle(positionOfTitle);
            removeLockedTitleAndItsScore(positionOfTitle);      // remove locked title and score
            // ............... List Views ................ //
            if(positionOfTitle > 0){
                positionOfTitle -= 1;
            }
            setupLockedListView();
            setupUnlockedListView();
            // .......... Update Current Points .......... //
            decreaseUserCurrentPoints(positionOfTitle);
            // ............ Update Text View  ............ //
            updateTextViewForCurrentPoints();
        }
        updateLockedListView();
        updateUnlockedListView();
    }

    private void toastUnsuccessfulMsg() {
        Toast.makeText(getContext(),
                "You don't have enough points to buy this title.",
                Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("LongLogTag")
    private void toastSuccessfulMsg(int positionOfTitle) {
        Toast.makeText(getActivity(),
                ("Congratulations, You unlocked: "
                        + Titles.getInstance().getLockedTitles().get(positionOfTitle)),
                Toast.LENGTH_SHORT).show();
    }

    private void unlockTitle(int positionOfTitle) {
        Titles.getInstance().addUnlockedTitle(
                Titles.getInstance().getLockedTitles().get(positionOfTitle));
    }

    private void removeLockedTitleAndItsScore(int positionOfTitle) {
        Titles.getInstance().removeLockedTitle(positionOfTitle);
        Titles.getInstance().removeLockedScore(positionOfTitle);
        populateListView();
    }

    private void setupLockedListView() {
        loggedUser.sendDataToCustomJson(Titles.getInstance());
    }

    private void updateLockedListView() {
        lockedStoreListView = getView().findViewById(R.id.listViewUnlockedItemsId);

        ArrayAdapter adapter = new ArrayAdapter<>(
                getActivity(), R.layout.list_of_monitors, Titles.getInstance().getLockedTitles());

        lockedStoreListView.setAdapter(adapter);
    }


    private void setupUnlockedListView() {
        loggedUser.sendDataToCustomJson(Titles.getInstance());
    }

    private void updateUnlockedListView() {
        unlockedStoreListView = getView().findViewById(R.id.listViewUnlockedItemsId);

        ArrayAdapter adapter = new ArrayAdapter<>(
                getActivity(), R.layout.list_of_monitors, Titles.getInstance().getUnlockedTitles());

        unlockedStoreListView.setAdapter(adapter);
    }

    private void decreaseUserCurrentPoints(int positionOfTitle) {
        if(positionOfTitle >= 0 && Titles.getInstance().getLockedScoreList().size() > 0){
            loggedUser.setCurrentPoints(loggedUser.getCurrentPoints()
                    - Titles.getInstance().getLockedScoreList().get(positionOfTitle));

            RewardHandler.getInstance().setUserCurrentPointsInServer(
                    loggedUser, loggedUser.getCurrentPoints());
        }
    }

    // .................. Set the user title if long pressed ................... //
    private void initListLongClickToSetTitle() {
        unlockedStoreListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
                startNicknameSetupDialog(pos);
                return true;
            }
        });
    }

    private void startNicknameSetupDialog(int positionOfTitle) {
        StoreSetUserNicknameDialog dialog = new StoreSetUserNicknameDialog();
        dialog.setDescriptionPosition(positionOfTitle);
        dialog.setTargetFragment(StoreFragment.this, STORE_FRAGMENT_CODE);
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), StorePurchaseDialog.getDialogTag());
    }

    @Override
    public void setNicknamePosition(int positionOfDescription) {
        String saveUserName = loggedUser.getName();

        if (Titles.getInstance().getUnlockedTitles().size() == 0) {
            updateLockedListView();
            updateUnlockedListView();
        }
        loggedUser.setName(saveUserName
                + " (Nickname: "
                + Titles.getInstance().getUnlockedTitles().get(positionOfDescription)
                + ")");

        Toast.makeText(getActivity(), ("Nickname is set, go to side panel to see the change."),
                Toast.LENGTH_LONG).show();

        // ............. edit the user in the server ............... //
        ServerHandler.editUserWIthId(getActivity(), this::response,
                PreferenceHandler.getInstance().getLoggedInUserId(getActivity()), loggedUser);

        // ............. rest back user old info ................ //
        loggedUser.setName(saveUserName);
    }

    private void response(User user) {
        // do nothing
    }
}