package com.matvey.perelman.grapher_for_android.ui.helper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.matvey.perelman.grapher_for_android.R;

public class HelperAdapter extends RecyclerView.Adapter<HelperAdapter.HelperHolder> {
    private String[][] strs;
    public HelperAdapter(){

    }
    public void resetContent(String[][] strs){
        this.strs = strs;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public HelperHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.helper_element, parent, false);
        return new HelperHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HelperHolder holder, int position) {
        holder.set(strs[position]);
    }

    @Override
    public int getItemCount() {
        return strs.length;
    }

    public static class HelperHolder extends RecyclerView.ViewHolder{
        private final TextView title;
        private final TextView content;
        public HelperHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.element_title);
            content = itemView.findViewById(R.id.element_content);
        }
        public void set(String[] arr){
            title.setText(arr[0]);
            content.setText("");
            for(int i = 1; i < arr.length; ++i){
                content.append(arr[i]);
                if(i != arr.length - 1)
                    content.append("\n");
            }
        }
    }
}
