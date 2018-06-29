package client;

import java.rmi.*;

public interface Notification extends Remote {

    public void joinMessage(String name) throws RemoteException;

    public void sendMessage(String name, String message) throws RemoteException;

    public void leaveMessage(String name) throws RemoteException;

    public void newLocationMessage(String locationString) throws RemoteException;
    
    public void wrongCommandMessage(String message) throws RemoteException;

}
