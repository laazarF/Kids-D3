package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.MessageType;

public class PutHandler implements MessageHandler {

	private Message clientMessage;
	
	public PutHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}
	
	@Override
	public void run() {
		if (clientMessage.getMessageType() == MessageType.PUT ||
		    clientMessage.getMessageType() == MessageType.PUT_FILE) {
			AppConfig.timestampedStandardPrint("Primio PUT/PUT_FILE poruku");

			// Provera da li je fajl poruka
			if (clientMessage.getMessageText().contains("::")) {
				String[] split = clientMessage.getMessageText().split("::");
				if (split.length == 3) {
					try {
						int key = Integer.parseInt(split[0]);
						String fileName = split[1];
						String base64 = split[2];
						AppConfig.chordState.putFileValue(key, fileName, base64);
					} catch (NumberFormatException e) {
						AppConfig.timestampedErrorPrint("Greška prilikom parsiranja file PUT ključa.");
					}
				} else {
					AppConfig.timestampedErrorPrint("Neispravna PUT poruka za fajl: " + clientMessage.getMessageText());
				}
			} else {
				String[] splitText = clientMessage.getMessageText().split(":");
				if (splitText.length == 2) {
					try {
						int key = Integer.parseInt(splitText[0]);
						int value = Integer.parseInt(splitText[1]);
						AppConfig.chordState.putValue(key, value);
					} catch (NumberFormatException e) {
						AppConfig.timestampedErrorPrint("Got put message with bad text: " + clientMessage.getMessageText());
					}
				} else {
					AppConfig.timestampedErrorPrint("Got put message with bad text: " + clientMessage.getMessageText());
				}
			}
		} else {
			AppConfig.timestampedErrorPrint("Put handler got a message that is not PUT");
		}
	}

}
