public class PositionAndDepth 
{
    private Tile[][] tiles;
    private int depth;

    public PositionAndDepth(Tile[][] tiles, int depth)
    {
        this.tiles = tiles;
        this.depth = depth;
    }

    public Tile[][] getTiles()
    {
        return tiles;
    }

    public int getDepth()
    {
        return depth;
    }
}
