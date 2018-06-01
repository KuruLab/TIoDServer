package server;

import java.rmi.*;
import client.Notification;

//Interface do cliente para o servidor
//
//O cliente pode invocar estes m�todos remotos no servidor:
//
public interface DungeonServerInterface extends Remote {

    public void join(Notification n, String name) throws RemoteException;

    public void talk(String name, String s) throws RemoteException;

    public void leave(Notification n, String name) throws RemoteException;

    //public void playerIsReady(int slot, boolean isReady) throws RemoteException;

    //public void playCard(int card, int i, int j) throws RemoteException;

    //public boolean isSlotEmpty(int slot) throws RemoteException;
}
