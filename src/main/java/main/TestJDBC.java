package main;

import entities.Person;
import services.PersonService;
import utils.MyConnection;

import java.sql.SQLException;

public class TestJDBC {

    public static void main(String[] args) {
        PersonService ps = new PersonService();
        Person p = new Person(1,22, "Ali", "Ben Foulen");


        try {
            System.out.println(ps.readAll());
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

    }
}
