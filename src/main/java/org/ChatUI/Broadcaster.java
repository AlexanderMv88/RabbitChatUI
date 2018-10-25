package org.ChatUI;

import org.ChatUI.ui.NavigatorUI;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Component
@EnableScheduling
public class Broadcaster implements Serializable {

    static ExecutorService executorService = Executors.newSingleThreadExecutor();

    public interface BroadcastListener {
        void receiveBroadcast(WebSocketMsg message);

    }

    private static LinkedList<BroadcastListener> listeners = new LinkedList<BroadcastListener>();
    private static Map<BroadcastListener, String> userlist = new ConcurrentHashMap<BroadcastListener, String>();

    public static synchronized void register(BroadcastListener newlistener) {
        listeners.add(newlistener);
        LinkedList<BroadcastListener> removeList = new LinkedList<BroadcastListener>();
        for (final BroadcastListener listener: listeners) {
            if (listener != newlistener && !((NavigatorUI)listener).getPushConnection().isConnected()) {
                removeList.add(listener);
            }
        }
        for (final BroadcastListener listener: removeList) {
            listeners.remove(listener);
            userlist.remove(listener);
        }
    }

    public static void pushUserList(BroadcastListener targetListener, String username) {
        if (userlist.get(targetListener) != null) {
            userlist.remove(targetListener);
            userlist.put(targetListener, username);
        } else {
            userlist.put(targetListener, username);
        }
        for (final BroadcastListener listener: listeners) {

        }
    }

    public static synchronized void unregister(BroadcastListener listener) {
        listeners.remove(listener);
    }

    public static synchronized void broadcast(final WebSocketMsg message) {
        for (final BroadcastListener listener: listeners)
            executorService.execute(() -> {
                listener.receiveBroadcast(message);
            });
    }

    @Scheduled(fixedRate=1000)
    public static void timer(){
        WebSocketMsg msg = new WebSocketMsg("time");
        broadcast(msg);

    }


}