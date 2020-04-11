package fr.aboucorp.teamchess;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ModelLoader;
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
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;


public class Board3d extends ApplicationAdapter {
	/** Chargeur de modele 3D */
	private ModelBatch modelBatch;
	/** Camera de la vue 3D */
	private PerspectiveCamera camera;
	/** Controller de la camera permettant à l'utilisateur de la faire pivoter */
	public CameraInputController camController;
	/** Environnement 3D pour l'affichage des modèles */
	private Environment environment;
	public AssetManager assets;
	/** Ecouteur d'évènement au clic sur la fenêtre */
	private InputAdapter androidInputAdapter;
	private GestureDetector.GestureListener androidListener;

	private ModelBuilder modelBuilder;

	private  Array<ModelInstance> boardCells  = new Array();
	private Array<ModelInstance> blackPieces = new Array();
	private Array<ModelInstance> whitePieces = new Array();

	private boolean piecesLoading;

	public Board3d(InputAdapter androidInputAdapter, GestureDetector.GestureListener androidListener){
		this.assets = new AssetManager(new InternalFileHandleResolver());
		this.androidInputAdapter = androidInputAdapter;
		this.androidListener = androidListener;
	}

	@Override
	public void create () {
		this.modelBatch = new ModelBatch();
		this.initEnvironment();
		this.initCamera();
		InputMultiplexer multiplexer = new InputMultiplexer(new GestureDetector(androidListener), camController);
		multiplexer.addProcessor(androidInputAdapter);
		Gdx.input.setInputProcessor(multiplexer);
		createBoard();
		loadPieces();
	}

	private void loadPieces(){
		this.assets.load("data/knight.g3db", Model.class);
		this.assets.load("data/bishop.g3db", Model.class);
		this.assets.load("data/pawn.g3db", Model.class);
		this.assets.load("data/queen.g3db", Model.class);
		this.assets.load("data/king.g3db", Model.class);
		this.assets.load("data/rook.g3db", Model.class);
		this.piecesLoading = true;
	}

	private void doneLoading(){
		Model knightModel = assets.get("data/knight.g3db", Model.class);
		Model bishopModel = assets.get("data/bishop.g3db", Model.class);
		Model pawnModel = assets.get("data/pawn.g3db", Model.class);
		Model queenModel = assets.get("data/queen.g3db", Model.class);
		Model kingModel = assets.get("data/king.g3db", Model.class);
		Model rookModel = assets.get("data/rook.g3db", Model.class);

		// Load black pieces
		ModelInstance whiteRightKnight = new ModelInstance(knightModel,6,0,0);
		whiteRightKnight.transform.rotate(Vector3.Y, -90);
		this.whitePieces.add(whiteRightKnight);
		ModelInstance whiteLeftKnight =  new ModelInstance(knightModel,1,0,0);
		whiteLeftKnight.transform.rotate(Vector3.Y, -90);
		this.whitePieces.add(whiteLeftKnight);

		ModelInstance whiteRightBishop = new ModelInstance(bishopModel,5,0,0);
		whiteRightBishop.transform.rotate(Vector3.Y, -90);
		this.whitePieces.add(whiteRightBishop);

		ModelInstance whiteLeftBishop = new ModelInstance(bishopModel,2,0,0);
		whiteLeftBishop.transform.rotate(Vector3.Y, -90);
		this.whitePieces.add(whiteLeftBishop);

		this.whitePieces.add( new ModelInstance(queenModel,4,0,0));
		this.whitePieces.add( new ModelInstance(kingModel,3,0,0));

		ModelInstance whiteLeftRook = new ModelInstance(rookModel,7,0,0);
		whiteLeftRook.transform.rotate(Vector3.Y, -90);
		this.whitePieces.add(whiteLeftRook);

		ModelInstance whiteRightRook =  new ModelInstance(rookModel,0,0,0);
		whiteRightRook.transform.rotate(Vector3.Y, -90);
		this.whitePieces.add(whiteRightRook);

		for(int  i = 0 ; i < 8 ; i++){
			this.whitePieces.add( new ModelInstance(pawnModel,i,0,1));
		}
		// Apply material color
		for (ModelInstance whitePiece : whitePieces) {
			whitePiece.materials.get(0).set(ColorAttribute.createDiffuse(Color.WHITE));
		}

		// Load white pieces
		ModelInstance blackRightKnight = new ModelInstance(knightModel,6,0,7);
		blackRightKnight.transform.rotate(Vector3.Y, 90);
		this.blackPieces.add(blackRightKnight);

		ModelInstance blackLeftKnight = new ModelInstance(knightModel,1,0,7);
		blackLeftKnight.transform.rotate(Vector3.Y, 90);
		this.blackPieces.add( blackLeftKnight);

		ModelInstance blackLeftBishop = new ModelInstance(bishopModel,5,0,7);
		blackLeftBishop.transform.rotate(Vector3.Y, 90);
		this.blackPieces.add(blackLeftBishop);

		ModelInstance blackRightBishop =  new ModelInstance(bishopModel,2,0,7);
		blackRightBishop.transform.rotate(Vector3.Y, 90);
		this.blackPieces.add(blackRightBishop);

		this.blackPieces.add( new ModelInstance(queenModel,4,0,7));
		this.blackPieces.add( new ModelInstance(kingModel,3,0,7));

		ModelInstance blackLeftRook =  new ModelInstance(rookModel,7,0,7);
		blackLeftRook.transform.rotate(Vector3.Y, 90);
		this.blackPieces.add(blackLeftRook );

		ModelInstance blackRightRook =  new ModelInstance(rookModel,0,0,7);
		blackRightRook.transform.rotate(Vector3.Y, 90);
		this.blackPieces.add( blackRightRook);
		for(int  i = 0 ; i < 8 ; i++){
			this.blackPieces.add( new ModelInstance(pawnModel,i,0,6));
		}
		// Apply material color
		for (ModelInstance blackPiece : blackPieces
		) {
			blackPiece.materials.get(0).set(ColorAttribute.createDiffuse(Color.GRAY));
		}
		piecesLoading = false;
	}

