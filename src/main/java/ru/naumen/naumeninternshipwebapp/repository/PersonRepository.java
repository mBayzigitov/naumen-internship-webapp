package ru.naumen.naumeninternshipwebapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.naumen.naumeninternshipwebapp.model.Person;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    public Optional<Person> getPersonByName(String name);

    public List<Person> findAllByOrderByName();
}
