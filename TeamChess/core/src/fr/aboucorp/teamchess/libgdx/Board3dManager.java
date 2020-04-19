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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.aboucorp.teamchess.entities.model.ChessCell;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.ChessPiece;
import fr.aboucorp.teamchess.entities.model.GameElement;
import fr.aboucorp.teamchess.entities.model.Location;
import fr.aboucorp.teamchess.entities.model.pieces.Bishop;
import fr.aboucorp.teamchess.entities.model.pieces.King;
import fr.aboucorp.teamchess.entities.model.pieces.Knight;
import fr.aboucorp.teamchess.entities.model.pieces.Pawn;
import fr.aboucorp.teamchess.entities.model.pieces.Queen;
import fr.aboucorp.teamchess.entities.model.pieces.Rook;
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
    private BitmapFont font;
    private SpriteBatch spriteBatch;
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


    private final ArrayList<ModelInstance> devStuff;


    public static Map<Class,String> modelsPath =  new HashMap<>();

    private ModelBuilder modelBuilder;
    private AssetManager assets;
    private boolean boardIsLoading;
    private ChessModel selectedModel;
    private Material3dManager material3dManager;



    public Board3dManager() {
        this.devStuff = new ArrayList<ModelInstance>();
        this.assets = new AssetManager(new InternalFileHandleResolver());
        this.material3dManager = Material3dManager.getInstance();
        this.whitePieceModels = new ChessModelList();
        this.blackPieceModels = new ChessModelList();
        this.chessCellModels = new ChessModelList();
        this.loadingPieces = new ArrayList<ChessPiece>();

    }

    @Override
    public void create() {
        this.modelBatch = new ModelBatch();
        this.spriteBatch = new SpriteBatch();
        this.modelBuilder = new ModelBuilder();
        this.initEnvironment();
        this.initCamera();
        InputMultiplexer multiplexer = new InputMultiplexer(new GestureDetector(androidListener), camController);
        multiplexer.addProcessor(androidInputAdapter);
        Gdx.input.setInputProcessor(multiplexer);
        setModelsPath();
        this.loadModels();
        font = new BitmapFont();
        font.setColor(Color.RED);
        createAxis();
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
        for (ChessModel cellModel : this.chessCellModels) {
            modelBatch.render(cellModel, this.environment);
        }
        for (ChessModel piece : this.whitePieceModels) {
            modelBatch.render(piece, this.environment);
        }
        for (ChessModel piece : this.blackPieceModels) {
            modelBatch.render(piece, this.environment);
        }
        for (ModelInstance stuff : this.devStuff) {
            modelBatch.render(stuff, this.environment);
        }
        this.modelBatch.end();

        this.spriteBatch.begin();
        for (ChessModel cellModel : this.chessCellModels) {
            Matrix4 tmpMat4 = new Matrix4();
            Vector3 cellCenter2 = new Vector3(cellModel.getLocation().getX(),cellModel.getLocation().getY(),cellModel.getLocation().getZ());
            Matrix4 textTransform = new Matrix4();
            textTransform.idt()
                    .translate(cellCenter2.add(new Vector3(0.25f,0f,0.25f)))
                    .scl(0.04f)
                    .rotate(1, 0, 0, 90)
                    .rotate(0, 1, 0, 180);

            spriteBatch.setProjectionMatrix(tmpMat4.set(camera.combined).mul(textTransform));
            font.draw(spriteBatch, ((ChessCellModel)cellModel).getLabel(), 0, 0);
        }

        spriteBatch.end();

    }

    @Override
    public void dispose () {
        this.modelBatch.dispose();
        this.font.dispose();
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
        Model compositeModel = createModelFromParts();

        for (GameElement cell: cells) {
            Material material;
            if (cell.getChessColor() == ChessColor.BLACK) {
                material = new Material(ColorAttribute.createDiffuse(Color.BLACK));
            } else {
                material = new Material(ColorAttribute.createDiffuse(Color.WHITE));
            }
            ChessCellModel cellModel = new ChessCellModel(compositeModel, cell.getLocation(), material,((ChessCell)cell).getCellLabel());
            this.chessCellModels.add(cellModel);
        }

    }

    private Model createModelFromParts() {
        Material defaultMat = new Material();
        int attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;
        this.modelBuilder.begin();
        MeshPartBuilder top = this.modelBuilder.part("top", GL20.GL_TRIANGLES, attr, defaultMat);
        top.rect(-0.5f, 0f, -0.5f, -0.5f, 0f, 0.5f, 0.5f, 0f, 0.5f, 0.5f, 0f, -0.5f, 0, 1, 0);
        MeshPartBuilder other = this.modelBuilder.part("others", GL20.GL_TRIANGLES, attr, defaultMat);
        other.rect(-0.5f, 0f, -0.5f, 0.5f, 0f, -0.5f, 0.5f, -0.1f, -0.5f, -0.5f, -0.1f, -0.5f, 0, 0, -1);
        other.rect(0.5f, 0f, -0.5f, 0.5f, 0f, 0.5f, 0.5f, -0.1f, 0.5f, 0.5f, -0.1f, -0.5f, 1, 0, 0);
        other.rect(0.5f, 0f, 0.5f,-0.5f, 0f, 0.5f,-0.5f, -0.1f, 0.5f, 0.5f, -0.1f, 0.5f,  0, 0, 1);
        other.rect(-0.5f, 0f, 0.5f,-0.5f, 0f, -0.5f,-0.5f, -0.1f, -0.5f, -0.5f, -0.1f, 0.5f,   1, 0, 0);
        other.rect(-0.5f, -0.1f, -0.5f, 0.5f, -0.1f, -0.5f, 0.5f, -0.1f, 0.5f, -0.5f, -0.1f, 0.5f, 0, -1, 0);
        return modelBuilder.end();
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
            if(piece.getChessColor() == ChessColor.WHITE){
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
                if(piece.getChessColor() == ChessColor.WHITE){
                    model.transform.rotate(Vector3.Y, -90);
                }else{
                    model.transform.rotate(Vector3.Y, 90);
                }
            }else if(piece instanceof Bishop){
                model = new BishopModel(bishopModel,piece.getLocation(), material);
                if(piece.getChessColor() == ChessColor.WHITE){
                    model.transform.rotate(Vector3.Y, -90);
                }else{
                    model.transform.rotate(Vector3.Y, 90);
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

    public void moveSelectedModelToLocation(Location location) {
        this.selectedModel.move(location);
    }

    public void selectPiece(ChessPiece touched) {
        if(this.selectedModel != null){
            this.material3dManager.resetMaterial(this.selectedModel);
        }
        ChessModel pieceModel;
        if(touched.getChessColor() == ChessColor.BLACK){
            pieceModel = this.blackPieceModels.getByLocation(touched.getLocation());

        }else{
            pieceModel = this.whitePieceModels.getByLocation(touched.getLocation());
        }
        this.material3dManager.setSelectedMaterial(pieceModel);
        this.selectedModel = pieceModel;
    }

    public void resetSelection() {
        if(selectedModel != null){
            this.material3dManager.resetMaterial(this.selectedModel);
        }
        this.selectedModel = null;
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
        this.camera.position.set(3.5f, 16f,-10f);
        this.camera.lookAt(3.5f,0,3.5f);
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
        this.moveSelectedModelToLocation(cell.getLocation());
    }

    public void highlightedCellFromLocation(Location location) {
        this.material3dManager.setSelectedMaterial(this.chessCellModels.getByLocation(location));
    }
}
