package servent.handler;

import app.AppConfig;
import mutex.SuzukiKasamiMutex;
import servent.message.Message;
import servent.message.RequestMutexMessage;

public class RequestMutexHandler implements MessageHandler {

    private final Message clientMessage;

    public RequestMutexHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (!(clientMessage instanceof RequestMutexMessage)) {
            AppConfig.timestampedErrorPrint("Invalid message type for RequestMutexHandler.");
            return;
        }

        RequestMutexMessage reqMsg = (RequestMutexMessage) clientMessage;

        int senderId = AppConfig.getIdByPort(reqMsg.getSenderPort());
        int reqNumber = reqMsg.getRequestNumber();

        AppConfig.mutex.handleRequest(senderId, reqNumber);
    }
}