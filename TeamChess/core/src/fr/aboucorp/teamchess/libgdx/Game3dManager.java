package fr.aboucorp.teamchess.libgdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import fr.aboucorp.teamchess.libgdx.exceptions.CellNotFoundException;
import fr.aboucorp.teamchess.libgdx.models.ChessCell;
import fr.aboucorp.teamchess.libgdx.models.ChessModel;
import fr.aboucorp.teamchess.libgdx.models.ChessPiece;
import fr.aboucorp.teamchess.libgdx.utils.ChessCellArray;


public class Game3dManager extends ApplicationAdapter {
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

	private BoardManager boardManager;

	private ChessPiece selectedPiece;

	public Game3dManager(){
	}

	@Override
	public void create () {
		this.modelBatch = new ModelBatch();
		this.initEnvironment();
		this.initCamera();
		InputMultiplexer multiplexer = new InputMultiplexer(new GestureDetector(androidListener), camController);
		multiplexer.addProcessor(androidInputAdapter);
		Gdx.input.setInputProcessor(multiplexer);
		this.boardManager = new BoardManager();
		this.boardManager.initBoard();
	}

	@Override
	public void render () {
		if (this.boardManager.isBoardIsLoading() && this.boardManager.getAssets().update()) {
			try {
				this.boardManager.doneLoading();
			}
			catch(CellNotFoundException ex){
				Gdx.app.log("fr.aboucorp.teamchess","Unable to Load pieces on cells",ex);
			}
		}
			this.camController.update();
			Gdx.gl.glClearColor(135 / 255f, 206 / 255f, 235 / 255f, 1);
			Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
			this.modelBatch.begin(camera);
			for (ModelInstance instance : this.boardManager.getChessCells()) {
				modelBatch.render(instance, this.environment);
			}
			for (ModelInstance instance :  this.boardManager.getWhitePieces()) {
				modelBatch.render(instance, this.environment);
			}
			for (ModelInstance instance : this.boardManager.getBlackPieces()) {
				modelBatch.render(instance, this.environment);
			}
			this.modelBatch.end();
	}


	@Override
	public void dispose () {
		modelBatch.dispose();
	}

	public void selectPiece(ChessPiece piece){
		if(selectedPiece != null){
			Material oldMat = selectedPiece.materials.get(0);
			oldMat.clear();
			oldMat.set(piece.getOriginalMaterial());
		}
		Material newMat = new Material();
		newMat.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 0.3f));
		newMat.set(ColorAttribute.createDiffuse(Color.ROYAL));
		Material oldMat = piece.materials.get(0);
		oldMat.clear();
		oldMat.set(newMat);
		selectedPiece = piece;
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

	public ChessCellArray getChessCells() {
		return this.boardManager.getChessCells();
	}

	public Array<ChessModel> getBlackPieces() {
		return this.boardManager.getBlackPieces();
	}

	public Array<ChessModel> getWhitePieces() {
		return this.boardManager.getWhitePieces();
	}

	public void setAndroidInputAdapter(InputAdapter androidInputAdapter) {
		this.androidInputAdapter = androidInputAdapter;
	}

	public void setAndroidListener(GestureDetector.GestureListener androidListener) {
		this.androidListener = androidListener;
	}

	public void movePieceIntoCell(ChessCell cell) {
		this.selectedPiece.transform.setTranslation(cell.getBoundingBox().getCenter(new Vector3()));
	}
}
