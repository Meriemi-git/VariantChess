package fr.aboucorp.teamchess.app.views;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import fr.aboucorp.teamchess.R;
import fr.aboucorp.teamchess.app.listeners.GDXGestureListener;
import fr.aboucorp.teamchess.app.listeners.GDXInputAdapter;
import fr.aboucorp.teamchess.app.managers.BoardManager;
import fr.aboucorp.teamchess.app.managers.PartyManager;
import fr.aboucorp.teamchess.entities.model.events.BoardEvent;
import fr.aboucorp.teamchess.entities.model.events.GameEvent;
import fr.aboucorp.teamchess.entities.model.events.GameEventManager;
import fr.aboucorp.teamchess.entities.model.events.GameEventSubscriber;
import fr.aboucorp.teamchess.entities.model.events.TurnEvent;
import fr.aboucorp.teamchess.libgdx.Board3dManager;

public class BoardActivity extends AndroidApplication implements GameEventSubscriber {

    public FrameLayout board_panel;
    public Button btn_end_turn;
    public GameEventManager eventManager;
    private PartyManager party_manager;
    private TextView lbl_turn;
    private TextView party_logs;

    public BoardActivity(){
        this.eventManager = GameEventManager.getINSTANCE();
        this.eventManager.subscribe(BoardEvent.class,this);
        this.eventManager.subscribe(TurnEvent.class,this);
    }

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
        BoardManager boardManager = new BoardManager(board3dManager);
        this.party_manager = new PartyManager(boardManager);
        InputAdapter inputAdapter = new GDXInputAdapter();
        board3dManager.setAndroidInputAdapter(inputAdapter);
        GDXGestureListener gestureListener = new GDXGestureListener(this.party_manager);
        board3dManager.setAndroidListener(gestureListener);
        this.bindViews();
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        board_panel.addView(initializeForView(board3dManager, config));
        this.party_manager.startGame();
        this.lbl_turn.setText("Turn of " + BoardActivity.this.party_manager.getPartyInfos());
    }

    public void bindViews() {
        this.board_panel = findViewById(R.id.board_layout);
        this.btn_end_turn = findViewById(R.id.btn_end_turn);
        this.lbl_turn = findViewById(R.id.lbl_turn);
        this.party_logs = findViewById(R.id.party_logs);
    }

    public void bindListeners() {
        btn_end_turn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
               BoardActivity.this.party_manager.endTurn();
                BoardActivity.this.lbl_turn.setText("Turn of " + BoardActivity.this.party_manager.getPartyInfos());
            }
        });
    }

    @Override
    public void receiveGameEvent(final GameEvent event) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                BoardActivity.this.party_logs.setText( BoardActivity.this.party_logs.getText() + "\n" + event.eventMessage);
                Log.i("fr.aboucorp.teamchess", String.format("BoardActivity : Event of type %s Message : %s;",event.getClass().getSimpleName(),event.eventMessage));
            }
        });
    }
}
