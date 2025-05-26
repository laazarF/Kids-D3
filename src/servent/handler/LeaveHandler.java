package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import servent.message.Message;
import servent.message.MessageType;

import java.util.List;

public class LeaveHandler implements MessageHandler {

    private final Message clientMessage;

    public LeaveHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.LEAVE) {
            AppConfig.timestampedErrorPrint("LEAVE handler dobio pogrešnu poruku.");
            return;
        }

        AppConfig.mutex.requestCS();

        int leavingPort = clientMessage.getSenderPort();
        ServentInfo leavingNode = new ServentInfo("localhost", leavingPort);

        AppConfig.timestampedStandardPrint("Čvor na portu " + leavingPort + " je napustio sistem. (LEAVE)");

        // Ako napuštač jeste moj predecessor
        ServentInfo predecessor = AppConfig.chordState.getPredecessor();
        if (predecessor != null && predecessor.getListenerPort() == leavingPort) {
            AppConfig.chordState.setPredecessor(null);
            AppConfig.timestampedStandardPrint("Resetovan predecessor jer je čvor " + leavingPort + " napustio sistem.");
        }

        // Ako napuštač jeste jedan od successora
        ServentInfo[] succTable = AppConfig.chordState.getSuccessorTable();
        for (int i = 0; i < succTable.length; i++) {
            if (succTable[i] != null && succTable[i].getListenerPort() == leavingPort) {
                succTable[i] = null;
                AppConfig.timestampedStandardPrint("Uklonjen successor sa pozicije " + i + " jer je čvor " + leavingPort + " napustio sistem.");
            }
        }

        // Ažuriraj successor tabelu ako imamo još poznatih čvorova
        List<ServentInfo> allKnownNodes = AppConfig.chordState.getAllNodeInfo();
        allKnownNodes.removeIf(s -> s.getListenerPort() == leavingPort);
        AppConfig.chordState.refreshSuccessorTable(allKnownNodes);

        AppConfig.mutex.releaseCS();
    }
}