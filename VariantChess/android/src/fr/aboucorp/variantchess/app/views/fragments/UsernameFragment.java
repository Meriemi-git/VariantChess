package fr.aboucorp.variantchess.app.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.heroiclabs.nakama.api.User;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.exceptions.UsernameDuplicateException;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;
import fr.aboucorp.variantchess.app.views.activities.MainActivity;
import fr.aboucorp.variantchess.app.views.activities.VariantChessActivity;

public class UsernameFragment extends VariantChessFragment  implements Validator.ValidationListener{
    private Button username_btn_validate;
    @NotEmpty
    private EditText username_txt_username;
    private Validator validator;
    private SessionManager sessionManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.username_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.bindViews();
        this.bindListeners();
        this.validator = new Validator(this);
        this.validator.setValidationListener(this);
        this.sessionManager = SessionManager.getInstance(this.getActivity());
    }

    @Override
    protected void bindViews() {
        this.username_btn_validate = this.getView().findViewById(R.id.username_btn_validate);
        this.username_txt_username = this.getView().findViewById(R.id.username_txt_username);
    }

    @Override
    protected void bindListeners() {
        this.username_btn_validate.setOnClickListener(v -> this.validator.validate());
    }


    @Override
    public void onValidationSucceeded() {
        try {
            User user = this.sessionManager.updateDisplayName(this.username_txt_username.getText().toString());
            ((VariantChessActivity) this.getActivity()).userIsConnected(user);
        } catch (UsernameDuplicateException e) {
            Toast.makeText(this.getActivity(), R.string.username_already_exists, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this.getActivity());
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this.getActivity(), message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
