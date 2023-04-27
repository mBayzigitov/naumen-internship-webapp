package ru.naumen.naumeninternshipwebapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.naumen.naumeninternshipwebapp.model.Person;
import ru.naumen.naumeninternshipwebapp.repository.PersonRepository;

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
        List<String> ignoredNames = new ArrayList<>();

        try {
            scanner = new Scanner(file.getInputStream());
        } catch (Exception exc) {
            return "Ошибка, файл не был загружен";
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
                        if (numericAge < 1 || numericAge > 120) {
                            ignoredNames.add(name);
                            continue;
                        }
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
                        ignoredNames.add(person.getName());
                    }
                }
            }
        } catch (Exception exc) {
            return "Ошибка, файл не был загружен";
        }

        if (counter == 0) return "В файле не найдены записи типа [Имя]_[возраст] " +
                ", либо записи не удовлетворяют условиям, добавлено 0 строк";

        return (areThereAnyExistedNames) ?
                "Файл загружен успешно, " + counter + " записей добавлено." +
                        "\nИмена, которые не были добавлены: " +
                        ignoredNames :
                "Файл загружен успешно, " + counter + " записей добавлено.";
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
