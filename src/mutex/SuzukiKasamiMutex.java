package mutex;

import app.AppConfig;
import servent.message.BasicMessage;
import servent.message.MessageType;
import servent.message.RequestMutexMessage;
import servent.message.TokenMessage;
import servent.message.util.MessageUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class SuzukiKasamiMutex {

    private static final int N = AppConfig.SERVENT_COUNT;

    // Broj zahteva za svaki proces
    private final int[] requestNumbers = new int[N];

    // Red čekanja
    private final Queue<Integer> requestQueue = new LinkedList<>();

    // Da li imamo token?
    private boolean hasToken = false;

    // Da li trenutno koristimo CS?
    private final AtomicBoolean usingCS = new AtomicBoolean(false);

    // Da li smo poslali zahtev
    private final AtomicBoolean waiting = new AtomicBoolean(false);

    public synchronized void handleRequest(int senderId, int senderRN) {
        requestNumbers[senderId] = Math.max(requestNumbers[senderId], senderRN);

        if (hasToken && !usingCS.get() && !requestQueue.contains(senderId)) {
            if (requestNumbers[senderId] > AppConfig.tokenNumbers[senderId]) {
                requestQueue.add(senderId);
                sendTokenToNext();
            }
        }
    }

    public synchronized void handleToken(int[] receivedTokenNumbers) {
        System.arraycopy(receivedTokenNumbers, 0, AppConfig.tokenNumbers, 0, N);
        hasToken = true;
        AppConfig.setHasToken(true);
        notifyAll();
    }

    public synchronized void requestCS() {
        int myId = AppConfig.myServentInfo.getId();
        requestNumbers[myId]++;

        for (int i = 0; i < N; i++) {
            if (i != myId) {
                RequestMutexMessage reqMsg = new RequestMutexMessage(
                        AppConfig.myServentInfo.getListenerPort(),
                        AppConfig.getInfoById(i).getListenerPort(),
                        requestNumbers[myId]
                );
                MessageUtil.sendMessage(reqMsg);
            }
        }


        waiting.set(true);

        while (!hasToken) {
            try {
                wait();
            } catch (InterruptedException ignored) {}
        }

        usingCS.set(true);
        waiting.set(false);
    }

    public synchronized void releaseCS() {
        int myId = AppConfig.myServentInfo.getId();
        AppConfig.tokenNumbers[myId] = requestNumbers[myId];
        usingCS.set(false);

        for (int i = 0; i < N; i++) {
            if (i != myId && requestNumbers[i] > AppConfig.tokenNumbers[i]) {
                if (!requestQueue.contains(i)) {
                    requestQueue.add(i);
                }
            }
        }

        sendTokenToNext();
    }

    private void sendTokenToNext() {
        if (!requestQueue.isEmpty()) {
            int nextId = requestQueue.poll();

            TokenMessage tokenMsg = new TokenMessage(
                    AppConfig.myServentInfo.getListenerPort(),
                    AppConfig.getInfoById(nextId).getListenerPort(),
                    AppConfig.tokenNumbers
            );
            MessageUtil.sendMessage(tokenMsg);
            hasToken = false;
            AppConfig.setHasToken(false);
        }
    }
}