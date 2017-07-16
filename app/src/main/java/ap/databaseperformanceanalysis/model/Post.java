package ap.databaseperformanceanalysis.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class Post {

    //Needs to be public to iBoxDB
    public String key;
    public String subject;
    public String title;
    public String content;
    public Author author;
    public Date date;

    //Needs these Strings to iBoxDB
    @JsonIgnore
    public String authorKey;
    @JsonIgnore
    public String dateString;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
        this.authorKey = author.getKey();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
        this.dateString = date.toString();
    }
}
