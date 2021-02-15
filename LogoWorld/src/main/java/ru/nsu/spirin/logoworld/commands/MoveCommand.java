package ru.nsu.spirin.logoworld.commands;

import ru.nsu.spirin.logoworld.logic.World;
import ru.nsu.spirin.logoworld.math.Direction;

public class MoveCommand implements Command {
    private World world;
    private int steps;

    public MoveCommand(World world) {
        this.world = world;
        this.steps = 0;
    }

    @Override
    public boolean validateArgs(String[] args) {
        if (args.length != 2 || args[0].length() != 1) return false;
        if (Direction.convertCharacterToDirection(args[0].charAt(0)) == Direction.UNKNOWN) return false;
        try {
            int s = Integer.parseInt(args[1]);
            return s > 0;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean execute(String[] args) {
        if (steps >= Integer.parseInt(args[1]) || !world.isValid()) {
            steps = 0;
            return false;
        }
        steps++;
        world.moveTurtle(this, Direction.convertCharacterToDirection(args[0].charAt(0)));
        return true;
    }
}
