
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    private static final int WRAPPER = 2;
    private static final int ROWS = 9 + WRAPPER;
    private static final int COLS = 9 + WRAPPER;
    private char[][] field = new char [ROWS][COLS];
    private static int numberOfMines;
    private boolean[][] cellMarked = new boolean[ROWS][COLS];
    private boolean[][] discoveredCells = new boolean[ROWS][COLS];
    private static boolean inGame = true;
    private static int selectedX;
    private static int selectedY;
    private static boolean markCell;
    private static final String HEADING  = " |123456789|";
    private static final String SEPARATOR = "-|---------|";


    public static void main(String[] args) {
        // write your code here

        Main m = new Main();
        m.initBoard(getNumberMines());
        m.insertNumberMinesAround();
        m.printScreen();
        m.initGame();


    }

    private void initGame() {

        while(inGame) {

            getCoordinatesAndAction();
            updateField();
            checkWin();
            printScreen();

        }
    }

    private void checkWin() {

        if(!inGame){
            System.out.println("You stepped on a mine and failed!");
            return;
        }

        int discCells = 0;
        int minesMarked = 0;
        int minesWrongMarked = 0;

        for (int i = 1; i < ROWS - 1; i++) {
            for (int j = 1; j < COLS -1; j++) {
                if (cellMarked[i][j] && field[i][j] == 'X') { //si encuentra una marca que no corresponde a una mina NO HA GANADO
                    minesMarked++;
                }

                if (cellMarked[i][j] && field[i][j] != 'X') { //si encuentra una marca que no corresponde a una mina NO HA GANADO
                    minesWrongMarked++;
                }

                if(discoveredCells[i][j] && field[i][j] != 'X') {
                    discCells++;
                }
            }
        }

        if ((ROWS - WRAPPER) * (COLS - WRAPPER) - numberOfMines == discCells || minesMarked == numberOfMines && minesWrongMarked == 0) {
            System.out.println("Congratulations! You found all the mines!");
            inGame = false;
        }


    }

    private void updateField() {

        for (int i = 1; i < ROWS - 1; i++) {

            for (int j = 1; j < COLS -1; j++) {

                if (j == selectedX && i == selectedY) { //si la casilla seleccionada no esta explorada

                    if (!discoveredCells[i][j]) {

                        if (markCell) { //marcar como posible mine la casilla

                            cellMarked[i][j] = !cellMarked[i][j];

                        } else { //explorar la casilla

                            discoverCell(i, j);
                        }

                    }else {

                        System.out.println("Cell has been explored yet");
                    }
                }
            }
        }

    }//end of updateField



    private void discoverCell(int x, int y) {
        if (field[x][y] == 'X') {
            discoveredCells[x][y] = true;
            inGame = false;

        }else if (field[x][y] != 'X') {

            if (field[x][y] != '0') {
                discoveredCells[x][y] = true;

            }else if(field[x][y] == '0'){ //celda vacia sin minas alrededor.

                floodFill(field, discoveredCells, y, x);
            }

        }


    }


    //Floodfill algorithm:
    private void floodFill(char[][] field, boolean[][] discoveredCells, int y, int x) {
        //quit if off the grid:
        if (!validCoord(x, y)) return;

        //quit if visited:
        if(discoveredCells[x][y])
            {return;}
        discoveredCells[x][y] = true;

        //quit if hit wall:
        if(field[x][y]!='0') return;

        //recursively fill in all directions
        floodFill(field,discoveredCells,y - 1,x - 1);
        floodFill(field,discoveredCells,y - 1,   x    );
        floodFill(field,discoveredCells,y - 1,x + 1);

        floodFill(field,discoveredCells,   y,    x - 1);
        floodFill(field,discoveredCells,   y,    x + 1);

        floodFill(field,discoveredCells,y + 1,x - 1);
        floodFill(field,discoveredCells,y + 1,   x    );
        floodFill(field,discoveredCells,y + 1,x + 1);
    }


    private void getCoordinatesAndAction() {

        Scanner sc = new Scanner(System.in);
        int x = 0, y = 0;
        String command="";

        while(true) {
            System.out.println("Set/unset mine marks or claim a cell as free:");
            String[] input = sc.nextLine().split(" ");

            if (input.length == 3) {

                try {
                    x = Integer.parseInt(input[0]);
                    y = Integer.parseInt(input[1]);
                    command = input[2];
                } catch (NumberFormatException e) {
                    System.out.println("Incorrect number format. Must introduce coordinates betwewn 1 and"
                            + (ROWS - WRAPPER) + ".");
                    selectedX = selectedY = -1;
                }
                if (validCoord(x, y) && validCommand(command)) {
                    selectedX = x;
                    selectedY = y;
                    markCell = "mine".equals(command);
                    break;

                } else {
                    System.out.println("Coordinates go from 1 to " + (ROWS - 2));
                    System.out.println("Invalid command ('mine' or 'free'");
                    selectedX = selectedY = -1;
                }




            } else { //number of parameters incorrect
                System.out.println("Must introduce 3 parameters");
            }

        }


    }


    private void insertNumberMinesAround() {

        for (int i = 1; i < ROWS - 1; i++) {
            for (int j = 1; j < COLS -1; j++) {
                if(field[i][j]!= 'X'){
                    char minesAround = checkNeighbours(i,j);
                    field[i][j] = minesAround;
                }

            }
        }
    }

    private char checkNeighbours(int x, int y) {
        int mines = 0;
        for(int i = x -1; i <= x + 1; i ++) {
            for(int j = y -1; j <= y + 1; j++) {
                if (!(i == x && j == y)) {
                    if (field[i][j] == 'X') {
                        mines++;
                    }
                }
            }
        }

        return Character.forDigit(mines, 10);
    }

    private static int getNumberMines() {

        Scanner sc = new Scanner(System.in);
        int mines=0;

        while(true) {
            System.out.println("How many mines do you want on the field?");
            try {
                String input = sc.nextLine();
                mines = Integer.parseInt(input);
                if(mines > 0 && mines < ROWS * COLS) {
                    break; //correct input: number of mines greater than 0
                }else {
                    System.out.println("Number of mines must be greater between 0 and "
                            + (ROWS - WRAPPER) * (COLS -WRAPPER)
                            + " mines");
                }
            }catch(NumberFormatException e) {
                System.out.println("invalid input"); //input user isn't a number
            }

        }

        numberOfMines = mines;
        return mines;

    }

    private void initBoard(int mines) {
        int minesLeft = mines;

        for (int i = 1; i < ROWS -1; i ++) { //iterate from 1 to ROWS -1 because of wrapper row and columns

                Arrays.fill(field[i], '.'); //fill each row with '.'
                Arrays.fill(cellMarked[i], false);
                Arrays.fill(discoveredCells[i], false);
        }

        while (minesLeft > 0) {
            for (int i = 1; i < ROWS - 1; i++) {
                for (int j = 1; j < COLS -1 ; j++) {
                    if (Math.random() > 0.95 && minesLeft > 0) {
                        if (field[i][j] != 'X') {
                            field[i][j] = 'X';
                            minesLeft--;
                        }

                    }
                }
            }
        }
    }

    private void printField() {
        System.out.println(HEADING);
        System.out.println(SEPARATOR);
        for (int i = 1; i < ROWS - 1; i++) {
            for (int j = 0; j < COLS; j++) {
                if(j==0) {
                    System.out.print(i+"|");
                }else {

                    if (j == COLS - 1){
                        System.out.print("|");
                    }else {
                        System.out.print(field[i][j]);
                    }
                }
            }
            System.out.println();
        }

        System.out.println(SEPARATOR);
    }

    private void printFieldHiddenMines() {
        System.out.println(HEADING);
        System.out.println(SEPARATOR);
        for (int i = 1; i < ROWS - 1; i++) {
            for (int j = 0; j < COLS; j++) {
                if (j == 0) {
                    System.out.print(i + "|");
                } else if (j == COLS -1){
                    System.out.print("|");
                } else {
                    if (field[i][j] == 'X') {
                        System.out.print( cellMarked[i][j] ? '*' : '.');
                    } else {
                        System.out.print( cellMarked[i][j] ? '*' : field[i][j]);
                    }
                }

            }
            System.out.println();
        }
        System.out.println(SEPARATOR);

    }

    private void printScreen() {
        System.out.println();
        System.out.println(HEADING);
        System.out.println(SEPARATOR);
        for (int i = 1; i < ROWS - 1; i++) {
            for (int j = 0; j < COLS; j++) {
                if(j==0) {
                    System.out.print(i+"|");
                }else {
                    if (j == COLS - 1){
                        System.out.print("|");
                    }else {

                        if(!discoveredCells[i][j]) {

                            if(!inGame && field[i][j] == 'X'){
                                System.out.print(field[i][j]);
                            }else {
                                System.out.print(cellMarked[i][j] ? '*' : '.');
                            }

                        } else {
                            if(field[i][j] == '0') {
                                System.out.print('/');
                            }else {
                                System.out.print(field[i][j]);
                            }

                        }
                    }

                }

            }
            System.out.println();
        }
        System.out.println(SEPARATOR);
    }

    private boolean validCoord(int x, int y) {
        return x > 0 && x <= ROWS - 2 &&
                y > 0 && y <= COLS - 2;
    }

    private boolean validCommand(String com) {
        return "mine".equals(com) || "free".equals(com);
    }


}
