package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.MessageType;

public class FollowRequestHandler implements MessageHandler {

    private final Message clientMessage;

    public FollowRequestHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.FOLLOW_REQUEST) {
            AppConfig.timestampedErrorPrint("FollowRequestHandler dobio pogrešan tip poruke.");
            return;
        }

        int followerPort = clientMessage.getSenderPort();
        AppConfig.chordState.addPendingRequest(followerPort);
        AppConfig.timestampedStandardPrint("Primljen zahtev za praćenje od " + followerPort);
    }
}