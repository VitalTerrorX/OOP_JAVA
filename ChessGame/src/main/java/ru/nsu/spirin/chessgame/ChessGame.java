package ru.nsu.spirin.chessgame;

import ru.nsu.spirin.chessgame.board.Board;
import ru.nsu.spirin.chessgame.view.ConsoleView;
import ru.nsu.spirin.chessgame.view.GameView;
import ru.nsu.spirin.chessgame.view.swing.Table;

public final class ChessGame {
    private GameView gameView;
    private Board board;

    public ChessGame() {
        gameView = Table.get();
    }

    public void run() {
        board = Board.createStandardBoard();
        gameView.show();
    }

    public void close() {
        gameView.close();
    }
}
