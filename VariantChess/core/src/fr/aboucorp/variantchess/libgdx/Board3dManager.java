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
import fr.aboucorp.variantchess.entities.enums.PieceId;
import fr.aboucorp.variantchess.entities.pieces.Bishop;
import fr.aboucorp.variantchess.entities.pieces.King;
import fr.aboucorp.variantchess.entities.pieces.Knight;
import fr.aboucorp.variantchess.entities.pieces.Pawn;
import fr.aboucorp.variantchess.entities.pieces.Queen;
import fr.aboucorp.variantchess.entities.pieces.Rook;
import fr.aboucorp.variantchess.entities.utils.SquareList;
import fr.aboucorp.variantchess.libgdx.models.ChessPieceM;
import fr.aboucorp.variantchess.libgdx.models.ChessSquareM;
import fr.aboucorp.variantchess.libgdx.models.GraphicsGameElement;
import fr.aboucorp.variantchess.libgdx.models.pieces.BishopM;
import fr.aboucorp.variantchess.libgdx.models.pieces.KingM;
import fr.aboucorp.variantchess.libgdx.models.pieces.KnightM;
import fr.aboucorp.variantchess.libgdx.models.pieces.PawnM;
import fr.aboucorp.variantchess.libgdx.models.pieces.QueenM;
import fr.aboucorp.variantchess.libgdx.models.pieces.RookM;
import fr.aboucorp.variantchess.libgdx.utils.GraphicGameArray;

public class Board3dManager extends ApplicationAdapter {

    private final GraphicGameArray chessSquareModels;
    private final GraphicGameArray whitePieceModels;
    private final GraphicGameArray blackPieceModels;
    private final GraphicGameArray whiteDeadPieceModels;
    private final GraphicGameArray blackDeadPieceModels;
    private final ArrayList<Piece> loadingPieces;
    private Map<Class, String> assetPaths;
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
    private ModelBuilder modelBuilder;
    private AssetManager assets;
    private boolean boardIsLoading;
    private GraphicsGameElement selectedModel;
    private Material3dManager material3dManager;
    private TextureAtlas piecesAtlas;

    public Board3dManager() {
        this.material3dManager = new Material3dManager();
        this.whitePieceModels = new GraphicGameArray();
        this.whiteDeadPieceModels = new GraphicGameArray();
        this.blackPieceModels = new GraphicGameArray();
        this.blackDeadPieceModels = new GraphicGameArray();
        this.chessSquareModels = new GraphicGameArray();
        this.loadingPieces = new ArrayList<>();
        this.assetPaths = new HashMap<>();
    }

    @Override
    public void create() {
        if (this.assets == null) {
            this.assets = new AssetManager(new InternalFileHandleResolver());
        }
        if (this.modelBatch == null) {
            this.modelBatch = new ModelBatch();
        }
        if (this.spriteBatch == null) {
            this.spriteBatch = new SpriteBatch();
        }
        if (this.modelBuilder == null) {
            this.modelBuilder = new ModelBuilder();
        }
        this.initEnvironment();
        this.initCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if (this.tacticalViewEnabled) {
            this.setTacticalCamera();
        }
        if (this.assetPaths.size() == 0) {
            this.setPaths();
            this.loadModels();
        }

    }

    @Override
    public void resize(int width, int height) {
        this.initCamera(width, height);
    }

