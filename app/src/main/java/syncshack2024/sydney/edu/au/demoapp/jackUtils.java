package syncshack2024.sydney.edu.au.demoapp;

import syncshack2024.sydney.edu.au.demoapp.enums.SportsCategory;
import java.util.HashMap;
import java.util.Map;

public class jackUtils {
    private static final Map<SportsCategory, Float> categoryHueMap = new HashMap<>();

    static {
        categoryHueMap.put(SportsCategory.TENNIS, 120.0f);      // Green
        categoryHueMap.put(SportsCategory.SWIMMING, 240.0f);    // Blue
        categoryHueMap.put(SportsCategory.BASKETBALL, 30.0f);   // Orange
    }

    public static float sportToHue(SportsCategory category) {
        return categoryHueMap.get(category);
    }

    public static SportsCategory stringToSports(String sport) {
        if (sport == null) {
            throw new IllegalArgumentException("Sport cannot be null");
        }

        switch (sport.toUpperCase()) {
            case "TENNIS":
                return SportsCategory.TENNIS;
            case "SWIMMING":
                return SportsCategory.SWIMMING;
            case "BASKETBALL":
                return SportsCategory.BASKETBALL;
            default:
                throw new IllegalArgumentException("Unknown sport: " + sport);
        }
    }
}
