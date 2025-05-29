package servent.message;

public class BackupFileMessage extends BasicMessage {
    public BackupFileMessage(int senderPort, int receiverPort, String messageText) {
        super(MessageType.BACKUP_FILE, senderPort, receiverPort, messageText);
    }

    @Override
    public String toString() {
        String[] parts = getMessageText().split("::");
        String key = parts.length > 0 ? parts[0] : "?";
        String fileName = parts.length > 1 ? parts[1] : "?";
        return "[BACKUP_FILE|" + getSenderPort() + "->" + getReceiverPort() +
                "] Key: " + key + ", FileName: " + fileName;
    }
}