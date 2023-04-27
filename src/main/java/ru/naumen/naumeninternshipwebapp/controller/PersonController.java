package ru.naumen.naumeninternshipwebapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.homyakin.iuliia.Schemas;
import ru.homyakin.iuliia.Translator;
import ru.naumen.naumeninternshipwebapp.exception.NamePatternException;
import ru.naumen.naumeninternshipwebapp.model.Person;
import ru.naumen.naumeninternshipwebapp.repository.PersonRepository;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/")
public class PersonController {

    private PersonRepository personRepository;
    private final Translator translator = new Translator(Schemas.ICAO_DOC_9303);

    @Autowired
    public PersonController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping("/people")
    public List<Person> addPerson() {
        return personRepository.findAll();
    }

    @PostMapping("/file-uploading")
    public ResponseEntity<String> recieveFile(@RequestParam("file") MultipartFile file) {
        Scanner scanner = null;
        Pattern pattern = Pattern.compile("(^|\s+)([A-ZА-ЯЁ][a-zа-яё]+)_([1-9]+)($|\s+)");
        int counter = 0;

        try {
            scanner = new Scanner(file.getInputStream());
        } catch (Exception exc) {
            return new ResponseEntity<>("Ошибка, файл не был загружен", HttpStatus.NOT_FOUND);
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
                        continue;
                    }
                }
            }
        } catch (Exception exc) {
            return new ResponseEntity<>("Ошибка, файл не был загружен", HttpStatus.NOT_FOUND);
        }

        return (counter == 0) ?
                new ResponseEntity<>("Ни одной записи не добавлено, проверьте условия ввода данных", HttpStatus.OK) :
                new ResponseEntity<>(counter + " записей добавлено", HttpStatus.OK);
    }

    @GetMapping("/getPerson/{name}")
    public Person getPerson(@PathVariable String name) {
        name = name.trim();
        String namePattern = "([A-ZА-ЯЁ][a-zа-яё]+)";

        System.out.println(name + "; " + name.matches(namePattern));

        if (!name.matches(namePattern)) {
            throw new NamePatternException();
        }

        Optional<Person> person = personRepository.getPersonByName(name);
//        System.out.println(person.get().getName());

        return person.orElse(null);
    }

    @DeleteMapping("/clear")
    public void clearPeopleList() {
        personRepository.deleteAll();
    }

    @ExceptionHandler
    private ResponseEntity<String> handleInvalidNamePattern(NamePatternException npe) {
        return new ResponseEntity<>("Invalid name pattern", HttpStatus.NOT_FOUND);
    }

}
