import java.util.Arrays;

public class Level
{
    private Tile[][] tiles;
    private int greenCount;

    public Level(Tile[][] tiles)
    {
        this.tiles = new Tile[tiles.length][tiles[0].length];
        for (int i = 0; i < tiles.length; i++)
        {
            this.tiles[i] = Arrays.copyOf(tiles[i], tiles[i].length);
        }

        greenCount = 0;
        for (Tile[] row : tiles)
            for (Tile tile : row)
                if (tile == Tile.GREEN)
                    greenCount++;
    }

    public Level(Tile[][] tiles, int greenCount)
    {
        this.tiles = new Tile[tiles.length][tiles[0].length];
        for (int i = 0; i < tiles.length; i++)
        {
            this.tiles[i] = Arrays.copyOf(tiles[i], tiles[i].length);
        }
        this.greenCount = greenCount;
    }

    // Created by chat and edited to work by me
    public boolean tilt(Direction d)
    {
        int rows = tiles.length;
        int cols = tiles[0].length;

        // Direction deltas
        int dr = 0, dc = 0;
        switch (d)
        {
            case UP -> dr = -1;
            case DOWN -> dr = 1;
            case LEFT -> dc = -1;
            case RIGHT -> dc = 1;
        }

        // Set processing order to avoid pushing tokens through each other
        int rowStart = (dr == 1) ? rows - 1 : 0;
        int rowEnd = (dr == 1) ? -1 : rows;
        int rowStep = (dr == 1) ? -1 : 1;

        int colStart = (dc == 1) ? cols - 1 : 0;
        int colEnd = (dc == 1) ? -1 : cols;
        int colStep = (dc == 1) ? -1 : 1;

        for (int r = rowStart; r != rowEnd; r += rowStep)
        {
            for (int c = colStart; c != colEnd; c += colStep)
            {
                Tile current = tiles[r][c];
                if (current != Tile.GREEN && current != Tile.BLUE) continue;

                int nr = r, nc = c;

                // Try sliding the token
                while (true)
                {
                    boolean hitOrHole = false;
                    int nextR = nr + dr;
                    int nextC = nc + dc;

                    if (nextR < 0 || nextR >= rows || nextC < 0 || nextC >= cols) break;
                    Tile nextTile = tiles[nextR][nextC];

                    switch (nextTile)
                    {
                        case EMPTY:
                            nr = nextR;
                            nc = nextC;
                            break;
                        case HOLE:
                            hitOrHole = true;
                            // Token falls into hole
                            if (current == Tile.BLUE)
                            {
                                tiles[r][c] = Tile.EMPTY;
                                return false; // Invalid move
                            }
                            else
                            {
                                tiles[r][c] = Tile.EMPTY; // Remove green from original spot
                                greenCount--;
                            }
                            // Don't place the GREEN in the hole
                            nr = -1;
                            break;
                        default:
                            hitOrHole = true;
                            break;

                    }
                    if (hitOrHole) break;
                }

                // Move the token if it changed position
                if (nr != r || nc != c)
                {
                    if (nr != -1)
                    { // Token didn't fall into hole
                        tiles[nr][nc] = current;
                    }
                    tiles[r][c] = Tile.EMPTY;
                }
            }
        }

        return true; // No blue tokens fell into holes
    }

    public Tile[][] getTiles()
    {
        return tiles;
    }

    public int getGreenCount()
    {
        return greenCount;
    }

    public boolean isWon()
    {
        return greenCount == 0;
    }

    @Override
    public String toString()
    {
        String ret = "";

        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                switch (tile) {
                    case EMPTY -> ret += "- ";
                    case BARRIER -> ret += "X ";
                    case HOLE -> ret += "H ";
                    case BLUE -> ret += "B ";
                    case GREEN -> ret += "G ";
                    default -> {
                    }
                }
            }
            ret += "\n";
        }

        return ret;
    }
}