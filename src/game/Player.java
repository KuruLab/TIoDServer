package game;

/**
 *
 * @author Kurumin
 */
public class Player {

    private String name;

    public Player() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString(){
        return name;
    }
}
