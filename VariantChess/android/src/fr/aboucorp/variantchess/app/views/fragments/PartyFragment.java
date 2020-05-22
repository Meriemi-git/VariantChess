package fr.aboucorp.variantchess.app.views.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.managers.MatchManager;
import fr.aboucorp.variantchess.app.managers.boards.BoardManager;
import fr.aboucorp.variantchess.app.managers.boards.ClassicBoardManager;
import fr.aboucorp.variantchess.app.utils.FragmentTag;
import fr.aboucorp.variantchess.app.views.activities.MainActivity;
import fr.aboucorp.variantchess.app.views.activities.VariantChessActivity;
import fr.aboucorp.variantchess.entities.Party;
import fr.aboucorp.variantchess.entities.PartyLifeCycle;
import fr.aboucorp.variantchess.entities.boards.Board;
import fr.aboucorp.variantchess.entities.boards.ClassicBoard;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.events.GameEventSubscriber;
import fr.aboucorp.variantchess.entities.events.models.BoardEvent;
import fr.aboucorp.variantchess.entities.events.models.GameEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnEvent;
import fr.aboucorp.variantchess.entities.rules.ClassicRuleSet;
import fr.aboucorp.variantchess.libgdx.Board3dManager;

public class PartyFragment extends VariantChessFragment implements GameEventSubscriber, BoardFragment.BoardFragmentListener, PartyLifeCycle {
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
    private Party party;

    public PartyFragment() {
        this.eventManager = GameEventManager.getINSTANCE();
        board3dManager = new Board3dManager();
        Board classicBoard = new ClassicBoard();
        ClassicRuleSet classicRules = new ClassicRuleSet(classicBoard);
        boardManager = new ClassicBoardManager(board3dManager, classicBoard, classicRules);
        this.matchManager = new MatchManager((VariantChessActivity) getActivity(), boardManager);
        boardFragment = new BoardFragment();
        boardFragment.setBoard3dManager(board3dManager);
        boardFragment.setBoardFragmentListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
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
        Fragment existingBoardFragment = getChildFragmentManager().findFragmentByTag(FragmentTag.BOARD);
        if (existingBoardFragment != null) {
            this.boardFragment = (BoardFragment) existingBoardFragment;
        }
        transaction.replace(R.id.board, boardFragment, FragmentTag.BOARD).commit();
    }


    public void bindViews() {
        this.btn_end_turn = getView().findViewById(R.id.btn_end_turn);
        this.lbl_turn = getView().findViewById(R.id.lbl_turn);
        this.party_logs = getView().findViewById(R.id.party_logs);
        this.btn_test = getView().findViewById(R.id.btn_test);
        this.fen_txt = getView().findViewById(R.id.fen_txt);
        this.switch_tactical = getView().findViewById(R.id.switch_tactical);
    }

    public void bindListeners() {
        btn_end_turn.setOnClickListener(v -> {
            PartyFragment.this.matchManager.endTurn(null);
            activity.runOnUiThread(() ->
                    PartyFragment.this.lbl_turn.setText("Turn of " + PartyFragment.this.matchManager.getPartyInfos()));
        });
        btn_test.setOnClickListener(v -> PartyFragment.this.matchManager.loadBoard(fen_txt.getText().toString().trim()));

        switch_tactical.setOnClickListener(v -> PartyFragment.this.boardManager.toogleTacticalView());
    }

    @Override
    public void receiveGameEvent(final GameEvent event) {
        if (isAdded()) {
            activity.runOnUiThread(() -> {
                if (event instanceof BoardEvent || event instanceof TurnEvent) {
                    PartyFragment.this.party_logs.setText(PartyFragment.this.party_logs.getText() + "\nLOG :" + event.message);
                }
                PartyFragment.this.lbl_turn.setText("Turn of " + PartyFragment.this.matchManager.getPartyInfos());
                Log.i("fr.aboucorp.variantchess", event.message);
            });
        }
    }

    public boolean confirmExit() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setMessage("If you exit the game you will loose the party");
        alertDialogBuilder.setPositiveButton("yes",
                (dialog, arg1) -> stopParty(this.party));

        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        return true;
    }

    @Override
    public void onBoardFragmentLoaded() {
        startParty(this.party);
    }

    @Override
    public void startParty(Party party) {
        this.eventManager.subscribe(GameEvent.class, this, 1);
        this.matchManager.startParty(party);
        activity.runOnUiThread(() ->
                this.lbl_turn.setText("Turn of " + PartyFragment.this.matchManager.getPartyInfos()));
    }

    @Override
    public void stopParty(Party party) {
        this.eventManager.unSubscribe(GameEvent.class, this);
        this.matchManager.stopParty(party);
        ((MainActivity) activity).setFragment(HomeFragment.class, FragmentTag.HOME);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.eventManager.unSubscribe(GameEvent.class, this);
        this.eventManager.destroy();
        Fragment fragment = this.getChildFragmentManager().findFragmentByTag(FragmentTag.BOARD);
        if (fragment != null) {
            this.getChildFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    public String getFenFromBoard() {
        return this.boardManager.getFenFromBoard();
    }

    @Override
    public void setArguments(@androidx.annotation.Nullable Bundle args) {
        super.setArguments(args);
        String fenArg = args.getString("fen");
        if(!TextUtils.isEmpty(fenArg)){
            this.party = new Party();
            this.party.getFenMoves().push(fenArg);
        }
    }
}
