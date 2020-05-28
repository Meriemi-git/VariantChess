package fr.aboucorp.variantchess.entities.enums;

import java.util.Arrays;

import fr.aboucorp.variantchess.entities.ChessColor;

public enum PieceId {
    WP1(0),
    WP2(1),
    WP3(2),
    WP4(3),
    WP5(4),
    WP6(5),
    WP7(6),
    WP8(7),
    WK(8),
    WQ(9),
    BP1(10),
    BP2(11),
    BP3(12),
    BP4(13),
    BP5(14),
    BP6(15),
    BP7(16),
    BP8(17),
    BK(18),
    BQ(19),
    WLN(20),
    WRN(21),
    WLR(22),
    WRR(23),
    WLB(24),
    WRB(25),
    BLN(26),
    BRN(27),
    BLR(28),
    BRR(29),
    BLB(30),
    BRB(31);

    private final double enumId;

    PieceId(int enumId) {
        this.enumId = enumId;
    }

    public static PieceId get(int numericId) {
        return Arrays.stream(values()).filter(x -> x.enumId == numericId).findFirst().get();
    }

    public static boolean isPawn(PieceId id) {
        return isWhitePawn(id) || isBlackPawn(id);
    }

    private static boolean isWhitePawn(PieceId id) {
        return id == WP1 || id == WP2 || id == WP3 || id == WP4 || id == WP5 || id == WP6 || id == WP7 || id == WP8;
    }

    private static boolean isBlackPawn(PieceId id) {
        return id == BP1 || id == BP2 || id == BP3 || id == BP4 || id == BP5 || id == BP6 || id == BP7 || id == BP8;
    }

    public static ChessColor getColor(PieceId pieceId) {
        if (isWhitePawn(pieceId)) {
            return ChessColor.WHITE;
        }
        return ChessColor.BLACK;
    }

    public static boolean isRook(PieceId id) {
        return id == BRR || id == BLR || id == WRR || id == WLR;
    }

    public static boolean isKnight(PieceId id) {
        return id == BRN || id == BLN || id == WRN || id == WLN;
    }

    public static boolean isBishop(PieceId id) {
        return id == BRB || id == BLB || id == WRB || id == WLB;
    }

    public static boolean isKing(PieceId id) {
        return id == BK || id == WK;
    }

    public static boolean isQueen(PieceId id) {
        return id == BQ || id == WQ;
    }
}
