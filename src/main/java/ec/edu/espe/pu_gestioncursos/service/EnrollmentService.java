package ec.edu.espe.pu_gestioncursos.service;

import ec.edu.espe.pu_gestioncursos.dto.EnrollmentResponse;
import ec.edu.espe.pu_gestioncursos.model.Enrollment;
import ec.edu.espe.pu_gestioncursos.repository.EnrollmentRepository;

public class EnrollmentService {
    //Dependencias que se van a inyectar por el constructor
    private final EnrollmentRepository enrollmentRepository;
    private final AcademicControlClient academicControlClient;
    private final ConfirmationService confirmationService;

    //Generamos el constructor
    public EnrollmentService(EnrollmentRepository enrollmentRepository, AcademicControlClient academicControlClient, ConfirmationService confirmationService) {
        this.enrollmentRepository = enrollmentRepository;
        this.academicControlClient = academicControlClient;
        this.confirmationService = confirmationService;
    }


    public EnrollmentResponse registerStudent (String studentEmail, double credits){
        //validacion del email vacio y sin @
        if (studentEmail == null || !studentEmail.contains("@")){
            throw new IllegalArgumentException("El correo del estudiante no es valido");
        }
        //validacion de creditos mayor a 0
        if (credits <= 0) {
            throw new IllegalArgumentException("Los creditos deben ser mayores a 0");
        }
        //verificar estudiante no suspendido
        if (academicControlClient.isStudentSuspended(studentEmail)){
            throw new IllegalStateException("El estudiante con email " + studentEmail + " esta suspendido");
        }

        //Generar una matricula mediante el modelo
        Enrollment enrollment = new Enrollment(studentEmail, credits);

        //Guardar la matricula al repositorio
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        //Generar codigo de confirmacion mediante el servicio externo
        String code = confirmationService.generateConfirmationCode();

        //Devolver la respuesta mediante el DTO
        return new EnrollmentResponse(savedEnrollment.getId(), code);
    }
}
