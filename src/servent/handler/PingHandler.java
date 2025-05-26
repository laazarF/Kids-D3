package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.PongMessage;
import servent.message.util.MessageUtil;

public class PingHandler implements MessageHandler {

    private final Message clientMessage;

    public PingHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.PING) {
            AppConfig.timestampedErrorPrint("PING handler dobio pogrešnu poruku.");
            return;
        }

        PongMessage pong = new PongMessage(AppConfig.myServentInfo.getListenerPort(), clientMessage.getSenderPort());
        MessageUtil.sendMessage(pong);
    }
}