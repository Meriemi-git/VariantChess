package fr.aboucorp.variantchess.app.views.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.views.activities.VariantChessActivity;

public class LinkDialogFragment extends DialogFragment implements Validator.ValidationListener{

    @Password(min = 8, scheme = Password.Scheme.ALPHA_NUMERIC)
    public EditText link_dialog_txt_pwd;

    private final String mail;
    private final String googleToken;

    private Validator validator;


    public LinkDialogFragment(String mail, String googleToken) {
        this.mail = mail;
        this.googleToken = googleToken;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.link_dialog_layout, null);
        final AppCompatTextView mail = view.findViewById(R.id.link_dialog_lbl_mail);
        mail.setText(this.mail);
        final AppCompatButton confirm = view.findViewById(R.id.link_dialog_btn_confirm);

        if(googleToken == null) {
            link_dialog_txt_pwd = view.findViewById(R.id.link_dialog_txt_pwd);
            validator = new Validator(this);
            validator.setValidationListener(this);
            confirm.setOnClickListener(v -> this.validator.validate());
        }else{
            confirm.setOnClickListener(v ->  ((VariantChessActivity) getActivity()).confirmLinkGoogleAccount(googleToken));
        }




        final AppCompatButton cancel = view.findViewById(R.id.link_dialog_btn_cancel);
        cancel.setOnClickListener(v -> dismiss() );

        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onValidationSucceeded() {
        ((VariantChessActivity) getActivity()).confirmLinkMailAccount(mail, link_dialog_txt_pwd.getText().toString());
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getActivity());

            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
