package ru.naumen.naumeninternshipwebapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.naumen.naumeninternshipwebapp.model.Person;
import ru.naumen.naumeninternshipwebapp.repository.PersonRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/")
public class PersonController {

    private PersonRepository personRepository;

    @Autowired
    public PersonController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping("/people")
    public List<Person> addPerson() {
        return personRepository.findAll();
    }

    @PostMapping("/file-uploading")
    public String recieveFile(@RequestParam("file") MultipartFile file) {
        Scanner scanner = null;
        Pattern pattern = Pattern.compile("(^|\s+)([A-ZА-ЯЁ][a-zа-яё]+)_([1-9]+)($|\s+)");
        int counter = 0;
        boolean areThereAnyExistedNames = false;
        List<String> existedNames = new ArrayList<>();

        try {
            scanner = new Scanner(file.getInputStream());
        } catch (Exception exc) {
            return "Error occured while uploading a file";
        }

        try {
            while (scanner.hasNextLine()) {
                String curLine = scanner.nextLine();

                Matcher matcher = pattern.matcher(curLine);
                while (matcher.find()) {
                    String name = matcher.group(2);
                    String age = matcher.group(3);

                    int numericAge;
                    try {
                        numericAge = Integer.parseInt(age);
                    } catch (NumberFormatException nfe) {
                        continue;
                    }

                    Person person = new Person(name, numericAge);

                    // exception in case when such record already exists
                    try {
                        personRepository.save(person);
                        counter++;
                    } catch (DataIntegrityViolationException e) {
                        areThereAnyExistedNames = true;
                        existedNames.add(person.getName());
                    }
                }
            }
        } catch (Exception exc) {
            return "Upload interrupted because of error";
        }


        if (counter == 0) return "Either entries of [Name]_[age] type were not found in the file,\n" +
                "or all of records provided already exist, 0 lines added";

        return (areThereAnyExistedNames) ?
                "File is uploaded successfully, " + counter + " lines added." +
                        "\nNames that weren't added, because they already exist: " +
                        existedNames :
                "File is uploaded successfully, " + counter + " lines added";
    }

    @GetMapping("/getPerson/{name}")
    public Person getPerson(@PathVariable String name) {
        return personRepository.getPersonByName(name);
    }

    @DeleteMapping("/clear")
    public void clearPeopleList() {
        personRepository.deleteAll();
    }

}
