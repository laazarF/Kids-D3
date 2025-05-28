package servent.message;

public class PutFileMessage extends BasicMessage {

    public PutFileMessage(int senderPort, int receiverPort, int key, String base64Content) {
        super(MessageType.PUT_FILE, senderPort, receiverPort, key + "::" + base64Content);
    }

    @Override
    public String toString() {
        return "[PUT_FILE|" + getSenderPort() + "->" + getReceiverPort() +
                "] " + "Key: " + getMessageText().split("::")[0] +
                ", FileName: " + getMessageText().split("::")[1] +
                ", [Base64 content hidden]";
    }
}
