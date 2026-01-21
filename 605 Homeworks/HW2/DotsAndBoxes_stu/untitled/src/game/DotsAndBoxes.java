package game;

import java.util.Scanner;

public class DotsAndBoxes {

    static final String PROMPT = "> ";



    public DotsAndBoxes(int rows, int columns) {
        GameBoard gameBoard = new GameBoard(rows, columns);
    }


    public void play(){}

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int rows = input.nextInt();
        int columns = input.nextInt();
        DotsAndBoxes dotsAndBoxes = new DotsAndBoxes(rows, columns);
        dotsAndBoxes.play();
    }

    public static balls() {
        for(int row = 0; row < rows_in_dots; row++){
            for(int col = 0; col < cols_in_dots; col++){
                // print row of dots and horizontal lines

// print dot    System.out.print(Dots[row][col]);

                // check to see if you print horiz line (if it's not last)
                if(row < #_of_rows_in_horiz_lines_arr (or if row < rows_in_dots - 1)){
                    System.out.print(Horizontal_lines_arr[row][col];
                }}
            /* so now you've printed one . - . - . row via the column inside for loop. You're out of the inner loop, but still in the outer loop. For every row you print a .-., you need a row of vertical lines right underneath. Do this here. */
            System.out.println();
            if(row < rows_in_dots - 1 (or if row < #_of_rows_in_vertical)){
                for(int c = 0; c < # of cols, c++){

                    // print vertical line        System.out.print(vert_lines[row][c]);
                    // print a space (where horiz line is) if it's not the last vert line being printed
                    if(c < # of cols - 1){
                        System.out.print(" ");
                    }
                }

                System.out.println();
            }
    }

}
