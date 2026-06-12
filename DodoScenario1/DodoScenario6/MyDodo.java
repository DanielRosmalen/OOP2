import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

/**
 *
 * @author Sjaak Smetsers & Renske Smetsers-Weeda
 * @version 3.1 -- 29-07-2017
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

    public void move() {
        if ( canMove() ) {
            step();
        } else {
            showError( "I'm stuck!" );
        }
    }

    public boolean canMove() {
        if ( borderAhead() || fenceAhead() ){
            return false;
        } else {
            return true;
        }
    }

    public void jump( int distance ) {
        int nrStepsTaken = 0;
        while ( nrStepsTaken < distance ) {
            move();
            nrStepsTaken++;
        }
    }

    public List<Egg> getListOfEggsInWorld() {
        return getWorld().getObjects(Egg.class);
    }

    public List<Integer> createListOfNumbers() {
        return new ArrayList<> (Arrays.asList( 2, 43, 7, -5, 12, 7 ));
    }

    public void practiceWithLists(){
        List<Integer> listOfNumbers = createListOfNumbers();
        System.out.println("First element: " + listOfNumbers.get(1));
    }

    public void practiceWithListsOfSurpriseEggs(){
        List<SurpriseEgg> listOfEgss = SurpriseEgg.generateListOfSurpriseEggs( 12, getWorld() );
    }
    
    public List<SurpriseEgg> makeListOfSurpriseEggs() {
        List<SurpriseEgg> lijst = SurpriseEgg.generateListOfSurpriseEggs(10, getWorld());
        return lijst;
    }
    
    public void printCoordinatesOfEgg(Egg egg) {
        System.out.println("x: " + egg.getX() + " y: " + egg.getY());
    }
    
    public void makeListOfSurpriseEggsAndPrintCoordinates() {
    List<SurpriseEgg> lijst = makeListOfSurpriseEggs();
    for (SurpriseEgg egg : lijst) {
        printCoordinatesOfEgg(egg);
    }
    }   


    public boolean validCoordinates(int x, int y) {
        return x >= 0 && x < getWorld().getWidth() && y >= 0 && y < getWorld().getHeight();
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

    public void turn180() {
        turnRight();
        turnRight();
    }

    public void faceEast() {
        while (getDirection() != EAST) {
            turnLeft();
        }
    }

    public void stepOneCellBackwards() {
        turn180();
        move();
        turn180();
    }

    public void walkToWorldEdgePrintingCoordinates() {
        while (!borderAhead()) {
            move();
        }
    }

    public void goBackToStartOfRowAndFaceBack() {
        turn180();
        walkToWorldEdgePrintingCoordinates();
        turn180();
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

    public int countAllEggsInWorld() {
        int total = 0;
        int row = 0;
        while (row < getWorld().getHeight()) {
            goToLocation(0, row);
            setDirection(EAST);
            total = total + countEggsInRow();
            row++;
        }
        System.out.println(total + " eieren in deze wereld.");
        return total;
    }

    public double averagePerRow() {
        double total = countAllEggsInWorld();
        return total / getWorld().getHeight();
    }

    public void hatchEgg() {
        if (onEgg()) {
            pickUpEgg();
            myNrOfEggsHatched++;
        } else {
            showError("There was no egg in this cell");
        }
    }

    public int getNrOfEggsHatched() {
        return myNrOfEggsHatched;
    }

    public void layEggIfNestEmpty() {
        if (onNest() && !onEgg()) {
            layEgg();
        }
    }

    public void layTrailOfEggs(int n) {
        if (n <= 0) {
            showError("Voer een aantal groter dan 0 in");
            return;
        }
        int eggsLayed = 0;
        while (eggsLayed < n) {
            if (!onEgg()) {
                layEgg();
            }
            eggsLayed++;
            if (eggsLayed < n && !borderAhead()) {
                move();
            } else {
                return;
            }
        }
    }

    public void layEggPattern() {
        int startX = getX();
        int startY = getY();
        int row = startY;
        while (row < getWorld().getHeight()) {
            goToLocation(startX, row);
            setDirection(EAST);
            layTrailOfEggs(row - startY + 1);
            row++;
        }
    }

    public void layEggPatternDouble() {
        int startX = getX();
        int startY = getY();
        int row = startY;
        int eggs = 1;
        while (row < getWorld().getHeight()) {
            goToLocation(startX, row);
            setDirection(EAST);
            layTrailOfEggs(eggs);
            eggs = eggs * 2;
            row++;
        }
    }

    public void layEggPyramid() {
        int startX = getX();
        int startY = getY();
        int row = startY;
        while (row < getWorld().getHeight()) {
            int offset = row - startY;
            int startColumn = startX - offset;
            int endColumn = startX + offset;
            if (startColumn < 0 || endColumn >= getWorld().getWidth()) {
                break;
            }
            goToLocation(startColumn, row);
            setDirection(EAST);
            layTrailOfEggs(2 * offset + 1);
            row++;
        }
    }

    public void highestEggsInRow() {
        ArrayList<Integer> highestRows = new ArrayList<Integer>();
        int maxEggs = -1;
        int row = 0;
        while (row < getWorld().getHeight()) {
            goToLocation(0, row);
            setDirection(EAST);
            int eggsInRow = countEggsInRow();
            if (eggsInRow > maxEggs) {
                maxEggs = eggsInRow;
                highestRows.clear();
                highestRows.add(row);
            } else if (eggsInRow == maxEggs) {
                highestRows.add(row);
            }
            row++;
        }
        System.out.println("Meeste eieren: " + maxEggs + " in rij(en): " + highestRows);
        goToLocation(0, 0);
        setDirection(EAST);
    }

    public void addParityBitsRows() {
        int row = 0;
        while (row < getWorld().getHeight()) {
            goToLocation(0, row);
            setDirection(EAST);
            int count = countEggsInRow();
            if (count % 2 != 0) {
                goToLocation(getWorld().getWidth() - 1, row);
                layGoldEgg();
            }
            row++;
        }
    }

    public void addParityBitsCol() {
        int col = 0;
        while (col < getWorld().getWidth()) {
            goToLocation(col, 0);
            setDirection(SOUTH);
            int count = countEggsInRow();
            if (count % 2 != 0) {
                goToLocation(col, getWorld().getHeight() - 1);
                layGoldEgg();
            }
            col++;
        }
    }

    public void addParityBits() {
        addParityBitsRows();
        addParityBitsCol();
    }

    public void genericAddParityBitsRow() {
        turnRight();
        while (!borderAhead()) {
            turnLeft();
            int count = countEggsInRow();
            if (count % 2 != 0) {
                while (!borderAhead()) {
                    move();
                }
                if (!onEgg()) {
                    layGoldEgg();
                }
                goBackToStartOfRowAndFaceBack();
            }
            turnRight();
            move();
        }
        turnLeft();
        int count = countEggsInRow();
        if (count % 2 != 0) {
            while (!borderAhead()) {
                move();
            }
            if (!onEgg()) {
                layGoldEgg();
            }
            goBackToStartOfRowAndFaceBack();
        }
    }

    public void genericAddParityBitsCol() {
        while (!borderAhead()) {
            turnLeft();
            int count = countEggsInRow();
            if (count % 2 != 0) {
                while (!borderAhead()) {
                    move();
                }
                if (!onEgg()) {
                    layGoldEgg();
                }
                goBackToStartOfRowAndFaceBack();
            }
            turnRight();
            move();
        }
        turnLeft();
        int count = countEggsInRow();
        if (count % 2 != 0) {
            while (!borderAhead()) {
                move();
            }
            if (!onEgg()) {
                layGoldEgg();
            }
            goBackToStartOfRowAndFaceBack();
        }
    }

    public void genericAddParityBits() {
        genericAddParityBitsRow();
        genericAddParityBitsCol();
    }
}