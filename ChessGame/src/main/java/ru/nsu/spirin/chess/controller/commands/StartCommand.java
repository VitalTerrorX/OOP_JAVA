package ru.nsu.spirin.chess.controller.commands;

import ru.nsu.spirin.chess.controller.Command;
import ru.nsu.spirin.chess.controller.CommandStatus;
import ru.nsu.spirin.chess.model.player.Alliance;
import ru.nsu.spirin.chess.utils.SettingsFile;
import ru.nsu.spirin.chess.model.scene.Scene;
import ru.nsu.spirin.chess.model.scene.SceneState;

public final class StartCommand extends Command {
    public StartCommand(Scene scene) {
        super(scene);
    }

    @Override
    public CommandStatus execute(String[] args) {
        if (getScene().getSceneState() != SceneState.NEW_GAME_MENU) return CommandStatus.INVALID_MENU;
        if (args.length != 2) return CommandStatus.WRONG_NUMBER_OF_ARGUMENTS;

        boolean isWhitePlayer;
        if (args[0].equalsIgnoreCase("white")) isWhitePlayer = true;
        else if (args[0].equalsIgnoreCase("black")) isWhitePlayer = false;
        else return CommandStatus.INVALID_ARGUMENTS;

        SettingsFile.saveSetting("LAST_USED_NAME", args[1]);
        getScene().startLocalGame(args[1], isWhitePlayer ?
                Alliance.WHITE :
                Alliance.BLACK);
        getScene().setSceneState(SceneState.BOARD_MENU);
        return CommandStatus.NORMAL;
    }
}
