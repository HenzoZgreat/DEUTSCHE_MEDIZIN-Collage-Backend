package Henok.example.DeutscheCollageBack_endAPI.Service.Utility;

import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.AcademicYear;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;

/**
 * Utility service for AcademicYear date operations.
 * Provides reusable methods for checking if dates fall within academic year ranges.
 */
@Service
public class AcademicYearUtilityService {

    /**
     * Checks if a given date falls within an academic year range.
     * Academic year format is expected to be like "2020-2021" (startYear-endYear).
     * 
     * @param date The date to check
     * @param academicYearGC The academic year string in format "YYYY-YYYY" (e.g., "2020-2021")
     * @return true if the date falls within the academic year range, false otherwise
     * @throws IllegalArgumentException if academicYearGC format is invalid
     */
    public boolean isDateInAcademicYear(LocalDate date, String academicYearGC) {
        if (date == null || academicYearGC == null || academicYearGC.trim().isEmpty()) {
            return false;
        }

        try {
            String[] parts = academicYearGC.trim().split("-");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid academic year format. Expected format: YYYY-YYYY (e.g., 2020-2021)");
            }

            int startYear = Integer.parseInt(parts[0].trim());
            int endYear = Integer.parseInt(parts[1].trim());

            // Academic year typically runs from around September/October of startYear to June/July of endYear
            // We'll check if the date's year matches either startYear or endYear
            int dateYear = date.getYear();
            
            // Check if date year is startYear (from September onwards) or endYear (up to August)
            // For simplicity, we'll consider the date is in the academic year if:
            // - dateYear == startYear and month >= 9 (September onwards)
            // - dateYear == endYear and month <= 8 (up to August)
            // - dateYear is between startYear and endYear (shouldn't happen but handle it)
            
            if (dateYear == startYear) {
                // If it's the start year, check if it's September or later
                return date.getMonthValue() >= 9;
            } else if (dateYear == endYear) {
                // If it's the end year, check if it's August or earlier
                return date.getMonthValue() <= 8;
            } else if (dateYear > startYear && dateYear < endYear) {
                // Date falls between the two years (should be rare but possible)
                return true;
            }
            
            return false;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid academic year format. Expected format: YYYY-YYYY (e.g., 2020-2021)", e);
        }
    }

    /**
     * Finds the AcademicYear entity that contains the given date.
     * 
     * @param date The date to check
     * @param academicYears List of AcademicYear entities to search through
     * @return The AcademicYear that contains the date, or null if none found
     */
    public AcademicYear findAcademicYearByDate(LocalDate date, java.util.List<AcademicYear> academicYears) {
        if (date == null || academicYears == null || academicYears.isEmpty()) {
            return null;
        }

        return academicYears.stream()
                .filter(ay -> isDateInAcademicYear(date, ay.getAcademicYearGC()))
                .findFirst()
                .orElse(null);
    }
}

