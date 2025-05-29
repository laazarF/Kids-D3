package cli.command;

import app.AppConfig;

import java.util.Set;

public class PendingCommand implements CLICommand {

    @Override
    public String commandName() {
        return "pending";
    }

    @Override
    public void execute(String args) {
        Set<Integer> pending = AppConfig.chordState.getPendingFollowRequests();

        if (pending.isEmpty()) {
            AppConfig.timestampedStandardPrint("Nema zahteva za praćenje.");
        } else {
            AppConfig.timestampedStandardPrint("Zahtevi za praćenje od sledećih portova:");
            for (int port : pending) {
                AppConfig.timestampedStandardPrint(" - " + port);
            }
        }
    }
}