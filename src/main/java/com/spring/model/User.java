package com.spring.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "user_name")
    private String user_name;

    @Column(name = "first_name")
    private String first_name;

    @NotNull
    @Column(name = "chat_id")
    private Long chat_id;

    @OneToMany(mappedBy = "userFilter", fetch = FetchType.EAGER)
    private Set<FilterUrl> userFilterUrl = new HashSet<>();

    public Set<FilterUrl> getUserFilterUrl() {
        return userFilterUrl;
    }

    public Integer getId() {
        return id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setChatIt(Long chatId) {
        this.chat_id = chatId;
    }

    public Long getChat_id() {
        return chat_id;
    }
}
