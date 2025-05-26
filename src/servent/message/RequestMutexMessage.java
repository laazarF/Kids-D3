package servent.message;

public class RequestMutexMessage extends BasicMessage {

    private static final long serialVersionUID = -123456789012345678L;

    private final int requestNumber;

    public RequestMutexMessage(int senderPort, int receiverPort, int requestNumber) {
        super(MessageType.REQUEST_MUTEX, senderPort, receiverPort);
        this.requestNumber = requestNumber;
    }

    public int getRequestNumber() {
        return requestNumber;
    }
}