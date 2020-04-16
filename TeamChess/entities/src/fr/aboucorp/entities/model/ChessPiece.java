package fr.aboucorp.entities.model;



public abstract class ChessPiece extends GameElement {

    public ChessPiece(Location location, ChessColor chessColor){
        super(location, chessColor);
    }

    public void move(Location location){
    }
}
