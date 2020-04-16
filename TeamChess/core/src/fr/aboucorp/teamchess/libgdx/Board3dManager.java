package fr.aboucorp.teamchess.libgdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.aboucorp.entities.model.ChessCell;
import fr.aboucorp.entities.model.ChessColor;
import fr.aboucorp.entities.model.ChessPiece;
import fr.aboucorp.entities.model.GameElement;
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
import fr.aboucorp.teamchess.libgdx.utils.ChessModelList;

public class Board3dManager extends ApplicationAdapter {

    /** Chargeur de modele 3D */
    private ModelBatch modelBatch;
    /** Camera de la vue 3D */
    private PerspectiveCamera camera;
    /** Controller de la camera permettant à l'utilisateur de la faire pivoter */
    public CameraInputController camController;
    /** Environnement 3D pour l'affichage des modèles */
    private Environment environment;
    /** Ecouteur d'évènement au clic sur la fenêtre */
    private InputAdapter androidInputAdapter;
    private GestureDetector.GestureListener androidListener;
    private final ChessModelList chessCellModels;
    private final ChessModelList whitePieceModels;
    private final ChessModelList blackPieceModels;
    private final ArrayList<ChessPiece> loadingPieces;

    private final ArrayList devStuff;


    public static Map<Class,String> modelsPath =  new HashMap<>();

    private ModelBuilder modelBuilder;
    private AssetManager assets;
    private boolean boardIsLoading;
    private ChessModel selectedPiece;
    private Material3dManager material3dManager;

    public Board3dManager() {
        this.devStuff = new ArrayList();
        this.assets = new AssetManager(new InternalFileHandleResolver());
        this.material3dManager = Material3dManager.getInstance();
        this.whitePieceModels = new ChessModelList();
        this.blackPieceModels = new ChessModelList();
        this.chessCellModels = new ChessModelList();
        this.loadingPieces = new ArrayList();
    }

    @Override
    public void create() {
        this.modelBatch = new ModelBatch();
        this.initEnvironment();
        this.initCamera();
        InputMultiplexer multiplexer = new InputMultiplexer(new GestureDetector(androidListener), camController);
        multiplexer.addProcessor(androidInputAdapter);
        Gdx.input.setInputProcessor(multiplexer);
        setModelsPath();
        this.loadModels();
    }

    @Override
    public void render () {
        if (this.boardIsLoading && this.assets.update()) {
            this.doneLoading();
        }
        this.camController.update();
        Gdx.gl.glClearColor(135 / 255f, 206 / 255f, 235 / 255f, 1);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        this.modelBatch.begin(camera);
        for (ChessModel cellModels : this.chessCellModels) {
            modelBatch.render(cellModels, this.environment);
        }
        for (ChessModel piece : this.whitePieceModels) {
            modelBatch.render(piece, this.environment);
        }
        for (ChessModel piece : this.blackPieceModels) {
            modelBatch.render(piece, this.environment);
        }
        this.modelBatch.end();
    }

    @Override
    public void dispose () {
        modelBatch.dispose();
    }



    private void setModelsPath() {
        modelsPath.put(Knight.class,"data/knight.g3db");
        modelsPath.put(King.class,"data/king.g3db");
        modelsPath.put(Queen.class,"data/queen.g3db");
        modelsPath.put(Pawn.class,"data/pawn.g3db");
        modelsPath.put(Rook.class,"data/rook.g3db");
        modelsPath.put(Bishop.class,"data/bishop.g3db");

    }


    public void createCells(List<ChessCell> cells) {
        this.modelBuilder = new ModelBuilder();
        Model model = this.modelBuilder.createBox(1f, 0.1f, 1f,
                new Material(),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
        for (GameElement cell: cells) {
            Material material;
            if(cell.getChessColor() == ChessColor.BLACK){
                material = new Material(ColorAttribute.createDiffuse(Color.BLACK));
            }else{
                material = new Material(ColorAttribute.createDiffuse(Color.WHITE));
            }
            ChessCellModel cellModel = new ChessCellModel(model,cell.getLocation(),material);
            this.chessCellModels.add(cellModel);
        }
    }

    public void createPieces(List<ChessPiece> pieces){
        this.loadingPieces.addAll(pieces);
    }

    private void loadModels(){
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
            }else if(piece instanceof Pawn){
                model = new PawnModel(pawnModel,piece.getLocation(), material);
            }else if(piece instanceof Knight){
                model = new KnightModel(knightModel,piece.getLocation(), material);
                if(piece.getChessColor() == ChessColor.BLACK){
                    model.transform.rotate(Vector3.Y, 90);
                }else{
                    model.transform.rotate(Vector3.Y, -90);
                }
            }else if(piece instanceof Bishop){
                model = new BishopModel(bishopModel,piece.getLocation(), material);
                if(piece.getChessColor() == ChessColor.BLACK){
                    model.transform.rotate(Vector3.Y, 90);
                }else{
                    model.transform.rotate(Vector3.Y, -90);
                }
            }else if(piece instanceof King){
                model = new KingModel(kingModel,piece.getLocation(), material);
            }else if(piece instanceof Queen){
                model= new QueenModel(queenModel,piece.getLocation(), material);
            }
            if(piece.getChessColor() == ChessColor.BLACK){
                this.blackPieceModels.add(model);
            }else{
                this.whitePieceModels.add(model);
            }
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

    public void selectPiece(ChessPiece touched) {
        if(this.selectedPiece != null){
            this.material3dManager.resetMaterial(this.selectedPiece);
        }
        ChessModel pieceModel;
        if(touched.getChessColor() == ChessColor.BLACK){
            pieceModel = this.blackPieceModels.getByLocation(touched.getLocation());

        }else{
            pieceModel = this.whitePieceModels.getByLocation(touched.getLocation());
        }
        this.material3dManager.setSelectedMaterial(pieceModel);
        this.selectedPiece = pieceModel;
        highLightPossibleMoves(touched);
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
        for (Iterator<ChessModel> iter = this.chessCellModels.iterator(); iter.hasNext();){
            ChessModel cell = iter.next();
            this.material3dManager.resetMaterial(cell);
        }
    }

    public ChessModelList getWhitePieceModels() {
        return whitePieceModels;
    }

    public ChessModelList getBlackPieceModels() {
        return blackPieceModels;
    }

    public ArrayList<ChessModel> getChessCellModels() {
        return this.chessCellModels;
    }


    /**
     * Initialisation de l'environnement 3D
     */
    private void initEnvironment(){
        this.environment = new Environment();
        this.environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        this.environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    }

    /**
     * Initialisation de la caméra pour la vue 3D
     */
    private void initCamera(){
        this.camera = new PerspectiveCamera(30, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.position.set(7f, 16f, 7f);
        this.camera.lookAt(0,0,0);
        this.camera.near = 1f;
        this.camera.far = 300f;
        this.camera.update();
        this.camController = new CameraInputController(camera);
    }

    public PerspectiveCamera getCamera() {
        return camera;
    }


    public void setAndroidInputAdapter(InputAdapter androidInputAdapter) {
        this.androidInputAdapter = androidInputAdapter;
    }

    public void setAndroidListener(GestureDetector.GestureListener androidListener) {
        this.androidListener = androidListener;
    }

    public void movePieceIntoCell(ChessCell cell) {
        this.moveSelectedPieceToLocation(cell.getLocation());
    }

}
