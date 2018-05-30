package game;

import java.util.List;

/**
 *
 * @author Kurumin
 */
public class GameSession {
    
    private String dungeonID;
    private List<Player> players;

    public GameSession(String dungeonID) {
        this.dungeonID = dungeonID;
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
