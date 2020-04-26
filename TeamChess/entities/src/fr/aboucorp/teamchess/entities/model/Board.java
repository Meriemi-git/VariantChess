package fr.aboucorp.teamchess.entities.model;

import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.entities.model.pieces.Bishop;
import fr.aboucorp.teamchess.entities.model.pieces.King;
import fr.aboucorp.teamchess.entities.model.pieces.Knight;
import fr.aboucorp.teamchess.entities.model.pieces.Pawn;
import fr.aboucorp.teamchess.entities.model.pieces.Queen;
import fr.aboucorp.teamchess.entities.model.pieces.Rook;
import fr.aboucorp.teamchess.entities.model.utils.PieceList;
import fr.aboucorp.teamchess.entities.model.utils.SquareList;

public class Board {

    private SquareList chessSquares;
    private PieceList blackPieces;
    private PieceList blackDeadPieces;
    private PieceList whitePieces;
    private PieceList whiteDeadPieces;

    public Board(){
        this.chessSquares = new SquareList();
        this.blackPieces = new PieceList();
        this.whitePieces = new PieceList();
        this.blackDeadPieces = new PieceList();
        this.whiteDeadPieces = new PieceList();
    }



    public void initBoard(){
        this.createSquares();
        this.createWhitePieces();
        this.createBlackPieces();
    }

    private void createSquares(){
        for (int x = 0; x < 8; x++) {
            for (int z = 0; z < 8; z++) {
                Square square = null;
                if(x % 2 == 0 && z % 2 != 0 || x % 2 != 0 && z % 2 == 0 ){
                    square = new Square(new Location(x, 0, z), ChessColor.WHITE);
                }else{
                square = new Square(new Location(x, 0, z), ChessColor.BLACK);
                }
                this.chessSquares.add(square);
            }
        }

    }

    private void createWhitePieces(){
        Knight whiteRightKnight = new Knight((Square) this.chessSquares.getItemByLocation(new Location(6,0,0)), ChessColor.WHITE, PieceId.WRN,this);
        Knight whiteLeftKnight =  new Knight((Square) this.chessSquares.getItemByLocation(new Location(1,0,0)), ChessColor.WHITE, PieceId.WLN,this);
        Bishop whiteRightBishop = new Bishop((Square) this.chessSquares.getItemByLocation(new Location(5,0,0)), ChessColor.WHITE, PieceId.WRB,this);
        Bishop whiteLeftBishop = new Bishop((Square) this.chessSquares.getItemByLocation( new Location(2,0,0)), ChessColor.WHITE, PieceId.WLB,this);
        Queen whiteQueen = new Queen((Square) this.chessSquares.getItemByLocation(new Location(4,0,0)), ChessColor.WHITE,PieceId.WQ,this);
        King whiteKing = new King((Square) this.chessSquares.getItemByLocation(new Location(3,0,0)), ChessColor.WHITE,PieceId.WK,this);
        Rook whiteLeftRook = new Rook((Square) this.chessSquares.getItemByLocation(new Location(7,0,0)), ChessColor.WHITE,PieceId.WLR,this);
        Rook whiteRightRook =  new Rook((Square) this.chessSquares.getItemByLocation(new Location(0,0,0)), ChessColor.WHITE,PieceId.WRR,this);
        for(int  i = 0 ; i < 8 ; i++){
            Pawn whitePawn =  new Pawn((Square) this.chessSquares.getItemByLocation(new Location(i,0,1)), ChessColor.WHITE,PieceId.get(i),this);
            this.whitePieces.add(whitePawn);
        }
        this.whitePieces.add(whiteRightKnight);
        this.whitePieces.add(whiteLeftKnight);
        this.whitePieces.add(whiteRightBishop);
        this.whitePieces.add(whiteLeftBishop);
        this.whitePieces.add(whiteQueen);
        this.whitePieces.add(whiteKing);
        this.whitePieces.add(whiteLeftRook);
        this.whitePieces.add(whiteRightRook);
    }

    private void createBlackPieces(){
        Knight blackRightKnight = new Knight((Square) this.chessSquares.getItemByLocation(new Location(6,0,7)), ChessColor.BLACK,PieceId.BRN,this);
        Knight blackLeftKnight = new Knight((Square) this.chessSquares.getItemByLocation(new Location(1,0,7)), ChessColor.BLACK,PieceId.BLN,this);
        Bishop blackLeftBishop = new Bishop((Square) this.chessSquares.getItemByLocation(new Location(5,0,7)), ChessColor.BLACK,PieceId.BLB,this);
        Bishop blackRightBishop =  new Bishop((Square) this.chessSquares.getItemByLocation(new Location(2,0,7)), ChessColor.BLACK,PieceId.BRB,this);
        Queen blackQueen = new Queen((Square) this.chessSquares.getItemByLocation(new Location(4,0,7)), ChessColor.BLACK,PieceId.BQ,this);
        King blackKing = new King((Square) this.chessSquares.getItemByLocation(new Location(3,0,7)), ChessColor.BLACK,PieceId.BK,this);
        Rook blackLeftRook =  new Rook((Square) this.chessSquares.getItemByLocation(new Location(7,0,7)), ChessColor.BLACK,PieceId.BLR,this);
        Rook blackRightRook =  new Rook((Square) this.chessSquares.getItemByLocation(new Location(0,0,7)), ChessColor.BLACK,PieceId.BRR,this);

        for(int  i = 0 ; i < 8 ; i++){
            Pawn blackPawn = new Pawn((Square) this.chessSquares.getItemByLocation(new Location(i,0,6)),ChessColor.BLACK,PieceId.get(i+10),this);
            this.blackPieces.add(blackPawn);
        }
        this.blackPieces.add(blackRightKnight);
        this.blackPieces.add(blackLeftKnight);
        this.blackPieces.add(blackLeftBishop);
        this.blackPieces.add(blackRightBishop);
        this.blackPieces.add(blackQueen);
        this.blackPieces.add(blackKing);
        this.blackPieces.add(blackLeftRook);
        this.blackPieces.add(blackRightRook);
    }

    public boolean isSquareFree(Square square){
        Square boardSquare = (Square) this.chessSquares.getItemByLocation(square.getLocation());
        if(boardSquare != null){
            return boardSquare.getPiece() == null;
        }
        return false;
    }

    public SquareList getSquares() {
        return chessSquares;
    }

    public PieceList getBlackPieces() {
        return blackPieces;
    }

    public PieceList getWhitePieces() {
        return whitePieces;
    }

    public PieceList getPiecesByColor(ChessColor color){
        return color == ChessColor.WHITE ? getWhitePieces() : getBlackPieces();
    }
}
