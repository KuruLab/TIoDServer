package dungeonserver;

import java.util.*;
import client.Notification;

class DungeonServerList {

    private Collection<Notification> threadList = new ArrayList<>();
    private int counter = 0;

    public synchronized void add(Notification item) {
        try {
            while (counter > 0) {
                wait();
            }
            threadList.add(item);
        } catch (InterruptedException e) {
            System.out.println("Addition interrupted.");
        } finally {
            notifyAll();
        }
    }

    public synchronized void remove(Notification item) {
        try {
            while (counter > 0) {
                wait();
            }
            threadList.remove(item);
        } catch (InterruptedException e) {
            System.out.println("Removal interrupted.");
        } finally {
            notifyAll();
        }
    }

    public synchronized void incCounter() {
        counter++;
        notifyAll();
    }

    public synchronized void decCounter() {
        counter--;
        notifyAll();
    }

    public Collection getCollection() {
        return threadList;
    }
}
