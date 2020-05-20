package fr.aboucorp.variantchess.app.views.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import fr.aboucorp.variantchess.app.utils.FragmentTag;
import fr.aboucorp.variantchess.app.views.activities.MainActivity;
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

public class PartyFragment extends VariantChessFragment implements GameEventSubscriber, BoardFragment.BoardFragmentListener {
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
    private Activity activity;
    private BoardFragment boardFragment;

    public PartyFragment() {
        this.eventManager = GameEventManager.getINSTANCE();
        this.eventManager.subscribe(GameEvent.class,this,1);
        board3dManager = new Board3dManager();
        Board classicBoard = new ClassicBoard();
        ClassicRuleSet classicRules = new ClassicRuleSet(classicBoard);
        boardManager = new ClassicBoardManager(board3dManager, classicBoard,classicRules);
        this.matchManager = new MatchManager((VariantChessActivity) getActivity(),boardManager);
        boardFragment = new BoardFragment();
        boardFragment.setBoard3dManager(board3dManager);
        boardFragment.setBoardFragmentListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            activity=(Activity) context;
        }

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
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.board, boardFragment,FragmentTag.BOARD);
        transaction.commit();

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
            PartyFragment.this.matchManager.endTurn();
            activity.runOnUiThread(() ->
                    PartyFragment.this.lbl_turn.setText("Turn of " + PartyFragment.this.matchManager.getPartyInfos()));
        });
        btn_test.setOnClickListener(v -> PartyFragment.this.matchManager.loadBoard(fen_txt.getText().toString().trim()));

        switch_tactical.setOnClickListener(v -> PartyFragment.this.boardManager.toogleTacticalView());
    }

    @Override
    public void receiveGameEvent(final GameEvent event) {
        if(isAdded()) {
            activity.runOnUiThread(() -> {
                if (event instanceof BoardEvent || event instanceof TurnEvent) {
                    PartyFragment.this.party_logs.setText(PartyFragment.this.party_logs.getText() + "\nLOG :" + event.message);
                }
                PartyFragment.this.lbl_turn.setText("Turn of " + PartyFragment.this.matchManager.getPartyInfos());
                Log.i("fr.aboucorp.variantchess", event.message);
            });
        }
    }

    public void startGame() {
        this.matchManager.startGame();
        activity.runOnUiThread(() ->
        this.lbl_turn.setText("Turn of " + PartyFragment.this.matchManager.getPartyInfos()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public boolean confirmExit() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setMessage("If you exit the game you will loose the party");
                alertDialogBuilder.setPositiveButton("yes",
                        (dialog, arg1) -> endGame());

        alertDialogBuilder.setNegativeButton("No", (dialog, which) ->  dialog.dismiss());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        return true;
    }

    public void endGame(){
        this.matchManager.endParty();
        ((MainActivity)activity).setFragment(new HomeFragment(),FragmentTag.HOME);
    }

    @Override
    public void onBoardFragmentLoaded() {
        startGame();
    }
}
