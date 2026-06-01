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
    }
}
