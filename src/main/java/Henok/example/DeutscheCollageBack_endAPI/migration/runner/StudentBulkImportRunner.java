package Henok.example.DeutscheCollageBack_endAPI.migration.runner;

import Henok.example.DeutscheCollageBack_endAPI.migration.DTO.StudentImportDTO;
import Henok.example.DeutscheCollageBack_endAPI.migration.Service.BulkStudentImportService;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * One-time runner that executes automatically on application startup.
 *
 * Purpose:
 * - Reads the legacy student CSV (UTF-8) from src/main/resources/data/students_import.csv
 * - Maps each row to StudentImportDTO using exact column headers from your CSV
 * - Calls StudentImportService to create User + StudentDetails records
 * - Can be disabled easily after the migration is complete
 *
 * How to disable after successful import:
 * 1. Comment out @Component
 * OR
 * 2. Add @Profile("import") and run the app without that profile
 */
//@Component
//// @Profile("import")  // <-- uncomment this line after migration to prevent re-running
//@Order(1)  // Runs early, before other runners
//public class StudentBulkImportRunner implements CommandLineRunner {
//
//    private static final Logger log = LoggerFactory.getLogger(StudentBulkImportRunner.class);
//
//    private final BulkStudentImportService studentImportService;
//
//    public StudentBulkImportRunner(BulkStudentImportService studentImportService) {
//        this.studentImportService = studentImportService;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        // Safety check – prevent accidental re-import if data already exists
//        // Adjust the count threshold if you already have some test users
//        if (studentImportService.countExistingStudents() > 100) {
//            log.info("Existing students detected (>10). Skipping bulk import to prevent duplicates.");
//            return;
//        }
//
//        log.info("Starting one-time bulk student import from CSV...");
//
//        // Load CSV from classpath
//        Resource resource = new ClassPathResource("data/students_import.csv");
//        if (!resource.exists()) {
//            log.error("CSV file not found at {}", resource.getURL());
//            return;
//        }
//
//        List<StudentImportDTO> dtos = readCsv(resource.getInputStream());
//
//        if (dtos.isEmpty()) {
//            log.warn("CSV file is empty or could not be parsed.");
//            return;
//        }
//
//        log.info("Loaded {} records from CSV. Starting import...", dtos.size());
//
//        studentImportService.importStudents(dtos);
//
//        log.info("Bulk student import process finished.");
//        log.info("You can now disable this runner by commenting out @Component or adding @Profile(\"import\").");
//    }
//
//    /**
//     * Reads the CSV and maps it to StudentImportDTO using exact column names from your file.
//     * OpenCSV is forgiving – extra columns in CSV are ignored automatically.
//     */
//    private List<StudentImportDTO> readCsv(InputStream inputStream) throws Exception {
//        // Custom parser to trim whitespace properly
//        CSVParser csvParser = new CSVParserBuilder()
//                .withSeparator(',')
//                .withIgnoreLeadingWhiteSpace(true)
//                .build();
//
//        // CSVReader – skip 0 lines because there is NO header row
//        try (CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
//                .withCSVParser(csvParser)
//                .withSkipLines(0)  // Important: no header
//                .build()) {
//
//            // Use positional mapping (column index → DTO field)
//            ColumnPositionMappingStrategy<StudentImportDTO> strategy = new ColumnPositionMappingStrategy<>();
//            strategy.setType(StudentImportDTO.class);
//
//            // Define column positions exactly as in your CSV (0-based index)
//            String[] columns = new String[] {
//                    null,                          // 0  – row number (1,2,3...)
//                    "username",                    // 1  – DHMC-MD-01-12
//                    "firstNameENG",                // 2
//                    "fatherNameENG",               // 3
//                    "grandfatherNameENG",          // 4
//                    "gender",                      // 5
//                    "dateOfBirthGC",               // 6
//                    "maritalStatus",               // 7
//                    "phoneNumber",                 // 8
//                    "dateEnrolledGC",              // 9
//                    "departmentEnrolledId",        // 10
//                    null,                          // 11 – Batch
//                    null,                          // 12 – Recent_Batch
//                    null,                          // 13 – Entry_Year
//                    null,                          // 14 – studentRecentStatus text
//                    "studentRecentStatusId",       // 15
//                    null,                          // 16 – Current_ClassYear
//                    "batchClassYearSemesterId",    // 17
//                    null, null, null, null,        // 18-21 – empty
//                    "schoolBackgroundId",          // 22 – this is now correct (1, 4, etc.)
//                    "isTransfer",                  // 23
//                    "documentStatus",              // 24
//                    "remark",                      // 25
//                    null, null, null,              // 26-28 – contact fields (empty)
//                    "firstNameAMH",                // 29
//                    "fatherNameAMH",               // 30
//                    "grandfatherNameAMH",          // 31
//                    null,                          // 32 – Birth Pllace
//                    null,                          // 33 – country
//                    "placeOfBirthRegionCode",      // 34
//                    "placeOfBirthZoneCode",        // 35
//                    "placeOfBirthWoredaCode",      // 36
//                    null,                          // 37 – Student_NationalExamination_Id
//                    null,                          // 38 – Area_Type
//                    null,                          // 39 – Institution_code
//                    null,                          // 40 – Study_Program
//                    "programModalityCode",         // 41
//                    null, null, null, null, null, null, null, null, null, null, null  // 42-52 – remaining empty columns
//            };
//
//            strategy.setColumnMapping(columns);
//
//            CsvToBean<StudentImportDTO> csvToBean = new CsvToBeanBuilder<StudentImportDTO>(csvReader)
//                    .withMappingStrategy(strategy)
//                    .build();
//
//            return csvToBean.parse();
//        }
//    }}