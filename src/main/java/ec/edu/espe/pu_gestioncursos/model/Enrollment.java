package ec.edu.espe.pu_gestioncursos.model;

import java.util.UUID;

public class Enrollment {
    private String id;
    private String studentEmail;
    private double credits;
    private String status;

    //Genereamos el constructor
    public Enrollment(String studentEmail, double credits) {
        this.id = UUID.randomUUID().toString();
        this.studentEmail = studentEmail;
        this.credits = credits;
        this.status = "CREATED";
    }

    //Creamos Getters de todos
    public String getId() {
        return id;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public double getCredits() {
        return credits;
    }

    public String getStatus() {
        return status;
    }

    //Creamos Setters solo de status
    public void setStatus(String status) {
        this.status = status;
    }
}
