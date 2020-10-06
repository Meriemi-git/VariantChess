package fr.aboucorp.variantchess.app.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.MatchPresenceEvent;
import com.heroiclabs.nakama.MatchmakerMatched;
import com.heroiclabs.nakama.api.User;

import java.util.List;
import java.util.concurrent.ExecutionException;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.db.entities.ChessUser;
import fr.aboucorp.variantchess.app.db.entities.GameRules;
import fr.aboucorp.variantchess.app.db.viewmodel.UserViewModel;
import fr.aboucorp.variantchess.app.multiplayer.MatchmakingListener;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;
import fr.aboucorp.variantchess.app.utils.AsyncHandler;
import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.ChessMatch;
import fr.aboucorp.variantchess.entities.Player;

public class MatchmakingFragment extends VariantChessFragment implements MatchmakingListener {
    /**
     * Widgets
     */
    private ProgressBar progress_bar;
    private Chronometer matchmaking_chrono;
    private Button btn_cancel;
    /**
     * Nakama multiplayer session manager
     */
    private SessionManager sessionManager;
    /**
     * Nakama multiplayer session manager
     */
    private String matchmakingTicket;

    private GameRules gameRules;
    private ChessMatch chessMatch;
    private ChessUser chessUser;
    private UserViewModel userViewModel;

    @Override
    protected void bindViews() {
        this.progress_bar = this.getView().findViewById(R.id.progress_bar);
        this.matchmaking_chrono = this.getView().findViewById(R.id.matchmaking_chrono);
        this.btn_cancel = this.getView().findViewById(R.id.btn_cancel);
    }

    @Override
    protected void bindListeners() {
        this.btn_cancel.setOnClickListener(v -> cancelMatchMaking(matchmakingTicket));
    }

    @Override
    public void onMatchmakerMatched(MatchmakerMatched matched) {
        Log.i("fr.aboucorp.variantchess", "onMatchmakerMatched " + matched);
        AsyncHandler handler = new AsyncHandler() {
            @Override
            protected void executeAsync() throws Exception {
                // TODO need entire refocto
                Match match = sessionManager.joinMatchByToken(matched.getToken());
                List<User> users = sessionManager.getUsersFromMatched(matched);
                Player white = new Player(users.get(0).getUsername(), ChessColor.WHITE, users.get(0).getId());
                Player black = new Player(users.get(1).getUsername(), ChessColor.BLACK, users.get(1).getId());
                chessMatch = new ChessMatch();
                chessMatch.setWhitePlayer(white);
                chessMatch.setBlackPlayer(black);
                chessMatch.setMatchId(match.getMatchId());
            }

            @Override
            protected void callbackOnUI() {
                matchmaking_chrono.stop();
                Bundle args = new Bundle();
                args.putSerializable("chessMatch", chessMatch);
                args.putSerializable("chessUser", chessUser);
            }

            @Override
            protected void error(Exception ex) {
                super.error(ex);
                Toast.makeText(getContext(), R.string.matchmaking_cannot_join_match, Toast.LENGTH_LONG).show();
                cancelMatchMaking(matchmakingTicket);
                matchmaking_chrono.stop();
            }
        };
        handler.start();
    }

    @Override
    public void onMatchData(MatchData matchData) {
        Log.i("fr.aboucorp.variantchess", "onMatchData opcode" + matchData.getOpCode());
    }

    @Override
    public void onMatchPresence(MatchPresenceEvent matchPresence) {

    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        this.gameRules = (GameRules) args.getSerializable("gamerules");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.matchmaking_layout, container, false);
        if (savedInstanceState != null) {
            this.gameRules = (GameRules) savedInstanceState.getSerializable("gamerules");
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            this.gameRules = (GameRules) savedInstanceState.getSerializable("gamerules");
        }
        this.sessionManager = SessionManager.getInstance(getActivity());
        this.sessionManager.setMatchmakingListener(this);
        this.bindViews();
        this.bindListeners();
        this.userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        this.userViewModel.getConnected().observe(getViewLifecycleOwner(), connected -> {
            chessUser = connected;
            this.launchMatchmaking();
        });
    }

    private void launchMatchmaking() {
        this.matchmaking_chrono.start();
        try {
            this.sessionManager.launchMatchMaking(gameRules.name);
        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.e("fr.aboucorp.variantchess", "Error during matchmaking launch message" + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e("fr.aboucorp.variantchess", "Error during matchmaking launch message" + e.getMessage());
        }
    }

    private void cancelMatchMaking(String ticket) {
        AsyncHandler handler = new AsyncHandler() {
            @Override
            protected void executeAsync() {
                sessionManager.cancelMatchMaking(ticket);
                btn_cancel.setEnabled(false);
            }

            @Override
            protected void callbackOnUI() {
                Toast.makeText(getContext(), R.string.matchmaking_cancelled, Toast.LENGTH_LONG).show();
                matchmakingTicket = null;
                btn_cancel.setEnabled(false);
                matchmaking_chrono.stop();
            }

            @Override
            protected void error(Exception ex) {
                super.error(ex);
                Toast.makeText(getContext(), R.string.matchmaking_cancelled, Toast.LENGTH_LONG).show();
                matchmakingTicket = null;
                btn_cancel.setEnabled(false);
                matchmaking_chrono.stop();
            }
        };
        handler.start();
    }
}
