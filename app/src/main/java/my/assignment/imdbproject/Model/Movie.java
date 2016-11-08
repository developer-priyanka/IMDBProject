package my.assignment.imdbproject.Model;

import java.io.StringWriter;

/**
 * Created by root on 10/14/16.
 */

public class Movie {
    private String imagePath;
    private String title;
    private String description;
    private String lang;
    private String releaseDate;
    private String movieId;
    private String rating;

    public Movie(String name,String detail,String date,String language,String path,String id,String vote){
        title=name;
        description=detail;
        releaseDate=date;
        lang=language;
        imagePath=path;
        movieId=id;
        rating=vote;



    }


    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
