package fr.aboucorp.variantchess.app.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;
import fr.aboucorp.variantchess.app.utils.FragmentTag;
import fr.aboucorp.variantchess.app.viewmodel.UserViewModel;
import fr.aboucorp.variantchess.app.views.activities.VariantChessActivity;

public class HomeFragment extends VariantChessFragment {
    private Button home_btn_online_game;
    private Button home_btn_offline_game;
    private SessionManager sessionManager;
    private UserViewModel userViewModel;

    public HomeFragment() {
    }

    @Override
    protected void bindViews() {
        this.home_btn_online_game = this.getView().findViewById(R.id.home_btn_online_game);
        this.home_btn_offline_game = this.getView().findViewById(R.id.home_btn_offline_game);

    }

    @Override
    protected void bindListeners() {
        this.home_btn_offline_game.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putBoolean(ONLINE_ARGS_KEY, false);
            ((VariantChessActivity) this.getActivity()).setFragment(GameModeFragment.class, FragmentTag.GAME, args);
        });
        this.home_btn_online_game.setOnClickListener(v -> {
            if (this.userViewModel.getConnected().getValue() != null) {
                Bundle args = new Bundle();
                args.putBoolean(ONLINE_ARGS_KEY, true);
                ((VariantChessActivity) this.getActivity()).setFragment(GameModeFragment.class, FragmentTag.GAME, args);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
                builder.setTitle(R.string.home_connection_required_title);
                builder.setMessage(R.string.home_connection_required_title_message);
                builder.setPositiveButton(R.string.general_lbl_confirm, (dialog, id) -> {
                    ((VariantChessActivity) this.getActivity()).setFragment(AccountFragment.class, FragmentTag.ACCOUNT, null);
                });
                builder.setNegativeButton(R.string.general_close, (dialog, id) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
    }

    @Override
    public void onAttach(@androidx.annotation.NonNull Context context) {
        super.onAttach(context);
        this.userViewModel = new ViewModelProvider(this.getActivity()).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.bindViews();
        this.bindListeners();
        this.sessionManager = SessionManager.getInstance(this.getActivity());
    }
}
