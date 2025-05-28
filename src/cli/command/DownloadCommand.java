package cli.command;

import app.AppConfig;
import servent.message.AskGetMessage;
import servent.message.Message;
import servent.message.util.MessageUtil;

public class DownloadCommand implements CLICommand {
    @Override
    public String commandName() {
        return "download";
    }

    @Override
    public void execute(String args) {
        while (AppConfig.chordState.getSuccessorTable()[0] == null) {
            AppConfig.timestampedStandardPrint("Čekam da se inicijalizuje successor...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        int key = Math.abs(args.hashCode());
        Message askMsg = new AskGetMessage(
                AppConfig.myServentInfo.getListenerPort(),
                AppConfig.chordState.getNextNodePort(),
                String.valueOf(key)
        );
        MessageUtil.sendMessage(askMsg);
    }
}
