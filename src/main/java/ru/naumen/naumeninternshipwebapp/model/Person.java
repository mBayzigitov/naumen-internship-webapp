package ru.naumen.naumeninternshipwebapp.model;

import jakarta.persistence.*;

@Entity
@Table
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "age")
    private int age;

    @Column(name = "count")
    private long count;

    public Person() {

    }

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }


}
