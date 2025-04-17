package services;

import entities.Person;
import utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonService implements IService<Person>{

    private Connection cnx;

    public PersonService(){
        cnx = MyConnection.getInstance().getConnection();
    }


    @Override
    public void create(Person person) throws SQLException {
        String query = "insert into person(firstName, lastName, age)" +
                " values('"+ person.getFirstName() +
                "','"+ person.getLastName() +"'," + person.getAge() + ")";
        Statement st = cnx.createStatement();
        st.executeUpdate(query);
    }

    @Override
    public void update(Person person) throws SQLException {
        String query = "update person set firstName = ?, lastName = ?, age = ? where id = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setString(1, person.getFirstName());
        ps.setString(2, person.getLastName());
        ps.setInt(3, person.getAge());
        ps.setInt(4, person.getId());
        ps.executeUpdate();
    }

    @Override
    public void delete(Person person) throws SQLException{

    }

    @Override
    public List<Person> readAll() throws SQLException {
        List<Person> persons = new ArrayList<>();
        String query = "select * from person";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(query);

        while (rs.next()){
            int id = rs.getInt("id");
            int age = rs.getInt("age");
            String firstName = rs.getString("firstName");
            String lastName = rs.getString("lastName");
            Person p = new Person(id, age, firstName, lastName);
            persons.add(p);
        }

        return persons;
    }
}
