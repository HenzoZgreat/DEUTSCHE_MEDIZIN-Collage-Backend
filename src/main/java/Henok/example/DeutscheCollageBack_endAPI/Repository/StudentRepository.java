package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

interface StudentRepository extends JpaRepository<Student, Long> {
}
