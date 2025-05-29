package cli.command;

import app.AppConfig;

public class VisibilityCommand implements CLICommand {

    @Override
    public String commandName() {
        return "visibility";
    }

    @Override
    public void execute(String args) {
        if (args == null || (!args.equalsIgnoreCase("public") && !args.equalsIgnoreCase("private"))) {
            AppConfig.timestampedErrorPrint("Korišćenje: visibility [public/private]");
            return;
        }

        boolean newStatus = args.equalsIgnoreCase("public");
        AppConfig.chordState.setPublic(newStatus);
        AppConfig.timestampedStandardPrint("Vidljivost profila postavljena na: " + (newStatus ? "public" : "private"));
    }
}