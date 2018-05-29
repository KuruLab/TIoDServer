package dungeonserver;

import java.rmi.*;
import client.Notification;

//Interface do cliente para o servidor
//
//O cliente pode invocar estes m�todos remotos no servidor:
//
public interface DungeonServerInterface extends Remote {

    public void juntar(Notification n, String name) throws RemoteException;

    public void conversar(String name, String s) throws RemoteException;

    public void deixar(Notification n, String name) throws RemoteException;

    //public void playerIsReady(int slot, boolean isReady) throws RemoteException;

    //public void playCard(int card, int i, int j) throws RemoteException;

    //public boolean isSlotEmpty(int slot) throws RemoteException;
}
