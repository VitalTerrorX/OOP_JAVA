package ru.nsu.spirin.chess.model.player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import ru.nsu.spirin.chess.model.board.Board;
import ru.nsu.spirin.chess.model.move.Move;
import ru.nsu.spirin.chess.model.move.MoveStatus;
import ru.nsu.spirin.chess.model.move.MoveTransition;
import ru.nsu.spirin.chess.model.move.PawnPromotion;
import ru.nsu.spirin.chess.model.move.ResignMove;
import ru.nsu.spirin.chess.model.pieces.King;
import ru.nsu.spirin.chess.model.pieces.Piece;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public abstract class Player implements Serializable {
    private final Board board;
    private final King  playerKing;

    private final boolean isInCheck;
    private       boolean resigned;
    private       int     promotedPawns;

    private final Collection<Move> legalMoves;

    protected Player(Board board, Collection<Move> legalMoves, Collection<Move> opponentMoves) {
        this.board = board;
        this.playerKing = establishKing();
        this.legalMoves = ImmutableList.copyOf(Iterables.concat(legalMoves, calculateKingCastles(legalMoves, opponentMoves)));
        this.isInCheck = !Player.calculateAttacksOnTile(this.playerKing.getCoordinate(), opponentMoves).isEmpty();
        this.promotedPawns = 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerKing, isInCheck, resigned, promotedPawns);
    }

    public abstract Collection<Piece> getActivePieces();

    public abstract Alliance getAlliance();

    public abstract Player getOpponent();

    protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals, Collection<Move> opponentLegals);

    public Board getBoard() {
        return this.board;
    }

    public Piece getPlayerKing() {
        return this.playerKing;
    }

    public Collection<Move> getLegalMoves() {
        return this.legalMoves;
    }

    public boolean isInCheck() {
        return this.isInCheck;
    }

    public boolean isResigned() {
        return this.resigned;
    }

    public int getPromotedPawns() {
        return this.promotedPawns;
    }

    public boolean isInCheckMate() {
        return this.isInCheck && noEscapeMoves();
    }

    public boolean isInStaleMate() {
        return !this.isInCheck && noEscapeMoves();
    }

    public boolean isCastled() {
        return this.playerKing.isCastled();
    }

    public boolean isKingSideCastleCapable() {
        return this.playerKing.isKingSideCastleCapable();
    }

    public boolean isQueenSideCastleCapable() {
        return this.playerKing.isQueenSideCastleCapable();
    }

    public void resign() {
        this.resigned = true;
    }

    public MoveTransition makeMove(Move move) {
        return makeMoveAdvanced(move, true);
    }

    protected boolean noEscapeMoves() {
        for (Move move : legalMoves) {
            MoveTransition transition = makeMoveAdvanced(move, false);
            if (transition.getMoveStatus().isDone()) return false;
        }
        return true;
    }

    protected boolean noCastleOpportunities() {
        return this.isInCheck || this.playerKing.isCastled() || (!this.isKingSideCastleCapable() && !this.isQueenSideCastleCapable());
    }

    protected static Collection<Move> calculateAttacksOnTile(int piecePosition, Collection<Move> moves) {
        List<Move> attackMoves = new ArrayList<>();
        for (Move move : moves) {
            if (piecePosition == move.getDestinationCoordinate()) {
                attackMoves.add(move);
            }
        }
        return ImmutableList.copyOf(attackMoves);
    }

    private MoveTransition makeMoveAdvanced(Move move, boolean checkForTurn) {
        if (move instanceof ResignMove) return new MoveTransition(move.execute(), move, MoveStatus.DONE);
        if (!isMoveLegal(move, checkForTurn)) return new MoveTransition(this.board, move, MoveStatus.ILLEGAL_MOVE);
        Board transitionBoard = move.execute();
        Collection<Move> kingAttacks = Player.calculateAttacksOnTile(transitionBoard.getCurrentPlayer().getOpponent().getPlayerKing().getCoordinate(), transitionBoard.getCurrentPlayer().getLegalMoves());
        if (!kingAttacks.isEmpty()) return new MoveTransition(this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        if (move instanceof PawnPromotion) this.promotedPawns++;
        return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
    }

    private boolean isMoveLegal(Move move, boolean checkForTurn) {
        return (!checkForTurn || board.getCurrentPlayer() == this) && this.legalMoves.contains(move);
    }

    private King establishKing() {
        for (Piece piece : getActivePieces()) {
            if (piece.getType().isKing()) return (King) piece;
        }
        throw new RuntimeException("Board doesn't have a king!");
    }
}