	private void createBoard() {
		this.modelBuilder = new ModelBuilder();
		Model model = this.modelBuilder.createBox(1f, 0.1f, 1f,
				new Material(),
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
		for (int x = 0; x < 8; x++) {
			for (int z = 0; z < 8; z++) {
				ModelInstance modelInstance = new ModelInstance(model, x, 0, z);
				if(x % 2 == 0 && z % 2 != 0 || x % 2 != 0 && z % 2 == 0 ){
					modelInstance.materials.get(0).set(ColorAttribute.createDiffuse(Color.WHITE));
				}else{
					modelInstance.materials.get(0).set(ColorAttribute.createDiffuse(Color.BLACK));
				}
				boardCells.add(modelInstance);
			}
		}

		Model arrowX = modelBuilder.createArrow(0f,0f,0, 10f,0f,0f, 0.1f, 0.1f, 5,
				GL20.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(Color.RED)),
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		Model arrowY = modelBuilder.createArrow(0f,0f,0, 0f,10f,0f, 0.1f, 0.1f, 5,
				GL20.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(Color.BLUE)),
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		Model arrowZ = modelBuilder.createArrow(0f,0f,0, 0f,0f,10f, 0.1f, 0.1f, 5,
				GL20.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(Color.GREEN)),
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		boardCells.add(new ModelInstance(arrowX));
		boardCells.add(new ModelInstance(arrowY));
		boardCells.add(new ModelInstance(arrowZ));
	}

	@Override
	public void render () {
		if (piecesLoading && assets.update()) {
			doneLoading();
		}
			this.camController.update();
			Gdx.gl.glClearColor(135 / 255f, 206 / 255f, 235 / 255f, 1);
			Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
			this.modelBatch.begin(camera);
			for (ModelInstance instance : boardCells) {
				modelBatch.render(instance, this.environment);
			}
			for (ModelInstance instance : whitePieces) {
				modelBatch.render(instance, this.environment);
			}
			for (ModelInstance instance : blackPieces) {
				modelBatch.render(instance, this.environment);
			}
			this.modelBatch.end();
	}
	
	@Override
	public void dispose () {
		modelBatch.dispose();
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
}
