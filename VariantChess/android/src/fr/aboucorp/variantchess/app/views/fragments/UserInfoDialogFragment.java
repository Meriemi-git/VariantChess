package fr.aboucorp.variantchess.app.views.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;

import com.heroiclabs.nakama.api.User;

import fr.aboucorp.variantchess.R;

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
        final AppCompatTextView firstname = view.findViewById(R.id.user_dialog_txt_firstname);
        final AppCompatTextView lastname = view.findViewById(R.id.user_dialog_txt_lastname);
        final AppCompatTextView mail = view.findViewById(R.id.user_dialog_txt_mail);
        if (user != null) {
            firstname.setText(user.getDisplayName());
            lastname.setText(user.getUsername());
            mail.setText(user.getMetadata());
        }
        alertDialogBuilder.setView(view)
                .setTitle(getString(R.string.user_info_dialog_title))
                .setPositiveButton(R.string.general_close, (dialog, which) -> dialog.cancel());
        return alertDialogBuilder.create();
    }

    public void setUser(User user) {
        this.user = user;
    }
}