    @Override
    public void render() {
        if (this.boardIsLoading && this.assets.update()) {
            this.doneLoading();
        }
        if (!this.boardIsLoading) {
            this.camController.update();
            Gdx.gl.glClearColor(42 / 255f, 79 / 255f, 110 / 255f, 1);
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            this.modelBatch.begin(this.camera);
            for (GraphicsGameElement squareModel : this.chessSquareModels) {
                if (squareModel.isVisible(this.camera, this.tacticalViewEnabled)) {
                    this.modelBatch.render(squareModel.getModel3d(), this.environment);
                }
            }
            if (this.tacticalViewEnabled) {
                this.modelBatch.end();
                this.spriteBatch.begin();
            }
            for (GraphicsGameElement piece : this.whitePieceModels) {
                if (piece.isVisible(this.camera, this.tacticalViewEnabled)) {
                    if (this.tacticalViewEnabled) {
                        this.spriteBatch.setProjectionMatrix(this.calculateProjectionMatrix(piece.getLocation()));
                        this.spriteBatch.draw(piece.getModel2d(), 0, 0, 24, 24);
                    } else {
                        this.modelBatch.render(piece.getModel3d(), this.environment);
                    }
                }
            }
            for (GraphicsGameElement piece : this.blackPieceModels) {
                if (piece.isVisible(this.camera, this.tacticalViewEnabled)) {
                    if (this.tacticalViewEnabled) {
                        this.spriteBatch.setProjectionMatrix(this.calculateProjectionMatrix(piece.getLocation()));
                        this.spriteBatch.draw(piece.getModel2d(), 0, 0, 24, 24);
                    } else {
                        this.modelBatch.render(piece.getModel3d(), this.environment);
                    }
                }
            }
            for (GraphicsGameElement deadPiece : this.whiteDeadPieceModels) {
                if (deadPiece.isVisible(this.camera, this.tacticalViewEnabled)) {
                    if (this.tacticalViewEnabled) {
                        //this.spriteBatch.setProjectionMatrix(deadPiece.getTransformation());
                        //this.spriteBatch.draw(deadPiece.getModel2d(), 0, 0, 24, 24);
                    } else {
                        this.modelBatch.render(deadPiece.getModel3d(), this.environment);
                    }
                }
            }
            for (GraphicsGameElement deadPiece : this.blackDeadPieceModels) {
                if (deadPiece.isVisible(this.camera, this.tacticalViewEnabled)) {
                    if (this.tacticalViewEnabled) {
                        //this.spriteBatch.setProjectionMatrix(deadPiece.getTransformation());
                        //this.spriteBatch.draw(deadPiece.getModel2d(), 0, 0, 24, 24);
                    } else {
                        this.modelBatch.render(deadPiece.getModel3d(), this.environment);
                    }
                }
            }

            if (this.tacticalViewEnabled) {
                this.spriteBatch.end();
            } else {
                this.modelBatch.end();
            }
        }
    }

    @Override
    public void dispose() {
        this.clearBoard();
        this.modelBatch.dispose();
        this.spriteBatch.dispose();
        this.assets.dispose();
        this.assetPaths.clear();
        Gdx.app.log("fr.aboucorp.variantchess", "dispose Board3D");
    }

    public void clearBoard() {
        this.selectedModel = null;
        this.blackPieceModels.clear();
        this.whitePieceModels.clear();
        this.whiteDeadPieceModels.clear();
        this.blackDeadPieceModels.clear();
        this.chessSquareModels.clear();
    }

    private void doneLoading() {
        if (this.tacticalViewEnabled) {
            this.get2dModels();
        } else {
            this.get3dModels();
        }
    }

    private Matrix4 calculateProjectionMatrix(Location location) {
        Matrix4 tmpMat4 = new Matrix4();
        Vector3 squareCenter = new Vector3(location.getX() + 0.475f, location.getY(), location.getZ() - 0.5f);
        Matrix4 textTransform = new Matrix4();
        textTransform.idt()
                .translate(squareCenter)
                .scl(0.04f)
                .rotate(1, 0, 0, 90)
                .rotate(0, 1, 0, 180);

        return tmpMat4.set(this.camera.combined).mul(textTransform);
    }

    private void get2dModels() {
        if (this.assets.isLoaded(this.assetPaths.get(Piece.class))) {
            this.piecesAtlas = this.assets.get(this.assetPaths.get(Piece.class), TextureAtlas.class);
            for (Iterator<Piece> iter = this.loadingPieces.iterator(); iter.hasNext(); ) {
                Piece piece = iter.next();
                GraphicsGameElement element = this.getOrAddPieceByIdAndColor(piece.getPieceId(), piece.getChessColor(), piece.getLocation());
                element.move2D(piece.getLocation());
                String id = this.material3dManager.getRegionNameFromPieceId(piece.getPieceId());
                TextureAtlas.AtlasRegion region = this.piecesAtlas.findRegion(id);
                Sprite pieceSprite = new Sprite(region);
                element.setModel2d(pieceSprite);
            }
            this.boardIsLoading = false;
        } else {
            this.assets.load(this.assetPaths.get(Piece.class), TextureAtlas.class);
        }
    }

