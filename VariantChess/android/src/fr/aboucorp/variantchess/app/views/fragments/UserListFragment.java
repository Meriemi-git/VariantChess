package fr.aboucorp.variantchess.app.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.viewmodel.UserListAdapter;
import fr.aboucorp.variantchess.app.viewmodel.UserViewModel;

public class UserListFragment extends VariantChessFragment {
    private UserViewModel userViewModel;
    public RecyclerView recyclerview_users;
    private UserListAdapter userListAdapter;
    private Context context;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    private void initData() {
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.getUsers().observe(this, users -> userListAdapter.setUserList(users));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        userListAdapter = new UserListAdapter(context);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_list_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews();
        bindListeners();

        userViewModel.getUsers().observe(getActivity(), fruitlist -> {
            // update UI
            // Assign adapter to ListView
            recyclerview_users.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
            recyclerview_users.setLayoutManager(new LinearLayoutManager(context));
            recyclerview_users.setAdapter(userListAdapter);
        });
    }



    @Override
    protected void bindViews() {
        this.recyclerview_users = getView().findViewById(R.id.recyclerview_users);
    }

    @Override
    protected void bindListeners() {

    }
}
