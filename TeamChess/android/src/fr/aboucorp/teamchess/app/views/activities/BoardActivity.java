package fr.aboucorp.teamchess.app.views.activities;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import fr.aboucorp.teamchess.R;
import fr.aboucorp.teamchess.app.GameManager;
import fr.aboucorp.teamchess.app.listeners.GDXGestureListener;
import fr.aboucorp.teamchess.app.listeners.GDXInputAdapter;
import fr.aboucorp.teamchess.libgdx.Game3dManager;

public class BoardActivity extends AndroidApplication {

    public FrameLayout boardPanel;

    private GameManager gameManager;
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);
        bindViews();
        bindListeners();
        this.initializeBoard();
    }

    private void initializeBoard(){

        Game3dManager game3dManager = new Game3dManager();
        this.gameManager = new GameManager(game3dManager);
        InputAdapter inputAdapter = new GDXInputAdapter();
        game3dManager.setAndroidInputAdapter(inputAdapter);
        GDXGestureListener gestureListener = new GDXGestureListener(gameManager);
        game3dManager.setAndroidListener(gestureListener);
        this.bindViews();
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        boardPanel.addView(initializeForView(game3dManager, config));
    }

    public void bindViews() {
    this.boardPanel = findViewById(R.id.board_layout);
    }

    public void bindListeners() {
    }
}
