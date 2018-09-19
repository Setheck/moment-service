package com.setheck.momentservice.api;

import com.fasterxml.jackson.annotation.*;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.*;
import java.util.Date;

@Entity
@Table(name = "moments")
public class Moment
{
    public static final Moment EMPTY = new Moment(0, "","", false, new Date(0));

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy=GenerationType.AUTO)
    @JsonProperty
    private Integer id;

    @Column(name = "text")
    @JsonProperty
    private String text;

    @Column(name = "author")
    @JsonProperty
    private String author;

    @Column(name = "approved")
    @ColumnDefault("false")
    @JsonProperty
    private boolean approved;

    @CreationTimestamp
    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty
    private Date created;

    private Moment() {
        // Jackson deserialization
    }

    public Moment(Integer id, String text, String author, boolean approved, Date created) {
        this.id = id;
        this.text = text;
        this.author = author;
        this.approved = approved;
        this.created = created;
    }

    public Integer getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isApproved() {
        return approved;
    }

    public Date getCreated() {
        return created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Moment)) return false;
        Moment moment = (Moment) o;
        return id.equals(moment.id) &&
                Objects.equals(text, moment.text) &&
                Objects.equals(author, moment.author) &&
                Objects.equals(created, moment.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, author, created);
    }

    @Override
    public String toString() {
        return "Moment{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", author='" + author + '\'' +
                ", approved='" + approved + '\'' +
                ", created=" + created +
                '}';
    }
}
