package servent.handler;

import app.AppConfig;
import servent.message.BuddyPingMessage;
import servent.message.PingMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.util.MessageUtil;

public class BuddyPingHandler implements MessageHandler {

    private final Message clientMessage;

    public BuddyPingHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.BUDDY_PING) {
            AppConfig.timestampedErrorPrint("BuddyPingHandler dobio pogrešnu poruku.");
            return;
        }

        BuddyPingMessage buddyPing = (BuddyPingMessage) clientMessage;
        int targetPort = buddyPing.getTargetPort();

        if (targetPort == AppConfig.myServentInfo.getListenerPort()) {
            AppConfig.timestampedStandardPrint("Preskačem buddy ping jer se odnosi na samog sebe (" + targetPort + ")");
            return;
        }

        AppConfig.timestampedStandardPrint("Buddy pingujem čvor " + targetPort + " po zahtevu od " + clientMessage.getSenderPort());

        PingMessage ping = new PingMessage(
                AppConfig.myServentInfo.getListenerPort(),
                targetPort
        );

        MessageUtil.sendMessage(ping);
    }
}
