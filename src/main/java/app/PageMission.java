package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


/**
 * Example Index HTML class using Javalin
 * <p>
 * Generate a static HTML page using Javalin
 * by writing the raw HTML into a Java String object
 *
 * @author Timothy Wiley, 2023. email: timothy.wiley@rmit.edu.au
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 */
public class PageMission implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/PageMission.html";
    private static final String TEMPLATE = ("PageMission.html");
    private static final String DATABASE = "jdbc:sqlite:database/climate.db";

    @Override
    public void handle(Context context) throws Exception {
        // Create a simple HTML webpage in a String
        String html = "<html>";

        // Add some Head information
        html = html + "<head>" + 
               "<title>Subtask 1.2</title>";

        Map<String, Object> model = new HashMap<String, Object>();
        List<PersonaList> listPersona = findListPersona();
        model.put("listPersonas", listPersona);
        List<StudentList> listStudents = findStudent();
        model.put("listStudents", listStudents);


        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage
        context.render(TEMPLATE, model);
    }

        public List<PersonaList> findListPersona() {
            // JDBC Database Object
            Connection connection = null;
    
            String query = "SELECT Name, Description FROM Persona;";
            System.out.println(query);
            // JDBC Objects
            Statement statement = null;
            ResultSet resultSet = null;
    
            // Array List to store the results of the query
            ArrayList<PersonaList> listPersonas = new ArrayList<PersonaList>();
            for (PersonaList persona : listPersonas) {
                System.out.println("Name: " + persona.personaName);
                System.out.println("Bio: " + persona.personaBio);
                System.out.println();
            }
    
            try {
                // Connect to the database
                connection = DriverManager.getConnection(DATABASE);
    
                // Create a statement object
                statement = connection.createStatement();
    
                // Send the query to the database
                resultSet = statement.executeQuery(query);
    
                // Loop through the results
                while (resultSet.next()) {
                    // Get the data from the result set

                    String personaName = resultSet.getString("Name");
                    String personaBio = resultSet.getString("Description");

                    // Add the data to the ArrayList
                    listPersonas.add(new PersonaList(personaName, personaBio));
                }

            } catch (SQLException e) {
                // Print any errors
                System.out.println(e.getMessage());
            } finally {
                // Close the result set, statement and connection
                try {
                    if (resultSet != null) {
                        resultSet.close();
                    }
    
                    if (statement != null) {
                        statement.close();
                    }
    
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    // Print any errors
                    System.out.println(e.getMessage());
                }
            }
            
    
            // Return the ArrayList
            return listPersonas;
        }
        public List<StudentList> findStudent() {
            // JDBC Database Object
            Connection connection = null;
    
            String query = "SELECT ID, Name FROM Students;";
            System.out.println(query);
            // JDBC Objects
            Statement statement = null;
            ResultSet resultSet = null;
    
            // Array List to store the results of the query
            ArrayList<StudentList> listStudents = new ArrayList<StudentList>();
    
            try {
                // Connect to the database
                connection = DriverManager.getConnection(DATABASE);
    
                // Create a statement object
                statement = connection.createStatement();
    
                // Send the query to the database
                resultSet = statement.executeQuery(query);
    
                // Loop through the results
                while (resultSet.next()) {
                    // Get the data from the result set

                    String studentID = resultSet.getString("ID");
                    String studentName = resultSet.getString("Name");

                    // Add the data to the ArrayList
                    listStudents.add(new StudentList(studentID, studentName));
                }

            } catch (SQLException e) {
                // Print any errors
                System.out.println(e.getMessage());
            } finally {
                // Close the result set, statement and connection
                try {
                    if (resultSet != null) {
                        resultSet.close();
                    }
    
                    if (statement != null) {
                        statement.close();
                    }
    
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    // Print any errors
                    System.out.println(e.getMessage());
                }
            }
            
    
            // Return the ArrayList
            return listStudents;
        }


    public static class PersonaList{
        private String personaName;
        private String personaBio;

        public PersonaList(String personaName, String personaBio) {
            this.personaName = personaName;
            this.personaBio = personaBio; 
        }       

        public String getPersonaName(){
            return personaName;
        }

        public String getPersonaBio(){
            return personaBio;
        }
    }

    public static class StudentList{
        private String studentID;
        private String studentName;

        public StudentList(String studentID, String studentName) {
            this.studentID = studentID;
            this.studentName = studentName; 
        }       

        public String getStudentID(){
            return studentID;
        }

        public String getStudentName(){
            return studentName;
        }
    }

}
