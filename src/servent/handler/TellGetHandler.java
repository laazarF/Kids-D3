package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.MessageType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

public class TellGetHandler implements MessageHandler {

	private final Message clientMessage;

	public TellGetHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}

	@Override
	public void run() {
		if (clientMessage.getMessageType() != MessageType.TELL_GET) {
			AppConfig.timestampedErrorPrint("Tell get handler got a message that is not TELL_GET");
			return;
		}

		String content = clientMessage.getMessageText();

		if (content.contains("::")) {
			// Fajl poruka
			String[] split = content.split("::");
			if (split.length == 3) {
				try {
					int key = Integer.parseInt(split[0]);
					String filename = split[1];
					String base64 = split[2];

					AppConfig.timestampedStandardPrint("Primljen fajl sa ključem " + key + ": " + filename);

					// Dekodiranje i čuvanje fajla
					byte[] bytes = Base64.getDecoder().decode(base64);
					try (FileOutputStream fos = new FileOutputStream("downloads/" + filename)) {
						fos.write(bytes);
						AppConfig.timestampedStandardPrint("Fajl sačuvan kao downloads/" + filename);
					} catch (IOException e) {
						AppConfig.timestampedErrorPrint("Greška pri upisu fajla: " + e.getMessage());
					}

					AppConfig.pendingDownloads.computeIfPresent(key, (k, info) -> {
						info.setFileName(filename);
						return info;
					});

				} catch (NumberFormatException e) {
					AppConfig.timestampedErrorPrint("Greška u parsiranju ključa iz TELL_GET fajl poruke: " + content);
				}
			} else {
				AppConfig.timestampedErrorPrint("Loš format TELL_GET poruke za fajl: " + content);
			}
		} else {
			// Klasična poruka sa brojevima
			String[] parts = content.split(":");
			if (parts.length == 2) {
				try {
					int key = Integer.parseInt(parts[0]);
					int value = Integer.parseInt(parts[1]);

					if (value == -1) {
						AppConfig.timestampedStandardPrint("No such key: " + key);
					} else {
						AppConfig.timestampedStandardPrint("Dohvaćen par: " + key + " => " + value);
					}
				} catch (NumberFormatException e) {
					AppConfig.timestampedErrorPrint("Loš format brojevne TELL_GET poruke: " + content);
				}
			} else {
				AppConfig.timestampedErrorPrint("Neispravna TELL_GET poruka: " + content);
			}
		}
	}
}