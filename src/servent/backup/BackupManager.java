package servent.backup;

import app.AppConfig;
import servent.message.BackupFileMessage;
import servent.message.Message;
import servent.message.util.MessageUtil;

import java.util.Map;

public class BackupManager {

    public static void sendFileBackup(int targetPort) {
        for (Map.Entry<Integer, String> entry : AppConfig.chordState.getFileValueMap().entrySet()) {
            int key = entry.getKey();
            String value = entry.getValue(); // format: fileName::base64
            String msgText = key + "::" + value;

            Message backupMsg = new BackupFileMessage(
                    AppConfig.myServentInfo.getListenerPort(),
                    targetPort,
                    msgText
            );

            AppConfig.timestampedStandardPrint("📤 Šaljem BACKUP za ključ " + key + " ka " + targetPort);
            MessageUtil.sendMessage(backupMsg);
        }
    }
}