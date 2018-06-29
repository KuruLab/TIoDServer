package server;

import client.Notification;
import game.GameSession;
import game.CorePlayer;
import game.GameControl;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import map.Location;
import map.Room;

/**
 *
 * @author Kurumin
 */
public class DungeonServer extends UnicastRemoteObject implements Runnable, DungeonServerInterface{
    
    private GameSession game;  
    private DungeonServerList serverList;  
    
    //private int numJogadorCont = -1; // Numero de jogadores - Se -1 jogo parado
    
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
        serverList.incCounter();
        for (Iterator i = serverList.getCollection().iterator(); i.hasNext();) {
            Notification client = (Notification) i.next(); 
            client.sendMessage(name, s);
        }
        serverList.decCounter();
    }

    // ******************************************************    
    // Descreve o procedimento do metodo deixar
    // ******************************************************
    @Override
    public synchronized void leave(Notification n, String name) throws RemoteException {
        serverList.remove(name, n);
        
        for(CorePlayer player : game.getPlayers()){
            if(player.getName().equalsIgnoreCase(name)){
                game.getPlayers().remove(player);
                break;
            }
        }

        serverList.incCounter();
        for (Iterator i = serverList.getCollection().iterator(); i.hasNext();) {
            Notification client = (Notification) i.next();
            client.leaveMessage(name);
        }
        serverList.decCounter();
        System.out.println("The client  " + name + "  left the game\n");
    }

    // ****************************************************
    // Descreve o procedimento do metodo Juntar 
    // ****************************************************
    @Override
    public void join(Notification n, String name) throws RemoteException {
        //numJogadorCont++;
        /*for (int i = 0; i < game.getPlayers().size(); i++) {
            // ?
        }*/
        verifyPlayerName(name);
        
        serverList.add(name, n); 
        // Mostra no servidor name e URL - so para ver a diferenca
        System.out.println("The client " + name + " joined in: \n\tURL : " + n);
        serverList.incCounter();
        // Informar aos outros clientes que um novo usuario esta conectado
        // Enviando mensagem O cliente ( nome xxx ) juntou-se.
        for (Iterator i = serverList.getCollection().iterator(); i.hasNext();) {
            Notification client = (Notification) i.next();
            client.joinMessage(name);;
        }
        setupPlayer(n, name);
        
        serverList.decCounter();
    }
    
    private void setupPlayer(Notification n, String name) throws RemoteException{
         // Create and setup Player initial stage
        CorePlayer player = new CorePlayer(name);
        Location startLocation = this.game.getControl().getAStartPoint();
        this.game.getControl().incrementStartUsage(startLocation.getRoom());
        player.setLocation(startLocation);
        this.game.getPlayers().add(player); 
        n.newLocationMessage(startLocation.toString());
    }
    
    private void verifyPlayerName(String name){
        for(CorePlayer player : this.game.getPlayers())
            if(player.getName().equalsIgnoreCase(name)){
                System.err.println("Warning: the name "+name+" is already taken!");
                return;
            }
    }

    @Override
    public void proccessCommand(Notification n, String name, String s) throws RemoteException {
        System.out.println("Server processing command from client " + name + ".\nCommand: "+s );
        if(s.startsWith("/leave")){
            leave(n, name);
        }
        else if(s.startsWith("/move")){
            move(n, name, s);
        }
        else if(s.startsWith("/look")){
            look(n, name, s);
        }
        else if(s.startsWith("/talk")){
            talk(name, s.substring(6));
        }
        else{
            talk(name, s);
            wrongCommand(n);
        }
    }
    
    public void wrongCommand(Notification n) throws RemoteException{
        String help = "[Help]: Currently available commands:\n"
                + "/talk <message> -> say something in chat\n"
                + "/move <door> -> open the specified door\n"
                + "/look -> take a look around and gather information\n"
                + "/leave -> rage quit\n";
        n.wrongCommandMessage(help);
    }

    @Override
    public void move(Notification n, String name, String s) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void look(Notification n, String name, String s) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
