package cli.command;

import app.AppConfig;
import app.ServentInfo;
import servent.message.Message;
import servent.message.util.MessageUtil;
import servent.message.RemoveFileMessage;

public class RemoveCommand implements CLICommand {

    @Override
    public String commandName() {
        return "remove_file";
    }

    @Override
    public void execute(String args) {
        int key = Math.abs(args.hashCode()) % AppConfig.SERVENT_COUNT;

        if (!AppConfig.chordState.isKeyMine(key)) {
            AppConfig.timestampedErrorPrint("Niste vlasnik datoteke. Samo vlasnik može da je ukloni.");
            return;
        }

        AppConfig.timestampedStandardPrint("Vi ste vlasnik ključa " + key + ". Uklanjam datoteku...");

        Message removeMsg = new RemoveFileMessage(
                AppConfig.myServentInfo.getListenerPort(),
                AppConfig.myServentInfo.getListenerPort(),
                key + "::" + args
        );

        MessageUtil.sendMessage(removeMsg);
    }
}