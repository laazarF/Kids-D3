package servent.message;

public class TellGetMessage extends BasicMessage {

	private static final long serialVersionUID = -6213394344524749872L;

	public TellGetMessage(int senderPort, int receiverPort, int key, int value) {
		super(MessageType.TELL_GET, senderPort, receiverPort, key + ":" + value);
	}

	public TellGetMessage(int senderPort, int receiverPort, int key, String filePayload) {
		super(MessageType.TELL_GET, senderPort, receiverPort, key + "::" + filePayload);
	}

	@Override
	public String toString() {
		String[] parts = getMessageText().split("::");
		if (parts.length >= 2) {
			return "[TELL_GET|" + getSenderPort() + "->" + getReceiverPort() + "] Key: " + parts[0] + ", File: " + parts[1];
		}
		return "[TELL_GET|" + getSenderPort() + "->" + getReceiverPort() + "] " + getMessageText();
	}
}
