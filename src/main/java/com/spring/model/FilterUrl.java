package com.spring.model;


import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "filter")
public class FilterUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User userFilter;

    @NotNull
    @Column(name = "url")
    private String url;

    @NotNull
    @Column(name = "title")
    private String title;

    public Integer getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public void setUserFilter(User userFilter) {
        this.userFilter = userFilter;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
