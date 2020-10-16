package fr.aboucorp.variantchess.app.views.fragments;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.db.entities.GameRules;
import fr.aboucorp.variantchess.app.db.entities.VariantUser;
import fr.aboucorp.variantchess.app.exceptions.UnknownGameRulesException;
import fr.aboucorp.variantchess.app.managers.MatchManager;
import fr.aboucorp.variantchess.app.managers.OfflineMatchManager;
import fr.aboucorp.variantchess.app.managers.OnlineMatchManager;
import fr.aboucorp.variantchess.app.managers.boards.BoardManager;
import fr.aboucorp.variantchess.app.managers.boards.BoardManagerFactory;
import fr.aboucorp.variantchess.app.multiplayer.NakamaManager;
import fr.aboucorp.variantchess.app.utils.AsyncHandler;
import fr.aboucorp.variantchess.app.utils.PieceIconFactory;
import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.ChessMatch;
import fr.aboucorp.variantchess.entities.Player;
import fr.aboucorp.variantchess.entities.enums.PieceId;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.events.GameEventSubscriber;
import fr.aboucorp.variantchess.entities.events.models.GameEvent;
import fr.aboucorp.variantchess.entities.events.models.MoveEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnStartEvent;
import fr.aboucorp.variantchess.libgdx.Board3dManager;

import static fr.aboucorp.variantchess.app.utils.ArgsKey.GAME_RULES;
import static fr.aboucorp.variantchess.app.utils.ArgsKey.IS_ONLINE;
import static fr.aboucorp.variantchess.app.utils.ArgsKey.MATCH_ID;
import static fr.aboucorp.variantchess.app.utils.ArgsKey.VARIANT_USER;

public class BoardFragment extends AndroidFragmentApplication implements GameEventSubscriber, AndroidFragmentApplication.Callbacks {

    public FrameLayout board_panel;
    private LinearLayout opponent_dead_pieces_list_layout;
    private LinearLayout self_dead_pieces_list_layout;

    private Button btn_end_turn;
    private Button btn_surrend;
    private TextView lbl_turn;
    private TextView lbl_opponent;
    private TextView lbl_self;

    private BoardManager boardManager;
    private MatchManager matchManager;

    private NakamaManager nakamaManager;

    private VariantUser variantUser;
    private GameRules gameRules;
    private boolean isOnline;
    private String matchId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.board_layout, container, false);
        this.bindViews(view);
        this.nakamaManager = NakamaManager.getInstance();
        this.initializeBoard();
        if (this.isOnline) {
            this.joinMatch(this.matchId);
        } else {
            ChessMatch chessMatch = new ChessMatch();
            chessMatch.getPlayers().add(new Player("Player 1", ChessColor.WHITE, null));
            chessMatch.getPlayers().add(new Player("Player 2", ChessColor.BLACK, null));
            this.matchManager.startParty(chessMatch);
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
            if (event instanceof TurnStartEvent) {
                this.btn_end_turn.setEnabled(this.matchManager.isMyTurn());
                if (matchManager.isMyTurn()) {
                    this.lbl_turn.setText("Your turn");
                } else {
                    this.lbl_turn.setText("Turn of " + this.matchManager.getOpponent().getUsername());
                }
            } else if (event instanceof MoveEvent) {
                PieceId pieceId = ((MoveEvent) event).deadPiece;
                if (pieceId != null) {
                    addIconForDeadPiece(pieceId);
                }
            }
            Log.i("fr.aboucorp.variantchess", String.format("GameEvent Color : %s Message : %s", this.matchManager.getPartyInfos(), event.message));
        });
    }

    private void addIconForDeadPiece(PieceId pieceId) {
        ImageView piece = new ImageView(getContext());
        Drawable drawable = PieceIconFactory.getDrawableForPieceID(getContext(), pieceId);
        piece.setImageDrawable(drawable);
        if (matchManager.isMyTurn()) {
            this.self_dead_pieces_list_layout.addView(piece);
        }
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        if (args != null) {
            this.isOnline = args.getBoolean(IS_ONLINE);
            this.matchId = args.getString(MATCH_ID);
            this.variantUser = (VariantUser) args.getSerializable(VARIANT_USER);
            this.gameRules = (GameRules) args.getSerializable(GAME_RULES);
        }
    }



    private void bindViews(View view) {
        this.board_panel = view.findViewById(R.id.board_panel);
        this.opponent_dead_pieces_list_layout = view.findViewById(R.id.opponent_dead_pieces_list_layout);
        this.self_dead_pieces_list_layout = view.findViewById(R.id.self_dead_pieces_list_layout);

        this.btn_end_turn = view.findViewById(R.id.btn_end_turn);
        this.btn_surrend = view.findViewById(R.id.btn_surrend);

        this.lbl_turn = view.findViewById(R.id.lbl_turn);
        this.lbl_self = view.findViewById(R.id.lbl_self);
        this.lbl_opponent = view.findViewById(R.id.lbl_opponent);
    }

    private void bindListeners() {
        this.btn_end_turn.setOnClickListener(v -> {
            matchManager.passTurn();
            this.runOnUiThread(() ->
                    lbl_turn.setText("Turn of " + this.matchManager.getPartyInfos()));
        });
        this.btn_surrend.setOnClickListener(v -> {

        });
    }

    private void joinMatch(String matchID) {
        Log.i("fr.aboucorp.variantchess", String.format("Joining match with id : %s", matchID));
        AsyncHandler handler = new AsyncHandler() {
            @Override
            protected Object executeAsync() throws Exception {
                BoardFragment.this.nakamaManager.joinMatchById(matchID);
                return null;
            }

            @Override
            protected void callbackOnUI(Object arg) {
                BoardFragment.this.board_panel.setVisibility(View.VISIBLE);
            }

            @Override
            protected void error(Exception ex) {
                super.error(ex);
                Toast.makeText(getContext(), R.string.matchmaking_cannot_join_match, Toast.LENGTH_LONG).show();
            }
        };
        this.board_panel.setVisibility(View.INVISIBLE);
        handler.start();
    }

    private void initializeBoard() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.requireActivity());
        boolean isTactical = sharedPref.getBoolean(SettingsFragment.IS_TACTICAL_MODE_ON, false);

        GameEventManager gameEventManager = new GameEventManager();
        gameEventManager.subscribe(GameEvent.class, this, 1, "BoardFragment => GameEvent");

        Board3dManager board3dManager = new Board3dManager();
        board3dManager.setTacticalViewEnabled(isTactical);

        try {
            this.boardManager = BoardManagerFactory.getBoardManagerFromGameRules(this.gameRules, board3dManager, gameEventManager);
        } catch (UnknownGameRulesException e) {
            e.printStackTrace();
        }

        if (this.isOnline) {
            this.matchManager = new OnlineMatchManager(this, this.boardManager, gameEventManager, this.variantUser);
        } else {
            this.matchManager = new OfflineMatchManager(this, this.boardManager, gameEventManager);
        }
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        this.board_panel.addView(this.initializeForView(board3dManager, config));
    }

    public void setPlayerLabels(String selfUsername, String opponentUsername) {
        this.lbl_self.setText(selfUsername);
        this.lbl_opponent.setText(opponentUsername);
    }
}
