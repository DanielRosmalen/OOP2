import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 *
 * @author Sjaak Smetsers & Renske Smetsers-Weeda
 * @version 3.0 -- 20-01-2017
 */
public class MyDodo extends Dodo
{
    private int myNrOfEggsHatched;
    
    public MyDodo() {
        super( EAST );
        myNrOfEggsHatched = 0;
    }

    public void act() {
    }

    /**
     * Move one cell forward in the current direction.
     * 
     * <P> Initial: Dodo is somewhere in the world
     * <P> Final: If possible, Dodo has moved forward one cell
     *
     */
    public void move() {
        if ( canMove() ) {
            step();
        } else {
            showError( "I'm stuck!" );
        }
    }

    public void climbOverFence() {
        turnLeft();
        move();
        turnRight();
        move();
        move();
        turnRight();
        move();
        turnLeft();
    }
    
    public boolean grainAhead() {
        move();
        if (onGrain()) {
            stepOneCellBackwards();
            return true;
        }
        else {
            stepOneCellBackwards();
            return false;
        }
    }
    
    /**
     * Test if Dodo can move forward, (there are no obstructions
     *    or end of world in the cell in front of her).
     * 
     * <p> Initial: Dodo is somewhere in the world
     * <p> Final:   Same as initial situation
     * 
     * @return boolean true if Dodo can move (no obstructions ahead)
     *                 false if Dodo can't move
     *                      (an obstruction or end of world ahead)
     */
    public boolean canMove() {
        if ( borderAhead() || fenceAhead()){
            return false;
        }else {
            return true;
        }
    }

    public void turn180() {
        turnRight();
        turnRight();
    }
    
    /**
     * Hatches the egg in the current cell by removing
     * the egg from the cell.
     * Gives an error message if there is no egg
     * 
     * <p> Initial: Dodo is somewhere in the world. There is an egg in Dodo's cell.
     * <p> Final: Dodo is in the same cell. The egg has been removed (hatched).     
     */    
    public void hatchEgg () {
        if ( onEgg() ) {
            pickUpEgg();
            myNrOfEggsHatched++;
        } else {
            showError( "There was no egg in this cell" );
        }
    }
    
    /**
     * Returns the number of eggs Dodo has hatched so far.
     * 
     * @return int number of eggs hatched by Dodo
     */
    public int getNrOfEggsHatched() {
        return myNrOfEggsHatched;
    }
    
    /**
     * Move given number of cells forward in the current direction.
     * 
     * <p> Initial:   
     * <p> Final:  
     * 
     * @param   int distance: the number of steps made
     */
    public void jump( int distance ) {
        int nrStepsTaken = 0;               // set counter to 0
        while ( nrStepsTaken < distance ) { // check if more steps must be taken  
            move();                         // take a step
            nrStepsTaken++;                 // increment the counter
            System.out.println("moved " + nrStepsTaken);
        }
    }

    public void gotoEgg() {
        while (!onEgg()) {
            move();
        }
    }
    
    
    /**
     * Walks to edge of the world printing the coordinates at each step
     * 
     * <p> Initial: Dodo is on West side of world facing East.
     * <p> Final:   Dodo is on East side of world facing East.
     *              Coordinates of each cell printed in the console.
     */

    public void walkToWorldEdgePrintingCoordinates(){
        while( ! borderAhead() ){
            // print coordinates
            move();
        }
    }

    public void goBackToStartOfRowAndFaceBack() {
        turn180();
        walkToWorldEdgePrintingCoordinates();
        turn180();
    }
    
    public  void walkToWorldEdgeClimbingOverFences() {
        while (!borderAhead()) {
            if (fenceAhead()) {
                climbOverFence();
            } else {
                move();
            }
        }
    }
    
    public void pickUpGrainsAndPrintCoordinates() {
        while (!borderAhead()) {
            if (onGrain()) {
                pickUpGrain();
                System.out.println("x: " + getX() + " y: " + getY() );
            }
            move();
        }
        if (onGrain()) {
            pickUpGrain();
            System.out.println("x: " + getX() + " y: " + getY() );
        }
    }
    
    public void stepOneCellBackwards() {
        turn180();
        move();
        turn180();
    }
    
    public void layEggIfNestEmpty() {
        if (onNest() && !onEgg()) {
                layEgg();
            }
    }
    
    public void walkToWorldEdgeNestAndEggs() {
        while (!borderAhead()) {
            layEggIfNestEmpty();
            move();
        }
        layEggIfNestEmpty();
    }
    
    public  void walkToWorldEdgeClimbingOverFencesLayingEgg() {
        while (!borderAhead() && !onNest()) {
            if (fenceAhead()) {
                climbOverFence();
            }else {
                move();
            }
        }
        layEggIfNestEmpty();
    }
    
    public void walkAroundFencedArea() {
        while (!onEgg()) {
            if (fenceAhead()) {
                turnLeft();
            } else {
                move();
                turnRight();
            }
            
        }
    }
    
    
    public void easyMaze() {
        while (!onNest()) {
            if (fenceAhead()) {
                turnLeft();
            } else {
                move();
                turnRight();
            }
            
        }
    }
    
    public void anyMaze() {
        while (!onNest()) {
            if (fenceAhead()) {
                turnLeft();
            } else {
                move();
                turnRight();
            }
        
        }
        System.out.println("Goed gevonden Mimi!");
    }
    
    public void eggTrailToNest() {
        while (!onNest()) {
            while (!eggAhead() && !nestAhead()) {
                turnRight();
            }
            move();
            pickUpEgg();
        }
    }
    
    /**
     * Test if Dodo can lay an egg.
     *          (there is not already an egg in the cell)
     * 
     * <p> Initial: Dodo is somewhere in the world
     * <p> Final:   Same as initial situation
     * 
     * @return boolean true if Dodo can lay an egg (no egg there)
     *                 false if Dodo can't lay an egg
     *                      (already an egg in the cell)
     */

    public boolean canLayEgg( ){
        if( onEgg() ){
            return false;
            // E
        }else{
            return true;
        }
    }  
    
    public void wisselWaarden() {
     int waardeBlauweEi = 2;
     int waardeGoudenEi = 10;
     
     int tijdelijkeWaardeEi = waardeBlauweEi;
     waardeBlauweEi = waardeGoudenEi;
     waardeGoudenEi = tijdelijkeWaardeEi;
     
     System.out.println("Blauw: " + waardeBlauweEi + ", Goud: " + waardeGoudenEi);
    }
    
    public void faceEast() {
        while (getDirection() != EAST) {
            turnLeft();
        }
    }
    
    public void goToLocation(int CoordX, int CoordY) {
        while (getX() < CoordX) {
            setDirection(EAST);
            move();
        }
        while (getX() > CoordX) {
            setDirection(WEST);
            move();
        }
        
        while (getY() < CoordY) {
            setDirection(SOUTH);
            move();
        }
        while (getY() > CoordY) {
            setDirection(NORTH);
            move(); 
        }
    }
    
    
    public boolean validCoordinates(int x, int y) {
        return x >= 0 && x < getWorld().getWidth() && y >= 0 && y < getWorld().getHeight();
    }

    public int countEggsInRow() {
        int count = 0;
        while (!borderAhead()) {
            if (onEgg()) {
                count++;
            }
            move();
        }
        if (onEgg()) {
            count++;
        }
        goBackToStartOfRowAndFaceBack();
        return count;
    }
    
    
    
}


