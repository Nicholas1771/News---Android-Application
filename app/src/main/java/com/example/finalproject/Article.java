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

    Article(String title, String description, String date, String linkToArticle) {
        //Requires a title, description, date and link
        this.title = title;
        this.description = description;
        this.date = date;
        this.linkToArticle = linkToArticle;
    }

    //Getters
    String getTitle () {return title;}
    String getDescription () {return description;}
    String getDate () {return date;}
    String getLinkToArticle () {return linkToArticle;}
}
