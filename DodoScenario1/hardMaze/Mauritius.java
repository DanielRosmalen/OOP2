import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

import java.util.List;

import java.io.IOException;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Mauritius.
 * 
 * @author Sjaak Smetsers & Renske Smetsers-Weeda
 * @version 3.1 -- 03-07-2017
 */
public class Mauritius extends World
{
    private static final String WORLD_NAME = "worldEmpty.txt";
    private static File WORLD_FILE = null;

    private static final int MAXWIDTH = 10, MAXHEIGHT = 10, CELLSIZE = 60;

    private Scoreboard theScoreboard = new Scoreboard ( "Moves left:", MAXSTEPS, "Score:", 0);

    public static final int MAXSTEPS = 40;

    private static boolean traceOn = true;

    private static final char
    FENCE      = '#'            ,
    EGG_YELLOW = '$'            ,
    EGG_BLUE   = '.'            ,
    NEST       = '='            ,
    GRAIN      = '+'            ,
    DODO_N     = 'N'            ,
    DODO_S     = 'S'            ,
    DODO_E     = 'E'            ,
    DODO_W     = 'W'            ;

    private static WorldReader WORLD_READER = null;
    private static int WORLD_WIDTH, WORLD_HEIGHT;

    static {
        if ( ! WORLD_NAME.isEmpty() ) {
            WORLD_FILE   = new File ( WorldWriter.WORLD_PATH + WORLD_NAME );           
            initWorldInfo();
        } else {
            WORLD_WIDTH  = MAXWIDTH;
            WORLD_HEIGHT = MAXHEIGHT;
        }            
    }

    private static void initWorldInfo() {
        WORLD_READER = new WorldReader ( WORLD_FILE );
        WORLD_WIDTH  = WORLD_READER.getWorldWidth();
        WORLD_HEIGHT = WORLD_READER.getWorldHeight();
    }

    /**
     * Constructor for objects of class ChickenWorld.
     * 
     */
    public Mauritius() {    
        super(WORLD_WIDTH, WORLD_HEIGHT, CELLSIZE); 
        setPaintOrder (Message.class, Scoreboard.class, Dodo.class, Grain.class,
            Nest.class, Egg.class, Fence.class);        
        populate();
        prepare();
    }

    public static void traceOn() {
        traceOn = true;
    }

    public static void traceOff() {
        traceOn = false;
    }

    public static boolean traceIsOn() {
        return traceOn;
    }

    public void updateScore( int ... scores ){
        theScoreboard.updateScore( scores );
    }

    private Actor charToActor( char c ) {
        MyDodo newDodo;
        switch ( c ) {
            case FENCE:
                return new Fence();
            case NEST:
                return new Nest();
            case GRAIN:
                return new Grain();                
            case EGG_YELLOW:
                return new GoldenEgg();
            case EGG_BLUE:
                return new BlueEgg();
            case DODO_N: 
                newDodo = new MyDodo();
                newDodo.setDirection( Dodo.NORTH );
                return newDodo;
            case DODO_S:
                newDodo = new MyDodo();
                newDodo.setDirection( Dodo.SOUTH );
                return newDodo;
            case DODO_E:
                newDodo = new MyDodo();
                newDodo.setDirection( Dodo.EAST );
                return newDodo;
            case DODO_W:
                newDodo = new MyDodo();
                newDodo.setDirection( Dodo.WEST );
                return newDodo;
            default:
                return null;
        }
    }

    private void populate () {
        if ( WORLD_FILE != null ) {
            if ( WORLD_READER == null ) {
                WORLD_READER = new WorldReader ( WORLD_FILE );
            }
            try {
                while (WORLD_READER.hasNext()) {
                    WorldReader.Cell next_cell = WORLD_READER.next();
                    Actor actor = charToActor( next_cell.getChar() );
                    if ( actor != null ) {
                        addObject(actor, next_cell.getX(), next_cell.getY());
                    }
                }
                WORLD_READER.close();
                WORLD_READER = null;
            } catch ( IOException ioe ) {
            }
        }            
    }

