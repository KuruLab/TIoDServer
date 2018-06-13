package server;

import client.Notification;
import game.GameSession;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kurumin
 */
public class DungeonServer extends UnicastRemoteObject implements Runnable, DungeonServerInterface{
    
    private GameSession game;
    private DungeonServerList serverList;
    
    
    private int numJogadorCont = -1; // Numero de jogadores - Se -1 jogo parado
    
    public DungeonServer(String id) throws RemoteException {
        this.serverList = new DungeonServerList();
             
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
            
            String rmiUrl = "rmi://localhost:1099/TIoDungeoneering";
            String[] servicos = Naming.list(rmiUrl);
            for (int i = 0; i < servicos.length; i++) {
                System.out.println(i+" - "+servicos[i]);
                if (servicos[i].equals(rmiUrl.substring(4))) {
                    Naming.unbind("rmi:" + servicos[i]);
                }
            }
            // Implementa o Servidor de nome server com o RMI rebind
            Naming.rebind(rmiUrl, this);

            System.out.println("The dungeon \'"+game.getDungeonID()+"\' is ready!");
        } catch (java.net.MalformedURLException e) {
            System.out.println("Malformed URL name" + e.toString());
        } catch (RemoteException e) {
            System.out.println("Communication Error: " + e.toString());
        } catch (NotBoundException ex) {
            Logger.getLogger(DungeonServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // *****************************************************
    // Descreve o procedimento do metodo conversar
    // *****************************************************
    @Override
    public void talk(String name, String s) throws RemoteException {
        
    }

    // ******************************************************    
    // Descreve o procedimento do metodo deixar
    // ******************************************************
    @Override
    public synchronized void leave(Notification n, String name) throws RemoteException {
        
    }

    // ****************************************************
    // Descreve o procedimento do metodo Juntar 
    // ****************************************************
    @Override
    public void join(Notification n, String name) throws RemoteException {
        numJogadorCont++;
        for (int i = 0; i < game.getPlayers().size(); i++) {
            // ?
        }
        serverList.add(n); 
        // Mostra no servidor name e URL - so para ver a diferenca
        System.out.println("The client " + name + " joined in: \n\tURL : " + n);
        serverList.incCounter();
        
        // Informar aos outros clientes que um novo usuario esta conectado
        // Enviando mensagem O cliente ( nome xxx ) juntou-se na posicao I = (numero).
        //int count = 0;
        for (Iterator i = serverList.getCollection().iterator(); i.hasNext();) {
            Notification client = (Notification) i.next();
            client.joinMessage(name);
            //count++;
        }
        serverList.decCounter();
    }
}
