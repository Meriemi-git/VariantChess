package fr.aboucorp.teamchess.libgdx;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.aboucorp.entities.model.ChessCell;
import fr.aboucorp.entities.model.ChessColor;
import fr.aboucorp.entities.model.ChessPiece;
import fr.aboucorp.entities.model.Location;
import fr.aboucorp.entities.model.pieces.Bishop;
import fr.aboucorp.entities.model.pieces.King;
import fr.aboucorp.entities.model.pieces.Knight;
import fr.aboucorp.entities.model.pieces.Pawn;
import fr.aboucorp.entities.model.pieces.Queen;
import fr.aboucorp.entities.model.pieces.Rook;
import fr.aboucorp.teamchess.libgdx.models.ChessCellModel;
import fr.aboucorp.teamchess.libgdx.models.ChessModel;
import fr.aboucorp.teamchess.libgdx.models.pieces.BishopModel;
import fr.aboucorp.teamchess.libgdx.models.pieces.KingModel;
import fr.aboucorp.teamchess.libgdx.models.pieces.KnightModel;
import fr.aboucorp.teamchess.libgdx.models.pieces.PawnModel;
import fr.aboucorp.teamchess.libgdx.models.pieces.QueenModel;
import fr.aboucorp.teamchess.libgdx.models.pieces.RookModel;
import fr.aboucorp.teamchess.libgdx.utils.ChessCellArray;
import fr.aboucorp.teamchess.libgdx.utils.ChessModelList;

public class Board3dManager {

    private static Board3dManager INSTANCE;

    private final ChessCellArray chessCells;
    private final ChessModelList pieces;
    private final ArrayList<ChessPiece> loadingPieces;

    private final ArrayList devStuff;


    public static Map<Class,String> modelsPath =  new HashMap<>();

    private ModelBuilder modelBuilder;
    private AssetManager assets;
    private boolean boardIsLoading;
    private ChessModel selectedPiece;
    private Material3dManager material3dManager;

    private Board3dManager() {
        this.chessCells = new ChessCellArray();
        this.devStuff = new ArrayList();
        this.assets = new AssetManager(new InternalFileHandleResolver());
        this.material3dManager = Material3dManager.getInstance();
        this.pieces = new ChessModelList();
        this.loadingPieces = new ArrayList();
        setModelsPath();
    }

    private void setModelsPath() {
        modelsPath.put(King.class,"data/king.g3db");
        modelsPath.put(Queen.class,"data/queen.g3db");
        modelsPath.put(Pawn.class,"data/pawn.g3db");
        modelsPath.put(Rook.class,"data/rook.g3db");
        modelsPath.put(Bishop.class,"data/bishop.g3db");
        modelsPath.put(Knight.class,"data/knight.g3db");
    }

    public void initBoard(){
        this.loadPieces();
    }

    public void createCells(List<ChessCell> cells) {
        this.modelBuilder = new ModelBuilder();
        Model model = this.modelBuilder.createBox(1f, 0.1f, 1f,
                new Material(),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
        for (ChessCell cell: cells) {
            Material material;
            if(cell.getColor() == ChessColor.BLACK){
                material = new Material(ColorAttribute.createDiffuse(Color.BLACK));
            }else{
                material = new Material(ColorAttribute.createDiffuse(Color.WHITE));
            }
            ChessCellModel cellModel = new ChessCellModel(model,cell.getLocation(),material,cell);
            this.chessCells.add(cellModel);
        }
    }

    public void createPieces(List<ChessPiece> pieces){
        this.loadingPieces.addAll(pieces);
        loadPieces();
    }

    private void loadPieces(){
        for (String modelPath : modelsPath.values()) {
            this.assets.load(modelPath, Model.class);
        }
        this.boardIsLoading = true;
    }

    public void doneLoading() {

        Model knightModel = this.assets.get("data/knight.g3db", Model.class);
        Model bishopModel = this.assets.get("data/bishop.g3db", Model.class);
        Model pawnModel = this.assets.get("data/pawn.g3db", Model.class);
        Model queenModel = this.assets.get("data/queen.g3db", Model.class);
        Model kingModel = this.assets.get("data/king.g3db", Model.class);
        Model rookModel = this.assets.get("data/rook.g3db", Model.class);

        for (Iterator<ChessPiece> iter = this.loadingPieces.iterator(); iter.hasNext();) {
            ChessPiece piece = iter.next();
            Material material;
            if(piece.getChessColor() == ChessColor.BLACK){
                material = new Material(ColorAttribute.createDiffuse(Color.WHITE));
            }else{
                material = new Material(ColorAttribute.createDiffuse(Color.GRAY));
            }
            ChessModel model = null;
            if(piece instanceof Rook){
                model = new RookModel(rookModel,piece.getLocation(), material);
                model.transform.rotate(Vector3.Y, -90);
            }else if(piece instanceof Pawn){
                model = new PawnModel(pawnModel,piece.getLocation(), material);
            }else if(piece instanceof Knight){
                model = new KnightModel(knightModel,piece.getLocation(), material);
                model.transform.rotate(Vector3.Y, -90);
            }else if(piece instanceof Bishop){
                model = new BishopModel(bishopModel,piece.getLocation(), material);
                model.transform.rotate(Vector3.Y, -90);
            }else if(piece instanceof King){
                model = new KingModel(kingModel,piece.getLocation(), material);
            }else if(piece instanceof Queen){
                model= new QueenModel(queenModel,piece.getLocation(), material);
            }
            this.pieces.add(model);
            iter.remove();
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
        this.devStuff.add(new ModelInstance(arrowX));
        this.devStuff.add(new ModelInstance(arrowY));
        this.devStuff.add(new ModelInstance(arrowZ));
    }

    public void moveSelectedPieceToLocation(Location location) {
        this.selectedPiece.move(location);
    }

    public void selectPiece(ChessPiece piece) {
        if(this.selectedPiece != null){
            this.material3dManager.resetMaterial(this.selectedPiece);
        }
        ChessModel pieceModel = this.pieces.getByOriginalLocation(piece.getLocation());
        this.material3dManager.setSelectedMaterial(pieceModel);
        this.selectedPiece = pieceModel;
        highLightPossibleMoves(piece);
    }

    public void highLightPossibleMoves(ChessPiece piece) {
        // TODO implement Rule and moveset
        // List<ChessCell> possiblesMoves = piece.getPossibleMoves(piece,this.chessBoard3d);
        List<ChessCell> possiblesMoves = new ArrayList<>();
        if(possiblesMoves != null) {
            for (Iterator iter = possiblesMoves.iterator(); iter.hasNext(); ) {
                this.material3dManager.setSelectedMaterial((ChessModel) iter.next());
            }
        }
    }

    public void resetSelection() {
        if(selectedPiece != null){
            this.material3dManager.resetMaterial(this.selectedPiece);
        }
        this.selectedPiece = null;
        for (Iterator<ChessModel> iter = this.chessCells.iterator(); iter.hasNext();){
            ChessModel cell = iter.next();
            this.material3dManager.resetMaterial(cell);
        }
    }

    public ArrayList<ChessCellModel> getChessCells() {
        return chessCells;
    }

    public boolean isBoardIsLoading() {
        return boardIsLoading;
    }
    public AssetManager getAssets() {
        return assets;
    }

    public static Board3dManager getInstance(){
        if(INSTANCE == null){
            INSTANCE = new Board3dManager();
        }
        return INSTANCE;
    }

}
