package ec.edu.espe.pu_gestioncursos.repository;

import ec.edu.espe.pu_gestioncursos.model.Enrollment;

public interface EnrollmentRepository {
    //para guardar la matricula
    Enrollment save(Enrollment enrollment);
    // verificar existencia de matricula por email
    boolean existsByStudentEmail(String studentEmail);
}
