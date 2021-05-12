package ru.nsu.spirin.chess;

import ru.nsu.spirin.chess.utils.ThreadUtils;

import java.util.Arrays;

public final class Main {
    public static void main(String[] args) {
        ChessGame chessGame = null;
        try {
            chessGame = new ChessGame(!Arrays.asList(args).contains("--console"));
            chessGame.run();
        }
        catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        finally {
            if (chessGame != null) {
                chessGame.close();
            }
        }
        ThreadUtils.shutdown();
    }
}
