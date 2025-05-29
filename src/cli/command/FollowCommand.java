package cli.command;

import app.AppConfig;
import servent.message.FollowRequestMessage;
import servent.message.Message;
import servent.message.util.MessageUtil;

public class FollowCommand implements CLICommand {

    @Override
    public String commandName() {
        return "follow";
    }

    @Override
    public void execute(String args) {
        if (args == null || !args.contains(":")) {
            AppConfig.timestampedErrorPrint("Korišćenje: follow [adresa:port]");
            return;
        }

        String[] parts = args.split(":");
        if (parts.length != 2) {
            AppConfig.timestampedErrorPrint("Neispravan format adrese.");
            return;
        }

        try {
            String ip = parts[0];
            int port = Integer.parseInt(parts[1]);

            Message followMsg = new FollowRequestMessage(
                    AppConfig.myServentInfo.getListenerPort(),
                    port
            );

            MessageUtil.sendMessage(followMsg);
            AppConfig.timestampedStandardPrint("Zahtev za praćenje poslat ka " + port);

        } catch (NumberFormatException e) {
            AppConfig.timestampedErrorPrint("Port mora biti broj.");
        }
    }
}