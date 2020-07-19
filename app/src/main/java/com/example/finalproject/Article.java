package com.example.finalproject;

//This class represents and article from the BBC news website
class Article {

    //Title of the article
    private String title;

    //Description of the article
    private String description;

    //Date of the article
    private String date;

    //Link to the article webpage
    private String linkToArticle;

    //if true article is hidden from view (because not matching search terms)
    private boolean hidden;

    //if true article is to be hidden permanently
    private boolean permanentHidden;

    Article(String title, String description, String date, String linkToArticle) {
        //Requires a title, description, date and link
        this.title = title;
        this.description = description;
        this.date = date;
        this.linkToArticle = linkToArticle;
        hidden = false;
        permanentHidden = false;
    }

    //Getters
    String getTitle () {return title;}
    String getDescription () {return description;}
    String getDate () {return date;}
    String getLinkToArticle () {return linkToArticle;}
    boolean getHidden () {return hidden;}

    //Hides the article
    void hide() {
        hidden = true;
    }

    //Unhides the article
    void unhide() {

        //Only unhides the article if the article is not permanently hidden
        if (!permanentHidden) {
            hidden = false;
        }
    }

    //hides the article permanently (cant be reversed yet)
    void hidePermanently() {
        permanentHidden = true;
        hidden = true;
    }
}
