package ru.nsu.spirin.chess.controller.commands;

import ru.nsu.spirin.chess.model.board.BoardUtils;
import ru.nsu.spirin.chess.controller.Command;
import ru.nsu.spirin.chess.controller.CommandStatus;
import ru.nsu.spirin.chess.model.move.Move;
import ru.nsu.spirin.chess.model.move.MoveFactory;
import ru.nsu.spirin.chess.model.move.MoveStatus;
import ru.nsu.spirin.chess.model.player.Alliance;
import ru.nsu.spirin.chess.model.scene.Scene;
import ru.nsu.spirin.chess.model.scene.SceneState;

public final class MoveCommand extends Command {
    public MoveCommand(Scene scene) {
        super(scene);
    }

    @Override
    public CommandStatus execute(String[] args) {
        if (getScene().getSceneState() != SceneState.BOARD_MENU) return CommandStatus.INVALID_MENU;

        int length = args.length - 1;
        Alliance alliance;
        if (args[length].equalsIgnoreCase("WHITE")) alliance = Alliance.WHITE;
        else if (args[length].equalsIgnoreCase("BLACK")) alliance = Alliance.BLACK;
        else {
            System.out.println("Exception in MoveCommand: Can't get alliance");
            return CommandStatus.EXCEPTION;
        }

        Move move;

        if (length == 2) {
            int sourceCoordinate = BoardUtils.getCoordinateAtPosition(args[0]);
            int destinationCoordinate = BoardUtils.getCoordinateAtPosition(args[1]);
            if (sourceCoordinate == -1 || destinationCoordinate == -1) return CommandStatus.INVALID_ARGUMENTS;
            move = MoveFactory.createMove(getScene().getActiveGame().getBoard(), sourceCoordinate, destinationCoordinate);
        }
        else if (length == 1) {
            if (!args[0].equalsIgnoreCase("resign")) return CommandStatus.INVALID_ARGUMENTS;
            move = MoveFactory.createResignMove(getScene().getActiveGame().getBoard(), alliance);
        }
        else return CommandStatus.WRONG_NUMBER_OF_ARGUMENTS;

        MoveStatus status = getScene().getActiveGame().makeMove(move);
        if (status.isDone()) return CommandStatus.NORMAL;

        return CommandStatus.INVALID_MOVE;
    }
}
