package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.TokenMessage;

public class TokenMessageHandler implements MessageHandler {

    private final Message clientMessage;

    public TokenMessageHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (!(clientMessage instanceof TokenMessage)) {
            AppConfig.timestampedErrorPrint("Invalid message type for TokenMessageHandler.");
            return;
        }

        TokenMessage tokenMsg = (TokenMessage) clientMessage;

        AppConfig.mutex.handleToken(tokenMsg.getTokenNumbers());
    }
}