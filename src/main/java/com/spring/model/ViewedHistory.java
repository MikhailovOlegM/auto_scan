package com.spring.model;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "viewed_history")
public class ViewedHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User userViewedHistory;

    @NotNull
    @Column(name = "site_id")
    private String site_id;

    @NotNull
    @Column(name = "viewed_date")
    private LocalDate viewed_date;

    public ViewedHistory() {
    }

    public ViewedHistory(User userViewedHistory, @NotNull String site_id, @NotNull LocalDate viewed_date) {
        this.userViewedHistory = userViewedHistory;
        this.site_id = site_id;
        this.viewed_date = viewed_date;
    }

    public Integer getId() {
        return id;
    }

    public User getUserViewedHistory() {
        return userViewedHistory;
    }

    public void setUserViewedHistory(User userViewedHistory) {
        this.userViewedHistory = userViewedHistory;
    }

    public String getSiteId() {
        return site_id;
    }

    public void setSiteId(String siteId) {
        this.site_id = siteId;
    }

    public LocalDate getViewedDate() {
        return viewed_date;
    }

    public void setViewedDate(LocalDate viewed_date) {
        this.viewed_date = viewed_date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ViewedHistory that = (ViewedHistory) o;
        return id.equals(that.id) &&
                site_id.equals(that.site_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, site_id);
    }
}
