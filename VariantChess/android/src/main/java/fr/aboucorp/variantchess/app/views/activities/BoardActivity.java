package fr.aboucorp.variantchess.app.views.activities;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.preference.PreferenceManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.heroiclabs.nakama.api.NotificationList;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.db.entities.ChessUser;
import fr.aboucorp.variantchess.app.db.entities.GameRules;
import fr.aboucorp.variantchess.app.db.viewmodel.UserViewModel;
import fr.aboucorp.variantchess.app.exceptions.UnknownGameRulesException;
import fr.aboucorp.variantchess.app.managers.MatchManager;
import fr.aboucorp.variantchess.app.managers.OfflineMatchManager;
import fr.aboucorp.variantchess.app.managers.OnlineMatchManager;
import fr.aboucorp.variantchess.app.managers.boards.BoardManager;
import fr.aboucorp.variantchess.app.managers.boards.BoardManagerFactory;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;
import fr.aboucorp.variantchess.app.multiplayer.listeners.NotificationListener;
import fr.aboucorp.variantchess.app.views.fragments.SettingsFragment;
import fr.aboucorp.variantchess.entities.ChessMatch;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.events.GameEventSubscriber;
import fr.aboucorp.variantchess.entities.events.models.BoardEvent;
import fr.aboucorp.variantchess.entities.events.models.GameEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnEvent;
import fr.aboucorp.variantchess.libgdx.Board3dManager;

import static fr.aboucorp.variantchess.app.utils.ArgsKey.CHESS_MATCH;
import static fr.aboucorp.variantchess.app.utils.ArgsKey.CHESS_USER;
import static fr.aboucorp.variantchess.app.utils.ArgsKey.GAME_RULES;
import static fr.aboucorp.variantchess.app.utils.ArgsKey.IS_ONLINE;

public class BoardActivity extends AndroidApplication implements GameEventSubscriber, ViewModelStoreOwner, NotificationListener {

    public FrameLayout board_panel;
    private Button btn_end_turn;
    private Switch switch_tactical;
    private TextView lbl_turn;
    private TextView party_logs;
    private Board3dManager board3dManager;
    private BoardManager boardManager;
    private MatchManager matchManager;
    private Toolbar toolbar;
    private ChessUser chessUser;
    private ChessMatch chessMatch;
    private GameRules gameRules;
    private GameEventManager gameEventManager;
    private boolean isOnline;
    private UserViewModel userViewModel;
    private ViewModelStore viewModelStore;

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        this.setContentView(R.layout.board_layout);
        this.bindViews();
        this.bindListeners();
        this.setToolbar();
        AndroidApplicationConfiguration libgdxConfig = new AndroidApplicationConfiguration();
        this.board_panel.addView(this.initializeForView(this.board3dManager, libgdxConfig));
    }

    private void bindViews() {
        this.board_panel = this.findViewById(R.id.board);
        this.btn_end_turn = this.findViewById(R.id.btn_end_turn);
        this.lbl_turn = this.findViewById(R.id.lbl_turn);
        this.party_logs = this.findViewById(R.id.party_logs);
        this.switch_tactical = this.findViewById(R.id.switch_tactical);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.board_layout);
        SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.setNotificationListener(this);
        if (savedInstanceState != null) {
            this.isOnline = savedInstanceState.getBoolean(IS_ONLINE);
            this.chessMatch = (ChessMatch) savedInstanceState.getSerializable(CHESS_MATCH);
            this.chessUser = (ChessUser) savedInstanceState.getSerializable(CHESS_USER);
            this.gameRules = (GameRules) savedInstanceState.getSerializable(GAME_RULES);
        } else {
            this.isOnline = this.getIntent().getExtras().getBoolean(IS_ONLINE);
            this.chessMatch = (ChessMatch) this.getIntent().getExtras().getSerializable(CHESS_MATCH);
            this.chessUser = (ChessUser) this.getIntent().getExtras().getSerializable(CHESS_USER);
            this.gameRules = (GameRules) this.getIntent().getExtras().getSerializable(GAME_RULES);
        }
        this.bindViews();
        this.bindListeners();
        this.setToolbar();
        this.initializeBoard();
    }

    @Override
    public void onNotifications(NotificationList notifications) {
        // TODO react on disconnect
    }

    private void bindListeners() {
        this.btn_end_turn.setOnClickListener(v -> {
            BoardActivity.this.matchManager.endTurn(null);
            this.runOnUiThread(() ->
                    BoardActivity.this.lbl_turn.setText("Turn of " + BoardActivity.this.matchManager.getPartyInfos()));
        });
        this.switch_tactical.setOnClickListener(v -> BoardActivity.this.boardManager.toogleTacticalView());
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(CHESS_MATCH, this.chessMatch);
        savedInstanceState.putSerializable(CHESS_USER, this.chessUser);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("If you exit the game you will loose the chessMatch");
        alertDialogBuilder.setPositiveButton("yes",
                (dialog, arg1) -> this.stopParty());
        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void receiveGameEvent(GameEvent event) {
        this.runOnUiThread(() -> {
            if (event instanceof BoardEvent || event instanceof TurnEvent) {
                BoardActivity.this.party_logs.setText(BoardActivity.this.party_logs.getText() + "\n" + event.message);
            }
            BoardActivity.this.lbl_turn.setText("Turn of " + BoardActivity.this.matchManager.getPartyInfos());
        });
    }

    private void setToolbar() {
        this.toolbar = this.findViewById(R.id.main_toolbar);
        this.toolbar.setTitle(this.getString(R.string.app_name));
        this.toolbar.setSubtitle(this.chessUser != null ? this.chessUser.username : "Disconnected");
        this.setActionBar(this.toolbar);
        this.toolbar.setNavigationOnClickListener(v -> this.onBackPressed());
        this.getActionBar().setDisplayHomeAsUpEnabled(true);
        this.getActionBar().setHomeButtonEnabled(false);
        this.getActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
    }

    private void stopParty() {
        this.matchManager.stopParty();
        this.board3dManager.exit();
    }



    @Override
    public void exit() {
        super.exit();
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return this.viewModelStore;
    }

    private void initializeBoard() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
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
        this.matchManager.startParty(this.chessMatch);
    }
}
