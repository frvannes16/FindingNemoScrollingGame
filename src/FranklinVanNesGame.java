import java.awt.event.KeyEvent;
import java.util.Random;

public class FranklinVanNesGame {

    // set to false to use your code
    private static final boolean DEMO = false;

    // Game window should be wider than tall:   H_DIM < W_DIM
    // (more effectively using space)
    private static final int H_DIM = 6;   // # of cells vertically by default: height of game
    private static final int W_DIM = 12;  // # of cells horizontally by default: width of game
    private static final int U_ROW = 0; //Starting User row
    private static final int TOP_ROW = 0;
    private static final int FIRST_COLUMN = 0;
    private static final Random randomizer = new Random();

    private static int SPEED = 3; //lower is faster
    private static int GRID_HEIGHT = H_DIM; //alternate names for readability
    private static int GRID_WIDTH = W_DIM;


    private Location[] objectLocations; //Holds the location of each object.
    private String[] objectTypes; //mapped to the above.
    private int numObjects;
    private boolean onTurtle = false;
    private static final int onTurtleTime = 5000;
    private boolean foundNemo = false;

    //Cell population stats
    private static final int percentFilled = 10; // out of 100
    private static final int percentGet = 30; //vs. percent avoid. out of 100
    private static final int percentTurtle = 3;
    private static final int percentNemo = 2; //The winning condition! 2% chance of Nemo being displayed on each population of the right row

    private Grid grid;
    private int userRow;
    private int userColumn;

    private int msElapsed;
    private int timesGet;
    private int timesAvoid;

    private int pauseTime = 100;
    private boolean paused = false;

    public FranklinVanNesGame() {
        init(H_DIM, W_DIM, U_ROW);
    }

    public FranklinVanNesGame(int hdim, int wdim, int uRow) {
        GRID_HEIGHT = hdim;
        GRID_WIDTH = wdim;
        init(hdim, wdim, uRow);
    }

    private void init(int hdim, int wdim, int uRow) {
        displayMenu(); //show displayMenu

        grid = new Grid("backgroundFranklinVanNes.png", hdim, wdim);


        //other Grid constructor of interest:
        //    comment the line above; uncomment the one below
        //You can adjust colors by creating Color objects: look at the Grid constructors
        //grid = new Grid(hdim, wdim, Color.MAGENTA);
        ///////////////////////////////////////////////
        objectLocations = new Location[hdim * wdim / 2]; //should be enought to store the locations of all objects
        objectTypes = new String[objectLocations.length];
        userRow = uRow;
        userColumn = TOP_ROW / 2; //Get them in the middle to start
        msElapsed = 0;
        timesGet = 0;
        timesAvoid = 0;
        updateTitle();
        grid.setImage(new Location(userRow, 0), "userFranklinVanNes.gif");
    }

    /**
     * Displays the menu with instructions and waits for a KeyEvent before proceeding.
     */
    public void displayMenu() {
        int key = -1;
        Grid menu = new Grid("menuFranklinVanNes.jpg", GRID_HEIGHT, GRID_WIDTH);
        while (key == -1) {
            key = menu.checkLastKeyPressed();
            System.out.print(""); /* When this is in place, it works.
            * I think it is because it allows enough time for the event handling thread
            * to make its changes on the lastKeyPressed variable in the Grid class. Without it
            * the program doesn't detect any new input. This is definitely a work-around solution.
            */
        }
    }

    public void play() {
        while (!isGameOver()) {
            grid.pause(pauseTime);
            handleKeyPress();
            if (!paused) {
                //Conditions for when on a turtle
                if (onTurtle && msElapsed % (onTurtleTime) == 0) {
                    resetSpeed();
                    onTurtle = false;
                }
                //scrolling timing control
                if (msElapsed % (SPEED * pauseTime) == 0) {
                    scrollLeft();
                    populateRightEdge();
                }
                updateTitle();
                msElapsed += pauseTime;
            }
        }
    }


    private void clearUserLocation() {
        clearLocation(new Location(userRow, userColumn));
    }

