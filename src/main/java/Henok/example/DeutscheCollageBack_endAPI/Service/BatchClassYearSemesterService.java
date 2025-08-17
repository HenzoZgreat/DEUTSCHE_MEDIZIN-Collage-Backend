package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.Entity.BatchClassYearSemester;
import Henok.example.DeutscheCollageBack_endAPI.Repository.BatchClassYearSemesterRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatchClassYearSemesterService {

    @Autowired
    private BatchClassYearSemesterRepo batchClassYearSemesterRepository;

    public void saveAll(List<BatchClassYearSemester> combinations) {
        batchClassYearSemesterRepository.saveAll(combinations);
    }
}