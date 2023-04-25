package ru.naumen.naumeninternshipwebapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.naumen.naumeninternshipwebapp.model.Person;
import ru.naumen.naumeninternshipwebapp.repository.PersonRepository;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/")
public class PersonController {

    @Autowired
    private PersonRepository personRepository;

    @GetMapping("/people")
    public List<Person> addPerson() {
        return personRepository.findAll();
    }

}