    private void removeAllActors() {
        removeObjects( getObjects( null ) );
    }

    private char getActorAt( int x, int y ){
        List<Actor> actors = getObjectsAt(x, y, null);
        if ( actors.size() > 0 ) {
            Actor actor = actors.get( 0 );
            if ( actor instanceof MyDodo ) {
                MyDodo dodo = (MyDodo) actor;
                switch ( dodo.getDirection() ) {
                    case Dodo.NORTH: return DODO_N;
                    case Dodo.SOUTH: return DODO_S;
                    case Dodo.EAST:  return DODO_E;
                    default:    return DODO_W;
                }
            } else if ( actor instanceof Fence ) {
                return FENCE;
            } else if ( actor instanceof GoldenEgg ) {
                return EGG_YELLOW;
            } else if ( actor instanceof BlueEgg ) {
                return EGG_BLUE;
            } else if ( actor instanceof Nest ) {
                return NEST;
            } else if ( actor instanceof Grain ) {
                return GRAIN;
            } else {
                return ' ';
            }
        } else {
            return ' ';
        }
    }

    public void saveToFile() {
        WorldWriter writer = new WorldWriter ( "saved.txt" );
        try {
            writer.write( String.format("%d %d\n", WORLD_WIDTH, WORLD_HEIGHT) );
            for ( int y = 0; y < WORLD_HEIGHT; y++ ) {
                for ( int x = 0; x < WORLD_WIDTH; x++ ) {
                    writer.write( getActorAt( x, y ) );
                }
                writer.write( '\n' );
            }
            writer.close();
        } catch ( IOException ioe ) {
        }
    }

    public void populateFromFile() {
        File world_files = new File ( WorldWriter.WORLD_PATH );
        JFileChooser chooser = new JFileChooser( world_files );
        FileNameExtensionFilter filter = new FileNameExtensionFilter( "Plain text files", "txt" );
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog( null );
        if ( returnVal == JFileChooser.APPROVE_OPTION ) {
            WORLD_FILE = chooser.getSelectedFile();
            initWorldInfo();
            Greenfoot.setWorld( new Mauritius () );
        }
    }

    public static boolean checkCellContent( Actor actor, int x, int y, Class... forbiddenClasses) {
        World world = actor.getWorld();
        List<Actor> allActorsInCell = world.getObjectsAt( x, y, Actor.class );
        allActorsInCell.remove( actor );        
        for ( Actor otherActor: allActorsInCell ) {
            for ( Class forbidden: forbiddenClasses ){
                if ( forbidden.isInstance( otherActor ) ) {
                    showError( world, " cell already occupied " );
                    return false;
                }
            }
        }
        return true;
    }

    private static void showError( World world, String err_msg ) {
        Message.showMessage(  new Alert (err_msg), world );
    }

