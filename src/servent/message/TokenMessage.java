package servent.message;

public class TokenMessage extends BasicMessage {

    private static final long serialVersionUID = -987654321098765432L;

    private final int[] tokenNumbers;

    public TokenMessage(int senderPort, int receiverPort, int[] tokenNumbers) {
        super(MessageType.TOKEN_MUTEX, senderPort, receiverPort);
        this.tokenNumbers = tokenNumbers.clone(); // safety
    }

    public int[] getTokenNumbers() {
        return tokenNumbers;
    }
}