package fr.aboucorp.teamchess.libgdx;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import fr.aboucorp.generic.model.Board;
import fr.aboucorp.generic.model.Location;
import fr.aboucorp.generic.model.enums.Color;
import fr.aboucorp.teamchess.libgdx.exceptions.CellNotFoundException;
import fr.aboucorp.teamchess.libgdx.models.ChessBoard;
import fr.aboucorp.teamchess.libgdx.models.ChessCell;
import fr.aboucorp.teamchess.libgdx.models.ChessPiece;
import fr.aboucorp.teamchess.libgdx.models.pieces.Bishop;
import fr.aboucorp.teamchess.libgdx.models.pieces.King;
import fr.aboucorp.teamchess.libgdx.models.pieces.Knight;
import fr.aboucorp.teamchess.libgdx.models.pieces.Pawn;
import fr.aboucorp.teamchess.libgdx.models.pieces.Queen;
import fr.aboucorp.teamchess.libgdx.models.pieces.Rook;
import fr.aboucorp.teamchess.libgdx.utils.ChessCellArray;

public class BoardManager {

    private ChessBoard chessBoard;
    private ModelBuilder modelBuilder;
    private AssetManager assets;
    private boolean boardIsLoading;


    public BoardManager() {
        this.assets = new AssetManager(new InternalFileHandleResolver());
        this.chessBoard = new ChessBoard();
    }

