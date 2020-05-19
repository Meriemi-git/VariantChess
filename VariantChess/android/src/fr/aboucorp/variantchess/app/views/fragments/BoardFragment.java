package fr.aboucorp.variantchess.app.views.fragments;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.listeners.GDXGestureListener;
import fr.aboucorp.variantchess.app.listeners.GDXInputAdapter;
import fr.aboucorp.variantchess.app.managers.MatchManager;
import fr.aboucorp.variantchess.app.managers.boards.BoardManager;
import fr.aboucorp.variantchess.app.managers.boards.ClassicBoardManager;
import fr.aboucorp.variantchess.app.views.activities.VariantChessActivity;
import fr.aboucorp.variantchess.entities.boards.Board;
import fr.aboucorp.variantchess.entities.boards.ClassicBoard;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.events.GameEventSubscriber;
import fr.aboucorp.variantchess.entities.events.models.BoardEvent;
import fr.aboucorp.variantchess.entities.events.models.GameEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnEvent;
import fr.aboucorp.variantchess.entities.rules.ClassicRuleSet;
import fr.aboucorp.variantchess.libgdx.Board3dManager;

public class BoardFragment extends AndroidFragmentApplication implements GameEventSubscriber {

    public Button btn_end_turn;
    public Button btn_test;
    public Switch switch_tactical;
    public GameEventManager eventManager;
    private MatchManager matchManager;
    private BoardManager boardManager;
    private TextView lbl_turn;
    private TextView party_logs;
    private EditText fen_txt;
    private Activity activity;

    public BoardFragment(){
        this.eventManager = GameEventManager.getINSTANCE();
        this.eventManager.subscribe(fr.aboucorp.variantchess.entities.events.models.GameEvent.class,this,1);
    }

    @Override
    public View initializeForView(ApplicationListener listener) {
        bindViews();
        bindListeners();
        Board3dManager board3dManager = new Board3dManager();
        Board classicBoard = new ClassicBoard();
        fr.aboucorp.variantchess.entities.rules.ClassicRuleSet classicRules = new ClassicRuleSet(classicBoard);
        boardManager = new ClassicBoardManager(board3dManager, classicBoard,classicRules);
        this.matchManager = new MatchManager((VariantChessActivity) activity,boardManager);
        InputAdapter inputAdapter = new GDXInputAdapter(board3dManager);
        board3dManager.setAndroidInputAdapter(inputAdapter);
        GDXGestureListener gestureListener = new GDXGestureListener(boardManager);
        board3dManager.setAndroidListener(gestureListener);
        this.bindViews();
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        View view =  initializeForView(board3dManager, config);
        this.matchManager.startGame();
        this.lbl_turn.setText("Turn of " + BoardFragment.this.matchManager.getPartyInfos());
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }


    public void bindViews() {
        this.btn_end_turn = this.activity.findViewById(R.id.btn_end_turn);
        this.lbl_turn = this.activity.findViewById(R.id.lbl_turn);
        this.party_logs = this.activity.findViewById(R.id.party_logs);
        this.btn_test = this.activity.findViewById(R.id.btn_test);
        this.fen_txt = this.activity.findViewById(R.id.fen_txt);
        this.switch_tactical = this.activity.findViewById(R.id.switch_tactical);
    }

    public void bindListeners() {
        btn_end_turn.setOnClickListener(v -> {
           BoardFragment.this.matchManager.endTurn();
           BoardFragment.this.lbl_turn.setText("Turn of " + BoardFragment.this.matchManager.getPartyInfos());
        });
        btn_test.setOnClickListener(v -> BoardFragment.this.matchManager.loadBoard(fen_txt.getText().toString().trim()));

        switch_tactical.setOnClickListener(v -> BoardFragment.this.boardManager.toogleTacticalView());
    }

    @Override
    public void receiveGameEvent(final GameEvent event) {
        this.runOnUiThread(() -> {
            if(event instanceof BoardEvent || event instanceof TurnEvent){
                BoardFragment.this.party_logs.setText( BoardFragment.this.party_logs.getText() + "\nLOG :" + event.message);
            }
            BoardFragment.this.lbl_turn.setText("Turn of " + BoardFragment.this.matchManager.getPartyInfos());
            Log.i("fr.aboucorp.variantchess",event.message);
        });
    }

    @Override
    public void startActivity(Intent intent) {

    }
}
