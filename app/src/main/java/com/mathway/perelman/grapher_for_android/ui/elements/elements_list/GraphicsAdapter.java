package com.mathway.perelman.grapher_for_android.ui.elements.elements_list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mathway.perelman.grapher_for_android.R;
import com.mathway.perelman.grapher_for_android.model.MainModel;
import com.mathway.perelman.grapher_for_android.controller.ModelUpdater;

public class GraphicsAdapter extends RecyclerView.Adapter<TextElementView> {
    private final ModelUpdater updater;
    private final LinearLayoutManager llm;
    public GraphicsAdapter(LinearLayoutManager llm){
        this.llm = llm;
        this.updater = MainModel.getInstance().updater;
    }
    @NonNull
    @Override
    public TextElementView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.text_element, parent, false);
        return new TextElementView(
                itemView,
                this::onRemove,
                this::openSettings,
                this::textChanged);
    }

    @Override
    public void onBindViewHolder(@NonNull TextElementView holder, int position) {
        holder.set(position, updater.elements.get(position));
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull TextElementView holder) {
        holder.onDetach();
    }

    private void onRemove(int idx){
        updater.remove(idx, true);
        notifyDataSetChanged();
    }

    public void update(){
        updater.getMain().runOnUiThread(this::notifyDataSetChanged);
    }

    private void openSettings(int idx){
        updater.startSettings(idx);
    }

    private void textChanged(int idx){
        updater.getMain().recalculate();
    }
    public void make_element(){
        TextElement te = new TextElement();
        updater.elements.add(te);
        llm.scrollToPosition(updater.elements.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return updater.elements.size();
    }
}
