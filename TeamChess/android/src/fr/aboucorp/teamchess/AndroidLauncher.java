package fr.aboucorp.teamchess;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import fr.aboucorp.teamchess.board.listeners.GDXGestureListener;
import fr.aboucorp.teamchess.board.listeners.GDXInputAdapter;

public class AndroidLauncher extends AndroidApplication {
	private FrameLayout boardPanel;
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.board);
		initializeBoard();
	}

	private void initializeBoard(){
		InputAdapter inputAdapter = new GDXInputAdapter();
		GDXGestureListener gestureListener = new GDXGestureListener();
		Board3d board3d = new Board3d(inputAdapter,gestureListener);
		this.bindView();
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		boardPanel.addView(initializeForView(board3d, config));
	}

	private void bindView(){
		this.boardPanel = (FrameLayout) findViewById(R.id.board_layout);
	}
}
