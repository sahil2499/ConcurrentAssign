// Online Java Compiler
// Use this editor to write, compile and run your Java code online

import java.util.ArrayList;
import java.util.List;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
public class Vacuum {

    // Constants
    private static final int CELL_SIZE = 0; // in meters
    private static final int CLEANING_TIME = 2; // in seconds

    // Direction constants
    private static final char UP = 'U';
    private static final char DOWN = 'D';
    private static final char LEFT = 'L';
    private static final char RIGHT = 'R';

    private static int roomSize;
    private static boolean[][] room;
    private static boolean[][] occupied;
   

    // Robot class
    static class Robot extends Thread {
        private volatile boolean running;
        int x, y; // position
        char direction;
        boolean collided;
        boolean[][] visited=new boolean[roomSize][roomSize];
        public Robot(int x, int y, char direction) {
            this.running =true;
            this.x = x;
            this.y = y;
            this.direction = direction;
            this.collided = false;
        }

        
        public void run() {
            while(running){
               
            while (!collided && !isRoomClean()) {
                move();
                try {
                    Thread.sleep(CLEANING_TIME * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                 if(!running){
                     //System.out.print("break");
                break;
            }
            }
           break;
            }
        }
        private void moveInSpiral() {
            switch (direction) {
                case UP:
                   
                    if (y > 0 && !visited[x][y - 1]) {
                        y--;
                        direction=LEFT;
                       
                    } else if (x > 0 && !visited[x - 1][y]) {
                        x--;
                    } else {
                        this.running=false;
                    }
                   
                    break;
                case RIGHT:
                    if (x > 0 && !visited[x-1][y]) {
                        x--;
                        direction=UP;
                    } else if (y < roomSize-1 && !visited[x][y+1]) {
                        y++;
                    } else {
                        this.running=false;
                    }
                    break;
                case DOWN:
                    if (y < roomSize - 1 && !visited[x][y + 1]) {
                        y++;
                        direction=RIGHT;
                    } else if (x < roomSize - 1 && !visited[x + 1][y]) {
                        x++;
                    } else {
                        this.running=false;
                    }
                    break;
                case LEFT:
                    if (x < roomSize-1 && !visited[x +1][y]) {
                        x++;
                        direction=DOWN;
                    } else if (y >0 && !visited[x][y - 1]) {
                        y--;
                    } else {
                        System.out.print("stopping thread!!");
                        this.running=false;
                    }
                    break;
            }
        }

        private void moveAlongEdge() {
           
            if(x==0){
                if(y-1>=0 && visited[x][y-1]==false){
                    y--;
                    direction=LEFT;
                }else{
                    this.running=false;
                }
               
            }else if(y==0){
                if(x+1<=roomSize-1 && visited[x+1][y]==false){
                    x++;
                   direction=DOWN;
                }else{
                    this.running=false;
                }
           
            }else if(y==roomSize -1){
                if(x-1>=0 && visited[x-1][y]==false){
                    x--;
                    direction=UP;
                }else{
                    this.running=false;
                }
               
            }else{
                if(y+1<=roomSize-1 && visited[x][y+1]==false){
                    y++;
                    direction=RIGHT;
                }else{
                    this.running=false;
                }
               
            }
           
        }

        public void move() {
            int oldX = x;
            int oldY = y;
            this.visited[oldX][oldY]=true;
            if ((x == 0 || y == 0 || x == roomSize - 1 || y == roomSize - 1)&&!((x == 0 && y == 0) || (x == roomSize - 1 && y == 0) || (x == roomSize - 1 && y == roomSize - 1)
                    || (x == 0 && y == roomSize - 1))) {
                //System.out.println("on edge!!");
                moveAlongEdge();
            } else {
                moveInSpiral();
            }
            if(oldX==x && oldY==y){
                return;
            }
            visited[x][y]=true;
           
            if (occupied[x][y]) {
                collided = true;
            } else {
                // Move to the new position
                room[oldX][oldY] = true;
                occupied[oldX][oldY]=false;
                occupied[x][y]=true;
               
                room[x][y] = true;
            }
        }
    }

    public static void main(String[] args) {
        String roomFile = "roomFile.txt";
        String robotsFile = "robotsFile.txt";
        roomSize = readRoomSize(roomFile);

        // Read the robots' initial positions and directions
        List<Robot> robots = readRobotsData(robotsFile);
        // Initialize the room grid
        room = new boolean[roomSize][roomSize];
        occupied= new boolean[roomSize][roomSize];
       
        

        // Place robots in the initial positions
        if (!placeRobots(robots)) {
            System.out.println("INPUT ERROR: Collision occurred");
            System.exit(0);
        }

        // Start the simulation by starting the threads
        for (Robot robot : robots) {
            robot.start();
        }

        // Wait for all threads to complete
        for (Robot robot : robots) {
            try {
                robot.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Check for collisions and print the appropriate message
        for (Robot robot : robots) {
            if (robot.collided) {
                System.out.println("COLLISION AT CELL (" + robot.x + ";" + robot.y + ")");
                System.exit(0);
            }
        }
        //System.out.print("heere!!");
        // Check if the room is clean and print the appropriate message
        if (isRoomClean()) {
            System.out.println("ROOM CLEAN");
        } else {
            System.out.println("ROOM NOT CLEAN");
        }
    }

    // Place robots in their initial positions on the room grid
    private static boolean placeRobots(List<Robot> robots) {
        for (Robot robot : robots) {
            if (room[robot.x][robot.y]) {
                return false; // Collision occurred
            }
            room[robot.x][robot.y] = true;
        }
        return true;
    }

    // Read the room dimensions from the room.txt file
    private static int readRoomSize(String roomFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(roomFile))) {
            String line = reader.readLine();
            return Integer.parseInt(line);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return 0;
    }

    // Read the robots' initial positions and directions from the robots.txt file
    private static List<Robot> readRobotsData(String robotsFile) {
        List<Robot> robots = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(robotsFile))) {
            String line = reader.readLine();
            int numRobots = Integer.parseInt(line);

            for (int i = 0; i < numRobots ; i++) {
                line = reader.readLine();
                String[] parts = line.split(" ");
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                char direction = parts[2].charAt(0);
                robots.add(new Robot(x, y, direction));
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return robots;
    }
    // Check if the entire room is clean
    private static boolean isRoomClean() {
        for (int i = 0; i < roomSize; i++) {
            for (int j = 0; j < roomSize; j++) {
                if (!room[i][j]) {
                    return false; // Found a dirty cell
                }
            }
        }
        return true; // All cells are clean
    }
}