package ru.nsu.spirin.chess.controller.commands;

import ru.nsu.spirin.chess.controller.Command;
import ru.nsu.spirin.chess.controller.CommandStatus;
import ru.nsu.spirin.chess.model.scene.Scene;
import ru.nsu.spirin.chess.model.scene.SceneState;

public final class NewGameCommand extends Command {
    public NewGameCommand(Scene scene) {
        super(scene);
    }

    @Override
    public CommandStatus execute(String[] args) {
        if (getScene().getSceneState() != SceneState.MAIN_MENU) return CommandStatus.INVALID_MENU;
        if (args.length != 0) return CommandStatus.WRONG_NUMBER_OF_ARGUMENTS;
        getScene().setSceneState(SceneState.NEW_GAME_MENU);
        return CommandStatus.NORMAL;
    }
}