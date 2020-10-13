package fr.aboucorp.variantchess.app.views.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.heroiclabs.nakama.Match;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.db.entities.ChessUser;
import fr.aboucorp.variantchess.app.db.entities.GameRules;
import fr.aboucorp.variantchess.app.exceptions.UnknownGameRulesException;
import fr.aboucorp.variantchess.app.managers.MatchManager;
import fr.aboucorp.variantchess.app.managers.OfflineMatchManager;
import fr.aboucorp.variantchess.app.managers.OnlineMatchManager;
import fr.aboucorp.variantchess.app.managers.boards.BoardManager;
import fr.aboucorp.variantchess.app.managers.boards.BoardManagerFactory;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;
import fr.aboucorp.variantchess.app.utils.AsyncHandler;
import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.ChessMatch;
import fr.aboucorp.variantchess.entities.Player;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.events.GameEventSubscriber;
import fr.aboucorp.variantchess.entities.events.models.GameEvent;
import fr.aboucorp.variantchess.libgdx.Board3dManager;

import static fr.aboucorp.variantchess.app.utils.ArgsKey.BLACK;
import static fr.aboucorp.variantchess.app.utils.ArgsKey.CHESS_USER;
import static fr.aboucorp.variantchess.app.utils.ArgsKey.GAME_RULES;
import static fr.aboucorp.variantchess.app.utils.ArgsKey.IS_ONLINE;
import static fr.aboucorp.variantchess.app.utils.ArgsKey.MATCH_ID;
import static fr.aboucorp.variantchess.app.utils.ArgsKey.WHITE;

public class BoardFragment extends AndroidFragmentApplication implements GameEventSubscriber, AndroidFragmentApplication.Callbacks {

    public FrameLayout board_panel;
    private Button btn_end_turn;
    private Switch switch_tactical;
    private TextView lbl_turn;
    private TextView party_logs;

    private Board3dManager board3dManager;
    private BoardManager boardManager;
    private MatchManager matchManager;

    private GameEventManager gameEventManager;

    private SessionManager sessionManager;

    private ChessUser chessUser;
    private GameRules gameRules;
    private boolean isOnline;
    private String matchId;
    private Player white;
    private Player black;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.board_layout, container, false);
        this.bindViews(view);
        this.sessionManager = SessionManager.getInstance();
        this.initializeBoard();
        if (isOnline) {
            joinMatch(this.matchId);
        } else {
            ChessMatch chessMatch = new ChessMatch();
            chessMatch.setWhitePlayer(new Player("Player 1", ChessColor.WHITE, null));
            chessMatch.setBlackPlayer(new Player("Player 2", ChessColor.BLACK, null));
            matchManager.startParty(chessMatch);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.bindListeners();
    }

    @Override
    public void receiveEvent(GameEvent event) {
        this.runOnUiThread(() -> {
            party_logs.setText(party_logs.getText() + "\n" + event.message);
            lbl_turn.setText("Turn of " + matchManager.getPartyInfos());
            Log.i("fr.aboucorp.variantchess", String.format("GameEvent Color : %s Message : %s", matchManager.getPartyInfos(), event.message));
        });
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        if (args != null) {
            this.isOnline = args.getBoolean(IS_ONLINE);
            this.matchId = args.getString(MATCH_ID);
            this.chessUser = (ChessUser) args.getSerializable(CHESS_USER);
            this.gameRules = (GameRules) args.getSerializable(GAME_RULES);
            this.white = (Player) args.getSerializable(WHITE);
            this.black = (Player) args.getSerializable(BLACK);
        }
    }



    private void bindViews(View view) {
        this.board_panel = view.findViewById(R.id.board);
        this.btn_end_turn = view.findViewById(R.id.btn_end_turn);
        this.lbl_turn = view.findViewById(R.id.lbl_turn);
        this.party_logs = view.findViewById(R.id.party_logs);
        this.switch_tactical = view.findViewById(R.id.switch_tactical);
    }

    private void bindListeners() {
        this.btn_end_turn.setOnClickListener(v -> {
            matchManager.endTurn(null);
            this.runOnUiThread(() ->
                    lbl_turn.setText("Turn of " + matchManager.getPartyInfos()));
        });
        this.switch_tactical.setOnClickListener(v -> boardManager.toogleTacticalView());
    }

    private void joinMatch(String matchID) {
        Log.i("fr.aboucorp.variantchess", String.format("Joining match with id : %s", matchID));
        AsyncHandler handler = new AsyncHandler() {
            @Override
            protected Object executeAsync() throws Exception {
                Match match = sessionManager.joinMatchById(matchID);
                ChessMatch chessMatch = new ChessMatch();
                chessMatch.setWhitePlayer(white);
                chessMatch.setBlackPlayer(black);
                chessMatch.setMatchId(match.getMatchId());
                return chessMatch;
            }

            @Override
            protected void callbackOnUI(Object arg) {
                ChessMatch chessMatch = (ChessMatch) arg;
                Log.i("fr.aboucorp.variantchess", "Starting party");
                matchManager.startParty(chessMatch);
            }

            @Override
            protected void error(Exception ex) {
                super.error(ex);
                Toast.makeText(getContext(), R.string.matchmaking_cannot_join_match, Toast.LENGTH_LONG).show();
            }
        };
        handler.start();
    }

    private void initializeBoard() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean isTactical = sharedPref.getBoolean(SettingsFragment.IS_TACTICAL_MODE_ON, false);

        this.gameEventManager = new GameEventManager();
        this.gameEventManager.subscribe(GameEvent.class, this, 1);

        this.board3dManager = new Board3dManager();
        this.board3dManager.setTacticalViewEnabled(isTactical);

        try {
            this.boardManager = BoardManagerFactory.getBoardManagerFromGameRules(this.gameRules, board3dManager, gameEventManager);
        } catch (UnknownGameRulesException e) {
            e.printStackTrace();
        }

        if (this.isOnline) {
            this.matchManager = new OnlineMatchManager(this.boardManager, this.gameEventManager, this.chessUser);
        } else {
            this.matchManager = new OfflineMatchManager(this.boardManager, this.gameEventManager);
        }
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        this.board_panel.addView(this.initializeForView(this.board3dManager, config));
    }
}
