package com.matvey.perelman.grapher_for_android.ui.grapher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.matvey.perelman.grapher_for_android.model.MainModel;

public class GraphicsFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return new GraphicsView(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        MainModel.getInstance().openedWindow = MainModel.GRAPHICS;
    }
}