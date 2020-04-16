package fr.aboucorp.teamchess.app.views.activities;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import fr.aboucorp.teamchess.R;
import fr.aboucorp.teamchess.app.listeners.GDXGestureListener;
import fr.aboucorp.teamchess.app.listeners.GDXInputAdapter;
import fr.aboucorp.teamchess.app.managers.BoardManager;
import fr.aboucorp.teamchess.libgdx.Board3dManager;

public class BoardActivity extends AndroidApplication {

    public FrameLayout boardPanel;

    private BoardManager boardManager;
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);
        bindViews();
        bindListeners();
        this.initializeBoard();
    }

    private void initializeBoard(){
        Board3dManager board3dManager = new Board3dManager();
        this.boardManager = new BoardManager(board3dManager);
        InputAdapter inputAdapter = new GDXInputAdapter();
        board3dManager.setAndroidInputAdapter(inputAdapter);
        GDXGestureListener gestureListener = new GDXGestureListener(this.boardManager);
        board3dManager.setAndroidListener(gestureListener);
        this.bindViews();
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        boardPanel.addView(initializeForView(board3dManager, config));
        this.boardManager.startGame();
    }

    public void bindViews() {
    this.boardPanel = findViewById(R.id.board_layout);
    }

    public void bindListeners() {
    }
}
