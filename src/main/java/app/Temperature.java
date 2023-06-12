package app;

public class Temperature {

    private String cityOrStateName;
    private String startYearTemp;
    private String endYearTemp;

    // Default constructor
    public Temperature() {

    }
    
    // Constructor for Temperature List (page 2b)
    public Temperature(String cityOrStateName, String startYearTemp, String endYearTemp) {
        this.cityOrStateName = cityOrStateName;
        this.startYearTemp = startYearTemp;
        this.endYearTemp = endYearTemp;
    }

    public String getCityOrStateName() {
        return cityOrStateName;
    }

    public String getStartYearTemp() {
        return startYearTemp;
    }

    public String getEndYearTemp() {
        return endYearTemp;
    }
}