    public void initBoard(){
        this.createBoard();
        this.loadPieces();
    }
    private void createBoard() {
        this.modelBuilder = new ModelBuilder();
        Model model = this.modelBuilder.createBox(1f, 0.1f, 1f,
                new Material(),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
        for (int x = 0; x < 8; x++) {
            for (int z = 0; z < 8; z++) {
                ChessCell cell = null;
                if(x % 2 == 0 && z % 2 != 0 || x % 2 != 0 && z % 2 == 0 ){
                    cell = new ChessCell(model, new Location(x, 0, z), Color.WHITE);
                    cell.materials.get(0).set(ColorAttribute.createDiffuse(com.badlogic.gdx.graphics.Color.WHITE));
                }else{
                    cell = new ChessCell(model, new Location(x, 0, z), Color.BLACK);
                    cell.materials.get(0).set(ColorAttribute.createDiffuse(com.badlogic.gdx.graphics.Color.BLACK));
                }
                this.chessBoard.getChessCells().add(cell);
            }
        }
    }

    private void loadPieces(){
        this.assets.load("data/knight.g3db", Model.class);
        this.assets.load("data/bishop.g3db", Model.class);
        this.assets.load("data/pawn.g3db", Model.class);
        this.assets.load("data/queen.g3db", Model.class);
        this.assets.load("data/king.g3db", Model.class);
        this.assets.load("data/rook.g3db", Model.class);
        this.boardIsLoading = true;
    }

    public void doneLoading() throws CellNotFoundException {
        Model knightModel = this.assets.get("data/knight.g3db", Model.class);
        Model bishopModel = this.assets.get("data/bishop.g3db", Model.class);
        Model pawnModel = this.assets.get("data/pawn.g3db", Model.class);
        Model queenModel = this.assets.get("data/queen.g3db", Model.class);
        Model kingModel = this.assets.get("data/king.g3db", Model.class);
        Model rookModel = this.assets.get("data/rook.g3db", Model.class);

        // Load black pieces
        Knight whiteRightKnight = new Knight(knightModel,this.chessBoard.getChessCells().getPieceByLocation(6,0,0), Color.WHITE);
        whiteRightKnight.transform.rotate(Vector3.Y, -90);
        this.chessBoard.getWhitePieces().add(whiteRightKnight);

        Knight whiteLeftKnight =  new Knight(knightModel,this.chessBoard.getChessCells().getPieceByLocation(1,0,0), Color.WHITE);
        whiteLeftKnight.transform.rotate(Vector3.Y, -90);
        this.chessBoard.getWhitePieces().add(whiteLeftKnight);

        Bishop whiteRightBishop = new Bishop(bishopModel,this.chessBoard.getChessCells().getPieceByLocation(5,0,0), Color.WHITE);
        whiteRightBishop.transform.rotate(Vector3.Y, -90);
        this.chessBoard.getWhitePieces().add(whiteRightBishop);

        Bishop whiteLeftBishop = new Bishop(bishopModel,this.chessBoard.getChessCells().getPieceByLocation(2,0,0), Color.WHITE);
        whiteLeftBishop.transform.rotate(Vector3.Y, -90);
        this.chessBoard.getWhitePieces().add(whiteLeftBishop);

        this.chessBoard.getWhitePieces().add( new Queen(queenModel,this.chessBoard.getChessCells().getPieceByLocation(4,0,0), Color.WHITE));
        this.chessBoard.getWhitePieces().add( new King(kingModel,this.chessBoard.getChessCells().getPieceByLocation(3,0,0), Color.WHITE));

        Rook whiteLeftRook = new Rook(rookModel,this.chessBoard.getChessCells().getPieceByLocation(7,0,0), Color.WHITE);
        whiteLeftRook.transform.rotate(Vector3.Y, -90);
        this.chessBoard.getWhitePieces().add(whiteLeftRook);

       Rook whiteRightRook =  new Rook(rookModel,this.chessBoard.getChessCells().getPieceByLocation(0,0,0), Color.WHITE);
        whiteRightRook.transform.rotate(Vector3.Y, -90);
        this.chessBoard.getWhitePieces().add(whiteRightRook);

        for(int  i = 0 ; i < 8 ; i++){
            this.chessBoard.getWhitePieces().add( new Pawn(pawnModel,this.chessBoard.getChessCells().getPieceByLocation(i,0,1), Color.WHITE));
        }
        // Apply material color
        for (ModelInstance whitePiece : chessBoard.getWhitePieces()) {
            whitePiece.materials.get(0).set(ColorAttribute.createDiffuse(com.badlogic.gdx.graphics.Color.WHITE));
        }

        // Load white pieces
        Knight blackRightKnight = new Knight(knightModel,this.chessBoard.getChessCells().getPieceByLocation(6,0,7), Color.BLACK);
        blackRightKnight.transform.rotate(Vector3.Y, 90);
        this.chessBoard.getBlackPieces().add(blackRightKnight);

        Knight blackLeftKnight = new Knight(knightModel,this.chessBoard.getChessCells().getPieceByLocation(1,0,7), Color.BLACK);
        blackLeftKnight.transform.rotate(Vector3.Y, 90);
        this.chessBoard.getBlackPieces().add( blackLeftKnight);

        Bishop blackLeftBishop = new Bishop(bishopModel,this.chessBoard.getChessCells().getPieceByLocation(5,0,7), Color.BLACK);
        blackLeftBishop.transform.rotate(Vector3.Y, 90);
        this.chessBoard.getBlackPieces().add(blackLeftBishop);

       Bishop blackRightBishop =  new Bishop(bishopModel,this.chessBoard.getChessCells().getPieceByLocation(2,0,7), Color.BLACK);
        blackRightBishop.transform.rotate(Vector3.Y, 90);
        this.chessBoard.getBlackPieces().add(blackRightBishop);

        this.chessBoard.getBlackPieces().add( new Queen(queenModel,this.chessBoard.getChessCells().getPieceByLocation(4,0,7), Color.BLACK));
        this.chessBoard.getBlackPieces().add( new King(kingModel,this.chessBoard.getChessCells().getPieceByLocation(3,0,7), Color.BLACK));

       Rook blackLeftRook =  new Rook(rookModel,this.chessBoard.getChessCells().getPieceByLocation(7,0,7), Color.BLACK);
        blackLeftRook.transform.rotate(Vector3.Y, 90);
        this.chessBoard.getBlackPieces().add(blackLeftRook );

        Rook blackRightRook =  new Rook(rookModel,this.chessBoard.getChessCells().getPieceByLocation(0,0,7), Color.BLACK);
        blackRightRook.transform.rotate(Vector3.Y, 90);
        this.chessBoard.getBlackPieces().add( blackRightRook);

        for(int  i = 0 ; i < 8 ; i++){
            this.chessBoard.getBlackPieces().add( new Pawn(pawnModel,this.chessBoard.getChessCells().getPieceByLocation(i,0,6), Color.BLACK));
        }
        // Apply material color
        for (ModelInstance blackPiece : this.chessBoard.getBlackPieces()
        ) {
            blackPiece.materials.get(0).set(ColorAttribute.createDiffuse(com.badlogic.gdx.graphics.Color.GRAY));
        }
        this.boardIsLoading = false;
    }

    private void createAxis(){
        Model arrowX = modelBuilder.createArrow(0f,0f,0, 10f,0f,0f, 0.1f, 0.1f, 5,
                GL20.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(com.badlogic.gdx.graphics.Color.RED)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        Model arrowY = modelBuilder.createArrow(0f,0f,0, 0f,10f,0f, 0.1f, 0.1f, 5,
                GL20.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(com.badlogic.gdx.graphics.Color.BLUE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        Model arrowZ = modelBuilder.createArrow(0f,0f,0, 0f,0f,10f, 0.1f, 0.1f, 5,
                GL20.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(com.badlogic.gdx.graphics.Color.GREEN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        chessBoard.getDevStuff().add(new ModelInstance(arrowX));
        chessBoard.getDevStuff().add(new ModelInstance(arrowY));
        chessBoard.getDevStuff().add(new ModelInstance(arrowZ));
    }

    public ChessCellArray getChessCells() {
        return chessBoard.getChessCells();
    }

    public Array<ChessPiece> getBlackPieces() {
        return this.chessBoard.getBlackPieces();
    }

    public Array<ChessPiece> getWhitePieces() {
        return this.chessBoard.getWhitePieces();
    }

    public Array<ModelInstance> getDevStuff() {
        return this.chessBoard.getDevStuff();
    }

    public boolean isBoardIsLoading() {
        return boardIsLoading;
    }

    public AssetManager getAssets() {
        return assets;
    }
}
