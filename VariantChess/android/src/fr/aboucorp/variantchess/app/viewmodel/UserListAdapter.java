package fr.aboucorp.variantchess.app.viewmodel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.heroiclabs.nakama.api.User;

import java.util.List;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.views.fragments.UserInfoDialogFragment;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {
    private LayoutInflater layoutInflater;
    private List<User> userList;
    private Context context;

    public UserListAdapter(Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = layoutInflater.inflate(R.layout.item_list_user, parent, false);
        return new UserListAdapter.UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        if (userList == null) {
            return;
        }

        final User user = userList.get(position);
        if (user != null) {
            holder.userText.setText(user.getDisplayName() + " " + user.getUsername());
            holder.itemView.setOnClickListener(v -> {
                DialogFragment dialogFragment = UserInfoDialogFragment.newInstance(user);
                dialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "yolo");
            });
        }
    }

    @Override
    public int getItemCount() {
        if (userList == null) {
            return 0;
        } else {
            return userList.size();
        }
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView userText;
        public UserViewHolder(View itemView) {
            super(itemView);
            userText = itemView.findViewById(R.id.tvuser);
        }
    }
}
