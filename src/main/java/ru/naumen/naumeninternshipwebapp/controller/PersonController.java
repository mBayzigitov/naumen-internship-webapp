package ru.naumen.naumeninternshipwebapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import ru.homyakin.iuliia.Schemas;
import ru.homyakin.iuliia.Translator;
import ru.naumen.naumeninternshipwebapp.exception.NamePatternException;
import ru.naumen.naumeninternshipwebapp.model.Person;
import ru.naumen.naumeninternshipwebapp.repository.PersonRepository;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
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
        return personRepository.findAllByOrderByName();
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

        if (!name.matches(namePattern)) {
            throw new NamePatternException();
        }

        Optional<Person> person = personRepository.getPersonByName(name);

        if (person.isEmpty()) {
            int newPerson_age;
            RestTemplate restTemplate = new RestTemplate();

            // using api.agify.io interface
            String url = "https://api.agify.io/?name=" + translator.translate(name);
            Person new_person = null;

            try {
                new_person = restTemplate.getForObject(url, Person.class);
            } catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerExc) {
                new_person = createPersonWithRandomAgeValue(name);
            }

            if (new_person == null || new_person.getAge() == 0) {
                new_person = createPersonWithRandomAgeValue(name);
            }

            new_person.setName(name);
            new_person.setCount(1);

            personRepository.save(new_person);
            return new_person;
        }

        // incrementing person's count value inside Optional
        person.get().setCount(person.get().getCount() + 1);
        personRepository.save(person.get());

        return person.orElse(null);
    }

    @DeleteMapping("/clear")
    public void clearPeopleList() {
        personRepository.deleteAll();
    }

    @GetMapping("req-amount")
    public int getRequestsAmount() {
        Integer amount = personRepository.getRequestsAmount();

        return (amount == null) ?
                0 : amount;
    }

    @GetMapping("freq")
    public ResponseEntity<Object> getFrequency() {
        List<Person> people = personRepository.getAllWhereCountNotZero();
        Integer numberOfRequests = personRepository.getRequestsAmount();

        List<Map<String, String>> data = new ArrayList<>();

        try {
            if (people.isEmpty()
                    || numberOfRequests == null
                    || numberOfRequests == 0) return new ResponseEntity<>(data, HttpStatus.OK);

            for (int i = 0; i < people.size(); i++) {
                data.add(new HashMap<>());
                data.get(i).put("name", people.get(i).getName());
                data.get(i).put("amount", Integer.toString(people.get(i).getCount()));

                double freqValue = (double)people.get(i).getCount() / (double)numberOfRequests * 100.00;
                NumberFormat formatter = new DecimalFormat("#0.00");

                data.get(i).put("freq", formatter.format(freqValue));
            }
        } catch (Exception exc) {
            exc.printStackTrace();
            return new ResponseEntity<>("Error occured", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("oldest")
    public Person getOldestPerson() {
        return personRepository.findTopByOrderByAgeDesc().orElse(null);
    }

    @ExceptionHandler
    private ResponseEntity<String> handleInvalidNamePattern(NamePatternException npe) {
        return new ResponseEntity<>("Invalid name pattern", HttpStatus.NOT_FOUND);
    }

    private static Person createPersonWithRandomAgeValue(String name) {
        return new Person(name,
                (int) ((Math.random() * (120 - 1)) + 1)); // creating random age value between 1 and 120
    }

}
