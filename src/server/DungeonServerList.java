package server;

import java.util.*;
import client.Notification;

class DungeonServerList {

    private HashMap<String, Notification> threadList = new HashMap<>();
    private int counter = 0;

    public synchronized void add(String name, Notification item) {
        try {
            while (counter > 0) {
                wait();
            }
            threadList.put(name, item);
        } catch (InterruptedException e) {
            System.out.println("Addition interrupted.");
        } finally {
            notifyAll();
        }
    }

    public synchronized void remove(String name, Notification item) {
        try {
            while (counter > 0) {
                wait();
            }
            threadList.remove(name);
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
        return threadList.values();
    }
}
