package fr.aboucorp.teamchess.libgdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.input.GestureDetector;

import fr.aboucorp.entities.model.ChessCell;
import fr.aboucorp.entities.model.ChessPiece;
import fr.aboucorp.teamchess.libgdx.models.ChessModel;


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

	private Board3dManager board3dManager;


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
		this.board3dManager = Board3dManager.getInstance();
	}

	@Override
	public void render () {
		if (this.board3dManager.isBoardIsLoading() && this.board3dManager.getAssets().update()) {
				this.board3dManager.doneLoading();
		}
		this.camController.update();
		Gdx.gl.glClearColor(135 / 255f, 206 / 255f, 235 / 255f, 1);
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		this.modelBatch.begin(camera);
		for (ChessModel cell : this.board3dManager.getChessCells()) {
			modelBatch.render(cell, this.environment);
		}
		this.modelBatch.end();
	}

	@Override
	public void dispose () {
		modelBatch.dispose();
	}


	public void selectPiece(ChessPiece piece){
		board3dManager.selectPiece(piece);
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
		this.board3dManager.moveSelectedPieceToLocation(cell.getLocation());
	}

    public void resetSelection() {
		this.board3dManager.resetSelection();
    }
}
