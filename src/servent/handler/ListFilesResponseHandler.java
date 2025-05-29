package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.MessageType;

public class ListFilesResponseHandler implements MessageHandler {

    private final Message clientMessage;

    public ListFilesResponseHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.LIST_FILES_RESPONSE) {
            AppConfig.timestampedErrorPrint("LIST_FILES_RESPONSE handler dobio pogrešan tip poruke.");
            return;
        }

        AppConfig.timestampedStandardPrint("Odgovor na list_files:\n" + clientMessage.getMessageText());
    }
}