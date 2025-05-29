package servent.message;

public class FollowRequestMessage extends BasicMessage {

    public FollowRequestMessage(int senderPort, int receiverPort) {
        super(MessageType.FOLLOW_REQUEST, senderPort, receiverPort, "");
    }
}