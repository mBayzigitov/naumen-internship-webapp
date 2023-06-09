package ru.naumen.naumeninternshipwebapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final Translator translator = new Translator(Schemas.ICAO_DOC_9303); // using Iuliia library for cyrillic transliteration

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    ////////////////////////////////////////////////////////////////////////////////////
    // <------------------------ REPOSITORY ACCESS METHODS ------------------------>
    ////////////////////////////////////////////////////////////////////////////////////

    @Transactional(readOnly = true)
    public List<Person> listPeople() {
        return personRepository.findAllByOrderByName();
    }

    @Transactional
    public Person save(Person person) {
        return personRepository.save(person);
    }

    @Transactional(readOnly = true)
    public Optional<Person> getPersonByNameFromRepository(String name) {
        return personRepository.getPersonByName(name);
    }

    @Transactional
    public void deleteAll() {
        personRepository.deleteAll();
    }

    @Transactional(readOnly = true)
    public List<Person> getOldestPeople() {
        return personRepository.findOldestPeople();
    }

    @Transactional(readOnly = true)
    public List<Person> findAllByOrderByCountDesc() {
        return personRepository.findAllByOrderByCountDesc();
    }

    @Transactional(readOnly = true)
    public Integer getRequestsAmount() {
        return personRepository.getRequestsAmount();
    }

    @Transactional(readOnly = true)
    public int getPeopleRequestsAmount() {
        Integer amount = personRepository.getRequestsAmount();

        return (amount == null) ?
                0 : amount;
    }
    // <------------------------------------------------------------------------>


    ///////////////////////////////////////////////////////////////////////
    // <------------------------ COMMON LOGIC ------------------------>
    ///////////////////////////////////////////////////////////////////////
    public ResponseEntity<String> addPeopleViaFile(MultipartFile file) {
        Scanner scanner;

        /* 1. (^|s+)
              This is the beginning of the regular expression and it matches either
              the start of a string or one or more whitespace characters at the
              beginning of a string. The parentheses indicate that this is a capturing group.
           2. ([A-ZА-ЯЁ][a-zа-яё]+)
              This matches a word that starts with an uppercase letter (A-Z or А-ЯЁ in Cyrillic)
              followed by one or more lowercase letters (a-z or а-яё in Cyrillic).
              The parentheses indicate that this is a capturing group.
           3. _
            This matches an underscore character.
           4. ([1-9]+)
              This matches one or more digits from 1 to 9. The parentheses indicate that this is a capturing group.
           5. ($|s+)
              This matches either the end of a string or one or more whitespace characters at the end of a string.
              The parentheses indicate that this is a capturing group.
        */
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
                    } catch (NumberFormatException nf_exc) {
                        continue;
                    }

                    Person person = new Person(name, numericAge);

                    // exception in case when such record already exists
                    try {
                        save(person);
                        counter++;
                    } catch (DataIntegrityViolationException exception) {
                        continue; // just skip invalid record
                    }
                }
            }
        } catch (Exception exc) {
            return new ResponseEntity<>("Ошибка, файл не был загружен", HttpStatus.NOT_FOUND);
        }

        return (counter == 0) ?
                new ResponseEntity<>("Ни одной записи не добавлено, проверьте условия ввода данных", HttpStatus.OK) :
                new ResponseEntity<>(counter + " записи(-ей) добавлено", HttpStatus.OK);
    }

    public Person getPersonByName(String name) {
        name = name.trim();

        /*
              This regexp matches a word that starts with an uppercase letter (A-Z or А-ЯЁ in Cyrillic)
              followed by one or more lowercase letters (a-z or а-яё in Cyrillic).
              The parentheses indicate that this is a capturing group.
         */
        String namePattern = "([A-ZА-ЯЁ][a-zа-яё]+)";

        if (!name.matches(namePattern)) {
            throw new NamePatternException();
        }

        Optional<Person> person = getPersonByNameFromRepository(name);

        if (person.isEmpty()) {
            Person new_person = createPersonViaApi(name);
            save(new_person);
            return new_person;
        }

        // incrementing person's count value inside Optional
        person.get().setCount(person.get().getCount() + 1);
        save(person.get());

        return person.orElse(null);

    }

    private Person createPersonViaApi(String name) {
        String requestURL = "https://api.agify.io/?name=";

        int newPerson_age;
        RestTemplate restTemplate = new RestTemplate();

        /*
            Using api.agify.io interface. This service responses with null age when sending cyrillic name
            so in order to recieve valid responses from api I use transliteration with Iuliia lib.
        */
        String url = requestURL + translator.translate(name);
        Person new_person;

        try {
            new_person = restTemplate.getForObject(url, Person.class);
        } catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerExc) {
            new_person = createPersonWithRandomAgeValue(name);
        }

        if (new_person == null || new_person.getAge() < 1 || new_person.getAge() > 120) {
            new_person = createPersonWithRandomAgeValue(name);
        }

        new_person.setName(name);
        new_person.setCount(1);

        return new_person;
    }

    private static Person createPersonWithRandomAgeValue(String name) {
        return new Person(name,
                (int) ((Math.random() * (120 - 1)) + 1)); // creating random age value between 1 and 120
    }

    public ResponseEntity<Object> getPeopleRequestsFreq() {
        List<Person> people = findAllByOrderByCountDesc();
        int numberOfRequests = getPeopleRequestsAmount();

        if (numberOfRequests == 0) numberOfRequests = 1; // avoiding division by zero

        List<Map<String, String>> data = new ArrayList<>();

        try {
            if (people.isEmpty()) return new ResponseEntity<>(data, HttpStatus.OK);

            /*
            this creates json with fields 'name', 'amount' and 'freq' for further
            serialization in PersonStats TS object
             */
            for (int i = 0; i < people.size(); i++) {
                data.add(new HashMap<>());
                data.get(i).put("name", people.get(i).getName());
                data.get(i).put("amount", Integer.toString(people.get(i).getCount()));

                double freqValue = (double)people.get(i).getCount() / (double)numberOfRequests * 100.00;
                NumberFormat formatter = new DecimalFormat("#0.00"); // reduce the number to two decimal places

                data.get(i).put("freq", formatter.format(freqValue));
            }
        } catch (Exception exc) {
            exc.printStackTrace();
            return new ResponseEntity<>("Error occured", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

}
