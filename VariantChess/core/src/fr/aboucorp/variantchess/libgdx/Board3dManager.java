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

import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.GameElement;
import fr.aboucorp.variantchess.entities.Location;
import fr.aboucorp.variantchess.entities.Piece;
import fr.aboucorp.variantchess.entities.Square;
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

    /**
     * Chargeur de modele 3D
     */
    private ModelBatch modelBatch;

    private SpriteBatch spriteBatch;

    /**
     * Camera de la vue 3D
     */
    private PerspectiveCamera camera;

    /**
     * Controller de la camera permettant à l'utilisateur de la faire pivoter
     */
    private CameraInputController camController;

    /**
     * Environnement 3D pour l'affichage des modèles
     */
    private Environment environment;

    /**
     * Ecouteur d'évènement au clic sur la fenêtre
     */
    private InputAdapter androidInputAdapter;

    private GestureDetector.GestureListener androidListener;

    private boolean tacticalViewEnabled;

    private final ChessModelList chessSquareModels;
    private final ChessModelList whitePieceModels;
    private final ChessModelList blackPieceModels;
    private final ChessModelList whiteDeadPieceModels;
    private final ChessModelList blackDeadPieceModels;
    private final ArrayList<Piece> loadingPieces;
    private final ArrayList<ModelInstance> devStuff;
    private static Map<Class, String> assetPaths = new HashMap<>();

    private ModelBuilder modelBuilder;
    private AssetManager assets;
    private boolean boardIsLoading;
    private ChessModel selectedModel;
    private Material3dManager material3dManager;
    private TextureAtlas piecesAtlas;

    public Board3dManager() {
        this.devStuff = new ArrayList<>();
        this.material3dManager =new Material3dManager();
        this.whitePieceModels = new ChessModelList();
        this.whiteDeadPieceModels = new ChessModelList();
        this.blackPieceModels = new ChessModelList();
        this.blackDeadPieceModels = new ChessModelList();
        this.chessSquareModels = new ChessModelList();
        this.loadingPieces = new ArrayList<>();
    }

    @Override
    public void create() {
        this.assets = new AssetManager(new InternalFileHandleResolver());
        this.modelBatch = new ModelBatch();
        this.spriteBatch = new SpriteBatch();
        this.modelBuilder = new ModelBuilder();
        this.initEnvironment();
        this.initCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        if(this.tacticalViewEnabled){
            this.setTacticalCamera();
        }
        this.setPaths();
        this.loadModels();
    }

    @Override
    public void render() {
        if ((this.boardIsLoading || this.material3dManager.picturesLoading) && this.assets.update()) {
            this.doneLoading();
        }
        this.camController.update();
        Gdx.gl.glClearColor(42 / 255f, 79 / 255f, 110 / 255f, 1);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        this.modelBatch.begin(this.camera);
        for (ChessModel piece : this.whitePieceModels) {
            if (piece.isVisible(this.camera) && !this.tacticalViewEnabled) {
                this.modelBatch.render(piece, this.environment);
            }
        }

        for (ChessModel piece : this.blackPieceModels) {
            if (piece.isVisible(this.camera) && !this.tacticalViewEnabled) {
                this.modelBatch.render(piece, this.environment);
            }
        }
        for (ChessModel deadPiece : this.whiteDeadPieceModels) {
            if (deadPiece.isVisible(this.camera) && !this.tacticalViewEnabled) {
                this.modelBatch.render(deadPiece, this.environment);
            }
        }
        for (ChessModel deadPiece : this.blackDeadPieceModels) {
            if (deadPiece.isVisible(this.camera) && !this.tacticalViewEnabled) {
                this.modelBatch.render(deadPiece, this.environment);
            }
        }
        for (ChessModel squareModel : this.chessSquareModels) {
            if (squareModel.isVisible(this.camera)) {
                this.modelBatch.render(squareModel, this.environment);
            }
        }
        for (ModelInstance stuff : this.devStuff) {
            this.modelBatch.render(stuff, this.environment);
        }
        this.modelBatch.end();
        if (this.tacticalViewEnabled) {
            this.displayPictures();
        }
    }

    private void displayPictures() {
        this.spriteBatch.begin();
        this.whitePieceModels.forEach(piece -> {
            Matrix4 tmpMat4 = new Matrix4();
            Vector3 squareCenter = new Vector3(piece.getLocation().getX() + 0.475f, piece.getLocation().getY(), piece.getLocation().getZ() - 0.5f);
            Matrix4 textTransform = new Matrix4();
            textTransform.idt()
                    .translate(squareCenter)
                    .scl(0.04f)
                    .rotate(1, 0, 0, 90)
                    .rotate(0, 1, 0, 180);

            this.spriteBatch.setProjectionMatrix(tmpMat4.set(this.camera.combined).mul(textTransform));
            TextureAtlas.AtlasRegion region = this.piecesAtlas.findRegion(this.material3dManager.getRegionNameFromPieceId(((ChessPieceM) piece).id));
            Sprite pieceSprite = new Sprite(region);
            this.spriteBatch.draw(pieceSprite, 0, 0, 24, 24);
        });

        this.blackPieceModels.forEach(piece -> {
            Matrix4 tmpMat4 = new Matrix4();
            Vector3 squareCenter = new Vector3(piece.getLocation().getX() + 0.475f, piece.getLocation().getY(), piece.getLocation().getZ() - 0.5f);
            Matrix4 textTransform = new Matrix4();
            textTransform.idt()
                    .translate(squareCenter)
                    .scl(0.04f)
                    .rotate(1, 0, 0, 90)
                    .rotate(0, 1, 0, 180);

            this.spriteBatch.setProjectionMatrix(tmpMat4.set(this.camera.combined).mul(textTransform));
            TextureAtlas.AtlasRegion region = this.piecesAtlas.findRegion(this.material3dManager.getRegionNameFromPieceId(((ChessPieceM) piece).id));
            Sprite pieceSprite = new Sprite(region);
            this.spriteBatch.draw(pieceSprite, 0, 0, 24, 24);
        });
        this.whiteDeadPieceModels.forEach(piece -> {
            Matrix4 tmpMat4 = new Matrix4();
            Vector3 squareCenter = new Vector3(piece.getLocation().getX() + 0.475f, piece.getLocation().getY(), piece.getLocation().getZ() - 0.5f);
            Matrix4 textTransform = new Matrix4();
            textTransform.idt()
                    .translate(squareCenter)
                    .scl(0.04f)
                    .rotate(1, 0, 0, 90)
                    .rotate(0, 1, 0, 180);

            this.spriteBatch.setProjectionMatrix(tmpMat4.set(this.camera.combined).mul(textTransform));
            TextureAtlas.AtlasRegion region = this.piecesAtlas.findRegion(this.material3dManager.getRegionNameFromPieceId(((ChessPieceM) piece).id));
            Sprite pieceSprite = new Sprite(region);
            this.spriteBatch.draw(pieceSprite, 0, 0, 24, 24);
        });
        this.blackDeadPieceModels.forEach(piece -> {
            Matrix4 tmpMat4 = new Matrix4();
            Vector3 squareCenter = new Vector3(piece.getLocation().getX() + 0.475f, piece.getLocation().getY(), piece.getLocation().getZ() - 0.5f);
            Matrix4 textTransform = new Matrix4();
            textTransform.idt()
                    .translate(squareCenter)
                    .scl(0.04f)
                    .rotate(1, 0, 0, 90)
                    .rotate(0, 1, 0, 180);

            this.spriteBatch.setProjectionMatrix(tmpMat4.set(this.camera.combined).mul(textTransform));
            TextureAtlas.AtlasRegion region = this.piecesAtlas.findRegion(this.material3dManager.getRegionNameFromPieceId(((ChessPieceM) piece).id));
            Sprite pieceSprite = new Sprite(region);
            this.spriteBatch.draw(pieceSprite, 0, 0, 24, 24);
        });
        this.spriteBatch.end();
    }


