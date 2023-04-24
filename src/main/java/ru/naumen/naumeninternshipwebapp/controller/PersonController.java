package ru.naumen.naumeninternshipwebapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.naumen.naumeninternshipwebapp.model.Person;
import ru.naumen.naumeninternshipwebapp.repository.PersonRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("")
public class PersonController {

    @Autowired
    private PersonRepository personRepository;

    @GetMapping("")
    public List<Person> addPerson() {
        personRepository.save(new Person("Mike", 21));
        return new ArrayList<>(Collections.singletonList(new Person("Mike", 21)));
    }

}
