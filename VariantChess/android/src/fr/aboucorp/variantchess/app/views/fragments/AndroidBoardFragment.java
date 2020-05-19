package fr.aboucorp.variantchess.app.views.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.badlogic.gdx.InputAdapter;

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

public class AndroidBoardFragment extends VariantChessFragment implements GameEventSubscriber {
    public Button btn_end_turn;
    public Button btn_test;
    public Switch switch_tactical;
    public GameEventManager eventManager;
    public MatchManager matchManager;
    private BoardManager boardManager;
    private TextView lbl_turn;
    private TextView party_logs;
    private EditText fen_txt;
    private Board3dManager board3dManager;

    public AndroidBoardFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.board_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bindViews();
        bindListeners();
        this.eventManager = GameEventManager.getINSTANCE();
        this.eventManager.subscribe(fr.aboucorp.variantchess.entities.events.models.GameEvent.class,this,1);
        board3dManager = new Board3dManager();
        Board classicBoard = new ClassicBoard();
        fr.aboucorp.variantchess.entities.rules.ClassicRuleSet classicRules = new ClassicRuleSet(classicBoard);
        boardManager = new ClassicBoardManager(board3dManager, classicBoard,classicRules);
        this.matchManager = new MatchManager((VariantChessActivity) getActivity(),boardManager);
        InputAdapter inputAdapter = new GDXInputAdapter(board3dManager);
        board3dManager.setAndroidInputAdapter(inputAdapter);
        GDXGestureListener gestureListener = new GDXGestureListener(boardManager);
        board3dManager.setAndroidListener(gestureListener);
        Fragment childFragment = new BoardFragment();
        BoardFragment.setBoard3dManager(board3dManager);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.board, childFragment).commit();
    }


    public void bindViews() {

        this.btn_end_turn = getView().findViewById(R.id.btn_end_turn);
        this.lbl_turn =  getView().findViewById(R.id.lbl_turn);
        this.party_logs =  getView().findViewById(R.id.party_logs);
        this.btn_test =  getView().findViewById(R.id.btn_test);
        this.fen_txt =  getView().findViewById(R.id.fen_txt);
        this.switch_tactical =  getView().findViewById(R.id.switch_tactical);
    }

    public void bindListeners() {
        btn_end_turn.setOnClickListener(v -> {
            AndroidBoardFragment.this.matchManager.endTurn();
            getActivity().runOnUiThread(() ->
                    AndroidBoardFragment.this.lbl_turn.setText("Turn of " + AndroidBoardFragment.this.matchManager.getPartyInfos()));
        });
        btn_test.setOnClickListener(v -> AndroidBoardFragment.this.matchManager.loadBoard(fen_txt.getText().toString().trim()));

        switch_tactical.setOnClickListener(v -> AndroidBoardFragment.this.boardManager.toogleTacticalView());
    }

    @Override
    public void receiveGameEvent(final GameEvent event) {
        getActivity().runOnUiThread(() -> {
            if(event instanceof BoardEvent || event instanceof TurnEvent){
                AndroidBoardFragment.this.party_logs.setText( AndroidBoardFragment.this.party_logs.getText() + "\nLOG :" + event.message);
            }
            AndroidBoardFragment.this.lbl_turn.setText("Turn of " + AndroidBoardFragment.this.matchManager.getPartyInfos());
            Log.i("fr.aboucorp.variantchess",event.message);
        });
    }

    public void startGame() {
        this.matchManager.startGame();
        getActivity().runOnUiThread(() ->
        this.lbl_turn.setText("Turn of " + AndroidBoardFragment.this.matchManager.getPartyInfos()));
    }
}
