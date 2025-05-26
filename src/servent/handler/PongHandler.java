package servent.handler;

import app.AppConfig;
import servent.failure.FailureDetector;
import servent.message.Message;
import servent.message.MessageType;

public class PongHandler implements MessageHandler {

    private final Message clientMessage;

    public PongHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.PONG) {
            AppConfig.timestampedErrorPrint("PONG handler dobio pogrešnu poruku.");
            return;
        }

        int senderPort = clientMessage.getSenderPort();
        FailureDetector.getInstance().onPongReceived(senderPort);
    }
}