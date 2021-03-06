package com.mathway.perelman.grapher_for_android.ui.elements.elements_list;

import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mathway.perelman.grapher_for_android.R;
import com.mathway.perelman.grapher_for_android.model.MainModel;

public class TextElementView extends RecyclerView.ViewHolder {
    private final TextView name;
    private final EditText text;
    private final View v;
    private int index;
    private TextElement data;

    public TextElementView(View v, OnClickIndexed remove_listener, OnClickIndexed settings_listener, OnClickIndexed text_changed) {
        super(v);
        this.v = v;
        name = v.findViewById(R.id.graphic_name);
        text = v.findViewById(R.id.graphic_text);
        Button remove = v.findViewById(R.id.graphic_delete);
        remove.setOnClickListener((view) -> remove_listener.onClick(index));
        remove.setOnLongClickListener((view) -> {
            settings_listener.onClick(index);
            return true;
        });
        text.setImeOptions(EditorInfo.IME_ACTION_DONE);
        text.setOnEditorActionListener((view, act, ev) -> {
            text_changed.onClick(index);
            return true;
        });
    }

    public void onDetach() {
        data.setText(text());
    }

    public void setTextFromFile(String text, TextElement e) {
        if (e == data)
            this.text.post(()->this.text.setText(text));
    }

    private String text() {
        if (text.getText() == null)
            return "";
        else
            return text.getText().toString();
    }

    public String getText(TextElement asker) {
        if (data == asker) {
            return text();
        } else {
            return null;
        }
    }

    public void set(int index, TextElement element) {
        this.index = index;
        this.data = element;
        element.tev = this;
        name.setText(element.name);
        setColor(element.color, element);
        text.setText(element.text());
    }

    public void setColor(int color, TextElement e) {
        if (e == data) {
            if(MainModel.dark_theme)
                name.setTextColor(color ^ 0x00ffffff);
            else
                name.setTextColor(color);
        }
    }

    public void setName(String name, TextElement e) {
        if (e == data)
            v.getHandler().post(() -> this.name.setText(name));
    }

}
