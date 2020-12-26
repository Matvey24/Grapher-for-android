package com.matvey.perelman.grapher_for_android.ui.helper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.matvey.perelman.grapher_for_android.R;
import com.matvey.perelman.grapher_for_android.model.MainModel;

public class HelperFragment extends Fragment {
    private HelperAdapter adapter;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_helper, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rv = view.findViewById(R.id.helper_view);
        adapter = new HelperAdapter();
        rv.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rv.setAdapter(adapter);
        MainModel.getInstance().openedWindow = MainModel.HELPER;
        setText();
    }
    public void setText(){
        adapter.strs = new String[][]{{"My grapher:", "the best", "unreal"}, {"My knowledge of android:", "the best"}, {"My graphers:", "For PC\nFor Android"}};
        adapter.notifyDataSetChanged();
    }
}