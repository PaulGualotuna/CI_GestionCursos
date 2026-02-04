package ec.edu.espe.pu_gestioncursos.dto;

public class EnrollmentResponse {
    //obtener id de la matricula
    private String enrollmentId;
    //obtener coddigo de confirmacion
    private String confirmationCode;

    //generamos el constructor
    public EnrollmentResponse(String enrollmentId, String confirmationCode) {
        this.enrollmentId = enrollmentId;
        this.confirmationCode = confirmationCode;
    }

    //generamos los getters
    public String getEnrollmentId() {
        return enrollmentId;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }
}
