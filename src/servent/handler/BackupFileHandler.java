package servent.handler;

import app.AppConfig;
import servent.message.Message;

public class BackupFileHandler implements MessageHandler {
    private final Message clientMessage;

    public BackupFileHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        String[] split = clientMessage.getMessageText().split("::");
        if (split.length < 3) {
            AppConfig.timestampedErrorPrint("Neispravan BACKUP_FILE format.");
            return;
        }

        int key = Integer.parseInt(split[0]);
        String fileName = split[1];
        String base64 = split[2];

        AppConfig.chordState.storeFileBackup(key, fileName, base64);
        AppConfig.timestampedStandardPrint("📦 Backup fajla sačuvan: " + fileName + " (ključ: " + key + ")");
    }
}