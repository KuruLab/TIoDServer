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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import map.Door;

import map.Dungeon;
import map.Level;
import map.Location;
import map.Room;
import puzzle.Condition;
import puzzle.Symbol;

/**
 *
 * @author Kurumin
 */
public class GameControl {
    
    private Dungeon dungeon;
    private HashMap<Room, Integer> startUsage;
    private HashMap<CorePlayer, MappingHistoric> historics;

    public GameControl(Dungeon dungeon) {
        this.dungeon = dungeon;
        this.startUsage = new HashMap<>();
        this.historics = new HashMap<>();
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
    
    public void updateHistoric(CorePlayer player, Location newLocation) {
        if (historics.containsKey(player)) {
            MappingHistoric historic = historics.get(player);
            historic.getVisitedRooms().add(newLocation);
        }
        else{
            MappingHistoric historic = new MappingHistoric();
            historic.getVisitedRooms().add(newLocation);
            historics.put(player, historic);
        }
    }
    
    // returns true if player has visited, false otherwise
    public boolean isVisited(CorePlayer player, Location location){
        if (historics.containsKey(player)) {
            MappingHistoric historic = historics.get(player);
            for(Location next : historic.getVisitedRooms()){
                if(next.toString().equalsIgnoreCase(location.toString()))
                    return true;
            }
        }
        return false;
    }
    
    public String processMovement(CorePlayer player, String move){
        String result = "";
        if(move.equalsIgnoreCase("")){
            Room room = player.getLocation().getRoom();
            List<Door> doorList = room.getDoors();
            Door door = doorList.get(0);
            Location nextLocation = getNextLocation(player, room, door);
            result += normalMovement(player, room, door, nextLocation);
        }
        else if(move.equalsIgnoreCase("random")){ 
            Room room = player.getLocation().getRoom();
            List<Door> doorList = room.getDoors();
            Random random = new Random();
            int index = random.nextInt(doorList.size());
            result += "You decided to walk randomly and moved to door "+index+". ";
            Door door = doorList.get(index);
            Location nextLocation = getNextLocation(player, room, door);
            result += normalMovement(player, room, door, nextLocation);
        }
        else if(move.equalsIgnoreCase("back")){
            result += "You decided to move back to your previous location. ";
            Location swap = player.getLocation();
            player.setLocation(player.getPrevious());
            player.setPrevious(swap);
        }
        else{
            try{
                int index = Integer.parseInt(move);
                Room room = player.getLocation().getRoom();
                List<Door> doorList = room.getDoors();
                try{
                    Door door = doorList.get(index);
                    Location nextLocation = getNextLocation(player, room, door);
                    result += normalMovement(player, room, door, nextLocation);
                }
                catch(java.lang.IndexOutOfBoundsException ex){
                    System.err.println(ex.getMessage());
                    result += "Your request makes sense, but there is not such door! "
                            + "Please, take a better /look around and tell me your path. ";
                }
            }
            catch(java.lang.NumberFormatException ex){
                System.err.println(ex.getMessage());
                result += "Your request makes no sense! "
                        + "You must tell me the number of the door you want to go. ";
            }
        }
        return result;
    }
  
    public Location getNextLocation(CorePlayer player, Room room, Door door) {
        Room nextRoom;
        if (door.getA().getId() == room.getId()) {
            nextRoom = door.getB();
        } else {
            nextRoom = door.getA();
        }
        Location nextLocation = new Location(player.getLocation().getDungeon(), player.getLocation().getLevel(), nextRoom);
        return nextLocation;
    }
    
    public String normalMovement(CorePlayer player, Room room, Door door, Location nextLocation) {
        String result = "";
        Room nextRoom = nextLocation.getRoom();
        if (canMoveTo(player, nextLocation)) {
            if (door.isOpen()) {
                result += "You walked towards room " + nextRoom.getId() + " through the open door. ";
            } else {
                if (door.getCondition().getKeyLevel() > 0) {
                    result += "Fortunately, your locksmith is working! ";
                } else {
                    result += "You opened the door without any difficulties. ";
                }
                //result += "You walked towards room "+nextRoom.getId()+". ";
            }
            door.setOpen(true);
            System.out.println(nextRoom.getDoors().get(nextRoom.getDoors().indexOf(door)).isOpen());

            player.setPrevious(player.getLocation());
            player.setLocation(nextLocation);

            result += "You are now inside room " + nextRoom.getId() + ". ";

            if (isVisited(player, nextLocation)) {
                result += "As you can see, you have been here before. ";
            } else {
                result += "This is your first time here. "
                        + "Take a /look around for more information. ";
            }

            updateHistoric(player, nextLocation);
        } else {
            //Condition condition = door.getCondition();
            result += "I'm sorry, but you can't open this door. "
                    + "It is locked and you don't have the required key. ";
        }
        return result;
    }
    
    // check if the player have the necessary keylevel
    public boolean canMoveTo(CorePlayer player, Location location){
        Room room = location.getRoom();
        Condition condition = room.getCondition();
        if(condition.getKeyLevel() > 0){
            List<Symbol> keyList = player.getIventory().getSymbols();
            boolean hasKey = false;
            for(Symbol key : keyList){
                hasKey = hasKey || (condition.getKeyLevel() <= key.getValue());
            }
            return hasKey;
        }
        else return true;
    }
    
    public String getLocationInformation(CorePlayer player){
        Room room = player.getLocation().getRoom();
        Room prev; 
        Location prevLoc = player.getPrevious();
        
        if(prevLoc == null)
            prev = room;
        else
            prev = prevLoc.getRoom();
            
        String result = "You are currently at the room "+room.getId()+". ";
        result += "It's coordinates are "+Arrays.toString(room.getCoord())+". ";
        if(room.getLore() != null)
            result += room.getLore();
        
        for(Symbol symbol : room.getSymbols()){
            if(symbol.isKey()){
                result += "This room contains the "+symbol.toString()+" key! ";
            }
            else if(symbol.isSwitch()){
                result += "This room contains a switch! ";
            }
            else if(symbol.isBoss()){
                result += "There is an evil boss inside, guarding it, beware! ";
            }
            else if(symbol.isStart()){
                result += "Looks like this is the entrance of this level. ";
            }
            else if(symbol.isStair()){
                result += "There are stairs to another level. ";
            }
            else if(symbol.isNothinig()){
                result += "There is nothing else here. ";
            }
            else result += "Something is wrong with this map! ";
        }
        if(room.getDoors().size() < 2 && prev.getId() != room.getId()){
            result += "This room is a dead end. The only door is the one from where you came from. ";
        }
        else{
            if(room.getDoors().size() <= 2){
                result += "There is just a single door ahead. ";
                for(Door door : room.getDoors()){
                    if( !( // we don't want the room from where we came from
                        (door.getA().getId() == room.getId() && door.getB().getId() == prev.getId()) 
                    ||  (door.getA().getId() == prev.getId() && door.getB().getId() == room.getId()))){
                        if(door.getA().getId() == room.getId())
                            result += "It leads to the room "+door.getB().getId()+" ";
                        else
                            result += "It leads to the room "+door.getA().getId()+" ";
                        if(door.isOpen())
                            result += "and it is open. ";
                        else{
                            if(door.getCondition().getKeyLevel() > 0)
                                result += "and it is locked. ";
                            else
                                result += "and it is closed. ";
                        }
                    }
                }
            }
            else{
                result += "There are "+(room.getDoors().size()-1)+" doors ahead:\n";
                for(int i = 0; i < room.getDoors().size(); i++){
                    Door door = room.getDoors().get(i);
                    if( !( // we don't want the room from where we came from
                        (door.getA().getId() == room.getId() && door.getB().getId() == prev.getId()) 
                    ||  (door.getA().getId() == prev.getId() && door.getB().getId() == room.getId()))){
                        if(door.getA().getId() == room.getId())
                            result += "The door "+i+" leads to the room "+door.getB().getId()+" ";
                        else
                            result += "The door "+i+" leads to the room "+door.getA().getId()+" ";
                        
                        if(door.isOpen())
                            result += "and it is open. ";
                        else{
                            if(door.getCondition().getKeyLevel() > 0)
                                result += "and it is locked. ";
                            else
                                result += "and it is closed. ";
                        }
                        if(i < room.getDoors().size() - 1)
                        result += "\n";
                    }
                }
            }
        }
        
        return result;
    }
}