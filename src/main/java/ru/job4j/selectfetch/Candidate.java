package ru.job4j.selectfetch;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "candidate_fetch")
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private int experience;
    private int salary;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private VacanciesDB vacanciesDB;

    public Candidate() {
    }

    public Candidate(String name, int experience, int salary) {
        this.name = name;
        this.experience = experience;
        this.salary = salary;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public VacanciesDB getVacanciesDB() {
        return vacanciesDB;
    }

    public void setVacanciesDB(VacanciesDB vacanciesDB) {
        this.vacanciesDB = vacanciesDB;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Candidate candidate = (Candidate) o;
        return id == candidate.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Candidate{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", experience=" + experience
                + ", salary=" + salary
                + ", vacanciesDB=" + vacanciesDB
                + '}';
    }
}
