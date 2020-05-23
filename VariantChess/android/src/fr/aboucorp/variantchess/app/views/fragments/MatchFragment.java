package fr.aboucorp.variantchess.app.views.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.listeners.MatchEventListener;
import fr.aboucorp.variantchess.app.managers.MatchManager;
import fr.aboucorp.variantchess.app.managers.boards.BoardManager;
import fr.aboucorp.variantchess.app.managers.boards.ClassicBoardManager;
import fr.aboucorp.variantchess.app.parcelables.MatchP;
import fr.aboucorp.variantchess.app.utils.FragmentTag;
import fr.aboucorp.variantchess.app.views.activities.MainActivity;
import fr.aboucorp.variantchess.entities.Match;
import fr.aboucorp.variantchess.entities.PartyLifeCycle;
import fr.aboucorp.variantchess.entities.boards.Board;
import fr.aboucorp.variantchess.entities.boards.ClassicBoard;
import fr.aboucorp.variantchess.entities.events.GameEventSubscriber;
import fr.aboucorp.variantchess.entities.events.models.BoardEvent;
import fr.aboucorp.variantchess.entities.events.models.GameEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnEndEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnEvent;
import fr.aboucorp.variantchess.entities.rules.ClassicRuleSet;
import fr.aboucorp.variantchess.libgdx.Board3dManager;

public class MatchFragment extends VariantChessFragment implements GameEventSubscriber, BoardFragment.BoardFragmentListener, PartyLifeCycle, MatchEventListener {
    private Button btn_end_turn;
    private Switch switch_tactical;
    private TextView lbl_turn;
    private TextView party_logs;

    private MatchManager matchManager;
    private BoardManager boardManager;
    private Board3dManager board3dManager;

    private Activity activity;
    private BoardFragment boardFragment;

    private MatchP matchP;

    public MatchFragment() {
        this.board3dManager = new Board3dManager();
        Board classicBoard = new ClassicBoard();
        ClassicRuleSet classicRules = new ClassicRuleSet(classicBoard);
        this.boardManager = new ClassicBoardManager(this.board3dManager, classicBoard, classicRules);
        this.matchManager = new MatchManager(this.boardManager,this);
        this.boardFragment = new BoardFragment();
        this.boardFragment.setBoard3dManager(this.board3dManager);
        this.boardFragment.setBoardFragmentListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.board_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.bindViews();
        this.bindListeners();
        FragmentTransaction transaction = this.getChildFragmentManager().beginTransaction();
        Fragment existingBoardFragment = this.getChildFragmentManager().findFragmentByTag(FragmentTag.BOARD);
        if (existingBoardFragment != null) {
            this.boardFragment = (BoardFragment) existingBoardFragment;
        }
        transaction.replace(R.id.board, this.boardFragment, FragmentTag.BOARD).commit();
    }


    protected void bindViews() {
        this.btn_end_turn = this.getView().findViewById(R.id.btn_end_turn);
        this.lbl_turn = this.getView().findViewById(R.id.lbl_turn);
        this.party_logs = this.getView().findViewById(R.id.party_logs);
        this.switch_tactical = this.getView().findViewById(R.id.switch_tactical);
    }

    protected void bindListeners() {
        this.btn_end_turn.setOnClickListener(v -> {
            MatchFragment.this.matchManager.endTurn(null);
            this.activity.runOnUiThread(() ->
                    MatchFragment.this.lbl_turn.setText("Turn of " + MatchFragment.this.matchManager.getPartyInfos()));
        });
        this.switch_tactical.setOnClickListener(v -> MatchFragment.this.boardManager.toogleTacticalView());
    }

    @Override
    public void receiveGameEvent(final GameEvent event) {
        if (this.isAdded()) {
            this.activity.runOnUiThread(() -> {
                if (event instanceof BoardEvent || event instanceof TurnEvent) {
                    MatchFragment.this.party_logs.setText(MatchFragment.this.party_logs.getText() + "\n" + event.message);
                }
                MatchFragment.this.lbl_turn.setText("Turn of " + MatchFragment.this.matchManager.getPartyInfos());
            });
        }
    }

    public void confirmExit() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.activity);
        alertDialogBuilder.setMessage("If you exit the game you will loose the match");
        alertDialogBuilder.setPositiveButton("yes",
                (dialog, arg1) -> this.stopParty());
        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onBoardFragmentLoaded() {
        this.startParty(this.matchP);
    }

    @Override
    public void startParty(Match match) {
        this.matchManager.startParty(match);
        this.activity.runOnUiThread(() ->
                this.lbl_turn.setText("Turn of " + MatchFragment.this.matchManager.getPartyInfos()));
    }

    @Override
    public void stopParty() {
        this.matchManager.stopParty();
        ((MainActivity) this.activity).setFragment(HomeFragment.class, FragmentTag.HOME,null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        this.matchP = args != null ? args.getParcelable("matchP") : null;
    }

    @Override
    public void OnMatchEvent(GameEvent event) {
        if(!(event instanceof TurnEvent)) {
            this.activity.runOnUiThread(() ->
                    this.party_logs.append("\n" + event.message));
        }
    }
}
