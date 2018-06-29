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
import java.util.Arrays;
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
            //if(!client.getPlayerName().equalsIgnoreCase(name))
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
            client.joinMessage(name);
        }
        setupPlayer(n, name);
        
        serverList.decCounter();
    }
    
    private void setupPlayer(Notification n, String name) throws RemoteException{
         // Create and setup Player initial stage
        CorePlayer player = new CorePlayer(name);
        this.game.getPlayers().add(player);
        Location startLocation = this.game.getControl().getAStartPoint();
        this.game.getControl().incrementStartUsage(startLocation.getRoom());
        
        player.setLocation(startLocation);
        
        String initialMsg = game.getControl().postMovementProcessing(player, player.getLocation(), player.getLocation().getRoom());
        initialMsg += game.getControl().getLocationInformation(player);
        n.locationInformationMessage(initialMsg);
    }
    
    private void verifyPlayerName(String name){
        for(CorePlayer player : this.game.getPlayers())
            if(player.getName().equalsIgnoreCase(name)){
                System.err.println("Warning: the name "+name+" is already taken and this will break the game system! Enjoy the bugs!");
                return;
            }
    }
    
    private CorePlayer findPlayerByName(String name){
        for(CorePlayer player : this.game.getPlayers())
            if(player.getName().equalsIgnoreCase(name)){
                return player;
            }
        System.err.println("Warning: It was not possible to find player "+name+"! Enjoy the bugs!");
        return null;
    }

    @Override
    public void proccessCommand(Notification n, String name, String s) throws RemoteException {
        // o bot pode entrar aqui como um controlador geral
        String[] command = s.replaceAll("\n", " ").split(" ");
        System.out.println("Server processing command from client " + name + ".\nCommand: "+Arrays.toString(command) );
        
        if(command[0].equalsIgnoreCase("/leave")){
            leave(n, name);
        }
        else if(command[0].equalsIgnoreCase("/move")){
            if(command.length > 1)
                move(n, name, command[1]);
            else
                move(n, name, "");
        }
        else if(command[0].equalsIgnoreCase("/look")){
            look(n, name);
        }
        else if(command[0].equalsIgnoreCase("/loot")){
            loot(n, name);
        }
        else if(command[0].equalsIgnoreCase("/inventory")){
            inventory(n, name);
        }
        else if(command[0].equalsIgnoreCase("/historic")){
            historic(n, name);
        }
        else if(command[0].equalsIgnoreCase("/attack")){
            if(command.length > 1)
                attack(n, name, command[1]);
            else
                attack(n, name, "");
        }
        else if(command[0].equalsIgnoreCase("/talk")){
            // ou o bot pode entrar aqui só pra chat mesmo
            if(command.length > 1)
                talk(name, command[1]);
            /*else
                talk(name, "");*/      
        }
        else if(command[0].equalsIgnoreCase("/help")){
            help(n);
        }
        else{
            //talk(name, s.substring(0, s.length()-1));
            wrongCommand(n);
        }
    }
    
    public void wrongCommand(Notification n) throws RemoteException{
        n.wrongCommandMessage();
    }

    @Override
    public void move(Notification n, String name, String arg) throws RemoteException {
        CorePlayer player = findPlayerByName(name);
        String result = game.getControl().processMovement(player, arg);
        n.movementMessage(result);
    }

    @Override
    public void look(Notification n, String name) throws RemoteException {
        CorePlayer player = findPlayerByName(name);
        String info = game.getControl().getLocationInformation(player);
        n.locationInformationMessage(info);
    }
    
    @Override
    public void loot(Notification n, String name) throws RemoteException {
        CorePlayer player = findPlayerByName(name);
        String lootMsg = game.getControl().processLoot(player);
        n.lootMessage(lootMsg);
    }
    
    @Override
    public void inventory(Notification n, String name) throws RemoteException {
        CorePlayer player = findPlayerByName(name);
        String lootMsg = game.getControl().getInventory(player);
        n.lootMessage(lootMsg);
    }
    
    @Override
    public void help(Notification n) throws RemoteException {
        String help = "I will help you! These are the currently available commands:\n"
                + " - /talk <message> -> say something in chat\n"
                + " - /move <i> (default is 0) | back | random | stair -> open the specified door | return to previous location | open a random door | enter the stair."
                + " Examples: \"/move\" (to open door 0), \"/move 2\" (to open door 2), \"/move random\", \"/move back\", \"/move stair\". \n"
                + " - /look -> take a look around and gather information\n"
                + " - /loot -> get the room's items\n"
                + " - /inventory -> to list what you have found so far\n"
                + " - /attack -> perform your killing strike\n"
                + " - /historic -> check where you already visited\n"
                + " - /leave -> rage quit\n"
                + " - /help -> display this message\n";
        n.helpMessage(help);
    }

    @Override
    public void attack(Notification n, String name, String arg) throws RemoteException {
        CorePlayer player = findPlayerByName(name);
        String attackMsg = game.getControl().attack(player, arg);
        n.attackMessage(attackMsg);
    }

    @Override
    public void historic(Notification n, String name) throws RemoteException {
        CorePlayer player = findPlayerByName(name);
        String historic = game.getControl().getHistoric(player);
        n.historicMessage(historic);
    }
}