    /**
     * Prepare the world for the start of the program.
     * That is: create the initial objects and add them to the world.
     */
    private void prepare()
    {
        Fence fence = new Fence();
        addObject(fence,0,0);
        Fence fence2 = new Fence();
        addObject(fence2,1,0);
        Fence fence3 = new Fence();
        addObject(fence3,2,0);
        Fence fence4 = new Fence();
        addObject(fence4,11,0);
        fence4.setLocation(11,0);
        Fence fence5 = new Fence();
        addObject(fence5,10,0);
        Fence fence6 = new Fence();
        addObject(fence6,9,0);
        Fence fence7 = new Fence();
        addObject(fence7,8,0);
        Fence fence8 = new Fence();
        addObject(fence8,7,0);
        Fence fence9 = new Fence();
        addObject(fence9,6,0);
        Fence fence10 = new Fence();
        addObject(fence10,5,0);
        Fence fence11 = new Fence();
        addObject(fence11,4,0);
        Fence fence12 = new Fence();
        addObject(fence12,3,0);
        Fence fence13 = new Fence();
        addObject(fence13,11,1);
        Fence fence14 = new Fence();
        addObject(fence14,11,2);
        Fence fence15 = new Fence();
        addObject(fence15,11,3);
        Fence fence16 = new Fence();
        addObject(fence16,11,4);
        Fence fence17 = new Fence();
        addObject(fence17,11,5);
        Fence fence18 = new Fence();
        addObject(fence18,11,6);
        Fence fence19 = new Fence();
        addObject(fence19,11,7);
        Fence fence20 = new Fence();
        addObject(fence20,11,8);
        Fence fence21 = new Fence();
        addObject(fence21,11,9);
        Fence fence22 = new Fence();
        addObject(fence22,11,10);
        Fence fence23 = new Fence();
        addObject(fence23,11,11);
        Fence fence24 = new Fence();
        addObject(fence24,0,1);
        Fence fence25 = new Fence();
        addObject(fence25,0,2);
        Fence fence26 = new Fence();
        addObject(fence26,0,3);
        Fence fence27 = new Fence();
        addObject(fence27,0,4);
        Fence fence28 = new Fence();
        addObject(fence28,0,5);
        Fence fence29 = new Fence();
        addObject(fence29,0,6);
        Fence fence30 = new Fence();
        addObject(fence30,0,7);
        Fence fence31 = new Fence();
        addObject(fence31,0,8);
        Fence fence32 = new Fence();
        addObject(fence32,0,9);
        Fence fence33 = new Fence();
        addObject(fence33,0,10);
        Fence fence34 = new Fence();
        addObject(fence34,0,11);
        Fence fence35 = new Fence();
        addObject(fence35,1,11);
        Fence fence36 = new Fence();
        addObject(fence36,2,11);
        Fence fence37 = new Fence();
        addObject(fence37,3,11);
        Fence fence38 = new Fence();
        addObject(fence38,4,11);
        Fence fence39 = new Fence();
        addObject(fence39,5,11);
        Fence fence40 = new Fence();
        addObject(fence40,6,11);
        Fence fence41 = new Fence();
        addObject(fence41,7,11);
        Fence fence42 = new Fence();
        addObject(fence42,8,11);
        Fence fence43 = new Fence();
        addObject(fence43,9,11);
        Fence fence44 = new Fence();
        addObject(fence44,10,11);
        Fence fence45 = new Fence();
        addObject(fence45,1,2);
        Fence fence46 = new Fence();
        addObject(fence46,2,2);
        Fence fence47 = new Fence();
        addObject(fence47,3,2);
        Fence fence48 = new Fence();
        addObject(fence48,4,2);
        Fence fence49 = new Fence();
        addObject(fence49,5,2);
        Fence fence50 = new Fence();
        addObject(fence50,6,2);
        Fence fence51 = new Fence();
        addObject(fence51,7,2);
        Fence fence52 = new Fence();
        addObject(fence52,8,2);
        Fence fence53 = new Fence();
        addObject(fence53,9,2);
        Fence fence54 = new Fence();
        addObject(fence54,10,4);
        Fence fence55 = new Fence();
        addObject(fence55,9,4);
        Fence fence56 = new Fence();
        addObject(fence56,8,4);
        Fence fence57 = new Fence();
        addObject(fence57,7,4);
        Fence fence58 = new Fence();
        addObject(fence58,6,4);
        Fence fence59 = new Fence();
        addObject(fence59,5,4);
        Fence fence60 = new Fence();
        addObject(fence60,4,4);
        Fence fence61 = new Fence();
        addObject(fence61,3,4);
        Fence fence62 = new Fence();
        addObject(fence62,2,4);
        Fence fence63 = new Fence();
        addObject(fence63,1,6);
        Fence fence64 = new Fence();
        addObject(fence64,2,6);
        Fence fence65 = new Fence();
        addObject(fence65,3,6);
        Fence fence66 = new Fence();
        addObject(fence66,4,6);
        Fence fence67 = new Fence();
        addObject(fence67,5,6);
        Fence fence68 = new Fence();
        addObject(fence68,6,6);
        Fence fence69 = new Fence();
        addObject(fence69,7,6);
        Fence fence70 = new Fence();
        addObject(fence70,8,6);
        Fence fence71 = new Fence();
        addObject(fence71,9,6);
        Fence fence72 = new Fence();
        addObject(fence72,10,8);
        Fence fence73 = new Fence();
        addObject(fence73,9,8);
        Fence fence74 = new Fence();
        addObject(fence74,8,8);
        Fence fence75 = new Fence();
        addObject(fence75,7,8);
        Fence fence76 = new Fence();
        addObject(fence76,6,8);
        Fence fence77 = new Fence();
        addObject(fence77,5,8);
        Fence fence78 = new Fence();
        addObject(fence78,4,8);
        Fence fence79 = new Fence();
        addObject(fence79,3,8);
        Fence fence80 = new Fence();
        addObject(fence80,2,8);
        Fence fence81 = new Fence();
        addObject(fence81,1,10);
        Fence fence82 = new Fence();
        addObject(fence82,2,10);
        Fence fence83 = new Fence();
        addObject(fence83,3,10);
        Fence fence84 = new Fence();
        addObject(fence84,4,10);
        Fence fence85 = new Fence();
        addObject(fence85,5,10);
        Fence fence86 = new Fence();
        addObject(fence86,5,10);
        Fence fence87 = new Fence();
        addObject(fence87,6,10);
        Fence fence88 = new Fence();
        addObject(fence88,7,10);
        Fence fence89 = new Fence();
        addObject(fence89,8,10);
        fence89.setLocation(8,10);
        Fence fence90 = new Fence();
        addObject(fence90,9,10);
        Fence fence91 = new Fence();
        addObject(fence91,10,10);
        Nest nest = new Nest();
        addObject(nest,10,9);
        MyDodo myDodo = new MyDodo();
        addObject(myDodo,1,1);
        myDodo.easyMaze();
        fence14.setLocation(11,2);
        removeObject(fence14);
        fence16.setLocation(11,4);
        removeObject(fence16);
        fence17.setLocation(11,5);
        removeObject(fence17);
        fence18.setLocation(11,6);
        removeObject(fence18);
        fence19.setLocation(11,7);
        removeObject(fence19);
        fence20.setLocation(11,8);
        removeObject(fence20);
        fence21.setLocation(11,9);
        removeObject(fence21);
        fence22.setLocation(11,10);
        removeObject(fence22);
        fence23.setLocation(11,11);
        removeObject(fence23);
        fence44.setLocation(10,11);
        removeObject(fence44);
        fence91.setLocation(10,10);
        removeObject(fence91);
        fence72.setLocation(10,8);
        removeObject(fence72);
        nest.setLocation(10,9);
        nest.setLocation(10,9);
        nest.setLocation(11,8);
        fence73.setLocation(9,8);
        removeObject(fence73);
        fence90.setLocation(9,10);
        removeObject(fence90);
        fence43.setLocation(9,11);
        removeObject(fence43);
        fence42.setLocation(8,11);
        removeObject(fence42);
        removeObject(fence89);
        fence88.setLocation(7,10);
        removeObject(fence88);
        fence41.setLocation(7,11);
        removeObject(fence41);
        fence40.setLocation(6,11);
        removeObject(fence40);
        fence87.setLocation(6,10);
        removeObject(fence87);
        fence85.setLocation(5,10);
        removeObject(fence86);
        removeObject(fence85);
        fence39.setLocation(5,11);
        removeObject(fence39);
        fence38.setLocation(4,11);
        removeObject(fence38);
        fence84.setLocation(4,10);
        removeObject(fence84);
        fence83.setLocation(3,10);
        removeObject(fence83);
        removeObject(fence37);
        fence36.setLocation(2,11);
        fence36.setLocation(2,11);
        removeObject(fence36);
        fence82.setLocation(2,10);
        removeObject(fence82);
        fence35.setLocation(1,11);
        removeObject(fence35);
        fence81.setLocation(1,10);
        removeObject(fence81);
        fence34.setLocation(0,11);
        removeObject(fence34);
        fence33.setLocation(0,10);
        removeObject(fence33);
        fence32.setLocation(0,9);
        removeObject(fence32);
        fence31.setLocation(0,8);
        removeObject(fence31);
        fence80.setLocation(2,8);
        removeObject(fence80);
        fence79.setLocation(3,8);
        removeObject(fence79);
        fence78.setLocation(4,8);
        removeObject(fence78);
        fence77.setLocation(5,8);
        removeObject(fence77);
        fence76.setLocation(6,8);
        removeObject(fence76);
        fence75.setLocation(7,8);
        removeObject(fence75);
        fence74.setLocation(8,8);
        removeObject(fence74);
        nest.setLocation(10,9);
        removeObject(nest);
        fence30.setLocation(0,7);
        removeObject(fence30);
        fence29.setLocation(0,6);
        removeObject(fence29);
        fence63.setLocation(1,6);
        removeObject(fence63);
        removeObject(fence64);
        fence65.setLocation(3,6);
        removeObject(fence65);
        fence66.setLocation(4,6);
        removeObject(fence66);
        fence67.setLocation(5,6);
        removeObject(fence67);
        fence68.setLocation(6,6);
        removeObject(fence68);
        fence69.setLocation(7,6);
        removeObject(fence69);
        fence70.setLocation(8,6);
        removeObject(fence70);
        fence71.setLocation(9,6);
        removeObject(fence71);
        fence54.setLocation(10,4);
        removeObject(fence54);
        fence55.setLocation(9,4);
        removeObject(fence55);
        fence56.setLocation(8,4);
        removeObject(fence56);
        fence57.setLocation(7,4);
        removeObject(fence57);
        fence58.setLocation(6,4);
        removeObject(fence58);
        fence59.setLocation(5,4);
        removeObject(fence59);
        fence60.setLocation(4,4);
        removeObject(fence60);
        fence61.setLocation(3,4);
        removeObject(fence61);
        fence62.setLocation(2,4);
        removeObject(fence62);
        fence28.setLocation(0,5);
        removeObject(fence28);
        fence27.setLocation(0,4);
        removeObject(fence27);
        fence26.setLocation(0,3);
        removeObject(fence26);
        fence15.setLocation(11,3);
        removeObject(fence15);
        fence53.setLocation(9,2);
        removeObject(fence53);
        fence52.setLocation(8,2);
        removeObject(fence52);
        fence51.setLocation(7,2);
        removeObject(fence51);
        fence50.setLocation(6,2);
        removeObject(fence50);
        fence49.setLocation(5,2);
        removeObject(fence49);
        fence48.setLocation(4,2);
        removeObject(fence48);
        fence47.setLocation(3,2);
        removeObject(fence47);
        fence46.setLocation(2,2);
        removeObject(fence46);
        fence45.setLocation(1,2);
        removeObject(fence45);
        removeObject(fence25);
        fence24.setLocation(0,1);
        removeObject(fence24);
        fence13.setLocation(11,1);
        removeObject(fence13);
        fence4.setLocation(11,0);
        removeObject(fence4);
        fence5.setLocation(10,0);
        removeObject(fence5);
        fence6.setLocation(9,0);
        removeObject(fence6);
        fence7.setLocation(8,0);
        removeObject(fence7);
        fence8.setLocation(7,0);
        removeObject(fence8);
        fence9.setLocation(6,0);
        removeObject(fence9);
        fence10.setLocation(5,0);
        removeObject(fence10);
        fence11.setLocation(4,0);
        removeObject(fence11);
        fence12.setLocation(3,0);
        removeObject(fence12);
        fence3.setLocation(2,0);
        removeObject(fence3);
        fence2.setLocation(1,0);
        removeObject(fence2);
        fence.setLocation(0,0);
        removeObject(fence);
        myDodo.setLocation(11,8);
        removeObject(myDodo);
    }
}