private void setPaths() {
        assetPaths.put(Knight.class, "data/knight.g3db");
        assetPaths.put(King.class, "data/king.g3db");
        assetPaths.put(Queen.class, "data/queen.g3db");
        assetPaths.put(Pawn.class, "data/pawn.g3db");
        assetPaths.put(Rook.class, "data/rook.g3db");
        assetPaths.put(Bishop.class, "data/bishop.g3db");
        assetPaths.put(Piece.class, "data/pictures/pieces.atlas");
    }

    public void createSquares(List<Square> squares) {
        this.modelBuilder = new ModelBuilder();
        Model compositeModel = this.createModelFromParts();

        for (GameElement square : squares) {
            Material material;
            if (square.getChessColor() == ChessColor.BLACK) {
                material = new Material(ColorAttribute.createDiffuse(Color.GRAY));
            } else {
                material = new Material(ColorAttribute.createDiffuse(Color.WHITE));
            }
            ChessSquareM squareModel = new ChessSquareM(compositeModel, square.getLocation(), material, ((Square) square).getSquareLabel());
            this.chessSquareModels.add(squareModel);
        }

    }

    private Model createModelFromParts() {
        int attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;
        this.modelBuilder.begin();
        this.modelBuilder.node().id = "root";
        MeshPartBuilder top = this.modelBuilder.part("top", GL20.GL_TRIANGLES, attr, new Material());
        top.rect(-0.5f, 0f, -0.5f, -0.5f, 0f, 0.5f, 0.5f, 0f, 0.5f, 0.5f, 0f, -0.5f, 0, 1, 0);
        MeshPartBuilder other = this.modelBuilder.part("others", GL20.GL_TRIANGLES, attr, new Material());
        other.rect(-0.5f, 0f, -0.5f, 0.5f, 0f, -0.5f, 0.5f, -0.1f, -0.5f, -0.5f, -0.1f, -0.5f, 0, 0, -1);
        other.rect(0.5f, 0f, -0.5f, 0.5f, 0f, 0.5f, 0.5f, -0.1f, 0.5f, 0.5f, -0.1f, -0.5f, 1, 0, 0);
        other.rect(0.5f, 0f, 0.5f, -0.5f, 0f, 0.5f, -0.5f, -0.1f, 0.5f, 0.5f, -0.1f, 0.5f, 0, 0, 1);
        other.rect(-0.5f, 0f, 0.5f, -0.5f, 0f, -0.5f, -0.5f, -0.1f, -0.5f, -0.5f, -0.1f, 0.5f, 1, 0, 0);
        other.rect(-0.5f, -0.1f, -0.5f, 0.5f, -0.1f, -0.5f, 0.5f, -0.1f, 0.5f, -0.5f, -0.1f, 0.5f, 0, -1, 0);
        return this.modelBuilder.end();
    }

    public void createPieces(List<Piece> pieces) {
        this.loadingPieces.addAll(pieces);
        this.boardIsLoading = true;
    }

    private void loadModels() {
        for (Map.Entry<Class, String> entrySet : assetPaths.entrySet()) {
            if (entrySet.getKey().equals(Piece.class)) {
                this.assets.load(entrySet.getValue(), TextureAtlas.class);
            } else {
                this.assets.load(entrySet.getValue(), Model.class);
            }
        }

        this.boardIsLoading = true;
    }

    private void doneLoading() {
        try {
            Model knightModel = this.assets.get("data/knight.g3db", Model.class);
            Model bishopModel = this.assets.get("data/bishop.g3db", Model.class);
            Model pawnModel = this.assets.get("data/pawn.g3db", Model.class);
            Model queenModel = this.assets.get("data/queen.g3db", Model.class);
            Model kingModel = this.assets.get("data/king.g3db", Model.class);
            Model rookModel = this.assets.get("data/rook.g3db", Model.class);
            this.piecesAtlas = this.assets.get(assetPaths.get(Piece.class), TextureAtlas.class);
            for (Iterator<Piece> iter = this.loadingPieces.iterator(); iter.hasNext(); ) {
                Piece piece = iter.next();
                Material material;
                if (piece.getChessColor() == ChessColor.WHITE) {
                    material = new Material(ColorAttribute.createDiffuse(Color.WHITE));
                } else {
                    material = new Material(ColorAttribute.createDiffuse(Color.GRAY));
                }
                ChessPieceM model = null;
                if (piece instanceof Rook) {
                    model = new RookM(rookModel, piece.getLocation(), material, piece.getPieceId());
                } else if (piece instanceof Pawn) {
                    model = new PawnM(pawnModel, piece.getLocation(), material, piece.getPieceId());
                } else if (piece instanceof Knight) {
                    model = new KnightM(knightModel, piece.getLocation(), material, piece.getPieceId());
                    if (piece.getChessColor() == ChessColor.WHITE) {
                        model.transform.rotate(Vector3.Y, -90);
                    } else {
                        model.transform.rotate(Vector3.Y, 90);
                    }
                } else if (piece instanceof Bishop) {
                    model = new BishopM(bishopModel, piece.getLocation(), material, piece.getPieceId());
                    if (piece.getChessColor() == ChessColor.WHITE) {
                        model.transform.rotate(Vector3.Y, -90);
                    } else {
                        model.transform.rotate(Vector3.Y, 90);
                    }
                } else if (piece instanceof King) {
                    model = new KingM(kingModel, piece.getLocation(), material, piece.getPieceId());
                } else if (piece instanceof Queen) {
                    model = new QueenM(queenModel, piece.getLocation(), material, piece.getPieceId());
                }
                if (piece.getChessColor() == ChessColor.BLACK) {
                    this.blackPieceModels.add(model);
                } else {
                    this.whitePieceModels.add(model);
                }
                iter.remove();
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        this.boardIsLoading = false;
    }

    public void selectPiece(Piece touched) {
        if (this.selectedModel != null) {
            this.material3dManager.resetMaterial(this.selectedModel);
        }
        ChessModel pieceModel;
        if (touched.getChessColor() == ChessColor.BLACK) {
            pieceModel = this.blackPieceModels.getByLocation(touched.getLocation());

        } else {
            pieceModel = this.whitePieceModels.getByLocation(touched.getLocation());
        }
        this.material3dManager.setSelectedMaterial(pieceModel);
        this.selectedModel = pieceModel;
    }

    public void resetSelection() {
        if (this.selectedModel != null) {
            this.material3dManager.resetMaterial(this.selectedModel);
        }
        this.selectedModel = null;
        for (Iterator<ChessModel> iter = this.chessSquareModels.iterator(); iter.hasNext(); ) {
            ChessModel square = iter.next();
            this.material3dManager.resetMaterial(square);
        }
    }

    public fr.aboucorp.variantchess.libgdx.utils.ChessModelList getWhitePieceModels() {
        return this.whitePieceModels;
    }

    public fr.aboucorp.variantchess.libgdx.utils.ChessModelList getBlackPieceModels() {
        return this.blackPieceModels;
    }

    private ChessModelList getChessSquareModels() {
        return this.chessSquareModels;
    }

    /**
     * Initialisation de l'environnement 3D
     */
    private void initEnvironment() {
        this.environment = new Environment();
        this.environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 0.6f));
        this.environment.set(new ColorAttribute(ColorAttribute.Specular, 0.4f, 0.4f, 0.4f, 1f));

        this.environment.add(new DirectionalLight().set(0.6f, 0.6f, 0.6f, -1f, -0.8f, -0.2f));
    }

    /**
     * Initialisation de la caméra pour la vue 3D
     */
    private void initCamera(int width , int height) {
        this.camera = new PerspectiveCamera(30, width, height);
        this.camera.position.set(3.5f, 15f, -5f);
        this.camera.lookAt(3.475f, 0, 3.475f);
        this.camera.near = 1f;
        this.camera.far = 300f;
        this.camera.update();
        this.camController = new CameraInputController(this.camera);
        this.camController.target.set(new Vector3(3.5f, 0, 3.5f));
        InputMultiplexer multiplexer = new InputMultiplexer(new GestureDetector(this.androidListener), this.camController);
        multiplexer.addProcessor(this.androidInputAdapter);
        Gdx.input.setInputProcessor(multiplexer);
    }

    public void moveSelectedPieceIntoSquare(Square square) {
        this.selectedModel.move(square.getLocation());
    }

    public void highlightEmptySquareFromLocation(Square square) {
        this.material3dManager.setSelectedMaterial(this.chessSquareModels.getByLocation(square.getLocation()));
    }

    public void highlightOccupiedSquareFromLocation(Square square) {
        this.material3dManager.setOccupiedMaterial(this.chessSquareModels.getByLocation(square.getLocation()));
        if (square.getPiece().getChessColor() == ChessColor.WHITE) {
            this.material3dManager.setOccupiedMaterial(this.getWhitePieceModels().getByLocation(square.getPiece().getLocation()));
        } else {
            this.material3dManager.setOccupiedMaterial(this.getBlackPieceModels().getByLocation(square.getPiece().getLocation()));
        }
    }

    public void moveToSquare(Piece piece, Square square) {
        ChessModel pieceModel;
        if (piece.getChessColor() == ChessColor.WHITE) {
            pieceModel = this.getWhitePieceModels().getByLocation(piece.getLocation());
        } else {
            pieceModel = this.getBlackPieceModels().getByLocation(piece.getLocation());
        }
        pieceModel.move(square.getLocation());
    }

    public ChessModelList getSquareModelsFromPossibleMoves(SquareList possiblesMoves) {
        ChessModelList possibleSquares = new ChessModelList();
        if (possiblesMoves != null) {
            for (ChessModel squareModel : this.getChessSquareModels()) {
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
        for (Square square : possiblesMoves) {
            this.material3dManager.resetMaterial(this.getChessSquareModels().getByLocation(square.getLocation()));
            if (square.getPiece() != null) {
                if (square.getPiece().getChessColor() == ChessColor.WHITE) {
                    this.material3dManager.resetMaterial(this.getWhitePieceModels().getByLocation(square.getPiece().getLocation()));
                } else {
                    this.material3dManager.resetMaterial(this.getBlackPieceModels().getByLocation(square.getPiece().getLocation()));
                }
            }
        }
    }

    public void moveToEven(Piece piece) {
        ChessModel eatenPiece;
        if (piece.getChessColor() == ChessColor.WHITE) {
            eatenPiece = this.getWhitePieceModels().removeByLocation(piece.getLocation());
            int xpos = 7 + (this.whiteDeadPieceModels.size < 8 ? 1 : 2);
            int zpos = this.whiteDeadPieceModels.size < 8 ? 7 - this.whiteDeadPieceModels.size : 7 - (this.whiteDeadPieceModels.size - 8);
            eatenPiece.move(new Location(xpos, 0, zpos));
            this.whiteDeadPieceModels.add(eatenPiece);
        } else {
            eatenPiece = this.getBlackPieceModels().removeByLocation(piece.getLocation());
            int xpos = 0 - (this.blackDeadPieceModels.size < 8 ? 1 : 2);
            int zpos = this.blackDeadPieceModels.size < 8 ? this.blackDeadPieceModels.size : this.blackDeadPieceModels.size - 8;
            eatenPiece.move(new Location(xpos, 0, zpos));
            this.blackDeadPieceModels.add(eatenPiece);
        }
        this.material3dManager.resetMaterial(eatenPiece);
        eatenPiece.transform.rotate(Vector3.Y, 180);
    }

    public void clearBoard() {
        this.selectedModel = null;
        this.blackPieceModels.clear();
        this.whitePieceModels.clear();
        this.whiteDeadPieceModels.clear();
        this.blackDeadPieceModels.clear();
        this.chessSquareModels.clear();
        this.devStuff.clear();
    }

    public void toogleTacticalView() {
        this.tacticalViewEnabled = !this.tacticalViewEnabled;
        if (this.tacticalViewEnabled) {
            this.setTacticalCamera();
        } else {
            this.set3DCamera();
        }
    }

    private void set3DCamera() {
        this.camera.direction.set(0, 0, 0);
        this.camera.up.set(0, 1, 0);
        this.camera.position.set(3.5f, 15f, -8f);
        this.camera.lookAt(3.5f, 0, 3.5f);
        this.camera.update();
    }

    private void setTacticalCamera() {
        this.camera.direction.set(0, 0, 0);
        this.camera.lookAt(3.5f, 0, 3.5f);
        this.camera.up.set(0, 1, 0);
        this.camera.position.set(new Vector3(3.475f, 20, 3.475f));
        this.camera.lookAt(3.5f, 0, 3.5f);
        this.camera.rotate(Vector3.Y, -45);
            /*if (turnColor == ChessColor.BLACK) {
                this.camera.rotate(Vector3.Y, -180);
            }*/
        this.camera.update();
    }

    public void moveCameraOnNewTurn(ChessColor color) {
        this.camera.direction.set(0, 0, 0);
        this.camera.up.set(0, 1, 0);
        if (this.tacticalViewEnabled) {

            this.camera.position.set(3.5f, 20f, 3.5f);
            this.camera.lookAt(new Vector3(3.5f, 0, 3.5f));
            if (color == ChessColor.WHITE) {
                this.camera.update();
                this.camera.rotate(Vector3.Y, 180);
            }
        } else {
            if (color == ChessColor.WHITE) {
                this.camera.position.set(3.5f, 15f, -5f);
            } else {
                this.camera.position.set(3.5f, 15f, 13f);
            }
        }
        this.camera.lookAt(3.5f, 0, 3.5f);
        this.camera.update();
    }

    public PerspectiveCamera getCamera() {
        return this.camera;
    }

    public void setAndroidInputAdapter(InputAdapter androidInputAdapter) {
        this.androidInputAdapter = androidInputAdapter;
    }

    public void setAndroidListener(GestureDetector.GestureListener androidListener) {
        this.androidListener = androidListener;
    }

    @Override
    public void resize(int width, int height) {
        this.initCamera(width, height);
    }

    @Override
    public void dispose() {
        this.modelBatch.dispose();
        this.spriteBatch.dispose();
        this.assets.dispose();
        Gdx.app.log("fr.aboucorp.variantchess","dispose Board3D");
    }

    public boolean isTacticalViewEnabled() {
        return this.tacticalViewEnabled;
    }

    public void setTacticalViewEnabled(boolean tacticalViewEnabled) {
        this.tacticalViewEnabled = tacticalViewEnabled;
    }

    public void exit() {
        Gdx.app.exit();
        Gdx.app.log("fr.aboucorp.variantchess","Exit VariantChess app");
    }
}
