package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Example Index HTML class using Javalin
 * <p>
 * Generate a static HTML page using Javalin
 * by writing the raw HTML into a Java String object
 *
 * @author Timothy Wiley, 2023. email: timothy.wiley@rmit.edu.au
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 */
public class PageIndex implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/";

    // Name of the Thymeleaf HTML template page in the resources folder
    private static final String TEMPLATE = ("pageIndex.html");

    // Database location
    private static final String DATABASE = "jdbc:sqlite:database/climate.db";

    @Override
    public void handle(Context context) throws Exception {
        // The model of data to provide to Thymeleaf.
        // In this example the model will remain empty

        int totalTempYears = getTotalTempYears();
        int totalPopYears = getTotalPopYears();
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("yearsTempData", totalTempYears);
        model.put("yearsPopData", totalPopYears);

        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage using Thymeleaf
        context.render(TEMPLATE, model);
    }


// All our methods for retrieving dynamic data from the database go here

    public int getTotalTempYears() {
        // JDBC Database Object
        Connection connection = null;

        // Initialise the total number of years available
        int totalTempYearsAvail = 0;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Create a new SQL statement object
            Statement statement = connection.createStatement();

            // Write SQL statement to execute, and execute it
            String query = "SELECT COUNT(*) FROM GlobalTempObservation";
            statement.execute(query);

            // Get the results of the query
            ResultSet results = statement.executeQuery(query);

            // Get the first row of the result
            if (results.next()) {
                totalTempYearsAvail = results.getInt("COUNT(*)");
            }
        }

        catch (SQLException e) {
            e.printStackTrace();
        }
        return totalTempYearsAvail;
    }

    public int getTotalPopYears() {

        // JDBC Database Object
        Connection connection = null;

        // Initialise the total number of years available
        int totalPopYearsAvail = 0;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Create a new SQL statement object
            Statement statement = connection.createStatement();

            // Write SQL statement to execute, and execute it
            String query = "SELECT COUNT(DISTINCT Year) AS NumPopYears FROM CountryTempObservation WHERE Population > 0";
            statement.execute(query);

            // Get the results of the query
            ResultSet results = statement.executeQuery(query);

            // Get the first row of the result
            if (results.next()) {
                totalPopYearsAvail = results.getInt("NumPopYears");
            }
        }

        catch (SQLException e) {
            e.printStackTrace();
        }
        return totalPopYearsAvail;
    }
}
