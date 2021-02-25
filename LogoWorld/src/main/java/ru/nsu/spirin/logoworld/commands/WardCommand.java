package ru.nsu.spirin.logoworld.commands;

import ru.nsu.spirin.logoworld.logic.Program;
import ru.nsu.spirin.logoworld.logic.World;

public class WardCommand implements Command {
    private final World world;
    private final Program program;

    public WardCommand(Program program, World world) {
        this.program = program;
        this.world = world;
    }

    @Override
    public boolean validateArgs(String[] args) {
        return args.length == 0;
    }

    @Override
    public boolean execute(String[] args) {
        if (!world.getIsTurtleDrawing() || !world.isValid()) {
            program.setNextCommand();
            return false;
        }
        world.setIsTurtleDrawing(false);
        return true;
    }
}
