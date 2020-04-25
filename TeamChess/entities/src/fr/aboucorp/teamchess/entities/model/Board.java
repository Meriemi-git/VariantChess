package fr.aboucorp.teamchess.entities.model;

import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.entities.model.pieces.Bishop;
import fr.aboucorp.teamchess.entities.model.pieces.King;
import fr.aboucorp.teamchess.entities.model.pieces.Knight;
import fr.aboucorp.teamchess.entities.model.pieces.Pawn;
import fr.aboucorp.teamchess.entities.model.pieces.Queen;
import fr.aboucorp.teamchess.entities.model.pieces.Rook;
import fr.aboucorp.teamchess.entities.model.utils.ChessCellList;
import fr.aboucorp.teamchess.entities.model.utils.ChessPieceList;

public class Board {

    private ChessCellList chessCells;
    private ChessPieceList blackPieces;
    private ChessPieceList blackDeadPieces;
    private ChessPieceList whitePieces;
    private ChessPieceList whiteDeadPieces;

    public Board(){
        this.chessCells = new ChessCellList();
        this.blackPieces = new ChessPieceList();
        this.whitePieces = new ChessPieceList();
        this.blackDeadPieces = new ChessPieceList();
        this.whiteDeadPieces = new ChessPieceList();
    }



    public void initBoard(){
        this.createCells();
        this.createWhitePieces();
        this.createBlackPieces();
    }

    private void createCells(){
        for (int x = 0; x < 8; x++) {
            for (int z = 0; z < 8; z++) {
                ChessCell cell = null;
                if(x % 2 == 0 && z % 2 != 0 || x % 2 != 0 && z % 2 == 0 ){
                    cell = new ChessCell(new Location(x, 0, z), ChessColor.BLACK);
                }else{
                    cell = new ChessCell(new Location(x, 0, z), ChessColor.WHITE);
                }
                this.chessCells.add(cell);
            }
        }

    }

    private void createWhitePieces(){
        Knight whiteRightKnight = new Knight((ChessCell) this.chessCells.getItemByLocation(new Location(6,0,0)), ChessColor.WHITE, PieceId.WRN,this);
        Knight whiteLeftKnight =  new Knight((ChessCell) this.chessCells.getItemByLocation(new Location(1,0,0)), ChessColor.WHITE, PieceId.WLN,this);
        Bishop whiteRightBishop = new Bishop((ChessCell) this.chessCells.getItemByLocation(new Location(5,0,0)), ChessColor.WHITE, PieceId.WRB,this);
        Bishop whiteLeftBishop = new Bishop((ChessCell) this.chessCells.getItemByLocation( new Location(2,0,0)), ChessColor.WHITE, PieceId.WLB,this);
        Queen whiteQueen = new Queen((ChessCell) this.chessCells.getItemByLocation(new Location(4,0,0)), ChessColor.WHITE,PieceId.WQ,this);
        King whiteKing = new King((ChessCell) this.chessCells.getItemByLocation(new Location(3,0,0)), ChessColor.WHITE,PieceId.WK,this);
        Rook whiteLeftRook = new Rook((ChessCell) this.chessCells.getItemByLocation(new Location(7,0,0)), ChessColor.WHITE,PieceId.WLR,this);
        Rook whiteRightRook =  new Rook((ChessCell) this.chessCells.getItemByLocation(new Location(0,0,0)), ChessColor.WHITE,PieceId.WRR,this);
        for(int  i = 0 ; i < 8 ; i++){
            Pawn whitePawn =  new Pawn((ChessCell) this.chessCells.getItemByLocation(new Location(i,0,1)), ChessColor.WHITE,PieceId.get(i),this);
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
        Knight blackRightKnight = new Knight((ChessCell) this.chessCells.getItemByLocation(new Location(6,0,7)), ChessColor.BLACK,PieceId.BRN,this);
        Knight blackLeftKnight = new Knight((ChessCell) this.chessCells.getItemByLocation(new Location(1,0,7)), ChessColor.BLACK,PieceId.BLN,this);
        Bishop blackLeftBishop = new Bishop((ChessCell) this.chessCells.getItemByLocation(new Location(5,0,7)), ChessColor.BLACK,PieceId.BLB,this);
        Bishop blackRightBishop =  new Bishop((ChessCell) this.chessCells.getItemByLocation(new Location(2,0,7)), ChessColor.BLACK,PieceId.BRB,this);
        Queen blackQueen = new Queen((ChessCell) this.chessCells.getItemByLocation(new Location(4,0,7)), ChessColor.BLACK,PieceId.BQ,this);
        King blackKing = new King((ChessCell) this.chessCells.getItemByLocation(new Location(3,0,7)), ChessColor.BLACK,PieceId.BK,this);
        Rook blackLeftRook =  new Rook((ChessCell) this.chessCells.getItemByLocation(new Location(7,0,7)), ChessColor.BLACK,PieceId.BLR,this);
        Rook blackRightRook =  new Rook((ChessCell) this.chessCells.getItemByLocation(new Location(0,0,7)), ChessColor.BLACK,PieceId.BRR,this);

        for(int  i = 0 ; i < 8 ; i++){
            Pawn blackPawn = new Pawn((ChessCell) this.chessCells.getItemByLocation(new Location(i,0,6)),ChessColor.BLACK,PieceId.get(i+10),this);
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

    public boolean isCellFree(ChessCell cell){
        ChessCell boardCell = (ChessCell) this.chessCells.getItemByLocation(cell.getLocation());
        if(boardCell != null){
            return boardCell.getPiece() == null;
        }
        return false;
    }

    public ChessCellList getChessCells() {
        return chessCells;
    }

    public ChessPieceList getBlackPieces() {
        return blackPieces;
    }

    public ChessPieceList getWhitePieces() {
        return whitePieces;
    }

    public ChessPieceList getPieceByColor(ChessColor color){
        return color == ChessColor.WHITE ? getWhitePieces() : getBlackPieces();
    }
    public ChessPieceList getBlackDeadPieces() {
        return blackDeadPieces;
    }

    public ChessPieceList getWhiteDeadPieces() {
        return whiteDeadPieces;
    }



}
