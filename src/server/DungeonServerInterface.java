package server;

import java.rmi.*;
import client.Notification;

//Interface do cliente para o servidor
//
//O cliente pode invocar estes m�todos remotos no servidor:
//
public interface DungeonServerInterface extends Remote {

    public void proccessCommand(Notification n, String name, String s) throws RemoteException;
    
    public void join(Notification n, String name) throws RemoteException;

    public void talk(String name, String s) throws RemoteException;

    public void leave(Notification n, String name) throws RemoteException;

    public void move(Notification n, String name, String s) throws RemoteException;
    
    public void look(Notification n, String name, String s) throws RemoteException;
    
    public void help(Notification n) throws RemoteException;
}
