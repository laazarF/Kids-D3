package servent.failure;

import app.AppConfig;
import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.LeaveMessage;
import servent.message.PingMessage;
import servent.message.BuddyPingMessage;
import servent.message.util.MessageUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

public class FailureDetector {

    private static final int PING_INTERVAL_MS = 3000;
    private static final int SOFT_TIMEOUT_MS = 4000;
    private static final int HARD_TIMEOUT_MS = 10000;

    private final Map<Integer, Long> lastPongTimestamps = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final Set<Integer> softSuspected = ConcurrentHashMap.newKeySet();

    private static FailureDetector instance = new FailureDetector();

    public static FailureDetector getInstance() {
        return instance;
    }

    private FailureDetector() {}

    public void start() {
        scheduler.scheduleAtFixedRate(this::pingLoop, 2000, PING_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    private void pingLoop() {
        ServentInfo pred = AppConfig.chordState.getPredecessor();
        ServentInfo succ = AppConfig.chordState.getSuccessorTable()[0];

        if (succ != null && pred != null && succ.getListenerPort() == pred.getListenerPort()) {
            pingIfValid(succ); // samo jedan PING
        } else {
            pingIfValid(pred);
            pingIfValid(succ);
        }

        long now = System.currentTimeMillis();
        checkTimeout(pred, now);
        checkTimeout(succ, now);
    }

    private void pingIfValid(ServentInfo target) {
        if (target != null && target.getListenerPort() != AppConfig.myServentInfo.getListenerPort()) {
            AppConfig.timestampedStandardPrint("Slanje PING ka " + target.getListenerPort());
            PingMessage ping = new PingMessage(
                    AppConfig.myServentInfo.getListenerPort(),
                    target.getListenerPort()
            );
            MessageUtil.sendMessage(ping);
        }
    }

    private void checkTimeout(ServentInfo target, long now) {
        if (target == null || target.getListenerPort() == AppConfig.myServentInfo.getListenerPort()) return;

        int port = target.getListenerPort();
        Long lastSeen = lastPongTimestamps.get(port);

        if (lastSeen == null || now - lastSeen > SOFT_TIMEOUT_MS) {
            if (!softSuspected.contains(port)) {
                AppConfig.timestampedErrorPrint("Sumnjamo da je čvor " + port + " pao. Pitaćemo buddy-ja da proveri.");
                softSuspected.add(port);
                // TODO: Buddy sistem može se dodati kasnije
                // Pošalji zahtev buddy-ju da proveri ovaj čvor
                ServentInfo buddy = (target == AppConfig.chordState.getPredecessor())
                        ? AppConfig.chordState.getSuccessorTable()[0]
                        : AppConfig.chordState.getPredecessor();

                if (buddy != null
                        && buddy.getListenerPort() != AppConfig.myServentInfo.getListenerPort()
                        && buddy.getListenerPort() != port) {
                    BuddyPingMessage buddyPing = new BuddyPingMessage(
                            AppConfig.myServentInfo.getListenerPort(),
                            buddy.getListenerPort(),
                            port // target čvor koji buddy treba da proveri
                    );
                    MessageUtil.sendMessage(buddyPing);
                } else {
                    AppConfig.timestampedStandardPrint("❌ Buddy za čvor " + port + " je nevažeći (isti kao target ili self).");
                }
            } else if (now - lastSeen > HARD_TIMEOUT_MS) {
                AppConfig.timestampedErrorPrint("Čvor " + port + " potvrđeno pao. Pokrećemo LEAVE za njega.");
                softSuspected.remove(port);
                lastPongTimestamps.remove(port);

                if (AppConfig.mutex != null && AppConfig.tokenNumbers != null && AppConfig.tokenNumbers.length > 0) {
                    AppConfig.mutex.requestCS();
                }

                int leavingPort = port;
                ServentInfo leavingNode = new ServentInfo("localhost", leavingPort);

                // Uklanjamo iz successor tabele
                ServentInfo[] succTable = AppConfig.chordState.getSuccessorTable();
                for (int i = 0; i < succTable.length; i++) {
                    if (succTable[i] != null && succTable[i].getListenerPort() == leavingPort) {
                        succTable[i] = null;
                        AppConfig.timestampedStandardPrint("Uklonjen successor " + leavingPort + " iz tabele.");
                    }
                }

                // Ako je predecessor - resetuj
                if (AppConfig.chordState.getPredecessor() != null &&
                        AppConfig.chordState.getPredecessor().getListenerPort() == leavingPort) {
                    AppConfig.chordState.setPredecessor(null);
                    AppConfig.timestampedStandardPrint("Resetovan predecessor jer je " + leavingPort + " pao.");
                }

                // Ukloni iz liste poznatih čvorova
                List<ServentInfo> allNodes = AppConfig.chordState.getAllNodeInfo();
                allNodes.removeIf(s -> s.getListenerPort() == leavingPort);
                AppConfig.chordState.refreshSuccessorTable(allNodes);

                // Pošalji LEAVE poruku (propagira se dalje kao da je čvor sam napustio)
                BasicMessage leaveMsg = new LeaveMessage(AppConfig.myServentInfo.getListenerPort(), leavingPort);
                MessageUtil.sendMessage(leaveMsg);

                if (AppConfig.mutex != null && AppConfig.tokenNumbers != null && AppConfig.tokenNumbers.length > 0) {
                    AppConfig.mutex.releaseCS();
                }
            }
        }
    }

    public void onPongReceived(int port) {
        lastPongTimestamps.put(port, System.currentTimeMillis());
        softSuspected.remove(port);
        AppConfig.timestampedStandardPrint("Primljen PONG od " + port);
    }
}