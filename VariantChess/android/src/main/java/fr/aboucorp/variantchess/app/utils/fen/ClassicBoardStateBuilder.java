package fr.aboucorp.variantchess.app.utils.fen;

import java.util.List;

import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.Location;
import fr.aboucorp.variantchess.entities.Piece;
import fr.aboucorp.variantchess.entities.Square;
import fr.aboucorp.variantchess.entities.Turn;
import fr.aboucorp.variantchess.entities.boards.ClassicBoard;
import fr.aboucorp.variantchess.entities.enums.PieceId;
import fr.aboucorp.variantchess.entities.rules.ClassicRuleSet;

public class ClassicBoardStateBuilder extends BoardStateBuilder {

    public ClassicBoardStateBuilder(ClassicBoard board, ClassicRuleSet classicRuleSet) {
        super(board, classicRuleSet);
    }

    @Override
    public String getStateFromBoard(Turn actualTurn) {
        StringBuilder state = new StringBuilder();
        state.append(getFenFromBoard(actualTurn));
        state.append(" [")
                .append(actualTurn.getFrom().toString())
                .append("|")
                .append(actualTurn.getPlayed())
                .append("|")
                .append(actualTurn.getTo().toString())
                .append("]");
        return state.toString();
    }

    @Override
    public String getFenFromBoard(Turn actualTurn) {
        StringBuilder fenString = new StringBuilder();
        for (int z = 7; z >= 0; z--) {
            List<Square> lines = this.board.getSquares().getSquaresByLine(z);
            int emptySquare = 0;
            for (int x = 7; x >= 0; x--) {
                Square square = lines.get(x);
                Piece piece = square.getPiece();
                if (piece != null) {
                    if (emptySquare > 0) {
                        fenString.append(emptySquare);
                        emptySquare = 0;
                    }
                    fenString.append(piece.fen());
                } else {
                    emptySquare++;
                }
            }
            if (emptySquare > 0) {
                fenString.append(emptySquare);
            }
            fenString.append('/');
        }
        fenString.append(' ');
        fenString.append(actualTurn.getTurnColor() == ChessColor.WHITE ? 'w' : 'b');
        fenString.append(' ');
        if (((ClassicRuleSet) this.ruleSet).whiteCanCastleKing()) {
            fenString.append('K');
        }
        if (((ClassicRuleSet) this.ruleSet).whiteCanCastleQueen()) {
            fenString.append('Q');
        }
        if (((ClassicRuleSet) this.ruleSet).blackCanCastleKing()) {
            fenString.append('k');
        }
        if (((ClassicRuleSet) this.ruleSet).blackCanCastleQueen()) {
            fenString.append('q');
        }
        fenString.append(' ');
        if (((ClassicRuleSet) this.ruleSet).enPassant != null) {
            fenString.append(((ClassicRuleSet) this.ruleSet).enPassant.getSquareLabel());
        } else {
            fenString.append('-');
        }
        fenString.append(' ');
        fenString.append(((ClassicRuleSet) this.ruleSet).fiftyMoveCounter);
        fenString.append(' ');
        fenString.append(this.ruleSet.moveNumber);
        return fenString.toString();
    }

    @Override
    public PieceId getPiecePlayedFromState(String fenState) throws IllegalStateException {
        String[] infos = getInfosFromState(fenState);
        return PieceId.valueOf(infos[1]);
    }

    @Override
    public Location getFrom(String fenState) throws IllegalStateException {
        String[] infos = getInfosFromState(fenState);
        return Location.fromShortString(infos[0]);
    }

    @Override
    public Location getTo(String fenState) throws IllegalStateException {
        String[] infos = getInfosFromState(fenState);
        return Location.fromShortString(infos[2]);
    }

    private String[] getInfosFromState(String fenState) throws IllegalStateException {
        int startIndex = fenState.indexOf('[');
        int endIndex = fenState.indexOf(']');
        String[] infos = fenState.substring(startIndex + 1, endIndex).split("\\|");
        if (infos.length != 3) {
            throw new IllegalStateException("Invalid format for state informations");
        }
        return infos;
    }
}
