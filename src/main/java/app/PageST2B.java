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
public class PageST2B implements Handler {

        // URL of this page relative to http://localhost:7001/
        public static final String URL = "/PageST2B.html";
    
        // Name of the Thymeleaf HTML template page in the resources folder
        private static final String TEMPLATE = ("PageST2B.html");

        // Database location
        private static final String DATABASE = "jdbc:sqlite:database/climate.db";
    
        @Override
        public void handle(Context context) throws Exception {

            Map<String, Object> model = new HashMap<String, Object>();

            JDBCConnection jdbc = new JDBCConnection();
            List<String> countryNames = getCountryNames();
            model.put("countryNames", countryNames);

            String countryName = context.formParam("countrydropdown");
            String startYear = context.formParam("startyear");
            String avgMinMax = context.formParam("avgminmax");
            String cityOrState = context.formParam("statedropdown");
            String endYear = context.formParam("endyear");
            
            String dataType = "";
            String cityState = "";
            if (cityOrState != null) {
                if (cityOrState.equals("City")) {
                    cityState = "City";
                } else if (cityOrState.equals("State")) {
                    cityState = "State";
                }
            }
                
            if (avgMinMax != null) {
                if (avgMinMax.equals("AvgTemp")) {
                    dataType = "AvgTemp";
                } else if (avgMinMax.equals("MinTemp")) {
                    dataType = "MinTemp";
                } else if (avgMinMax.equals("MaxTemp")) {
                    dataType = "MaxTemp";
                }
            }

            String countryCode = getCountryCode(countryName);
            
            if (startYear == null || endYear == null || startYear.isEmpty() || endYear.isEmpty()) {
                model.put("noresult", new String("No result!"));
            } else {
                List<Temperature> tempChanges = getTempChanges(countryCode, cityState, dataType, startYear, endYear);
                model.put("tempchanges", tempChanges);
            }
            context.render(TEMPLATE, model);
            }

        public List<Temperature> getTempChanges(String countryCode, String cityOrState, String dataType, String startYear, String endYear) {

            String query = "SELECT " + cityOrState + "." + cityOrState + "Name " + "AS CityOrState,"
                        + "(SELECT " + dataType + " FROM " + cityOrState + "TempObservation WHERE " + cityOrState + "Code = " + cityOrState + "." + cityOrState + "Code AND YEAR = " + startYear + ") AS StartYear,"
                        + "(SELECT " + dataType + " FROM " + cityOrState + "TempObservation WHERE " + cityOrState + "Code = " + cityOrState + "." + cityOrState + "Code AND YEAR = " + endYear + ") AS EndYear "
                        + "FROM " + cityOrState
                        + " WHERE " + cityOrState + "." + cityOrState + "Code IN (SELECT " + cityOrState + "Code FROM " + cityOrState + "TempObservation WHERE CountryCode = " + "'" + countryCode + "'" + ")";

            // JDBC Objects
            Statement statement = null;
            ResultSet resultSet = null;

            // Array List to store the results of the query
            List<Temperature> tempChanges = new ArrayList<Temperature>();

            try {
                // Connect to the database
                Connection connection = DriverManager.getConnection(DATABASE);

                // Create a statement object
                statement = connection.createStatement();

                // Send the query to the database
                System.out.println(query);
                resultSet = statement.executeQuery(query);

                // Loop through the results
                while (resultSet.next()) {

                    // Get the data from the result set
                    String cityStateName = resultSet.getString("CityOrState");
                    String startYearTemp = resultSet.getString("StartYear");
                    String endYearTemp = resultSet.getString("EndYear");

                    // Create a new Temperature object
                    Temperature tempChange = new Temperature(cityStateName, startYearTemp, endYearTemp);
                        
                    // Add the data to the ArrayList
                    tempChanges.add(tempChange);
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }

            return tempChanges;

        }
        public String getCountryCode(String countryName) {
            Connection connection = null;

            // First, convert countryName to countryCode 
            String query = "SELECT Country.* from Country WHERE CountryName = " + "\"" + countryName + "\"";
            String countryCode = "";

            try {
                // Connect to the database
                connection = DriverManager.getConnection(DATABASE);

                // Create a statement object
                Statement statement = connection.createStatement();

                // Send the query to the database
                ResultSet results = statement.executeQuery(query);

                // Get countryCode
                if (results.next()) {
                    countryCode = results.getString("CountryCode");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return countryCode;
        }

        public ArrayList<String> getCountryNames() {
            // JDBC Database Object
            Connection connection = null;
    
            // SQL query string
            String query = "SELECT countryName FROM Country ORDER BY CountryName ASC;";
    
            // JDBC Objects
            Statement statement = null;
            ResultSet resultSet = null;
    
            // Array List to store the results of the query
            ArrayList<String> countryNames = new ArrayList<String>();
    
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
                    String country = resultSet.getString("CountryName");
    
                    // Add the data to the ArrayList
                    countryNames.add(country);
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
            return countryNames;
        }
}
    
    