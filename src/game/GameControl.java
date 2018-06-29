/*
 * Copyright (C) 2018 Kurumin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import map.Dungeon;
import map.Level;
import map.Location;
import map.Room;

/**
 *
 * @author Kurumin
 */
public class GameControl {
    
    private Dungeon dungeon;
    private HashMap<Room, Integer> startUsage;
    //private MappingHistoric historic;

    public GameControl(Dungeon dungeon) {
        this.dungeon = dungeon;
        this.startUsage = new HashMap<>();
        //this.historic = MappingHistoric();
    }
    
    public void incrementStartUsage(Room start){
        if(startUsage.containsKey(start)){
            Integer uses = startUsage.getOrDefault(start, 0) + 1;
            startUsage.put(start, uses);
        }
        else{
            startUsage.put(start, 1);
        }
    }
    
    // get randomly a start point among the lest used start points
    public Location getAStartPoint(){
        List<Level> tierOneLevels = dungeon.getTierMap().get(1);
        List<Level> lessUsedLevels = new ArrayList<>();
        int min = Integer.MAX_VALUE;
        for(Level level : tierOneLevels){
            int used = startUsage.getOrDefault(level.getStart(), 0);
            if(used == min){
                lessUsedLevels.add(level);
            }
            else if(used < min){
                min = used;
                lessUsedLevels.clear();
                lessUsedLevels.add(level);
            }
        }
        Collections.shuffle(lessUsedLevels);
        Location startPoint = new Location(dungeon, lessUsedLevels.get(0), lessUsedLevels.get(0).getStart());
        return startPoint;
    }
}
