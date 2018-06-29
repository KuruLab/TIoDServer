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
    
    private GameControl control;
    
    private Dungeon dungeon;
    
    public GameSession(String dungeonID) { 
        this.dungeonID = dungeonID;
        this.players = new ArrayList<>();
        this.startTime = new Date(System.currentTimeMillis());
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.ENGLISH);
        System.out.println("Starting a new Game Session: "+this.dungeonID+" at "+df.format(this.startTime));
        this.dungeon = loadDungeon();
        this.control = new GameControl(dungeon);
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public GameControl getControl() {
        return control;
    }

    public void setControl(GameControl control) {
        this.control = control;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
    }
}
