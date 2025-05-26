package servent.message;

public class PingMessage extends BasicMessage {

    private static final long serialVersionUID = 123456789L;

    public PingMessage(int senderPort, int receiverPort) {
        super(MessageType.PING, senderPort, receiverPort);
    }
}