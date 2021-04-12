package ca.cmpt276.walkinggroup.handler;

import android.content.Context;
import android.widget.Toast;

import ca.cmpt276.walkinggroup.model.User;

/**
 * Reward handler implements the server features for
 * the Gamification aspect of the app. It provides
 * function calls that the developers can use to send
 * and receive data from the server.
 * Created by jnhkm on 2018-04-04.
 *
 * communicates with the server the number of points the user has acquired
 *
 * takes in model data
 * outputs number of points to server
 */

public class RewardHandler {
    private final Integer POINTS_TO_GAIN = 5;
//    private final Integer USER_DEFAULT_POINTS = 60

    private static RewardHandler instance;

    private Context context;

    private RewardHandler() {
        // do nothing
    }

    public static RewardHandler getInstance() {
        if (instance == null) {
            instance = new RewardHandler();
        }

        return instance;
    }

    public void displayTotalPoints(Context context) {
        this.context = context;

        ServerHandler.getUserWithId(
                context,
                this::responseDisplayTotalPoints,
                PreferenceHandler.getInstance().getLoggedInUserId(context)
        );
    }

    public void responseDisplayTotalPoints(User user) {
        Toast.makeText(context, "You have " + user.getTotalPointsEarned(), Toast.LENGTH_SHORT).show();
    }

    public void addPoints(Context context) {
        this.context = context;

        ServerHandler.getUserWithId(
                context,
                this::getUserWithIdToAddPoints,
                PreferenceHandler.getInstance().getLoggedInUserId(context)
        );
    }

    public void spendPoints(Context context) {
        this.context = context;

        ServerHandler.getUserWithId(
                context,
                this::getUserWithIdToDecreasePoints,
                PreferenceHandler.getInstance().getLoggedInUserId(context)
        );
    }

    private User incrementPoints(User user) {
        Integer curPoints = 0;
        if(user.getCurrentPoints() != null){
            curPoints = user.getCurrentPoints();
        }

        Integer totalPoints = 0;
        if(user.getTotalPointsEarned() != null){
            totalPoints = user.getTotalPointsEarned();
        }

        curPoints += POINTS_TO_GAIN;
        totalPoints += POINTS_TO_GAIN;

        user.setCurrentPoints(curPoints);
        user.setTotalPointsEarned(totalPoints);

        return user;
    }

    private User decrementPoints(User user) {
        Integer curPoints = user.getCurrentPoints();
        Integer totalPoints = user.getTotalPointsEarned();

        curPoints -= POINTS_TO_GAIN;
//        totalPoints -= POINTS_TO_GAIN;

        user.setCurrentPoints(curPoints);
        user.setTotalPointsEarned(totalPoints);

        return user;
    }

    private void getUserWithIdToDecreasePoints(User user) {
        ServerHandler.editUserWIthId(
                context,
                this::responseEditUser,
                PreferenceHandler.getInstance().getLoggedInUserId(context),
                decrementPoints(user)
        );
    }

    private void getUserWithIdToAddPoints(User user) {
        ServerHandler.editUserWIthId(
                context,
                this::responseEditUser,
                PreferenceHandler.getInstance().getLoggedInUserId(context),
                incrementPoints(user)
        );
    }

    private void responseEditUser(User user) {
        // do nothing
    }

    // ................. testing by Kourosh ................... //
    public void setUserCurrentPointsInServer(User user, int currentPoints) {
        user.setCurrentPoints(currentPoints);
        ServerHandler.editUserWIthId(
                context,
                this::responseEditUser,
                PreferenceHandler.getInstance().getLoggedInUserId(context),
                user
        );
    }

    public void setUserTotalPointsInServer(User user, int totalPoints){
        user.setTotalPointsEarned(totalPoints);
        ServerHandler.editUserWIthId(
                context,
                this::responseEditUser,
                PreferenceHandler.getInstance().getLoggedInUserId(context),
                user
        );
    }


}
