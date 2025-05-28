package cli.command;

import app.AppConfig;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.PutFileMessage;
import servent.message.PutMessage;
import servent.message.util.MessageUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class UploadCommand implements CLICommand {
    @Override
    public String commandName() {
        return "upload";
    }

    @Override
    public void execute(String args) {
        // args = ime_fajla.jpg
        while (AppConfig.chordState.getSuccessorTable()[0] == null) {
            AppConfig.timestampedStandardPrint("Čekam da se inicijalizuje successor...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        Path path = Paths.get("resource/" + args);
        if (!Files.exists(path)) {
            AppConfig.timestampedErrorPrint("Fajl " + args + " ne postoji.");
            return;
        }

        try {
            byte[] content = Files.readAllBytes(path);
            String base64 = Base64.getEncoder().encodeToString(content);
            int key = Math.abs(args.hashCode()); // hash filename kao key
            String value = args + "::" + base64;

            int ownerPort = AppConfig.chordState.getNextNodeForKey(key).getListenerPort();
            Message putMsg = new PutFileMessage(
                    AppConfig.myServentInfo.getListenerPort(),
                    ownerPort,
                    key, value
            );
            MessageUtil.sendMessage(putMsg);
        } catch (IOException e) {
            AppConfig.timestampedErrorPrint("Greška prilikom čitanja fajla.");
        }
    }
}