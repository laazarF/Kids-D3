package servent.handler;

import java.util.*;

import app.AppConfig;
import app.ServentInfo;
import servent.backup.BackupManager;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.UpdateMessage;
import servent.message.util.MessageUtil;

public class UpdateHandler implements MessageHandler {

	private Message clientMessage;
	
	public UpdateHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}
	
	@Override
	public void run() {
		if (clientMessage.getMessageType() == MessageType.UPDATE) {
			AppConfig.timestampedStandardPrint("Primio UPDATE poruku od " + clientMessage.getSenderPort());
			if (clientMessage.getSenderPort() != AppConfig.myServentInfo.getListenerPort()) {
				ServentInfo newNodInfo = new ServentInfo("localhost", clientMessage.getSenderPort());
				List<ServentInfo> newNodes = new ArrayList<>();
				newNodes.add(newNodInfo);
				
				AppConfig.chordState.addNodes(newNodes);
				String newMessageText = "";
				if (clientMessage.getMessageText().equals("")) {
					newMessageText = String.valueOf(AppConfig.myServentInfo.getListenerPort());
				} else {
					newMessageText = clientMessage.getMessageText() + "," + AppConfig.myServentInfo.getListenerPort();
				}
				Message nextUpdate = new UpdateMessage(clientMessage.getSenderPort(), AppConfig.chordState.getNextNodePort(),
						newMessageText);
				MessageUtil.sendMessage(nextUpdate);

				// 🔁 Pozivanje slanja backup fajlova ka successor i predecessor
				ServentInfo predecessor = AppConfig.chordState.getPredecessor();
				ServentInfo successor = AppConfig.chordState.getSuccessorTable()[0];

				if (predecessor != null && !predecessor.equals(AppConfig.myServentInfo)) {
					BackupManager.sendFileBackup(predecessor.getListenerPort());
				}
				if (successor != null && !successor.equals(AppConfig.myServentInfo)) {
					BackupManager.sendFileBackup(successor.getListenerPort());
				}

				// ♻️ Restore backup fajlova ako je čvor postao novi vlasnik
				AppConfig.chordState.restoreBackupFiles();
				Map<Integer, String> localFiles = AppConfig.chordState.getFileValueMap();
				Set<Integer> keysToRemove = new HashSet<>();
				for (Integer key : localFiles.keySet()) {
					if (!AppConfig.chordState.isKeyMine(key)) {
						AppConfig.timestampedStandardPrint("🧹 Više nisam vlasnik ključa " + key + ", brišem lokalno...");
						keysToRemove.add(key);
					}
				}
				for (Integer key : keysToRemove) {
					localFiles.remove(key);
				}
			} else {
				String messageText = clientMessage.getMessageText();
				String[] ports = messageText.split(",");
				
				List<ServentInfo> allNodes = new ArrayList<>();
				for (String port : ports) {
					allNodes.add(new ServentInfo("localhost", Integer.parseInt(port)));
				}
				AppConfig.chordState.addNodes(allNodes);

				// 🔁 Pozivanje slanja backup fajlova ka successor i predecessor
				ServentInfo predecessor = AppConfig.chordState.getPredecessor();
				ServentInfo successor = AppConfig.chordState.getSuccessorTable()[0];

				if (predecessor != null && !predecessor.equals(AppConfig.myServentInfo)) {
					BackupManager.sendFileBackup(predecessor.getListenerPort());
				}
				if (successor != null && !successor.equals(AppConfig.myServentInfo)) {
					BackupManager.sendFileBackup(successor.getListenerPort());
				}

				// ♻️ Restore backup fajlova ako je čvor postao novi vlasnik
				AppConfig.chordState.restoreBackupFiles();
				Map<Integer, String> localFiles = AppConfig.chordState.getFileValueMap();
				Set<Integer> keysToRemove = new HashSet<>();
				for (Integer key : localFiles.keySet()) {
					if (!AppConfig.chordState.isKeyMine(key)) {
						AppConfig.timestampedStandardPrint("🧹 Više nisam vlasnik ključa " + key + ", brišem lokalno...");
						keysToRemove.add(key);
					}
				}
				for (Integer key : keysToRemove) {
					localFiles.remove(key);
				}

			}
		} else {
			AppConfig.timestampedErrorPrint("Update message handler got message that is not UPDATE");
		}
	}

}
