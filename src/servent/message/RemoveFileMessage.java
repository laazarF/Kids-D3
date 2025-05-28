package servent.message;

public class RemoveFileMessage extends BasicMessage {

    public RemoveFileMessage(int senderPort, int receiverPort, String messageText) {
        super(MessageType.REMOVE_FILE, senderPort, receiverPort, messageText);
    }
}
