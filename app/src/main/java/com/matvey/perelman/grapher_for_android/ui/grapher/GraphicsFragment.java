package com.matvey.perelman.grapher_for_android.ui.grapher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.matvey.perelman.grapher_for_android.controller.MainModel;

public class GraphicsFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MainModel.getInstance().openedWindow = MainModel.GRAPHICS;
        return new GraphicsView(getContext());
    }

}