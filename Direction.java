public enum Direction
{
    UP,
    RIGHT,
    DOWN,
    LEFT;

    public Direction opposite()
    {
        return switch (this)
        {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }
}