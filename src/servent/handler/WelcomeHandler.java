package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import servent.backup.BackupManager;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.UpdateMessage;
import servent.message.WelcomeMessage;
import servent.message.util.MessageUtil;

public class WelcomeHandler implements MessageHandler {

	private Message clientMessage;
	
	public WelcomeHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}
	
	@Override
	public void run() {
		if (clientMessage.getMessageType() == MessageType.WELCOME) {
			WelcomeMessage welcomeMsg = (WelcomeMessage)clientMessage;

			AppConfig.timestampedStandardPrint("Primio WELCOME poruku od " + clientMessage.getSenderPort());
			
			AppConfig.chordState.init(welcomeMsg);
			
			UpdateMessage um = new UpdateMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getNextNodePort(), "");
			MessageUtil.sendMessage(um);

			// 🔁 Pozivanje slanja backup fajlova ka successor i predecessor
			ServentInfo predecessor = AppConfig.chordState.getPredecessor();
			ServentInfo successor = AppConfig.chordState.getSuccessorTable()[0];

			if (predecessor != null && !predecessor.equals(AppConfig.myServentInfo)) {
				BackupManager.sendFileBackup(predecessor.getListenerPort());
			}
			if (successor != null && !successor.equals(AppConfig.myServentInfo)) {
				BackupManager.sendFileBackup(successor.getListenerPort());
			}

			// ♻️ Restore backup fajlova ako je čvor postao vlasnik
			AppConfig.chordState.restoreBackupFiles();
			
		} else {
			AppConfig.timestampedErrorPrint("Welcome handler got a message that is not WELCOME");
		}

	}

}
