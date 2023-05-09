package ru.naumen.naumeninternshipwebapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.naumen.naumeninternshipwebapp.model.Person;
import ru.naumen.naumeninternshipwebapp.service.PersonService;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/")
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/people")
    public List<Person> listPeople() {
        return personService.listPeople();
    }

    @PostMapping("/file-uploading")
    public ResponseEntity<String> recieveFile(@RequestParam("file") MultipartFile file) {
        return personService.addPeopleViaFile(file);
    }

    @GetMapping("/getPerson/{name}")
    public Person getPerson(@PathVariable String name) {
        return personService.getPersonByName(name);
    }

    @DeleteMapping("/clear")
    public void clearPeopleList() {
        personService.deleteAll();
    }

    @GetMapping("req-amount")
    public int getRequestsAmount() {
        return personService.getPeopleRequestsAmount();
    }

    @GetMapping("freq")
    public ResponseEntity<Object> getFrequency() {
        return personService.getPeopleRequestsFreq();
    }

    @GetMapping("oldest")
    public List<Person> getOldestPeople() {
        return personService.getOldestPeople();
    }

}
