package game;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Kurumin
 */
public class GameSession {
    
    private String dungeonID;
    private List<Player> players;
    private Date startTime;
    
    
    public GameSession(String dungeonID) { 
        this.dungeonID = dungeonID;
        this.players = new ArrayList<>();
        this.startTime = new Date(System.currentTimeMillis());
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.ENGLISH);
        System.out.println("Starting a new Game Session: "+this.dungeonID+" at "+df.format(this.startTime));
    }
    
    public String getDungeonID() {
        return dungeonID;
    }

    public void setDungeonID(String dungeonID) {
        this.dungeonID = dungeonID;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    } 
}
