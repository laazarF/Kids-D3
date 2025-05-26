package servent.message;

public class BuddyPingMessage extends BasicMessage {

    private static final long serialVersionUID = 234987239847L;

    private final int targetPort;

    public BuddyPingMessage(int senderPort, int receiverPort, int targetPort) {
        super(MessageType.BUDDY_PING, senderPort, receiverPort);
        this.targetPort = targetPort;
    }

    public int getTargetPort() {
        return targetPort;
    }

    @Override
    public String toString() {
        return "[BUDDY_PING] from " + getSenderPort() + " to " + getReceiverPort() + " (checking " + targetPort + ")";
    }
}