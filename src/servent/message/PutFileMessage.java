package servent.message;

public class PutFileMessage extends BasicMessage {

    public PutFileMessage(int senderPort, int receiverPort, int key, String fileName, String base64Content) {
        super(MessageType.PUT_FILE, senderPort, receiverPort, key + "::" + fileName + "::" + base64Content);
    }

    @Override
    public String toString() {
        String[] parts = getMessageText().split("::");
        String key = parts.length > 0 ? parts[0] : "?";
        String fileName = parts.length > 1 ? parts[1] : "?";
        return "[PUT_FILE|" + getSenderPort() + "->" + getReceiverPort() +
                "] " + "Key: " + key +
                ", FileName: " + fileName +
                ", [Base64 content hidden]";
    }
}