    /**
     * Given a location, the function will clear the selected grid cell.
     *
     * @param location to clear
     * @return success boolean
     */
    private boolean clearLocation(Location location) {
        try {
            grid.setImage(location, null);
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    /**
     * Adds the item to the grid in the given location. Updates the
     * object storage.
     *
     * @param location to add image
     * @param fileName e.g. "user.gif"
     */
    private void add(Location location, String fileName) {
        grid.setImage(location, fileName);
        objectLocations[numObjects] = location;
        objectTypes[numObjects++] = fileName;
    }

    //defaulted for adding Get items.
    private void addGet(Location location) {
        add(location, "getFranklinVanNes.gif");
    }

    //defaulted for adding avoid items
    private void addAvoid(Location location) {
        add(location, "avoid1FranklinVanNes.png");
    }

    public void handleKeyPress() {
        int key = grid.checkLastKeyPressed();

        //use Java constant names for key presses
        //http://docs.oracle.com/javase/7/docs/api/constant-values.html#java.awt.event.KeyEvent.VK_DOWN
        if (!paused) { // only take movement input when the game is not paused - NO CHEATING!
            if (key == KeyEvent.VK_UP && userRow != TOP_ROW) {
                clearUserLocation();
                handleCollision(new Location(userRow - 1, userColumn));
                grid.setImage(new Location(--userRow, userColumn), "userFranklinVanNes.gif");
            } else if (key == KeyEvent.VK_LEFT && userColumn != 0) {
                clearUserLocation();
                handleCollision(new Location(userRow, userColumn - 1));
                grid.setImage(new Location(userRow, --userColumn), "userFranklinVanNes.gif");
            } else if (key == KeyEvent.VK_RIGHT && userColumn != GRID_WIDTH - 1) {
                clearUserLocation();
                handleCollision(new Location(userRow, userColumn + 1));
                grid.setImage(new Location(userRow, ++userColumn), "userFranklinVanNes.gif");
            } else if (key == KeyEvent.VK_DOWN && userRow != GRID_HEIGHT - 1) {
                clearUserLocation();
                handleCollision(new Location(userRow + 1, userColumn));
                grid.setImage(new Location(++userRow, userColumn), "userFranklinVanNes.gif");
            }
        }
        if (key == KeyEvent.VK_Q) {
            System.exit(0);
        } else if (key == KeyEvent.VK_T) {
            boolean interval = (msElapsed % (3 * pauseTime) == 0);
            System.out.println("pauseTime " + pauseTime + " msElapsed reset " + msElapsed
                    + " interval " + interval);
        } else if (key == KeyEvent.VK_COMMA) slowTime();
        else if (key == KeyEvent.VK_PERIOD) accelerateTime();
        else if (key == KeyEvent.VK_P) paused = !paused; //toggle pause
    }

    private void accelerateTime() {
        if (SPEED - 1 > 0) SPEED--;
    }

    private void resetSpeed() {
        SPEED = 3;
    }

    private void slowTime() {
        SPEED++;
    }

    public void populateRightEdge() {
        //Go down the column and fill by probability defined above
        for (int row = 0; row < GRID_HEIGHT; row++) {
            int fillChance = randomizer.nextInt(100);
            if (fillChance <= percentFilled) { //If within fill likelihood. Fill the cell.
                int getChance = randomizer.nextInt(100);
                Location obLocation = new Location(row, GRID_WIDTH - 1);

                //Choose the object to add based on the random number.
                if (getChance <= percentGet + percentTurtle + percentNemo && getChance > percentTurtle)
                    addGet(obLocation);//Add a starfish
                else if (getChance <= percentTurtle + percentNemo && getChance > percentNemo)
                    add(obLocation, "turtleFranklinVanNes.gif"); //Add a turtle
                else if (getChance <= percentNemo && getScore() > 100)
                    add(obLocation, "nemoFranklinVanNes.png"); //add the winning condition after a certain amount of time! FIND NEMO!
                else addAvoid(obLocation); //remainder of the probability adds avoid fish hooks
            }
        }
    }

    //Used in debugging to print the objectLocations array
    private void printLocations() {
        System.out.print("Length: " + objectLocations.length + ". " + numObjects + " {");
        for (Location objectLocation : objectLocations) {
            if (objectLocation != null) System.out.print(objectLocation);
            else System.out.print("(null), ");
        }
        System.out.print("}\n");
    }

    //Used in debugging to print the objectTypes array
    private void printImages() {
        System.out.print("Length: " + objectTypes.length + ". " + numObjects + " {");
        for (String type : objectTypes) {
            if (type != null) System.out.print(type);
            else System.out.print("(null), ");
        }
        System.out.print("}\n");
    }

    /**
     * Clears the image and deletes location references
     * to it in the class. Typically only accessed within class code.
     *
     * @param idx index of the object in objectLocations and objecTypes.
     */
    private void removeObject(int idx) {
        //Repaint the location as blank
        clearLocation(objectLocations[idx]);
        numObjects--;
        objectLocations[idx] = objectLocations[numObjects]; //Rearrange object location array.
        objectLocations[numObjects] = null;
        objectTypes[idx] = objectTypes[numObjects];
        objectTypes[numObjects] = null;
    }

    /**
     * Removes an object based on its location within the grid.
     * @param loc
     */
    private void removeObject(Location loc) {
        int idx = findLocationIdx(loc); //finds the index and then deletes
        removeObject(idx);

    }


    public void scrollLeft() {
        handleCollision(new Location(userRow, userColumn + 1)); //anticipate any collisions first.
        int i = 0;
        while (objectLocations[i] != null) {
            Location location = objectLocations[i]; //retrieve object location
            String imageName = objectTypes[i]; //retrieve image name at location
            clearLocation(location); //clear the last location


            if (location.getCol() != FIRST_COLUMN) {
                //update stored location
                objectLocations[i] = new Location(location.getRow(), location.getCol() - 1);
                //assign image to new location
                grid.setImage(objectLocations[i], imageName);
                i++;
            } else removeObject(i);
        }
        paintUser();
    }

    /**
     * Called to paint the user based on its known location.
     * Used to keep the user always visible.
     */
    private void paintUser() {
        grid.setImage(new Location(userRow, userColumn), "userFranklinVanNes.gif");
    }

    //Find the index of a location in the objectLocations array.
    private int findLocationIdx(Location loc) {
        for (int i = 0; i < objectLocations.length; i++) {
            if (loc != null && loc.equals(objectLocations[i])) return i;
        }
        return -1;
    }

    public void handleCollision(Location loc) {

        //User is invincible while on the turtle.
        //Hit avoid
        if (!onTurtle && grid.getImage(loc) == "avoid1FranklinVanNes.png")
            timesAvoid++; //place any animation events here.

        //hit get
        if (grid.getImage(loc) == "getFranklinVanNes.gif") {
            //Eliminate the object.
            removeObject(loc);
            timesGet++;
        }
        //IF collides with a turtle. The user becomes invisible and can speed through.
        if (grid.getImage(loc) == "turtleFranklinVanNes.gif") {
            onTurtle = true;
            accelerateTime();
            accelerateTime();
        }

        //Hit nemo
        if (grid.getImage(loc) == "nemoFranklinVanNes.png") {
            foundNemo = true;
            System.out.println("YOU FOUND NEMO!");
        }
    }

    public int getScore() {
        return msElapsed / 1000 + (timesGet * 10);
    }

    public void updateTitle() {
        grid.setTitle("Game - Score:  " + getScore() + "      Lives left: " + (5 - timesAvoid));
    }

    public boolean isGameOver() {
        if (timesAvoid == 5) System.out.println("GAME OVER. You hit too many fishing hooks.");
        if (foundNemo) {
            System.out.println("Well done! You found NEMO!");
            Grid endGrid = new Grid("endScreenFranklinVanNes.jpg", GRID_HEIGHT, GRID_WIDTH);
        }
        return timesAvoid == 5 || foundNemo;
    }

    public static void test() {
        if (DEMO) {       // reference game:
            //   - play and observe first the mechanism of the demo to understand the basic game
            //   - go back to the demo anytime you don't know what your next step is
            //     or details about it are not concrete
            //         figure out according to the game play
            //         (the sequence of display and action) how the functionality
            //         you are implementing next is supposed to operate
            // It's critical to have a plan for each piece of code: follow, understand
            // and study the assignment description details; and explore the basic game.
            // You should always know what you are doing (your current, small goal) before
            // implementing that piece or talk to us.

            System.out.println("Running the demo: DEMO=" + DEMO);
            //default constructor   (4 by 10)
            FranklinVanNesGame mattGame = new FranklinVanNesGame();
            //other constructor: client adjusts game window size   TRY IT
            // MattGame game = new MattGame(10, 20, 0);
            mattGame.play();

        } else {
            System.out.println("Running student game: DEMO=" + DEMO);
            // !DEMO   -> your code should execute those lines when you are
            // implementing your game

            //test 1: with parameterless constructor
            FranklinVanNesGame franklinVanNesGame = new FranklinVanNesGame();

            //test 2: with constructor specifying grid size    IT SHOULD ALSO WORK as long as height < width
            //Game game = new Game(10, 20, 4);

            franklinVanNesGame.play();
        }
    }

    public static void main(String[] args) {
        test();
    }
}