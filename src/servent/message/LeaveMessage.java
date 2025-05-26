package servent.message;


public class LeaveMessage extends BasicMessage {

    private static final long serialVersionUID = -903984723480234L;

    public LeaveMessage(int senderPort, int receiverPort) {
        super(MessageType.LEAVE, senderPort, receiverPort);
    }

    @Override
    public String toString() {
        return "[LEAVE] from " + getSenderPort();
    }
}