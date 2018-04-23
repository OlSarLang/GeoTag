package com.example.left4candy.geotag;

public class TitleName {
    private String title;

    public TitleName(){
        this("hej");
    }

    public TitleName(String title){
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