    private void get3dModels() {
        Model knightModel = this.assets.get("data/knight.g3db", Model.class);
        Model bishopModel = this.assets.get("data/bishop.g3db", Model.class);
        Model pawnModel = this.assets.get("data/pawn.g3db", Model.class);
        Model queenModel = this.assets.get("data/queen.g3db", Model.class);
        Model kingModel = this.assets.get("data/king.g3db", Model.class);
        Model rookModel = this.assets.get("data/rook.g3db", Model.class);
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
            GraphicsGameElement element = new GraphicsGameElement(model.getLocation(), piece.getPieceId());
            element.setModel3d(model);
            if (piece.getChessColor() == ChessColor.BLACK) {
                this.blackPieceModels.add(element);
            } else {
                this.whitePieceModels.add(element);
            }
            //iter.remove();
        }
        this.boardIsLoading = false;
    }

    private GraphicsGameElement getOrAddPieceByIdAndColor(PieceId pieceId, ChessColor chessColor, Location location) {
        GraphicsGameElement element;
        if (chessColor == ChessColor.WHITE) {
            element = this.whitePieceModels.getElementById(pieceId);
            if (element == null) {
                element = this.whiteDeadPieceModels.getElementById(pieceId);
            }
            if (element == null) {
                element = new GraphicsGameElement(location, pieceId);
            }
            this.whitePieceModels.add(element);
        } else {
            element = this.blackPieceModels.getElementById(pieceId);
            if (element == null) {
                element = this.blackDeadPieceModels.getElementById(pieceId);
            }
            if (element == null) {
                element = new GraphicsGameElement(location, pieceId);
            }
            this.blackPieceModels.add(element);
        }
        return element;
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
    private void initCamera(int width, int height) {
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

    private void setTacticalCamera() {
        this.camera.direction.set(0, 0, 0);
        this.camera.lookAt(3.5f, 0, 3.5f);
        this.camera.up.set(0, 1, 0);
        this.camera.position.set(new Vector3(3.475f, 20, 3.475f));
        this.camera.lookAt(3.5f, 0, 3.5f);
        this.camera.rotate(Vector3.Y, -45);
        this.camera.update();
    }

    private void setPaths() {
        this.assetPaths.put(Knight.class, "data/knight.g3db");
        this.assetPaths.put(King.class, "data/king.g3db");
        this.assetPaths.put(Queen.class, "data/queen.g3db");
        this.assetPaths.put(Pawn.class, "data/pawn.g3db");
        this.assetPaths.put(Rook.class, "data/rook.g3db");
        this.assetPaths.put(Bishop.class, "data/bishop.g3db");
        this.assetPaths.put(Piece.class, "data/pictures/pieces.atlas");
    }

    private void loadModels() {
        if (this.tacticalViewEnabled) {
            this.assets.load(this.assetPaths.get(Piece.class), TextureAtlas.class);
        } else {
            for (Map.Entry<Class, String> entrySet : this.assetPaths.entrySet()) {
                if (!entrySet.getKey().equals(Piece.class)) {
                    this.assets.load(entrySet.getValue(), Model.class);
                }
            }
        }
        this.boardIsLoading = true;
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
            GraphicsGameElement element = new GraphicsGameElement(square.getLocation(), null);
            element.setModel3d(squareModel);
            this.chessSquareModels.add(element);
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

    public void selectPiece(Piece touched) {
        if (this.selectedModel != null) {
            this.material3dManager.resetMaterial(this.selectedModel, this.tacticalViewEnabled);
        }
        GraphicsGameElement element;
        if (touched.getChessColor() == ChessColor.BLACK) {
            element = this.blackPieceModels.getByLocation(touched.getLocation());

        } else {
            element = this.whitePieceModels.getByLocation(touched.getLocation());
        }
        this.material3dManager.setSelectedMaterial(element, this.tacticalViewEnabled);
        this.selectedModel = element;
    }

    public void resetSelection() {
        if (this.selectedModel != null) {
            this.material3dManager.resetMaterial(this.selectedModel, this.tacticalViewEnabled);
        }
        this.selectedModel = null;
        for (Iterator<GraphicsGameElement> iter = this.chessSquareModels.iterator(); iter.hasNext(); ) {
            GraphicsGameElement square = iter.next();
            this.material3dManager.resetMaterial(square, this.tacticalViewEnabled);
        }
    }

    public void moveSelectedPieceIntoSquare(Square square) {
        this.move(this.selectedModel, square.getLocation());
    }

    private void move(GraphicsGameElement element, Location location) {
        if (element.getModel2d() != null) {
            element.move2D(location);
        }
        if (element.getModel3d() != null) {
            element.move3D(location);
        }
    }

    public void highlightEmptySquareFromLocation(Square square) {
        this.material3dManager.setSelectedMaterial(this.chessSquareModels.getByLocation(square.getLocation()), this.tacticalViewEnabled);
    }

    public void highlightOccupiedSquareFromLocation(Square square) {
        this.material3dManager.setOccupiedMaterial(this.chessSquareModels.getByLocation(square.getLocation()), this.tacticalViewEnabled);
        if (square.getPiece().getChessColor() == ChessColor.WHITE) {
            this.material3dManager.setOccupiedMaterial(this.getWhitePieceModels().getByLocation(square.getPiece().getLocation()), this.tacticalViewEnabled);
        } else {
            this.material3dManager.setOccupiedMaterial(this.getBlackPieceModels().getByLocation(square.getPiece().getLocation()), this.tacticalViewEnabled);
        }
    }

    public GraphicGameArray getWhitePieceModels() {
        return this.whitePieceModels;
    }

    public GraphicGameArray getBlackPieceModels() {
        return this.blackPieceModels;
    }

    public void moveToSquare(Piece piece, Square square) {
        GraphicsGameElement element;
        if (piece.getChessColor() == ChessColor.WHITE) {
            element = this.getWhitePieceModels().getByLocation(piece.getLocation());
        } else {
            element = this.getBlackPieceModels().getByLocation(piece.getLocation());
        }
        this.move(element, square.getLocation());
    }

    public GraphicGameArray getSquareModelsFromPossibleMoves(SquareList possiblesMoves) {
        GraphicGameArray possibleSquares = new GraphicGameArray();
        if (possiblesMoves != null) {
            for (GraphicsGameElement element : this.getChessSquareModels()) {
                for (Square square : possiblesMoves) {
                    if (element.getLocation().equals(square.getLocation())) {
                        possibleSquares.add(element);
                    }
                }
            }
        }
        return possibleSquares;
    }

    private GraphicGameArray getChessSquareModels() {
        return this.chessSquareModels;
    }

    public void unHighlightSquares(SquareList possiblesMoves) {
        for (Square square : possiblesMoves) {
            this.material3dManager.resetMaterial(this.getChessSquareModels().getByLocation(square.getLocation()), this.tacticalViewEnabled);
            if (square.getPiece() != null) {
                if (square.getPiece().getChessColor() == ChessColor.WHITE) {
                    this.material3dManager.resetMaterial(this.getWhitePieceModels().getByLocation(square.getPiece().getLocation()), this.tacticalViewEnabled);
                } else {
                    this.material3dManager.resetMaterial(this.getBlackPieceModels().getByLocation(square.getPiece().getLocation()), this.tacticalViewEnabled);
                }
            }
        }
    }

    public void moveToEven(Piece piece) {
        if (this.tacticalViewEnabled) {
            // TODO
        } else {
            GraphicsGameElement eatenPiece;
            if (piece.getChessColor() == ChessColor.WHITE) {
                eatenPiece = this.getWhitePieceModels().removeByLocation(piece.getLocation());
                int xpos = 7 + (this.whiteDeadPieceModels.size < 8 ? 1 : 2);
                int zpos = this.whiteDeadPieceModels.size < 8 ? 7 - this.whiteDeadPieceModels.size : 7 - (this.whiteDeadPieceModels.size - 8);
                this.move(eatenPiece, new Location(xpos, 0, zpos));
                this.whiteDeadPieceModels.add(eatenPiece);
            } else {
                eatenPiece = this.getBlackPieceModels().removeByLocation(piece.getLocation());
                int xpos = 0 - (this.blackDeadPieceModels.size < 8 ? 1 : 2);
                int zpos = this.blackDeadPieceModels.size < 8 ? this.blackDeadPieceModels.size : this.blackDeadPieceModels.size - 8;
                this.move(eatenPiece, new Location(xpos, 0, zpos));
                this.blackDeadPieceModels.add(eatenPiece);
            }
            this.material3dManager.resetMaterial(eatenPiece, this.tacticalViewEnabled);
            eatenPiece.getModel3d().transform.rotate(Vector3.Y, 180);
        }
    }

    public void toogleTacticalView() {
        if (!this.tacticalViewEnabled) {
            if (this.piecesAtlas == null) {
                this.boardIsLoading = true;
            }
            this.setTacticalCamera();
        } else {
            this.set3DCamera();
        }
        this.tacticalViewEnabled = !this.tacticalViewEnabled;
    }

    private void set3DCamera() {
        this.camera.direction.set(0, 0, 0);
        this.camera.up.set(0, 1, 0);
        this.camera.position.set(3.5f, 15f, -8f);
        this.camera.lookAt(3.5f, 0, 3.5f);
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

    public boolean isTacticalViewEnabled() {
        return this.tacticalViewEnabled;
    }

    public void setTacticalViewEnabled(boolean tacticalViewEnabled) {
        this.tacticalViewEnabled = tacticalViewEnabled;
    }

    public void exit() {
        Gdx.app.exit();
    }
}
