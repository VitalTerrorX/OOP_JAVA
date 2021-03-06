package ru.nsu.spirin.chess.model.board.tile;

import ru.nsu.spirin.chess.model.pieces.Piece;

public final class EmptyTile extends Tile {
    public EmptyTile(int coordinate) {
        super(coordinate);
    }

    @Override
    public String toString() {
        return "-";
    }

    @Override
    public boolean isTileOccupied() {
        return false;
    }

    @Override
    public Piece getPiece() {
        return null;
    }
}
