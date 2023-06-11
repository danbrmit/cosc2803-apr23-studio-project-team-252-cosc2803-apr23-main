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
public class PageST2A implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/PageST2A.html";

    private static final String TEMPLATE = ("PageST2A.html");

    private static final String DATABASE = "jdbc:sqlite:database/climate.db";



    @Override
    public void handle(Context context) throws Exception {
        // Create a simple HTML webpage in a String
        String html = "<html>";

        // Add some Head information
        html = html + "<head>" + 
               "<title>Subtask 2.1</title>";

        Map<String, Object> model = new HashMap<String, Object>();

        
        String startYear = context.formParam("startYear");
        String endYear = context.formParam("endYear");
        String sortOrder = context.formParam("sortOrder");
        //TODO: THIS IS FOR POPULATION
        //String sortOption = context.formParam("sortOption");

        List<CountryPercentageChange> percentageChange = avgTempGlobal(startYear, endYear, sortOrder);
        String globalPercentageChange = globalPercentageChange(startYear, endYear);
        /* 
        for (CountryPercentageChange change : percentageChange) {
            System.out.println("Country: " + change.countryName);
            System.out.println("Percentage Change: " + change.pcntChange);
            System.out.println();
        }*/

        model.put("percentageChanges", percentageChange);
        model.put("globalPercentageChange", globalPercentageChange);
        //System.out.println(percentageChange);

        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage
        context.render(TEMPLATE, model);
    }
    public List<CountryPercentageChange> avgTempGlobal(String startYearStr, String endYearStr, String sortOrder) {
        // JDBC Database Object
        Connection connection = null;

        // SQL query string
        int startYear;
        int endYear;

        //TODO: ENSURE ASC AND DESC IS DYNAMICALLY CHANGEABLE
        if (startYearStr != null || endYearStr != null){
            startYear = Integer.parseInt(startYearStr);
            endYear = Integer.parseInt(endYearStr);
        }
        else{
            startYear = 0;
            endYear = 0;
        }
       
        //TODO: POPULATION SQL STATEMENT
        //String query = "SELECT C.CountryName, ((MAX(CTO.AvgTemp) - MIN(CTO.AvgTemp)) / MIN(CTO.AvgTemp)) * 100 AS PercentageChangeTemp, ((MAX(CTO.Population) - MIN(CTO.Population)) / MIN(CTO.Population)) * 100 AS PercentageChangePopulation FROM CountryTempObservation CTO JOIN Country C ON CTO.CountryCode = C.CountryCode WHERE CTO.Year > "+startYear+" AND CTO.Year < "+endYear+" GROUP BY C.CountryName ORDER BY PercentageChangeTemp DESC;";
        
        String query = "SELECT C.CountryName, ((MAX(CTO.AvgTemp) - MIN(CTO.AvgTemp)) / MIN(CTO.AvgTemp)) * 100 AS PercentageChange FROM CountryTempObservation CTO JOIN Country C ON CTO.CountryCode = C.CountryCode WHERE CTO.Year > "+startYear+" AND CTO.Year < "+endYear+" GROUP BY C.CountryName ORDER BY PercentageChange";
        if ("asc".equals(sortOrder)){
            query += " ASC;";
        }
        else{
            query += " DESC;";
        }
        
        System.out.println(query);
        // JDBC Objects
        Statement statement = null;
        ResultSet resultSet = null;

        // Array List to store the results of the query
        ArrayList<CountryPercentageChange> percentageChangeList = new ArrayList<CountryPercentageChange>();

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
                
                String countryName = resultSet.getString("CountryName");
                double percentageChangeDouble = resultSet.getDouble("PercentageChange");
                //TODO: ADD POPULATION
                //String percentageChangePop = resultSet.getString("PercentageChangePopulation");
                String percentageChangeTemp = String.format("%.2f%%", percentageChangeDouble);
                
                // Add the data to the ArrayList
                percentageChangeList.add(new CountryPercentageChange(countryName, percentageChangeTemp));
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
        return percentageChangeList;
    }

    public String globalPercentageChange(String startYear, String endYear) {

        String query = "SELECT ((MAX(AvgTemp) - MIN(AvgTemp)) / MIN(AvgTemp)) * 100 AS PercentageChange FROM GlobalTempObservation WHERE Year > " + startYear + " AND Year < " + endYear;
        Connection connection = null;
        System.out.println(query);
        // JDBC Objects
        Statement statement = null;
        ResultSet resultSet = null;

        try{
            // Connect to the database
            connection = DriverManager.getConnection(DATABASE);

            // Create a statement object
            statement = connection.createStatement();

            // Send the query to the database
            resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                double percentageChangeDouble = resultSet.getDouble("PercentageChange");
                String percentageChange = String.format("%.2f%%", percentageChangeDouble);

                return percentageChange;
            }
        } catch (SQLException e) {
            // Print any errors
            System.out.println(e.getMessage());
        }
    
        return "No change found.";
    }
    

    public static class CountryPercentageChange{
        private String countryName;
        private String pcntChange;
        //private String population;

        public CountryPercentageChange(String countryName, String pcntChange) {
            this.countryName = countryName;
            this.pcntChange = pcntChange;
            //this.population = population;
        }

        public String getCountryName(){
            return countryName;
        }

        public String getPcntChange(){
            return pcntChange;
        }
        /* 
        public String population(){
            return population;
        }*/
    }


}
