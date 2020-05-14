package fr.aboucorp.variantchess.app.views.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;

import fr.aboucorp.variantchess.R;

public class ErrorDialogFragment extends DialogFragment {

    private final String message;
    public ErrorDialogFragment(String message) {
        this.message = message;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.error_dialog_layout, null);
        final AppCompatTextView message = view.findViewById(R.id.error_dialog_txt_message);
        message.setText(this.message);
        final AppCompatButton button = view.findViewById(R.id.error_dialog_btn_dismiss);
        button.setOnClickListener(v -> this.dismiss());
        builder.setView(view);
        return builder.create();
    }



}
