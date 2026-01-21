package game;


/**
 * To start make all fields private and methods public
 */
public class Playground {

    public static void main(String[] args) {
        Dot[][] dots = new Dot[3][3];
        System.out.println("Dots done!");
        Lines myGrid = new Lines(3,3,dots);
        System.out.println("Grid done!");
        System.out.println("Vertical Line = " + myGrid.getLine(0,0,1,0).lineDebug());

    }
}