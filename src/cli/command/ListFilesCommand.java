package cli.command;

import app.AppConfig;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.util.MessageUtil;
import servent.message.BasicMessage;

public class ListFilesCommand implements CLICommand {

    @Override
    public String commandName() {
        return "list_files";
    }

    @Override
    public void execute(String args) {
        if (args == null || !args.contains(":")) {
            AppConfig.timestampedErrorPrint("Korišćenje: list_files [adresa:port]");
            return;
        }

        String[] parts = args.split(":");
        if (parts.length != 2) {
            AppConfig.timestampedErrorPrint("Neispravan format adrese.");
            return;
        }

        try {
            int targetPort = Integer.parseInt(parts[1]);

            Message msg = new BasicMessage(
                    MessageType.LIST_FILES_REQUEST,
                    AppConfig.myServentInfo.getListenerPort(),
                    targetPort,
                    ""
            );
            MessageUtil.sendMessage(msg);

        } catch (NumberFormatException e) {
            AppConfig.timestampedErrorPrint("Port mora biti broj.");
        }
    }
}