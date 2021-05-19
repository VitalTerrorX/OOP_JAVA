package ru.nsu.spirin.chess.view.swing.board;

import ru.nsu.spirin.chess.model.move.Move;
import ru.nsu.spirin.chess.model.move.MoveLog;
import ru.nsu.spirin.chess.utils.Pair;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

class GameHistoryPanel extends JPanel {
    private final DataModel model;
    private final JScrollPane scrollPane;

    private static final Dimension HISTORY_PANEL_DIMENSION = new Dimension(100, 400);

    GameHistoryPanel() {
        this.setLayout(new BorderLayout());
        setDoubleBuffered(false);
        this.model = new DataModel();
        final JTable table = new JTable(model);
        table.setRowHeight(15);
        this.scrollPane = new JScrollPane(table);
        this.scrollPane.setColumnHeaderView(table.getTableHeader());
        this.scrollPane.setPreferredSize(HISTORY_PANEL_DIMENSION);
        this.add(scrollPane, BorderLayout.CENTER);
        this.setVisible(true);
    }

    void updatePanel(MoveLog moveHistory) {
        int currentRow = 0;
        this.model.clear();
        for (Pair<Move, String> logEntry : moveHistory.getMoves()) {
            Move move = logEntry.getFirst();
            String moveText = logEntry.getSecond();
            if (move.getMovedPiece().getAlliance().isWhite()) {
                this.model.setValueAt(moveText, currentRow, 0);
            }
            if (move.getMovedPiece().getAlliance().isBlack()) {
                this.model.setValueAt(moveText, currentRow, 1);
                currentRow++;
            }
        }

        if (moveHistory.getMoves().size() > 0) {
            Pair<Move, String> logEntry = moveHistory.getMoves().get(moveHistory.size() - 1);
            Move lastMove = logEntry.getFirst();
            String moveText = logEntry.getSecond();

            if (lastMove.getMovedPiece().getAlliance().isWhite()) {
                this.model.setValueAt(moveText, currentRow, 0);
            }
            else if (lastMove.getMovedPiece().getAlliance().isBlack()) {
                this.model.setValueAt(moveText, currentRow - 1, 1);
            }
        }

        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    private static final class DataModel extends DefaultTableModel {
        private final List<Row> values;
        private static final String[] NAMES = {"White", "Black"};

        DataModel() {
            this.values = new ArrayList<>();
        }

        public void clear() {
            this.values.clear();
            setRowCount(0);
        }

        @Override
        public int getRowCount() {
            if (this.values == null) {
                return 0;
            }
            return this.values.size();
        }

        @Override
        public int getColumnCount() {
            return NAMES.length;
        }

        @Override
        public Object getValueAt(int row, int column) {
            final Row currentRow = this.values.get(row);
            if (column == 0) {
                return currentRow.getWhiteMove();
            }
            else if (column == 1) {
                return currentRow.getBlackMove();
            }
            return null;
        }

        @Override
        public void setValueAt(Object aValue, int row, int column) {
            final Row currentRow;
            if (this.values.size() <= row) {
                currentRow = new Row();
                this.values.add(currentRow);
            }
            else {
                currentRow = this.values.get(row);
            }

            if (column == 0) {
                currentRow.setWhiteMove((String) aValue);
                fireTableRowsInserted(row, row);
            }
            else if (column == 1) {
                currentRow.setBlackMove((String) aValue);
                fireTableCellUpdated(row, column);
            }


        }

        @Override
        public Class<?> getColumnClass(int column) {
            return Move.class;
        }

        @Override
        public String getColumnName(int column) {
            return NAMES[column];
        }
    }

    private static class Row {
        private String whiteMove;
        private String blackMove;

        Row() {}

        public String getWhiteMove() {
            return this.whiteMove;
        }

        public String getBlackMove() {
            return this.blackMove;
        }

        public void setWhiteMove(String move) {
            this.whiteMove = move;
        }

        public void setBlackMove(String move) {
            this.blackMove = move;
        }
    }
}