package ca.cmpt276.walkinggroup.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.cmpt276walkinggroupproject.walkinggroup.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import ca.cmpt276.walkinggroup.dialog.AddGroupDialog;
import ca.cmpt276.walkinggroup.dialog.GroupEditDialog;
import ca.cmpt276.walkinggroup.dialog.SendGroupMessageDialog;
import ca.cmpt276.walkinggroup.handler.PreferenceHandler;
import ca.cmpt276.walkinggroup.handler.RewardHandler;
import ca.cmpt276.walkinggroup.handler.ServerHandler;
import ca.cmpt276.walkinggroup.model.GPS;
import ca.cmpt276.walkinggroup.model.Group;
import ca.cmpt276.walkinggroup.model.Message;
import ca.cmpt276.walkinggroup.model.User;

/**
 * Displays the map with associated group locations using the API provided
 * by Brian Fraser
 *
 * Shows groups that may be around the current user, and allows them to click and join
 * them if they wish
 *
 * Takes in a list of groups from system
 * shows the map of the current User's GPS location
 * shows list of groups with markers on their starting locations
 */

public class GoogleMapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        AddGroupDialog.OnInputSelected,
        SendGroupMessageDialog.OnInputSelected{
    private static final String TAG = "GoogleMapFragment";
    private static final int EMERGENCY_MESSAGE_CODE = 3102;
    private static final int ADD_GROUP_REQUEST_CODE = 24153;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private static Marker startLocation;
    private static Marker endLocation;
    private static int numLocationsSelected = 0;

    private List<LatLng> latLngList = new ArrayList<>();
    private List<User> monitoredUsersGPSLocations = new ArrayList<>();
    private Group selectedGroup = new Group();
    private boolean isWalkingWithGroup = false;

    private boolean isLocationAlertDisplayed = false;

    private LocationManager manager;

    private GoogleMap googleMap;
    private CameraPosition cameraPosition;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int LOCATION_PERMISSIONS = 1;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;
    LocationCallback locationCallback;
    LocationRequest locationRequest;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Display the layout to the screen
        View view = inflater.inflate(R.layout.google_map_fragment, container, false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (googleMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, googleMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setOnMarkerClickListener(this);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        getLocationPermission();

        // User needs to have FINE_LOCATION permission
        if (isPermissionGranted(FINE_LOCATION)) {
            getDeviceLocation();
            startLocationRequest();
        }

        resetMap();
    }

    private void resetMap() {
        removeMapLongClickListener();
        initActiveGroups();
        initAddGroupButton();
        hideEmergencyButton();
        initActiveUserLocations();

        updateLocationUI();
    }

    @Override
    public void onResume() {
        super.onResume();

        // User needs to have FINE_LOCATION permission
        if (isPermissionGranted(FINE_LOCATION)) {
            getDeviceLocation();
            startLocationRequest();
        }

        if (googleMap != null)
            resetMap();
    }

    @Override
    public void onPause() {
        super.onPause();

        stopLocationRequest();
        googleMap.clear();
    }

    @Override
    public void sendGroupInput(String input) {
        Group group = initGroupFromInputs(input);

        removeMapLongClickListener();
        ServerHandler.addGroupToList(getActivity(), this::responseAddGroup, group);
    }

    @Override
    public void sendGroupCancel() {
        removeMapLongClickListener();
        resetGroupLocationSettings();

        changeAddGroupButtonToAddGroup();
    }

    @Override
    public void setMessageInput(String message) {
        Message msg = new Message();
        msg.setText(message);
        msg.setEmergency(true);

        ServerHandler.sendMessage(
                getActivity(),
                this::responseSendEmergencyMessage,
                msg,
                selectedGroup.getId()
        );
    }

    private void responseSendEmergencyMessage(Message message) {
        // Nothing
    }

    private void initActiveUserLocations() {
        monitoredUsersGPSLocations = new ArrayList<>();

        ServerHandler.getUserWithId(
                getActivity(),
                this::responseGetUserLocations,
                PreferenceHandler.getInstance().getLoggedInUserId(getActivity())
        );
    }

    private void responseGetUserLocations(User user) {
        for (User monitored : user.getMonitorsUsers()) {
            ServerHandler.getUserWithId(
                    getActivity(),
                    this::responseDrawUserLocations,
                    monitored.getId()
            );
        }
    }

    private void responseDrawUserLocations(User user) {
        if (googleMap != null && user.getLastGpsLocation().getTimestamp() != null) {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);

            for (User member : selectedGroup.getMemberUsers()) {
                if (member.getId().equals(user.getId()))
                    if (user.getLastGpsLocation() != null) {
                        Marker marker = drawMarker(
                                user.getName(),
                                BitmapDescriptorFactory.HUE_BLUE,
                                user.getLastGpsLocation().getLat(),
                                user.getLastGpsLocation().getLng());
                        marker.setSnippet(formatter.format(user.getLastGpsLocation().getTimestamp()));
                    }
            }
        }
    }

    private void initEmergencyButton() {
        Button button = getView().findViewById(R.id.emergencybtn);
        button.setVisibility(View.VISIBLE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendGroupMessageDialog dialog = new SendGroupMessageDialog();
                dialog.setTargetFragment(GoogleMapFragment.this, EMERGENCY_MESSAGE_CODE);
                dialog.setCancelable(false);
                dialog.show(getFragmentManager(), SendGroupMessageDialog.getDialogTag());
            }
        });
    }

    private void hideEmergencyButton() {
        Button button = getView().findViewById(R.id.emergencybtn);
        button.setVisibility(View.INVISIBLE);
    }

    private void initActiveGroups() {
        ServerHandler.getGroupsList(getActivity(), this::responseGetActiveGroups);
    }

    private void responseGetActiveGroups(List<Group> groups) {
        if (googleMap != null) {
            displayAllActiveGroups(groups);
        }
    }

    private void displayAllActiveGroups(List<Group> groups) {
        for (Group group : groups) {

            if (group.getRouteLatArray().size() > 0 &&
                    group.getRouteLngArray().size() > 0) {

                Marker marker = drawMarker(
                        group.getGroupDescription(),
                        BitmapDescriptorFactory.HUE_YELLOW,
                        group.getRouteLatArray().get(0),
                        group.getRouteLngArray().get(0));
                marker.setTag(group.getId());
            }
        }
    }

    private Marker drawMarker(String description, float color, double lat, double lng) {
        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(lat, lng));
        options.title(description);
        options.icon(BitmapDescriptorFactory
                .defaultMarker(color));

        Marker marker = googleMap.addMarker(options);

        return marker;
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        Group group = new Group();
        boolean isGroup = true;

        try {
            group.setId(Long.parseLong(marker.getTag().toString()));
        } catch (NullPointerException e) {
            group.setId(null);
        } catch ( NumberFormatException e1) {
            isGroup = false;
        }

        if (group.getId() != null && isGroup) {
            ServerHandler.getGroupDetails(
                    getActivity(),
                    this::responseOnMarkerClick,
                    group.getId());
        }

        return false;
    }

    private void responseOnMarkerClick(Group group) {
        selectedGroup = group;

        ServerHandler.getMembersFromGroup(getActivity(),
                this::responseGetMembersFromGroupOnMarkerClick,
                group.getId());
    }

    private void responseGetMembersFromGroupOnMarkerClick(List<User> users) {
        if (doesLeaderExist(selectedGroup) && isLeaderOfGroup(selectedGroup)) {
            drawSelectedGroupMarkers();
        }
        else if (isMemberOfGroup(users)){
            drawSelectedGroupMarkers();
        }
    }

    private boolean doesLeaderExist(Group group) {
        boolean retVal = false;

        if (group.getLeader() != null) {
            retVal = true;
        }

        return retVal;
    }

    private boolean isLeaderOfGroup(Group group) {
        return Objects.equals(
                PreferenceHandler.getInstance().getLoggedInUserId(getActivity()),
                group.getLeader().getId());
    }

    private boolean isMemberOfGroup(List<User> users) {
        boolean retVal = false;

        for (User user : users) {
            if (Objects.equals(
                    user.getId(),
                    PreferenceHandler.getInstance().getLoggedInUserId(getActivity()))) {

                retVal = true;
                break;
            }
        }

        return retVal;
    }

    private void drawSelectedGroupMarkers() {
        googleMap.clear();
        Toast.makeText(getActivity(), "Press/Hold screen to leave group", Toast.LENGTH_LONG).show();

        drawGroupMarkers(selectedGroup);
        initActiveUserLocations();
        initEmergencyButton();

        drawGroupLeaderMarker(selectedGroup);

        isWalkingWithGroup = true;
    }

    private void drawGroupLeaderMarker(Group group) {
        ServerHandler.getUserWithId(
                getActivity(),
                this::responseGroupLeader,
                group.getLeader().getId()
        );
    }

    private void responseGroupLeader(User user) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);

        if (user.getLastGpsLocation().getTimestamp() != null) {
            Marker marker = drawMarker(
                    "Leader: " + user.getName(),
                    BitmapDescriptorFactory.HUE_BLUE,
                    user.getLastGpsLocation().getLat(),
                    user.getLastGpsLocation().getLng());
            marker.setSnippet(formatter.format(user.getLastGpsLocation().getTimestamp()));
        }
    }

    private void drawGroupMarkers(Group group) {
        List<LatLng> geos = new ArrayList<>();
        for (int i = 0; i < group.getRouteLngArray().size(); i++) {
               geos.add(new LatLng(
                       group.getRouteLatArray().get(i),
                       group.getRouteLngArray().get(i)));
        }

        int i = 0;
        for (LatLng geo : geos) {
            Marker marker = drawMarker(
                    group.getGroupDescription(),
                    BitmapDescriptorFactory.HUE_MAGENTA,
                    geo.latitude,
                    geo.longitude);
            marker.setTag(group.getId());

            if (i == 0) {
                marker.setSnippet("Start Location");
                marker.showInfoWindow();
            }

            i++;
        }
    }

    private void changeAddGroupButtonToInstructions(String text) {
        Button button = getView().findViewById(R.id.add_group);

        button.setText(text);
    }

    private void changeAddGroupButtonToAddGroup() {
        Button button = getView().findViewById(R.id.add_group);

        button.setText(R.string.add_group);
    }

    private void initAddGroupButton() {
        Button button = getView().findViewById(R.id.add_group);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMapLongClickListener();
                changeAddGroupButtonToInstructions("Select/hold starting location");
            }
        });
    }

    private void responseAddGroup(Group group) {
        resetGroupLocationSettings();
        changeAddGroupButtonToAddGroup();
        initActiveGroups();
    }

    private void addMapLongClickListener() {
        numLocationsSelected = 0;

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (numLocationsSelected < 2) {
                    latLngList.add(latLng);

                    MarkerOptions options = new MarkerOptions();
                    options.position(latLng);

                    switch(numLocationsSelected) {
                        case 0:
                            startLocation = googleMap.addMarker(options);
                            numLocationsSelected++;
                            changeAddGroupButtonToInstructions("Select/hold end location");
                            break;
                        case 1:
                            endLocation = googleMap.addMarker(options);
                            numLocationsSelected++;
                            break;
                        default:
                            break;
                    }
                }

                if (numLocationsSelected >= 2) {
                    startDialogForGroupDescription();
                }
            }
        });
    }

    private Group initGroupFromInputs(String description) {
        User user = new User();
        Group group = new Group();

        user.setId(PreferenceHandler
                .getInstance()
                .getLoggedInUserId(getActivity()));

        group.setLeader(user);
        group.setGroupDescription(description);

        for (LatLng geo: latLngList) {
            group.addRouteLng(geo.longitude);
            group.addRouteLat(geo.latitude);
        }

        return group;
    }

    private void startDialogForGroupDescription() {
        setupAddGroupDialog();
    }

    private void removeMapLongClickListener() {
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                googleMap.clear();
                isWalkingWithGroup = false;
            }
        });
    }

    private void resetGroupLocationSettings() {
        startLocation.remove();
        endLocation.remove();

        latLngList = new ArrayList<>();
    }

    private void setupAddGroupDialog() {
        AddGroupDialog dialog = new AddGroupDialog();
        dialog.setTargetFragment(GoogleMapFragment.this, ADD_GROUP_REQUEST_CODE);

        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), AddGroupDialog.getDialogTag());
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (getActivity() != null) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();

                            if (lastKnownLocation != null) {
                                setDeviceLocation();
                            }
                        } else {
                            // DO NOTHING
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void setDeviceLocation() {
        updateLocationUI();
    }

    private void moveCameraToLastLocation() {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(lastKnownLocation.getLatitude(),
                        lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        isLocationAlertDisplayed = false;
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        isLocationAlertDisplayed = false;
                    }
                });
        final AlertDialog alert = builder.create();
        isLocationAlertDisplayed = true;
        alert.show();
    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        String[] permissions = {FINE_LOCATION};

        if (getActivity() != null) {
            if (!isPermissionGranted(FINE_LOCATION)) {
                requestPermissions(permissions, LOCATION_PERMISSIONS);
            }

            if (!isLocationTurnedOn(LocationManager.NETWORK_PROVIDER) &&
                    !isLocationAlertDisplayed) {
                buildAlertMessageNoGps();
            }
        }
    }

    private boolean isLocationTurnedOn(String provider) {
        return manager.isProviderEnabled(provider) && isPermissionGranted(FINE_LOCATION);
    }

    private boolean isPermissionGranted(String permission) {
        return getActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "LOCATIONS ENABLED", Toast.LENGTH_SHORT);

                    startLocationRequest();
                } else {
                    requestPermissions(permissions, LOCATION_PERMISSIONS);
                }
            }
        }

        updateLocationUI();
    }

    @SuppressLint("MissingPermission")
    protected void startLocationRequest() {
        setupLocationCallBack();
        setupLocationRequest(1000, 30000);

        fusedLocationProviderClient.requestLocationUpdates(locationRequest
                    , locationCallback
                    , null /* Looper */);

    }

    protected void stopLocationRequest() {
        if (locationCallback != null)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void setupLocationCallBack() {
         locationCallback = new LocationCallback() {
             @Override
             public void onLocationResult(LocationResult locationResult) {
                 // do work here
                 super.onLocationResult(locationResult);
                 lastKnownLocation = locationResult.getLastLocation();

                 if (lastKnownLocation != null) {
                     getDeviceLocation();

                     if (isWalkingWithGroup) {
                         sendLastGPSLocation();
                     }
                 }
             }

             @Override
             public void onLocationAvailability(LocationAvailability locationAvailability) {
                 if (locationAvailability.isLocationAvailable()) {
                     if (lastKnownLocation != null) {
                         getDeviceLocation();
                         moveCameraToLastLocation();
                     }
                }
                else {
                     getLocationPermission();
                 }
             }
         };
    }

    private void sendLastGPSLocation() {
        ServerHandler.getUserWithId(
                getActivity(),
                this::responseGetUserForLastGPSLocation,
                PreferenceHandler.getInstance().getLoggedInUserId(getActivity()));
    }

    private void responseGetUserForLastGPSLocation(User user) {
        GPS gps = new GPS(
                lastKnownLocation.getLatitude(),
                lastKnownLocation.getLongitude(),
                new Date(lastKnownLocation.getTime()));

        ServerHandler.setUserLastLocation(
                getActivity(),
                this::responseEditUserForLastGPSLocation,
                PreferenceHandler.getInstance().getLoggedInUserId(getActivity()),
                gps);
    }

    private void responseEditUserForLastGPSLocation(GPS gps) {
        Log.i(getTag(),"Last Location: " + gps);

        if (isAtDestination(gps)) {
            finish();
        }
    }

    private boolean isAtDestination(GPS gps) {
        return gps.getLat().equals(selectedGroup.getRouteLatArray().get(1)) &&
                gps.getLng().equals(selectedGroup.getRouteLngArray().get(1));
    }

    private void finish() {
        resetMap();

        RewardHandler
                .getInstance()
                .addPoints(getActivity());

        Toast.makeText(
                getActivity(),
                "You have reached your destination",
                Toast.LENGTH_SHORT).show();
    }

    private void responseEditUser(User user) {
        // Do nothing
    }

    private void setupLocationRequest(int minSpeed, int maxSpeed) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(maxSpeed);
        locationRequest.setFastestInterval(minSpeed);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (googleMap == null) {
            return;
        }
        try {
            if (isLocationTurnedOn(LocationManager.NETWORK_PROVIDER)) {
                googleMap.setMyLocationEnabled(true);
            } else {
                googleMap.setMyLocationEnabled(false);
            }
        } catch (SecurityException e)  {
            e.printStackTrace();
        }
    }
}
