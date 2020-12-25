package com.matvey.perelman.grapher_for_android.ui.elements.elements_list;

public class TextElement {
    public int color;
    public String name;
    private String text = "";
    public TextElementView tev;
    public String getText(){
        if(tev != null){
            String text = tev.getText(this);
            if(text != null) {
                this.text = text;
                return text;
            }
        }
        return text;
    }
    public void setTextFromFile(String text){
        this.text = text;
        if(tev != null){
            tev.setTextFromFile(text, this);
        }
    }
    public void setText(String text){
        this.text = text;
    }
    public String text(){return text;}
    public void setName(String name){
        this.name = name;
        if(tev != null){
            tev.setName(name, this);
        }
    }
    public void setColor(int c){
        this.color = c;
        if(tev != null){
            tev.setColor(c, this);
        }
    }
}
