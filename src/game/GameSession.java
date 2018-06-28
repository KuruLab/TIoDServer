package game;

import engine.DungeonGenerator;
import io.DungeonFileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import map.Dungeon;

/**
 *
 * @author Kurumin
 */
public class GameSession {
    
    private String dungeonID;
    private List<CorePlayer> players;
    private Date startTime;
    private Dungeon dungeon;
    
    public GameSession(String dungeonID) { 
        this.dungeonID = dungeonID;
        this.players = new ArrayList<>();
        this.startTime = new Date(System.currentTimeMillis());
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.ENGLISH);
        System.out.println("Starting a new Game Session: "+this.dungeonID+" at "+df.format(this.startTime));
        this.dungeon = loadDungeon();
    }
    
    private Dungeon loadDungeon(){
        String path = DungeonGenerator.folder + dungeonID + ".json";
        DungeonFileReader dfr = new DungeonFileReader(path);
        Dungeon readDungeon = dfr.parseJson();
        System.out.println(readDungeon);
        return readDungeon;
    }
    
    public String getDungeonID() {
        return dungeonID;
    }

    public void setDungeonID(String dungeonID) {
        this.dungeonID = dungeonID;
    }

    public List<CorePlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<CorePlayer> players) {
        this.players = players;
    } 
}
