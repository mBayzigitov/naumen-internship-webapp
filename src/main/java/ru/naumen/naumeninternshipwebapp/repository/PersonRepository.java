package ru.naumen.naumeninternshipwebapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.naumen.naumeninternshipwebapp.model.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    public Person getPersonByName(String name);
}
