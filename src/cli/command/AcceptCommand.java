package cli.command;

import app.AppConfig;

public class AcceptCommand implements CLICommand {

    @Override
    public String commandName() {
        return "accept";
    }

    @Override
    public void execute(String args) {
        int port;
        try {
            port = Integer.parseInt(args.trim());
        } catch (NumberFormatException | NullPointerException e) {
            AppConfig.timestampedErrorPrint("Korišćenje: accept [port]");
            return;
        }

        if (!AppConfig.chordState.getPendingFollowRequests().contains(port)) {
            AppConfig.timestampedErrorPrint("Nema follow zahteva od porta " + port);
            return;
        }

        AppConfig.chordState.acceptFollower(port);
        AppConfig.timestampedStandardPrint("Prihvaćen zahtev za praćenje od porta " + port);
    }
}