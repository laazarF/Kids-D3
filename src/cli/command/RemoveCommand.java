package cli.command;

import app.AppConfig;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.util.MessageUtil;
import servent.message.RemoveFileMessage;

public class RemoveCommand implements CLICommand {

    @Override
    public String commandName() {
        return "remove_file";
    }

    @Override
    public void execute(String args) {
        int key = Math.abs(args.hashCode());

        Message removeMsg = new RemoveFileMessage(
                AppConfig.myServentInfo.getListenerPort(),
                AppConfig.chordState.getNextNodePort(),
                key + "::" + args
        );

        MessageUtil.sendMessage(removeMsg);
    }
}