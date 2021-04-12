package ca.cmpt276.walkinggroup.handler;

import android.content.Context;

import java.util.List;

import ca.cmpt276.walkinggroup.model.GPS;
import ca.cmpt276.walkinggroup.model.Group;
import ca.cmpt276.walkinggroup.model.Message;
import ca.cmpt276.walkinggroup.model.PermissionRequest;
import ca.cmpt276.walkinggroup.model.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * Deals with server calls on a higher level so that other devs
 * do not need to worry about the server implementation details
 *
 * takes in login information or ids
 * returns model details
 *
 * takes in model details
 * notifies server of changes to data
 */

public class ServerHandler {
    /**
     *  context: The context in which the method is called in
     *  callback: The client-side method that will run after a successful server call
     *  Object (Optional): Data that will be passed in for use
     */

    private static WGServerProxy proxy;

    public static void setProxy(WGServerProxy newProxy) {
        proxy = newProxy;
    }

    public static void setOnReceiveToken(ProxyBuilder.SimpleCallback<String> callback) {
        ProxyBuilder.setOnTokenReceiveCallback(callback);
    }

    public static void login(Context  context,
                             ProxyBuilder.SimpleCallback<Void> callback,
                             User user) {

        Call<Void> caller = proxy.login(user);
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void register(Context context,
                                ProxyBuilder.SimpleCallback<User> callback,
                                User user
    ) {
        Call<User> caller = proxy.createNewUser(user);
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void getUserWithId(Context context,
                                        ProxyBuilder.SimpleCallback<User> callback,
                                        Long id
    ) {

        Call<User> caller = proxy.getUserById(id);
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void getUserWithEmail(Context context,
                                        ProxyBuilder.SimpleCallback<User> callback,
                                        String email
    ) {

        Call<User> caller = proxy.getUserByEmail(email);
        ProxyBuilder.callProxy(context, caller, callback);
    }


    public static void getAllUsers(Context context, ProxyBuilder.SimpleCallback<List<User>> callback){
        Call<List<User>> caller = proxy.getUsers();
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void editUserWIthId(Context context,
                                      ProxyBuilder.SimpleCallback<User> callback,
                                      Long id,
                                      User user
    ) {

        Call<User> caller = proxy.editUser(id, user);
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void getUserLastLocation(Context context,
                                           ProxyBuilder.SimpleCallback<GPS> callback,
                                           Long id
    ) {

        Call<GPS> caller = proxy.getLastLocation(id);
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void setUserLastLocation(Context context,
                                           ProxyBuilder.SimpleCallback<GPS> callback,
                                           Long id,
                                           GPS gps
    ) {

        Call<GPS> caller = proxy.setLastLocation(id, gps);
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void addUserToMonitoredByList(Context context,
                                          ProxyBuilder.SimpleCallback<List<User>> callback,
                                          User targetUser
    ) {

        Call<List<User>> caller = proxy.addMonitoredBy(PreferenceHandler.getInstance()
                .getLoggedInUserId(context), targetUser);
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void getMonitoredByList(Context context,
                           ProxyBuilder.SimpleCallback<List<User>> callback
    ) {

        Call<List<User>> caller = proxy.getMonitoredBy(
                PreferenceHandler
                        .getInstance()
                        .getLoggedInUserId(context));
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void addUserToMonitoringList(Context context,
                                                ProxyBuilder.SimpleCallback<List<User>> callback,
                                                User targetUser
    ) {

        Call<List<User>> caller = proxy.addMonitoring(PreferenceHandler.getInstance()
                .getLoggedInUserId(context), targetUser);
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void getMonitoringList(Context context,
                                          ProxyBuilder.SimpleCallback<List<User>> callback
    ) {

        Call<List<User>> caller = proxy.getMonitoring(
                PreferenceHandler
                        .getInstance()
                        .getLoggedInUserId(context));
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void deleteUserFromMonitoring(Context context,
                                                ProxyBuilder.SimpleCallback<Void> callback,
                                                User targetUser
    ) {

        Call<Void> caller = proxy.deleteMonitoring(
                PreferenceHandler
                        .getInstance()
                        .getLoggedInUserId(context),
                targetUser.getId());
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void deleteUserFromMonitoredBy(Context context,
                                                ProxyBuilder.SimpleCallback<Void> callback,
                                                User targetUser
    ) {

        Call<Void> caller = proxy.deleteMonitoredBy(
                PreferenceHandler
                        .getInstance()
                        .getLoggedInUserId(context),
                targetUser.getId());
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void getGroupsList(Context context,
                                     ProxyBuilder.SimpleCallback<List<Group>> callback
    ) {

        Call<List<Group>> caller = proxy.getGroups();
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void addGroupToList(Context context,
                                     ProxyBuilder.SimpleCallback<Group> callback,
                                     Group group
    ) {

        Call<Group> caller = proxy.addGroup(group);
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void getGroupDetails(Context context,
                                      ProxyBuilder.SimpleCallback<Group> callback,
                                      Long groupId
    ) {

        Call<Group> caller = proxy.getGroupDetails(groupId);
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void deleteGroup(Context context,
                                       ProxyBuilder.SimpleCallback<Void> callback,
                                       Group group
    ) {

        Call<Void> caller = proxy.deleteGroup(group.getId());
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void getMembersFromGroup(Context context,
                                   ProxyBuilder.SimpleCallback<List<User>> callback,
                                   Long groupId
    ) {

        Call<List<User>> caller = proxy.getMembers(groupId);
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void addMemberToGroup(Context context,
                                        ProxyBuilder.SimpleCallback<List<User>> callback,
                                        Long groupId,
                                        User user
    ) {

        Call<List<User>> caller = proxy.addMember(groupId, user);
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void deleteMemberFromGroup(Context context,
                                   ProxyBuilder.SimpleCallback<Void> callback,
                                   Long groupId, Long userId
    ) {

        Call<Void> caller = proxy.deleteMember(groupId, userId);
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void getAllMessages(Context context,
                                      ProxyBuilder.SimpleCallback<List<Message>> callback)
    {
        Call<List<Message>> caller = proxy.getAllMessages();
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void getAllMessagesForUser(Context context,
                                             ProxyBuilder.SimpleCallback<List<Message>> callback,
                                             Long userId){

        Call<List<Message>> caller = proxy.getAllMessagesForUser(userId);
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void getAllMessagesForUser(Context context,
                                             ProxyBuilder.SimpleCallback<List<Message>> callback,
                                             Long userId,
                                             String status
    ){
        Call<List<Message>> caller = proxy.getAllMessagesForUser(userId, status);
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void getEmergencyMessageForAll(Context context,
                                                 ProxyBuilder.SimpleCallback<List<Message>> callback){
        Call<List<Message>> caller = proxy.getEmergencyMessageForAll();
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void sendMessage(Context context,
                                   ProxyBuilder.SimpleCallback<Message> callback,
                                   Message message,
                                   Long groupId)
    {
        Call<Message> caller = proxy.sendMessage(groupId, message);
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void sendEmergencyMessage(Context context,
                                      ProxyBuilder.SimpleCallback<Message> callback,
                                      Message message,
                                      Long userId){
        Call<Message> caller = proxy.sendEmergencyMessage(userId, message);
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void getMessage(Context context,
                                  ProxyBuilder.SimpleCallback<Message> callback,
                                  Long messageId){
        Call<Message> caller = proxy.getMessage(messageId);
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void deleteMessage(Context context,
                                     ProxyBuilder.SimpleCallback<Void> callback,
                                     Long messageId){
        Call<Void> caller = proxy.deleteMessage(messageId);
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void markMessage(Context context,
                                     ProxyBuilder.SimpleCallback<User> callback,
                                     Long messageId,
                                     Long userId,
                                     Boolean isMessageRead){
        Call<User> caller = proxy.markMessage(messageId, userId, isMessageRead);
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void getPermissions(Context context,
                                      ProxyBuilder.SimpleCallback<List<PermissionRequest>> callback
    ) {
        Call<List<PermissionRequest>> caller = proxy.getPermissions();
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void getPermissionsForUser(Context context,
                                                       ProxyBuilder.SimpleCallback<List<PermissionRequest>> callback,
                                                       Long userId
    ) {
        Call<List<PermissionRequest>> caller = proxy.getPermissionsForUser(userId);
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void getPermissionsForUserWithStatus(Context context,
                                                       ProxyBuilder.SimpleCallback<List<PermissionRequest>> callback,
                                                       Long userId,
                                                       WGServerProxy.PermissionStatus status
    ) {
        Call<List<PermissionRequest>> caller = proxy.getPermissionsForUserWithStatus(userId, status);
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void getPermissionById(Context context,
                                         ProxyBuilder.SimpleCallback<PermissionRequest> callback,
                                         Long permissionId
    ) {
        Call<PermissionRequest> caller = proxy.getPermissionById(permissionId);
        ProxyBuilder.callProxy(context, caller, callback);
    }

    public static void markPermissionRequest(Context context,
                                             ProxyBuilder.SimpleCallback<PermissionRequest> callback,
                                             Long permissionId,
                                             WGServerProxy.PermissionStatus status
    ) {
        Call<PermissionRequest> caller = proxy.approveOrDenyPermissionRequest(permissionId, status);
        ProxyBuilder.callProxy(context, caller, callback);
    }



}
