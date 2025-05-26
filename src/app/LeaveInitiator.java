package app;

import servent.message.LeaveMessage;
import servent.message.util.MessageUtil;
import servent.message.BasicMessage;

public class LeaveInitiator {

    public static void initiateLeave() {
        int myPort = AppConfig.myServentInfo.getListenerPort();

        ServentInfo pred = AppConfig.chordState.getPredecessor();
        ServentInfo succ = AppConfig.chordState.getSuccessorTable()[0];

        if (pred != null && pred.getListenerPort() != myPort) {
            BasicMessage leaveToPred = new LeaveMessage(myPort, pred.getListenerPort());
            MessageUtil.sendMessage(leaveToPred);
        }

        if (succ != null && succ.getListenerPort() != myPort) {
            BasicMessage leaveToSucc = new LeaveMessage(myPort, succ.getListenerPort());
            MessageUtil.sendMessage(leaveToSucc);
        }
    }
}