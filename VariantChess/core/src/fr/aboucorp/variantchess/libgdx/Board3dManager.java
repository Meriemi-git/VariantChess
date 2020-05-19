package fr.aboucorp.variantchess.libgdx;

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
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
import java.util.stream.Stream;

import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.GameElement;
import fr.aboucorp.variantchess.entities.Location;
import fr.aboucorp.variantchess.entities.Piece;
import fr.aboucorp.variantchess.entities.Square;
import fr.aboucorp.variantchess.entities.enums.PieceId;
import fr.aboucorp.variantchess.entities.pieces.Bishop;
import fr.aboucorp.variantchess.entities.pieces.King;
import fr.aboucorp.variantchess.entities.pieces.Knight;
import fr.aboucorp.variantchess.entities.pieces.Pawn;
import fr.aboucorp.variantchess.entities.pieces.Queen;
import fr.aboucorp.variantchess.entities.pieces.Rook;
import fr.aboucorp.variantchess.entities.utils.SquareList;
import fr.aboucorp.variantchess.libgdx.models.ChessModel;
import fr.aboucorp.variantchess.libgdx.models.ChessPieceM;
import fr.aboucorp.variantchess.libgdx.models.ChessSquareM;
import fr.aboucorp.variantchess.libgdx.models.pieces.BishopM;
import fr.aboucorp.variantchess.libgdx.models.pieces.KingM;
import fr.aboucorp.variantchess.libgdx.models.pieces.KnightM;
import fr.aboucorp.variantchess.libgdx.models.pieces.PawnM;
import fr.aboucorp.variantchess.libgdx.models.pieces.QueenM;
import fr.aboucorp.variantchess.libgdx.models.pieces.RookM;
import fr.aboucorp.variantchess.libgdx.utils.ChessModelList;

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

    public boolean tacticalViewEnabled;

    private final ChessModelList chessSquareModels;
    private final ChessModelList whitePieceModels;
    private final ChessModelList blackPieceModels;
    private final ChessModelList whiteDeadPieceModels;
    private final ChessModelList blackDeadPieceModels;
    private final ArrayList<Piece> loadingPieces;
    private final ArrayList<ModelInstance> devStuff;
    public static Map<Class,String> assetPaths =  new HashMap<>();

    private ModelBuilder modelBuilder;
    private AssetManager assets;
    private boolean boardIsLoading;
    private fr.aboucorp.variantchess.libgdx.models.ChessModel selectedModel;
    private Material3dManager material3dManager;

    public Board3dManager() {
        this.devStuff = new ArrayList<ModelInstance>();
        this.assets = new AssetManager(new InternalFileHandleResolver());
        this.material3dManager = Material3dManager.getInstance();
        this.whitePieceModels = new fr.aboucorp.variantchess.libgdx.utils.ChessModelList();
        this.whiteDeadPieceModels = new fr.aboucorp.variantchess.libgdx.utils.ChessModelList();
        this.blackPieceModels = new fr.aboucorp.variantchess.libgdx.utils.ChessModelList();
        this.blackDeadPieceModels = new fr.aboucorp.variantchess.libgdx.utils.ChessModelList();
        this.chessSquareModels = new fr.aboucorp.variantchess.libgdx.utils.ChessModelList();
        this.loadingPieces = new ArrayList<Piece>();
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
        setPaths();
        this.loadModels();
        font = new BitmapFont();
        font.setColor(Color.RED);
        //createAxis();
    }

    @Override
    public void render () {
        if ((this.boardIsLoading || this.material3dManager.picturesLoading) && this.assets.update()) {
            this.doneLoading();
        }
        this.camController.update();
        Gdx.gl.glClearColor(42 / 255f, 79 / 255f, 110 / 255f, 1);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        this.modelBatch.begin(camera);
        for (fr.aboucorp.variantchess.libgdx.models.ChessModel piece : this.whitePieceModels) {
            if(piece.isVisible(this.camera) && !tacticalViewEnabled){
                modelBatch.render(piece, this.environment);
            }
        }

        for (fr.aboucorp.variantchess.libgdx.models.ChessModel piece : this.blackPieceModels) {
            if(piece.isVisible(this.camera) && !tacticalViewEnabled){
                modelBatch.render(piece, this.environment);
            }
        }
        for (fr.aboucorp.variantchess.libgdx.models.ChessModel deadPiece : this.whiteDeadPieceModels) {
            if(deadPiece.isVisible(this.camera) && !tacticalViewEnabled) {
                modelBatch.render(deadPiece, this.environment);
            }
        }
        for (fr.aboucorp.variantchess.libgdx.models.ChessModel deadPiece : this.blackDeadPieceModels) {
            if(deadPiece.isVisible(this.camera) && !tacticalViewEnabled) {
                modelBatch.render(deadPiece, this.environment);
            }
        }
        for (fr.aboucorp.variantchess.libgdx.models.ChessModel squareModel : this.chessSquareModels) {
            if(squareModel.isVisible(this.camera)){
                modelBatch.render(squareModel, this.environment);
            }
        }
        for (ModelInstance stuff : this.devStuff) {
            modelBatch.render(stuff, this.environment);
        }
        this.modelBatch.end();
        if(tacticalViewEnabled){
            displayPictures();
        }
        //renderNumbers();
    }

    private void displayPictures() {
        this.spriteBatch.begin();
        TextureAtlas piecesAtlas = this.assets.get(assetPaths.get(Piece.class),TextureAtlas.class);
        Stream.concat(this.whitePieceModels.stream(),this.blackPieceModels.stream()).forEach(piece -> {
            Matrix4 tmpMat4 = new Matrix4();
            Vector3 squareCenter = new Vector3(piece.getLocation().getX()+0.475f,piece.getLocation().getY(),piece.getLocation().getZ()-0.5f);
            Matrix4 textTransform = new Matrix4();
            textTransform.idt()
                    .translate(squareCenter)
                    .scl(0.04f)
                    .rotate(1, 0, 0, 90)
                    .rotate(0, 1, 0, 180);

            spriteBatch.setProjectionMatrix(tmpMat4.set(camera.combined).mul(textTransform));
            TextureAtlas.AtlasRegion region = piecesAtlas.findRegion(getRegionNameFromPieceId(((fr.aboucorp.variantchess.libgdx.models.ChessPieceM)piece).id));
            Sprite pieceSprite = new Sprite(region);
            spriteBatch.draw(pieceSprite, 0,0,24,24);
        });
        Stream.concat(this.whiteDeadPieceModels.stream(),this.blackDeadPieceModels.stream()).forEach(piece -> {
            Matrix4 tmpMat4 = new Matrix4();
            Vector3 squareCenter = new Vector3(piece.getLocation().getX()+0.475f,piece.getLocation().getY(),piece.getLocation().getZ()-0.5f);
            Matrix4 textTransform = new Matrix4();
            textTransform.idt()
                    .translate(squareCenter)
                    .scl(0.04f)
                    .rotate(1, 0, 0, 90)
                    .rotate(0, 1, 0, 180);

            spriteBatch.setProjectionMatrix(tmpMat4.set(camera.combined).mul(textTransform));
            TextureAtlas.AtlasRegion region = piecesAtlas.findRegion(getRegionNameFromPieceId(((fr.aboucorp.variantchess.libgdx.models.ChessPieceM)piece).id));
            Sprite pieceSprite = new Sprite(region);
            spriteBatch.draw(pieceSprite, 0,0,24,24);
        });
        this.spriteBatch.end();
    }

    private String getRegionNameFromPieceId(PieceId id) {
        switch(id){
            case WK :
                return "wk";
            case WQ :
                return "wq";
            case WLN :
                return "wrn";
            case WRN :
                return "wln";
            case WRR :
            case WLR :
                return "wr";
            case WRB :
            case WLB :
                return "wb";
            case WP1 :
            case WP2 :
            case WP3 :
            case WP4 :
            case WP5 :
            case WP6 :
            case WP7 :
            case WP8 :
                return "wp";
            case BK :
                return "bk";
            case BQ :
                return "bq";
            case BLN :
                return "brn";
            case BRN :
                return "bln";
            case BRR :
            case BLR :
                return "br";
            case BRB :
            case BLB :
                return "bb";
            case BP1 :
            case BP2 :
            case BP3 :
            case BP4 :
            case BP5 :
            case BP6 :
            case BP7 :
            case BP8 :
                return "bp";
            default:
                return "wp";
        }
    }

    private void renderNumbers() {
        this.spriteBatch.begin();
        for (fr.aboucorp.variantchess.libgdx.models.ChessModel squareModel : this.chessSquareModels) {
            Matrix4 tmpMat4 = new Matrix4();
            Vector3 squareCenter2 = new Vector3(squareModel.getLocation().getX(),squareModel.getLocation().getY(),squareModel.getLocation().getZ());
            Matrix4 textTransform = new Matrix4();
            textTransform.idt()
                    .translate(squareCenter2.add(new Vector3(0.25f,0f,0.25f)))
                    .rotate(1, 0, 0, 90)
                    .rotate(0, 1, 0, 180);

            spriteBatch.setProjectionMatrix(tmpMat4.set(camera.combined).mul(textTransform));
            font.draw(spriteBatch, ((fr.aboucorp.variantchess.libgdx.models.ChessSquareM)squareModel).getLabel(), 0, 0);
        }
        spriteBatch.end();
    }

    @Override
    public void dispose () {
        this.modelBatch.dispose();
        this.font.dispose();
    }

    private void setPaths() {
        assetPaths.put(Knight.class,"data/knight.g3db");
        assetPaths.put(King.class,"data/king.g3db");
        assetPaths.put(Queen.class,"data/queen.g3db");
        assetPaths.put(Pawn.class,"data/pawn.g3db");
        assetPaths.put(Rook.class,"data/rook.g3db");
        assetPaths.put(Bishop.class,"data/bishop.g3db");
        assetPaths.put(Piece.class,"data/pictures/pieces.atlas");
    }

    public void createSquares(List<Square> squares) {
        this.modelBuilder = new ModelBuilder();
        Model compositeModel = createModelFromParts();

        for (GameElement square: squares) {
            Material material;
            if (square.getChessColor() == ChessColor.BLACK) {
                material = new Material(ColorAttribute.createDiffuse(Color.GRAY));
            } else {
                material = new Material(ColorAttribute.createDiffuse(Color.WHITE));
            }
            fr.aboucorp.variantchess.libgdx.models.ChessSquareM squareModel = new ChessSquareM(compositeModel, square.getLocation(), material,((Square)square).getSquareLabel());
            this.chessSquareModels.add(squareModel);
        }

    }

    private Model createModelFromParts() {
        //Material defaultMat = new Material();
        int attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;
        this.modelBuilder.begin();
        modelBuilder.node().id = "root";
        MeshPartBuilder top = this.modelBuilder.part("top", GL20.GL_TRIANGLES, attr, new Material());
        top.rect(-0.5f, 0f, -0.5f, -0.5f, 0f, 0.5f, 0.5f, 0f, 0.5f, 0.5f, 0f, -0.5f, 0, 1, 0);
        MeshPartBuilder other = this.modelBuilder.part("others", GL20.GL_TRIANGLES, attr, new Material());
        other.rect(-0.5f, 0f, -0.5f, 0.5f, 0f, -0.5f, 0.5f, -0.1f, -0.5f, -0.5f, -0.1f, -0.5f, 0, 0, -1);
        other.rect(0.5f, 0f, -0.5f, 0.5f, 0f, 0.5f, 0.5f, -0.1f, 0.5f, 0.5f, -0.1f, -0.5f, 1, 0, 0);
        other.rect(0.5f, 0f, 0.5f,-0.5f, 0f, 0.5f,-0.5f, -0.1f, 0.5f, 0.5f, -0.1f, 0.5f,  0, 0, 1);
        other.rect(-0.5f, 0f, 0.5f,-0.5f, 0f, -0.5f,-0.5f, -0.1f, -0.5f, -0.5f, -0.1f, 0.5f,   1, 0, 0);
        other.rect(-0.5f, -0.1f, -0.5f, 0.5f, -0.1f, -0.5f, 0.5f, -0.1f, 0.5f, -0.5f, -0.1f, 0.5f, 0, -1, 0);
        return modelBuilder.end();
    }

    public void createPieces(List<Piece> pieces){
        this.loadingPieces.addAll(pieces);
        this.boardIsLoading = true;
    }

    private void loadModels(){
        for (Map.Entry<Class, String> entrySet : assetPaths.entrySet()) {
            if(entrySet.getKey().equals(Piece.class)){
                this.assets.load(entrySet.getValue(), TextureAtlas.class);
            }else{
                this.assets.load(entrySet.getValue(), Model.class);
            }
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

        for (Iterator<Piece> iter = this.loadingPieces.iterator(); iter.hasNext();) {
            Piece piece = iter.next();
            Material material;
            if(piece.getChessColor() == ChessColor.WHITE){
                material = new Material(ColorAttribute.createDiffuse(Color.WHITE));
            }else{
                material = new Material(ColorAttribute.createDiffuse(Color.GRAY));
            }
            ChessPieceM model = null;
            if(piece instanceof Rook){
                model = new RookM(rookModel,piece.getLocation(), material,piece.getPieceId());
            }else if(piece instanceof Pawn){
                model = new PawnM(pawnModel,piece.getLocation(), material,piece.getPieceId());
            }else if(piece instanceof Knight){
                model = new KnightM(knightModel,piece.getLocation(), material,piece.getPieceId());
                if(piece.getChessColor() == ChessColor.WHITE){
                    model.transform.rotate(Vector3.Y, -90);
                }else{
                    model.transform.rotate(Vector3.Y, 90);
                }
            }else if(piece instanceof Bishop){
                model = new BishopM(bishopModel,piece.getLocation(), material,piece.getPieceId());
                if(piece.getChessColor() == ChessColor.WHITE){
                    model.transform.rotate(Vector3.Y, -90);
                }else{
                    model.transform.rotate(Vector3.Y, 90);
                }
            }else if(piece instanceof King){
                model = new KingM(kingModel,piece.getLocation(), material,piece.getPieceId());
            }else if(piece instanceof Queen){
                model= new QueenM(queenModel,piece.getLocation(), material,piece.getPieceId());
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

    public void selectPiece(Piece touched) {
        if(this.selectedModel != null){
            this.material3dManager.resetMaterial(this.selectedModel);
        }
        fr.aboucorp.variantchess.libgdx.models.ChessModel pieceModel;
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
        for (Iterator<fr.aboucorp.variantchess.libgdx.models.ChessModel> iter = this.chessSquareModels.iterator(); iter.hasNext();){
            fr.aboucorp.variantchess.libgdx.models.ChessModel square = iter.next();
            this.material3dManager.resetMaterial(square);
        }
    }

    public fr.aboucorp.variantchess.libgdx.utils.ChessModelList getWhitePieceModels() {
        return whitePieceModels;
    }

    public fr.aboucorp.variantchess.libgdx.utils.ChessModelList getBlackPieceModels() {
        return blackPieceModels;
    }

    public ChessModelList getChessSquareModels() {
        return this.chessSquareModels;
    }

    /**
     * Initialisation de l'environnement 3D
     */
    private void initEnvironment(){
        this.environment = new Environment();
        this.environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 0.6f));
        this.environment.set(new ColorAttribute(ColorAttribute.Specular, 0.4f, 0.4f, 0.4f, 1f));

        this.environment.add(new DirectionalLight().set(0.6f, 0.6f, 0.6f, -1f, -0.8f, -0.2f));
    }

    /**
     * Initialisation de la caméra pour la vue 3D
     */
    private void initCamera(){
        this.camera = new PerspectiveCamera(30, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.position.set(3.5f, 15f,-5f);
        this.camera.lookAt(3.475f,0,3.475f);
        this.camera.near = 1f;
        this.camera.far = 300f;
        this.camera.update();
        this.camController = new CameraInputController(camera);
        this.camController.target.set(new Vector3(3.5f,0,3.5f));
    }

    public void moveSelectedPieceIntoSquare(Square square) {
        this.selectedModel.move(square.getLocation());
    }

    public void highlightEmptySquareFromLocation(Square square) {
        this.material3dManager.setSelectedMaterial(this.chessSquareModels.getByLocation(square.getLocation()));
    }

    public void highlightOccupiedSquareFromLocation(Square square) {
        this.material3dManager.setOccupiedMaterial(this.chessSquareModels.getByLocation(square.getLocation()));
        if(square.getPiece().getChessColor() == ChessColor.WHITE){
            this.material3dManager.setOccupiedMaterial(this.getWhitePieceModels().getByLocation(square.getPiece().getLocation()));
        }else{
            this.material3dManager.setOccupiedMaterial(this.getBlackPieceModels().getByLocation(square.getPiece().getLocation()));
        }
    }

    public void moveToSquare(Piece piece, Square square) {
        fr.aboucorp.variantchess.libgdx.models.ChessModel squareModel = this.getChessSquareModels().getByLocation(square.getLocation());
        fr.aboucorp.variantchess.libgdx.models.ChessModel pieceModel;
        if(piece.getChessColor() == ChessColor.WHITE){
            pieceModel = this.getWhitePieceModels().getByLocation(piece.getLocation());
        }else{
            pieceModel = this.getBlackPieceModels().getByLocation(piece.getLocation());
        }
        pieceModel.move(square.getLocation());
    }

    public ArrayList<fr.aboucorp.variantchess.libgdx.models.ChessModel> getSquareModelsFromPossibleMoves(SquareList possiblesMoves) {
        ArrayList<fr.aboucorp.variantchess.libgdx.models.ChessModel> possibleSquares = new ArrayList<>();
        if(possiblesMoves  != null) {
            for (fr.aboucorp.variantchess.libgdx.models.ChessModel squareModel : getChessSquareModels()) {
                for (Square square : possiblesMoves) {
                    if (squareModel.getLocation().equals(square.getLocation())) {
                        possibleSquares.add(squareModel);
                    }
                }
            }
        }
        return possibleSquares;
    }

    public void unHighlightSquares(SquareList possiblesMoves) {
        for (Square square: possiblesMoves) {
            this.material3dManager.resetMaterial(getChessSquareModels().getByLocation(square.getLocation()));
            if(square.getPiece() !=null){
                if(square.getPiece().getChessColor() == ChessColor.WHITE){
                    this.material3dManager.resetMaterial(getWhitePieceModels().getByLocation(square.getPiece().getLocation()));
                }else{
                    this.material3dManager.resetMaterial(getBlackPieceModels().getByLocation(square.getPiece().getLocation()));
                }
            }
        }
    }

    public void moveToEven(Piece piece) {
        ChessModel eatenPiece;
        if(piece.getChessColor() == ChessColor.WHITE){
            eatenPiece = getWhitePieceModels().removeByLocation(piece.getLocation());
            int xpos = 7 + (whiteDeadPieceModels.size() < 8 ? 1 : 2);
            int zpos = whiteDeadPieceModels.size() < 8 ? 7 - whiteDeadPieceModels.size() : 7 - (whiteDeadPieceModels.size() - 8);
            eatenPiece.move(new Location(xpos,0, zpos));
            this.whiteDeadPieceModels.add(eatenPiece);
        }else{
            eatenPiece = getBlackPieceModels().removeByLocation(piece.getLocation());
            int xpos = 0 - (blackDeadPieceModels.size() < 8 ? 1 : 2);
            int zpos = blackDeadPieceModels.size() < 8 ? blackDeadPieceModels.size() : blackDeadPieceModels.size() - 8;
            eatenPiece.move(new Location(xpos,0, zpos));
            this.blackDeadPieceModels.add(eatenPiece);
        }
        this.material3dManager.resetMaterial(eatenPiece);
        eatenPiece.transform.rotate(Vector3.Y, 180);
    }

    public void clearBoard() {
        this.blackPieceModels.clear();
        this.whitePieceModels.clear();
        this.whiteDeadPieceModels.clear();
        this.blackDeadPieceModels.clear();
    }

    public void toogleTacticalView(ChessColor turnColor) {
        camera.direction.set(0, 0, 0);
        this.camera.lookAt(3.5f,0,3.5f);
        camera.up.set(0, 1, 0);
        if(tacticalViewEnabled){
            this.camera.position.set(3.5f, 15f,-8f);
            this.camera.lookAt(3.5f,0,3.5f);
        }else{
            this.camera.position.set(new Vector3(3.475f,20,3.475f));
            this.camera.lookAt(3.5f,0,3.5f);
            this.camera.rotate(Vector3.Y, -45);
            if(turnColor == ChessColor.BLACK){
                this.camera.rotate(Vector3.Y, -180);
            }
        }
        this.camera.update();
        this.tacticalViewEnabled = !this.tacticalViewEnabled;
    }

    public void moveCameraOnNewTurn(ChessColor color){
        this.camera.direction.set(0, 0, 0);
        this.camera.up.set(0, 1, 0);
        if(tacticalViewEnabled){

            this.camera.position.set(3.5f, 20f,3.5f);
            this.camera.lookAt(new Vector3(3.5f,0,3.5f));
            if(color == ChessColor.WHITE){
                camera.update();
                this.camera.rotate(Vector3.Y,180);
            }
        }else {
            if (color == ChessColor.WHITE) {
                this.camera.position.set(3.5f, 15f, -5f);
            } else {
                this.camera.position.set(3.5f, 15f, 13f);
            }
        }
        this.camera.lookAt(3.5f,0,3.5f);
        this.camera.update();
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


}
