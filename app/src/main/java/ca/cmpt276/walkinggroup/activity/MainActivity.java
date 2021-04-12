package ca.cmpt276.walkinggroup.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.cmpt276walkinggroupproject.walkinggroup.R;

import java.util.List;

import ca.cmpt276.walkinggroup.fragment.AccountFragment;
import ca.cmpt276.walkinggroup.fragment.GoogleMapFragment;
import ca.cmpt276.walkinggroup.fragment.GroupsFragment;
import ca.cmpt276.walkinggroup.fragment.LeaderBoardFragment;
import ca.cmpt276.walkinggroup.fragment.MessageFragment;
import ca.cmpt276.walkinggroup.fragment.MonitorFragment;
import ca.cmpt276.walkinggroup.fragment.PermissionsFragment;
import ca.cmpt276.walkinggroup.fragment.StoreFragment;
import ca.cmpt276.walkinggroup.handler.ServerHandler;
import ca.cmpt276.walkinggroup.model.Group;
import ca.cmpt276.walkinggroup.model.GroupCollection;
import ca.cmpt276.walkinggroup.model.Titles;
import ca.cmpt276.walkinggroup.model.User;
import ca.cmpt276.walkinggroup.handler.PreferenceHandler;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;

/**
 * Help from Android website for fragments + side menu:
 * https://developer.android.com/training/implementing-navigation/nav-drawer.html#DrawerLayout
 *
 * Displays and controls the flow of fragments that are visible to the user screen
 * takes no inputs and sets the screen
 *
 */

public class MainActivity extends AppCompatActivity {
    private static final String USER_ID_TAG = "user_id_tag_esketit";
    private static final String USER_LOGGED_IN_EMAIL = "currentUserLoggedInEmail";
    private static final String USER_LOGGED_IN_TOKEN = "currentUserLoggedInToken";

    private final String MAP_FRAGMENT_TAG = "map_tag";
    private final String MONITOR_FRAGMENT_TAG = "monitor_tag";
    private final String GROUPS_FRAGMENT_TAG = "groups_tag";
    private final String ACCOUNT_FRAGMENT_TAG = "account_tag";
    private final String MESSAGE_FRAGMENT_TAG = "message_tag";
    private final String PERMISSIONS_FRAGMENT_TAG = "permissions_tag";
    private final String STORE_FRAGMENT_TAG = "store_tag";
    private final String LEADER_BOARD_FRAGMENT_TAG = "leader_board_tag";

    private User user = new User();

    private DrawerLayout mDrawerLayout;

    private static WGServerProxy proxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupProxy();
        ServerHandler.setProxy(proxy);

        initToolBar();
        initActionBar();
        initNavigationClicks();

        setDefaultFragment();

        initGroupsFromServer();
    }

    /**
     * Fragment methods
     */

    private void setDefaultFragment() {
        onItemSelected(R.id.nav_map);
    }

    private void displayFragment(Fragment fragmentClass, String tag ) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = manager.findFragmentByTag(tag);

        if (fragment == null) {
            transaction.replace(R.id.fragment_container, fragmentClass, tag);
        }
        else {
            transaction.replace(R.id.fragment_container, fragment);
        }

        transaction.commit();
    }

    /**
     * Action methods
     */

    // Reacts to selections on the menu
    private void onItemSelected(int id) throws IllegalArgumentException {
        switch(id) {
            case R.id.nav_map:
                displayFragment(new GoogleMapFragment(), MAP_FRAGMENT_TAG);
                break;

            case R.id.nav_monitor:
                displayFragment(new MonitorFragment(), MONITOR_FRAGMENT_TAG );
                break;

            case R.id.nav_groups:
                displayFragment(new GroupsFragment(), GROUPS_FRAGMENT_TAG);
                break;

            case R.id.nav_account:
                displayFragment(new AccountFragment(), ACCOUNT_FRAGMENT_TAG);
                break;

            case R.id.nav_message:
                displayFragment(new MessageFragment(), MESSAGE_FRAGMENT_TAG);
                break;

            case R.id.nav_permissions:
                displayFragment(new PermissionsFragment(), PERMISSIONS_FRAGMENT_TAG);
                break;

            case R.id.nav_leader_board:
                displayFragment(new LeaderBoardFragment(), LEADER_BOARD_FRAGMENT_TAG);
                break;

            case R.id.nav_store:
                displayFragment(new StoreFragment(), STORE_FRAGMENT_TAG);
                break;

            case R.id.nav_logout:
                backToLogin();
                break;

            default:
                throw new IllegalArgumentException("Illegal ID was selected");
        }
    }

    // Reacts by opening the side-menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        setupLoggedInUserName();

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadUserFromServer(){
        ServerHandler.getUserWithEmail
                (MainActivity.this, this::getUserInstance,
                        PreferenceHandler.getInstance().getLoggedInUserEmail(MainActivity.this));
    }

    private void getUserInstance(User user) {
        this.user = user;
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadUserFromServer();

        Titles newSingleton = user.getDataFromCustomJson(
                user.getTitles(),
                user.getCustomJson());
        Titles.setInstance(newSingleton);
        setupLoggedInUserName();
    }

    /**
     * Init Methods
     */

    private void initGroupsFromServer() {
        ServerHandler.getGroupsList(
                MainActivity.this,
                MainActivity.this::responseSetGroupCollection);
    }

    private void setupProxy() {
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), PreferenceHandler.getInstance()
                .getLoggedInUserToken(MainActivity.this));
    }

    private void setupLoggedInUserName() {
        ServerHandler.getUserWithEmail(
                MainActivity.this,
                MainActivity.this::response,
                PreferenceHandler.getInstance().getLoggedInUserEmail(this));
    }

    private void initActionBar() {
        ActionBar actionbar = getSupportActionBar();

        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
    }

    private void initToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initNavigationClicks() {
        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);

                        mDrawerLayout.closeDrawers();

                        try {
                            onItemSelected(menuItem.getItemId());
                        } catch (IllegalArgumentException e) {
                            // TODO: Decide what to do if an illegal ID was somehow selected
                            // How would this even be possible tbh lol
                        }

                        return true;
                    }
                });
    }

    /**
     * Responses
     */

    private void responseSetGroupCollection(List<Group> groups) {
        GroupCollection.getInstance().setGroups(groups);
    }

    private void response(User user) {
        setUserLoggedInName(user.getName());
        savePreferences(user);
    }

    private void setUserLoggedInName(String name) {
        TextView view = findViewById(R.id.txtUserLoggedName);
        String label = getResources().getString(R.string.app_name) + " - " + name;

        view.setText(label);
    }

    private void savePreferences(User user) {
        saveIdPreference(user.getId());
    }

    private void saveIdPreference(Long id) {
        PreferenceHandler.getInstance()
                .saveUserId(MainActivity.this, id);
    }

    private void removeAllSavedPreferences() {
        PreferenceHandler.getInstance().removeSavedToken(MainActivity.this);
        PreferenceHandler.getInstance().removeSavedEmail(MainActivity.this);
        PreferenceHandler.getInstance().removeSavedId(MainActivity.this);
    }

    /**
     * Intent methods
     */

    private void backToLogin() {
        removeAllSavedPreferences();

        Intent intent = LoginActivity.makeIntent(MainActivity.this);
        startActivity(intent);

        finish();
    }

    static public Intent makeIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }


    public static String getUserLoggedInIdTag() {return USER_ID_TAG;}
    public static String getUserLoggedInEmailTag() {
        return USER_LOGGED_IN_EMAIL;}
    public static String getUserLoggedInTokenTag() { return USER_LOGGED_IN_TOKEN;}
}
