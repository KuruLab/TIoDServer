/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonserver;

import client.Notification;
import game.GameSession;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;

/**
 *
 * @author Kurumin
 */
public class DungeonServer extends UnicastRemoteObject implements Runnable, DungeonServerInterface{
    
    private GameSession game;
    private DungeonServerList serverList;
    private Date startTime;
    
    private int numJogadorCont = -1; // Numero de jogadores - Se -1 jogo parado
    
    public DungeonServer(String id) throws RemoteException {
        this.serverList = new DungeonServerList();
        this.startTime = new Date(System.currentTimeMillis());
        
        startNewGame(id);
    }

    public void startNewGame(String id) {
        this.game = new GameSession(id);
    }

    @Override
    public void run() {
        try {
            // Porta 1099 ea porta default do rmiregistry 
            LocateRegistry.createRegistry(1099);

            // Implementa o Servidor de nome server com o RMI rebind
            Naming.rebind("rmichat", this);

            System.out.println("The dungeon \'"+game.getDungeonID()+"\' is ready!");
        } catch (java.net.MalformedURLException e) {
            System.out.println("nome da URL mal formado para Servidor de Mesagem "
                    + e.toString());
        } catch (RemoteException e) {
            System.out.println("Erro de comunicação" + e.toString());
        }
    }
    
    // *****************************************************
    // Descreve o procedimento do metodo conversar
    // *****************************************************
    @Override
    public void conversar(String name, String s) throws RemoteException {
        
    }

    // ******************************************************    
    // Descreve o procedimento do metodo deixar
    // ******************************************************
    @Override
    public synchronized void deixar(Notification n, String name) throws RemoteException {
        
    }

    // ****************************************************
    // Descreve o procedimento do metodo Juntar 
    // ****************************************************
    @Override
    public void juntar(Notification n, String name) throws RemoteException {
    
    }
}
