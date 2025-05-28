package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.RemoveFileMessage;
import servent.message.util.MessageUtil;

public class RemoveFileHandler implements MessageHandler {

    private final Message clientMessage;

    public RemoveFileHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.REMOVE_FILE) {
            AppConfig.timestampedErrorPrint("Remove handler got non REMOVE_FILE message.");
            return;
        }

        String[] parts = clientMessage.getMessageText().split("::");
        if (parts.length != 2) {
            AppConfig.timestampedErrorPrint("Malformed REMOVE_FILE message: " + clientMessage.getMessageText());
            return;
        }

        int key;
        String filename;
        try {
            key = Integer.parseInt(parts[0]);
            filename = parts[1];
        } catch (NumberFormatException e) {
            AppConfig.timestampedErrorPrint("Key parse failed in REMOVE_FILE.");
            return;
        }

        if (AppConfig.chordState.isKeyMine(key)) {
            boolean removed = AppConfig.chordState.removeFileValue(key);
            if (removed) {
                AppConfig.timestampedStandardPrint("Fajl uspešno uklonjen iz sistema: " + filename);
            } else {
                AppConfig.timestampedErrorPrint("Fajl nije pronađen kod ovog čvora: " + filename);
            }
        } else {
            // Prosleđivanje sledećem čvoru u ruti
            int nextPort = AppConfig.chordState.getNextNodeForKey(key).getListenerPort();
            Message fwdMsg = new RemoveFileMessage(
                    clientMessage.getSenderPort(), nextPort, clientMessage.getMessageText()
            );
            MessageUtil.sendMessage(fwdMsg);
        }
    }
}