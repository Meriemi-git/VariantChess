/*
package fr.aboucorp.variantchess.app.views.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import fr.aboucorp.variantchess.app.di.factory.InjectingFragmentFactory;

public class InjectingNavHostFragment extends NavHostFragment {
    @Inject
    protected InjectingFragmentFactory daggerFragmentInjectionFactory;

    @Override
    public void onAttach(@NonNull Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        getChildFragmentManager().setFragmentFactory(daggerFragmentInjectionFactory);
        super.onCreate(savedInstanceState);
    }
}
*/
