package servent.handler;

import app.AppConfig;
import servent.message.BasicMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.util.MessageUtil;

import java.util.Set;

public class ListFilesRequestHandler implements MessageHandler {

    private final Message clientMessage;

    public ListFilesRequestHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        int requester = clientMessage.getSenderPort();

        if (!AppConfig.chordState.isPublic() &&
                !AppConfig.chordState.getFollowers().contains(requester)) {

            Message denyMsg = new BasicMessage(
                    MessageType.LIST_FILES_RESPONSE,
                    AppConfig.myServentInfo.getListenerPort(),
                    requester,
                    "Profil je privatan."
            );
            MessageUtil.sendMessage(denyMsg);
            return;
        }

        Set<Integer> fileKeys = AppConfig.chordState.getFileValueMap().keySet();
        StringBuilder sb = new StringBuilder("Fajlovi na čvoru " + AppConfig.myServentInfo.getListenerPort() + ":\n");

        for (Integer key : fileKeys) {
            String meta = AppConfig.chordState.getFileValueMap().get(key);
            if (meta != null && meta.contains("::")) {
                String fileName = meta.split("::")[0];
                sb.append("- ").append(fileName).append("\n");
            }
        }

        Message response = new BasicMessage(
                MessageType.LIST_FILES_RESPONSE,
                AppConfig.myServentInfo.getListenerPort(),
                requester,
                sb.toString()
        );
        MessageUtil.sendMessage(response);
    }
}