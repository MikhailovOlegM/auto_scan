package com.spring;

import com.SiteContainer;
import com.spring.model.FilterUrl;
import com.spring.model.User;
import com.spring.repository.FilterRespository;
import com.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class Handler {

    @Autowired
    private UserRepository repository;
    @Autowired
    private FilterRespository filterRespository;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public void test() {
        User oleg = new User();
        oleg.setUser_name("test");
        oleg.setUser_name("user_name");
        FilterUrl filterUrl = new FilterUrl();
        filterUrl.setUrl("https://rst.ua/oldcars/audi/?price[]=101&price[]=0&year[]=0&year[]=0&condition=0&engine[]=0&engine[]=0&fuel=0&gear=0&drive=0&results=1&saled=0&notcust=&sort=1&city=0&from=sform");

        repository.save(oleg);
        filterRespository.save(filterUrl);

        repository.findAll();
    }
}
