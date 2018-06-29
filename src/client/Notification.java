package client;

import java.rmi.*;

public interface Notification extends Remote {
    
    public String getPlayerName() throws RemoteException;
    
    public void helpMessage(String name) throws RemoteException;
    
    public void joinMessage(String name) throws RemoteException;

    public void sendMessage(String name, String message) throws RemoteException;

    public void leaveMessage(String name) throws RemoteException;

    public void newLocationMessage(String locationString) throws RemoteException;
    
    public void movementMessage(String locationString) throws RemoteException;
    
    public void locationInformationMessage(String message) throws RemoteException;
    
    public void wrongCommandMessage() throws RemoteException;

}
