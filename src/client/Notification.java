package client;

import java.rmi.*;

public interface Notification extends Remote {

    public void joinMessage(String name) throws RemoteException;

    public void sendMessage(String name, String message) throws RemoteException;

    public void leaveMessage(String name) throws RemoteException;

    //public void playerIsReady(int slot, boolean isReady) throws RemoteException;

    //public void setupPlayer(Player player) throws RemoteException;

    //public void updateBanner(Player player) throws RemoteException;

    //public void removeBanner(int id) throws RemoteException;

    //public void displayTable(Game game) throws RemoteException;

    //public void enableReadyButton(int p, int[] others) throws RemoteException;

    //public void displayList(ArrayList<Card> hand) throws RemoteException;

    //public void turnON_OFF(boolean on_off) throws RemoteException;
}
