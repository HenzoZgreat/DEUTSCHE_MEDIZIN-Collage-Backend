package Henok.example.DeutscheCollageBack_endAPI.Service.Utility;

import Henok.example.DeutscheCollageBack_endAPI.Entity.ClassYear;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Semester;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility service for ordering ClassYear and Semester entities.
 * Provides reusable methods for sorting academic periods in the correct order.
 */
@Service
public class ClassYearSemesterOrderingService {

    /**
     * Gets a comparator for ordering ClassYear entities.
     * Order: Numeric years (1, 2, 3, 4, 5, 6) first, then PC1, PC2, C1, C2, C3.
     * 
     * @return Comparator for ClassYear
     */
    public Comparator<ClassYear> getClassYearComparator() {
        return (cy1, cy2) -> {
            String year1 = cy1.getClassYear();
            String year2 = cy2.getClassYear();
            return compareClassYearStrings(year1, year2);
        };
    }

    /**
     * Compares two class year strings.
     * Order: Numeric (1, 2, 3...) first, then PC1, PC2, C1, C2, C3.
     * 
     * @param year1 First class year string
     * @param year2 Second class year string
     * @return Negative if year1 < year2, positive if year1 > year2, 0 if equal
     */
    public int compareClassYearStrings(String year1, String year2) {
        if (year1 == null && year2 == null) return 0;
        if (year1 == null) return 1;
        if (year2 == null) return -1;

        // Check if both are numeric
        boolean isNumeric1 = isNumeric(year1);
        boolean isNumeric2 = isNumeric(year2);

        if (isNumeric1 && isNumeric2) {
            // Both numeric: compare as integers
            return Integer.compare(Integer.parseInt(year1), Integer.parseInt(year2));
        } else if (isNumeric1) {
            // year1 is numeric, year2 is not: numeric comes first
            return -1;
        } else if (isNumeric2) {
            // year2 is numeric, year1 is not: numeric comes first
            return 1;
        } else {
            // Both non-numeric: compare PC and C years
            return compareNonNumericYears(year1, year2);
        }
    }

    /**
     * Compares non-numeric class years (PC1, PC2, C1, C2, C3).
     * Order: PC1, PC2, C1, C2, C3
     */
    private int compareNonNumericYears(String year1, String year2) {
        int priority1 = getNonNumericYearPriority(year1);
        int priority2 = getNonNumericYearPriority(year2);
        
        if (priority1 != -1 && priority2 != -1) {
            return Integer.compare(priority1, priority2);
        }
        
        // If one or both are unknown, use string comparison
        return year1.compareTo(year2);
    }

    /**
     * Gets priority for non-numeric class years.
     * PC1=1, PC2=2, C1=3, C2=4, C3=5
     */
    private int getNonNumericYearPriority(String year) {
        if (year == null) return -1;
        
        if (year.equals("PC1")) return 1;
        if (year.equals("PC2")) return 2;
        if (year.equals("C1")) return 3;
        if (year.equals("C2")) return 4;
        if (year.equals("C3")) return 5;
        
        return -1; // Unknown format
    }

    /**
     * Checks if a string is numeric.
     */
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Gets a comparator for ordering Semester entities.
     * Order: S1, S2, S3, FS
     * 
     * @return Comparator for Semester
     */
    public Comparator<Semester> getSemesterComparator() {
        return (s1, s2) -> {
            String code1 = s1.getAcademicPeriodCode();
            String code2 = s2.getAcademicPeriodCode();
            return compareSemesterStrings(code1, code2);
        };
    }

    /**
     * Compares two semester code strings.
     * Order: S1, S2, S3, FS
     * 
     * @param code1 First semester code
     * @param code2 Second semester code
     * @return Negative if code1 < code2, positive if code1 > code2, 0 if equal
     */
    public int compareSemesterStrings(String code1, String code2) {
        if (code1 == null && code2 == null) return 0;
        if (code1 == null) return 1;
        if (code2 == null) return -1;

        int priority1 = getSemesterPriority(code1);
        int priority2 = getSemesterPriority(code2);

        if (priority1 != -1 && priority2 != -1) {
            return Integer.compare(priority1, priority2);
        }

        // If unknown, use string comparison
        return code1.compareTo(code2);
    }

    /**
     * Gets priority for semester codes.
     * S1=1, S2=2, S3=3, FS=4
     */
    private int getSemesterPriority(String code) {
        if (code == null) return -1;
        
        if (code.equals("S1")) return 1;
        if (code.equals("S2")) return 2;
        if (code.equals("S3")) return 3;
        if (code.equals("FS")) return 4;
        
        return -1; // Unknown format
    }

    /**
     * Sorts a list of ClassYear entities using the standard ordering.
     * 
     * @param classYears List of ClassYear entities to sort
     * @return Sorted list
     */
    public List<ClassYear> sortClassYears(List<ClassYear> classYears) {
        if (classYears == null) {
            return null;
        }
        return classYears.stream()
                .sorted(getClassYearComparator())
                .collect(Collectors.toList());
    }

    /**
     * Sorts a list of Semester entities using the standard ordering.
     * 
     * @param semesters List of Semester entities to sort
     * @return Sorted list
     */
    public List<Semester> sortSemesters(List<Semester> semesters) {
        if (semesters == null) {
            return null;
        }
        return semesters.stream()
                .sorted(getSemesterComparator())
                .collect(Collectors.toList());
    }
}

