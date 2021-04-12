package ca.cmpt276.walkinggroup.proxy;

import java.util.List;

import ca.cmpt276.walkinggroup.model.GPS;
import ca.cmpt276.walkinggroup.model.Group;
import ca.cmpt276.walkinggroup.model.Message;
import ca.cmpt276.walkinggroup.model.PermissionRequest;
import ca.cmpt276.walkinggroup.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * The ProxyBuilder class will handle the apiKey and token being injected as a header to all calls
 * This is a Retrofit interface.
 *
 * takes in data for server
 * Communicates with server
 * returns data provided by server
 */
public interface WGServerProxy {
    @GET("getApiKey")
    Call<String> getApiKey(@Query("groupName") String groupName, @Query("sfuUserId") String sfuId);

    @POST("/users/signup")
    Call<User> createNewUser(@Body User user);

    @POST("/login")
    Call<Void> login(@Body User userWithEmailAndPassword);

    @GET("/users")
    Call<List<User>> getUsers();

    @GET("/users/{id}")
    Call<User> getUserById(@Path("id") Long userId);

    @GET("/users/byEmail")
    Call<User> getUserByEmail(@Query("email") String email);

    @POST("/users/{id}")
    Call<User> editUser(@Path("id") Long userId, @Body User user);

    @GET("/users/{id}/lastGpsLocation")
    Call<GPS> getLastLocation(@Path("id") Long userId);

    @POST("/users/{id}/lastGpsLocation")
    Call<GPS> setLastLocation(@Path("id") Long userId, @Body GPS gps);

    @GET ("/users/{id}/monitoredByUsers")
    Call<List<User>> getMonitoredBy(@Path("id") Long sourceId);

    @POST("/users/{id}/monitoredByUsers")
    Call<List<User>> addMonitoredBy(@Path("id") Long sourceId, @Body User targetUser);

    @GET ("/users/{id}/monitorsUsers")
    Call<List<User>> getMonitoring(@Path("id") Long sourceId);

    @POST("/users/{id}/monitorsUsers")
    Call<List<User>> addMonitoring(@Path("id") Long sourceId, @Body User targetUser);

    @DELETE("/users/{idA}/monitorsUsers/{idB}")
    Call<Void> deleteMonitoring(@Path("idA") Long sourceId, @Path("idB") Long targetUser);

    @DELETE("/users/{idA}/monitoredByUsers/{idB}")
    Call<Void> deleteMonitoredBy(@Path("idA") Long sourceId, @Path("idB") Long targetUser);

    @GET("/groups")
    Call<List<Group>> getGroups();

    @POST("/groups")
    Call<Group> addGroup(@Body Group group);

    @GET ("/groups/{id}")
    Call<Group> getGroupDetails(@Path("id") Long groupId);

    @DELETE ("/groups/{id}")
    Call<Void> deleteGroup(@Path("id") Long groupId);

    @GET ("/groups/{id}/memberUsers")
    Call<List<User>> getMembers(@Path("id") Long groupId);

    @POST ("/groups/{id}/memberUsers")
    Call<List<User>> addMember(@Path("id") Long groupId, @Body User user);

    @DELETE ("/groups/{groupId}/memberUsers/{userId}")
    Call<Void> deleteMember(@Path("groupId") Long groupId, @Path("userId") Long userId);

    @GET("/messages")
    Call<List<Message>> getAllMessages();

    @GET("/messages?")
    Call<List<Message>> getAllMessagesForUser(@Query("foruser") Long userId);

    @GET("/messages?")
    Call<List<Message>> getAllMessagesForUser(@Query("foruser") Long userId,
                                              @Query("status") String status);

    @GET("/messages?is-emergency=true")
    Call<List<Message>> getEmergencyMessageForAll();

    @POST("/messages/togroup/{groupId}")
    Call<Message> sendMessage(@Path("groupId") Long groupId,
                              @Body Message message);

    @POST("/messages/toparentsof/{userId}")
    Call<Message> sendEmergencyMessage(@Path("userId") Long userId,
                                       @Body Message message);

    @GET("/messages/{id}")
    Call<Message> getMessage(@Path("id") Long messageId);

    @DELETE("messages/{id}")
    Call<Void> deleteMessage(@Path("id") Long messageId);

    @POST("/messages/{messageId}/readby/{userId}")
    Call<User>  markMessage(@Path("messageId") Long messageId,
                                @Path("userId") Long userId,
                                @Body Boolean isMessageRead);

    @GET("/permissions")
    Call<List<PermissionRequest>> getPermissions();

    @GET("/permissions?")
    Call<List<PermissionRequest>> getPermissionsForUser(@Query("userId") Long userId);

    @GET("/permissions?")
    Call<List<PermissionRequest>> getPermissionsForUserWithStatus(@Query("userId") Long userId,
                                                                  @Query("statusForUser") PermissionStatus status);

    @GET("/permissions/{id}")
    Call<PermissionRequest> getPermissionById(@Path("id") long permissionId);

    @POST("/permissions/{id}")
    Call<PermissionRequest> approveOrDenyPermissionRequest(
            @Path("id") long permissionId,
            @Body PermissionStatus status
    );

    enum PermissionStatus {
        PENDING,
        APPROVED,
        DENIED
    }

}
