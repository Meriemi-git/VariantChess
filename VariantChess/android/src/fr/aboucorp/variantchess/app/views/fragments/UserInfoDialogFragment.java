package fr.aboucorp.variantchess.app.views.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.db.user.User;

public class UserInfoDialogFragment extends DialogFragment {
    private User user;
    public static UserInfoDialogFragment newInstance(User user) {
        UserInfoDialogFragment fragment = new UserInfoDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.setUser(user);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.user_info_dialog_layout, null);
        final AppCompatTextView firstname = view.findViewById(R.id.txt_firstname);
        final AppCompatTextView lastname = view.findViewById(R.id.txt_lastname);
        final AppCompatTextView mail = view.findViewById(R.id.txt_mail);
        if (user != null) {
            firstname.setText(user.displayName);
            lastname.setText(user.username);
            mail.setText(user.mail);
        }
        alertDialogBuilder.setView(view)
                .setTitle(getString(R.string.user_info_dialog_title))
                .setPositiveButton(R.string.close, (dialog, which) -> dialog.cancel());
        return alertDialogBuilder.create();
    }

    public void setUser(User user) {
        this.user = user;
    }
}
