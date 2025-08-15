import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main
{
    private static final double SLEEP_TIME = 3;
    private static final double DELAY = 0.75;

    public static void clearConsole()
    {
        System.out.print("\033[H\033[2J");
    }

    public static boolean equals(Tile[][] pos1, Tile[][] pos2)
    {
        if (pos1.length != pos2.length) return false;
        for (int i = 0; i < pos1.length; i++)
        {
            if (pos1[i].length != pos2[i].length) return false;

            if (!Arrays.equals(pos1[i], pos2[i])) return false;
        }

        return true;
    }

    public static boolean reachedInFewerMovesAndEdit(Tile[][] tiles, int depth, ArrayList<PositionAndDepth> checkedPositions)
    {
        for (int i = 0; i < checkedPositions.size(); i++)
        {
            if (equals(checkedPositions.get(i).getTiles(), tiles)) 
            {
                if (depth < checkedPositions.get(i).getDepth()) 
                {
                    checkedPositions.set(i, new PositionAndDepth(tiles, depth));
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean alreadyChecked(Tile[][] tiles, ArrayList<PositionAndDepth> checkedPositions)
    {
        for (PositionAndDepth thing : checkedPositions)
        {
            if (equals(thing.getTiles(), tiles)) return true;
        }
        return false;
    }

    // No specifications
    public static ArrayList<Direction> solve(Level level, ArrayList<PositionAndDepth> checkedPositions)
    {
        return solve(level, 0, 1, checkedPositions, null, false);
    }

    // Specifying maxDepth
    public static ArrayList<Direction> solve(Level level, int maxDepth, ArrayList<PositionAndDepth> checkedPositions)
    {
        return solve(level, maxDepth, 1, checkedPositions, null, false);
    }

    // Recursive solve
    public static ArrayList<Direction> solve(Level level, int maxDepth, int depth, ArrayList<PositionAndDepth> checkedPositions, Direction lastDirection, boolean causedEnter)
    {
        if (maxDepth != 0 && depth > maxDepth) return null;
        for (Direction direction : Direction.values())
        {
            if (lastDirection != null && direction == lastDirection) continue;
            if (lastDirection != null && direction == lastDirection.opposite() && !causedEnter) continue;
            Level levelCopy = new Level(level.getTiles(), level.getGreenCount());
            boolean successful = levelCopy.tilt(direction);
            // System.out.println(levelCopy + "" + direction + ", " + lastDirection + ", " + depth);
            // System.out.println();
            if (equals(levelCopy.getTiles(), level.getTiles()) || !successful)
            {
                continue;
            }
            ArrayList<Direction> moves = new ArrayList<>();
            if (levelCopy.isWon())
            {
                moves.add(direction);
                return moves;
            }
            if (!alreadyChecked(levelCopy.getTiles(), checkedPositions))
            {
                checkedPositions.add(new PositionAndDepth(levelCopy.getTiles(), depth));
                // Check if green went in hole to remove the opposite check
                boolean causedEnter2 = level.getGreenCount() != levelCopy.getGreenCount();
                moves = solve(levelCopy, maxDepth, depth + 1, checkedPositions, direction, causedEnter2);
                if (moves != null)
                {
                    moves.add(0, direction);
                    return moves;
                }
            }
            else if (reachedInFewerMovesAndEdit(levelCopy.getTiles(), depth, checkedPositions))
            {
                boolean causedEnter2 = level.getGreenCount() != levelCopy.getGreenCount();
                moves = solve(levelCopy, maxDepth, depth + 1, checkedPositions, direction, causedEnter2);
                if (moves != null)
                {
                    moves.add(0, direction);
                    return moves;
                }
            }
        }

        return null;
    }

    public static int[] getNumRowsAndCols(String fileName) throws FileNotFoundException
    {
        File file = new File("Levels", fileName + ".txt");
        Scanner scanner = new Scanner(file);
        int rows = 0, cols = 0;

        while (scanner.hasNextLine())
        {
            rows++;
            cols = scanner.nextLine().length();
        }

        scanner.close();

        int[] ret = {rows, cols};
        return ret;
    }

    public static Tile[][] getTilesFromFile(String fileName) throws FileNotFoundException
    {
        File file = new File("Levels", fileName + ".txt");
        Scanner scanner = new Scanner(file);
        Tile[][] tiles;
        int[] rc = getNumRowsAndCols(fileName);
        int rows = rc[0];
        int cols = rc[1];
        tiles = new Tile[rows][cols];

        int i = 0;
        while (scanner.hasNextLine())
        {
            String line = scanner.nextLine();
            for (int j = 0; j < line.length(); j++)
            {
                char character = line.charAt(j);
                switch (character)
                {
                    case ' ':
                        tiles[i][j] = Tile.EMPTY;
                        break;
                    case 'x':
                    case 'X':
                        tiles[i][j] = Tile.BARRIER;
                        break;
                    case 'b':
                    case 'B':
                        tiles[i][j] = Tile.BLUE;
                        break;
                    case 'g':
                    case 'G':
                        tiles[i][j] = Tile.GREEN;
                        break;
                    case 'h':
                    case 'H':
                        tiles[i][j] = Tile.HOLE;
                        break;
                }
            }
            i++;
        }

        scanner.close();

        return tiles;
    }

    public static void pressKey(Robot robot, Direction dir) throws InterruptedException
    {
        int keyCode = switch (dir)
        {
            case UP -> KeyEvent.VK_UP;
            case RIGHT -> KeyEvent.VK_RIGHT;
            case DOWN -> KeyEvent.VK_DOWN;
            case LEFT -> KeyEvent.VK_LEFT;
        };

        robot.keyPress(keyCode);
        Thread.sleep((int)(DELAY * 1000));
        robot.keyRelease(keyCode);
    }

    public static void doMoves(ArrayList<Direction> directions) throws AWTException, InterruptedException
    {
        Robot robot = new Robot();

        for (Direction dir : directions)
        {
            pressKey(robot, dir);
        }
    }

    public static void main(String[] args) throws FileNotFoundException, AWTException, InterruptedException
    {
        Scanner scan1 = new Scanner(System.in);
        Scanner scan2 = new Scanner(System.in);

        String result;
        while (true)
        {
            int levelNum = 0;
            while (true)
            {
                System.out.print("Enter the level to solve (1-40): ");
                levelNum = scan1.nextInt();
                if (levelNum < 1 || levelNum > 40)
                    System.out.println("Invalid number.");
                else
                    break;
            }

            Tile[][] arrangement;
            Level level;
            System.out.println("Solving level " + levelNum + ":");
            arrangement = getTilesFromFile("Level" + levelNum);
            level = new Level(arrangement);
            System.out.println(level);
            ArrayList<PositionAndDepth> checkedPositions;
            ArrayList<Direction> answer;
            int max = 1;
            while (true)
            {
                checkedPositions = new ArrayList<>();
                checkedPositions.add(new PositionAndDepth(level.getTiles(), 0));
                answer = solve(level, max,  checkedPositions);
                if (answer == null)
                    max++;
                else
                    break;
            }
            System.out.println(answer);
            System.out.println("-------------------------------------------------\n");

            while (true)
            {
                System.out.print("Shall I do the moves for you? If yes there will be a " + (int)(SLEEP_TIME * 10) / 10.0 + " second delay before starting (y/n): ");
                result = scan2.nextLine();

                if (result.equalsIgnoreCase("y"))
                {
                    Thread.sleep((int)(SLEEP_TIME * 1000));
                    doMoves(answer);
                    break;
                }
                else if (result.equalsIgnoreCase("n"))
                {
                    break;
                }
                else
                {
                    System.out.println("Not an option.");
                }
            }

            System.out.print("Again? (y/n): ");
            result = scan2.nextLine();
            if (result.equalsIgnoreCase("n")) break;
            else 
            {
                System.out.println("Going again!");
                clearConsole();
            }
        }
    }
}