package ca.cmpt276.walkinggroup.model;

import java.util.List;

/*
    A collection class for storing the list of
    the messages that is being retrieved from
    the server.

    is a singleton of all messages
 */
public class MessageCollection {
    private List<Message> messageList;
    private static MessageCollection single_instance = null;

    public void addMessageToCollection(Message newMessage) {
        messageList.add(newMessage);
    }

    public void removeMessageFromCollection(Message targetMessage){
        messageList.remove(targetMessage);
    }

    public void setMessageList(List<Message> messageList){
        this.messageList = messageList;
    }

    public List<Message> getMessageList(){
        return messageList;
    }

    public int countMessages() {
        return messageList.size();
    }


    public Message getMessage(int index) {
        validateIndexWithException(index);
        return messageList.get(index);
    }

    // singleton:
    public static MessageCollection getInstance() {
        if (single_instance == null)
            single_instance = new MessageCollection();

        return single_instance;
    }

    private void validateIndexWithException(int index) {
        if (index < 0 || index >= countMessages()) {
            throw new IllegalArgumentException();
        }
    }
}
