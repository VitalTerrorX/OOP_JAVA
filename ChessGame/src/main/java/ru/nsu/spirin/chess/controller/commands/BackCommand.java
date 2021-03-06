package ru.nsu.spirin.chess.controller.commands;

import ru.nsu.spirin.chess.model.match.server.ConnectionStatus;
import ru.nsu.spirin.chess.controller.Command;
import ru.nsu.spirin.chess.controller.CommandStatus;
import ru.nsu.spirin.chess.utils.ScoresFile;
import ru.nsu.spirin.chess.model.scene.Scene;
import ru.nsu.spirin.chess.model.scene.SceneState;

import java.util.Arrays;

public final class BackCommand extends Command {
    private final SceneState[] allowed = new SceneState[] { SceneState.NEW_GAME_MENU, SceneState.CONNECTION_MENU, SceneState.RESULTS_MENU, SceneState.HIGH_SCORES_MENU, SceneState.ABOUT_MENU };

    public BackCommand(Scene scene) {
        super(scene);
    }

    @Override
    public CommandStatus execute(String[] args) {
        if (!Arrays.asList(allowed).contains(getScene().getSceneState())) return CommandStatus.INVALID_MENU;
        if (getScene().getSceneState() == SceneState.CONNECTION_MENU) {
            if (getScene().getActiveGame().connected() != ConnectionStatus.FAILED) return CommandStatus.INVALID_MENU;
        }
        if (args.length != 0) return CommandStatus.WRONG_NUMBER_OF_ARGUMENTS;
        if (getScene().getSceneState() == SceneState.RESULTS_MENU) {
            ScoresFile.saveScore(getScene().getActiveGame().getPlayerName(), ScoresFile.calculateTotalScore(getScene()));
        }
        getScene().setSceneState(getScene().getSceneState().getPrevScene());
        return CommandStatus.NORMAL;
    }
}
