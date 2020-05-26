package fr.aboucorp.variantchess.app.views.activities;

import android.app.AlertDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toolbar;


import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.heroiclabs.nakama.api.User;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.listeners.MatchEventListener;
import fr.aboucorp.variantchess.app.managers.MatchManager;
import fr.aboucorp.variantchess.app.managers.boards.ClassicBoardManager;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;
import fr.aboucorp.variantchess.entities.Match;
import fr.aboucorp.variantchess.entities.boards.Board;
import fr.aboucorp.variantchess.entities.boards.ClassicBoard;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.events.GameEventSubscriber;
import fr.aboucorp.variantchess.entities.events.models.BoardEvent;
import fr.aboucorp.variantchess.entities.events.models.GameEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnEvent;
import fr.aboucorp.variantchess.entities.rules.ClassicRuleSet;
import fr.aboucorp.variantchess.libgdx.Board3dManager;

public class BoardActivity extends AndroidApplication implements GameEventSubscriber, MatchEventListener {
    private Button btn_end_turn;
    private Switch switch_tactical;
    private TextView lbl_turn;
    private TextView party_logs;
    public FrameLayout board_panel;

    private Board3dManager board3dManager;
    private ClassicBoardManager boardManager;
    private MatchManager matchManager;
    private SessionManager sessionManager;
    private Toolbar toolbar;
    private User user;
    private Match match;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_layout);
        if(savedInstanceState != null){
            this.match = (Match) savedInstanceState.getSerializable("match");
        }else{
            this.match = (Match) getIntent().getExtras().getSerializable("match");
        }
        this.bindViews();
        this.bindListeners();
        this.setToolbar();
        this.sessionManager = SessionManager.getInstance(this);
        this.user = this.sessionManager.getUser();
        this.initializeBoard();
        this.matchManager.setEventListener(this);
        this.matchManager.startParty(this.match);
    }



    private void setToolbar() {
        this.toolbar = this.findViewById(R.id.main_toolbar);
        this.toolbar.setTitle(this.getString(R.string.app_name));
        this.toolbar.setSubtitle(this.user != null ? user.getDisplayName() : "Disconnected");
        this.setActionBar(this.toolbar);
        this.toolbar.setNavigationOnClickListener(v -> this.onBackPressed());
        this.getActionBar().setDisplayHomeAsUpEnabled(true);
        this.getActionBar().setHomeButtonEnabled(false);
        this.getActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
    }

    private void bindListeners() {
        this.btn_end_turn.setOnClickListener(v -> {
            BoardActivity.this.matchManager.endTurn(null);
            this.runOnUiThread(() ->
                    BoardActivity.this.lbl_turn.setText("Turn of " + BoardActivity.this.matchManager.getPartyInfos()));
        });
        this.switch_tactical.setOnClickListener(v -> BoardActivity.this.boardManager.toogleTacticalView());
    }

    private void bindViews() {
        this.board_panel = findViewById(R.id.board);
        this.btn_end_turn = this.findViewById(R.id.btn_end_turn);
        this.lbl_turn = this.findViewById(R.id.lbl_turn);
        this.party_logs = this.findViewById(R.id.party_logs);
        this.switch_tactical = this.findViewById(R.id.switch_tactical);
    }

    private void initializeBoard(){
        GameEventManager gameEventManager = new GameEventManager();
        gameEventManager.subscribe(GameEvent.class,this,1);
        this.board3dManager = new Board3dManager();
        Board classicBoard = new ClassicBoard(gameEventManager);
        ClassicRuleSet classicRules = new ClassicRuleSet(classicBoard,gameEventManager);
        this.boardManager = new ClassicBoardManager(this.board3dManager, classicBoard, classicRules,gameEventManager);
        this.matchManager = new MatchManager(this.boardManager,gameEventManager);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        board_panel.addView(initializeForView(board3dManager, config));
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

    @Override
    public void OnMatchEvent(GameEvent event) {
        if(!(event instanceof TurnEvent)) {
            this.runOnUiThread(() ->
                    this.party_logs.append("\n" + event.message));
        }
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("If you exit the game you will loose the match");
        alertDialogBuilder.setPositiveButton("yes",
                (dialog, arg1) -> this.stopParty());
        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void stopParty() {
        this.matchManager.stopParty();
        this.board3dManager.exit();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("match", this.match);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem disconnect = this.toolbar.getMenu().findItem(R.id.menu_action_disconnect);
        MenuItem profile = this.toolbar.getMenu().findItem(R.id.menu_action_profil);
        MenuItem settings = this.toolbar.getMenu().findItem(R.id.menu_action_settings);
        disconnect.setVisible(false);
        profile.setVisible(true);
        settings.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_action_profil:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
