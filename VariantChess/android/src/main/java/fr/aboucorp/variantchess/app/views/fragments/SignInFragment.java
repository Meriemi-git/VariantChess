package fr.aboucorp.variantchess.app.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.db.entities.ChessUserRepository;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;


public class SignInFragment extends VariantChessFragment  {
    private EditText txt_mail;

    private EditText txt_pwd;

    private Button btn_mail_connect;


    private SessionManager sessionManager;

    private ChessUserRepository chessUserRepository;

    public SignInFragment() {
    }

    @Override
    protected void bindViews() {
        this.txt_mail = this.getView().findViewById(R.id.signin_txt_mail);
        this.txt_pwd = this.getView().findViewById(R.id.signin_txt_pwd);
        this.btn_mail_connect = this.getView().findViewById(R.id.signin_btn_mail_connect);
    }

    @Override
    protected void bindListeners() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sign_in_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.sessionManager = SessionManager.getInstance(this.getActivity());
        this.bindViews();
        this.bindListeners();

        this.chessUserRepository = new ChessUserRepository(this.getActivity().getApplication());
    }
}
