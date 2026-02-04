package ec.edu.espe.pu_gestioncursos.service;


import ec.edu.espe.pu_gestioncursos.dto.EnrollmentResponse;
import ec.edu.espe.pu_gestioncursos.model.Enrollment;
import ec.edu.espe.pu_gestioncursos.repository.EnrollmentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class EnrollmentServiceTest {
    //Crear las dependencias simuadas por mockito
    private EnrollmentRepository enrollmentRepository;
    private AcademicControlClient academicControlClient;
    private ConfirmationService confirmationService;

    //Clase que vamos a probar
    private EnrollmentService enrollmentService;

    @BeforeEach
    void setUp(){
        //ARRANGE comun para todos los tests
        //crear mocks con mockito
        enrollmentRepository = mock(EnrollmentRepository.class);
        academicControlClient = mock(AcademicControlClient.class);
        confirmationService = mock(ConfirmationService.class);


        //Creamos servicio de prueba con dependencias mockeadas
        enrollmentService = new EnrollmentService (enrollmentRepository, academicControlClient, confirmationService);
    }

    //Registro exitoso, permita generar valores con datos validos y que retorne la ejecucion
    @Test
    void createEnrollment_validData_shouldSaveAndReturnConfirmation(){
        //ARRANGE solo de esta prueba
        String email = "paul@espe.edu.ec";
        double credits = 100.00;

        //Simular que no esta suspendido el estudiante
        when(academicControlClient.isStudentSuspended(email)).thenReturn(false);

        //Simular que el servicio de comfirmacion genera un codigo
        when(confirmationService.generateConfirmationCode()).thenReturn("CONF-1234");

        //Simular el comportamiento del repositorio
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(invocation ->
                invocation.getArgument(0));

        //ACT
        EnrollmentResponse response = enrollmentService.registerStudent(email, credits);

        //ASSERT

        //Resultados
        assertNotNull(response.getEnrollmentId(), "El orderId no debe ser nulo");
        assertEquals("CONF-1234", response.getConfirmationCode(), "El codigo debe coincidir");

        //Interacciones
        //Verificar que se consulto el suspendido
        verify(academicControlClient).isStudentSuspended(email);

        //Verificar que se guardo la orden
        verify(enrollmentRepository).save(any(Enrollment.class));

        //Verificar que se genero el codigo de confirmacion
        verify(confirmationService).generateConfirmationCode();
    }

    //Crear matricula con email no valido para lanzar una excepcion
    @Test
    void createrEnrollment_invalidEmail_shoulThrowException_andNotCallDependencies(){
        //ARRANGE
        String invalidEmail = "paul.espe.edu.ec"; //email sin @
        double credits = 50.0;

        //ACT + ARRANGE
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> enrollmentService
                .registerStudent(invalidEmail, credits), "Debe lanzar un illegalArgumentException por email " +
                "no valido");
        //assertEquals("Invalid email", ex.getMessage(), "El mensaje de la excepcion debe coincidir");
        assertEquals("El correo del estudiante no es valido", ex.getMessage());

        //ASSERT
        verifyNoInteractions(academicControlClient, enrollmentRepository, confirmationService);

    }

    //Crear matricula con credito menos o igual a cero para lanzar una excepcion
    @Test
    void createrEnrollment_invalidCredit_shoulThrowException_andNotCallDependencies(){
        //ARRANGE
        String invalidEmail = "paul@espe.edu.ec";
        double credits = 0;
        //ACT + ARRANGE
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> enrollmentService
                .registerStudent(invalidEmail, credits), "Debe lanzar un illegalArgumentException por credito " +
                "no valido");
        //assertEquals("Invalid email", ex.getMessage(), "El mensaje de la excepcion debe coincidir");
        assertEquals("Los creditos deben ser mayores a 0", ex.getMessage());

        //ASSERT
        verifyNoInteractions(academicControlClient, enrollmentRepository, confirmationService);
    }

    //crear un amatricula con estudiante suspendido, debe lanzar una excepcion y no guardarse
    @Test
    void createEnrollment_suspendedStudent_shouldThrowException_andNotSaveEnrollment() {
        //ARRANGE
        String email = "paul@espe.edu.ec";
        double credits = 20.0;
        //Simular que el estudiante esta suspendido
        when(academicControlClient.isStudentSuspended(email)).thenReturn(true);
        //ACT + ASSERT
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> enrollmentService
                .registerStudent(email, credits), "Debe lanzar una IllegalStateException por estudiante suspendido");
        assertEquals("El estudiante con email " + email + " esta suspendido", ex.getMessage());
        //Verificar que se consulto el suspendido
        verify(academicControlClient).isStudentSuspended(email);
        //Verificar que no se guardo la matricula ni se genero codigo
        verifyNoInteractions(enrollmentRepository, confirmationService);


    }

    //crear una matricula valida y Verificar guardado con ArgumentCaptor
    /*@Test
    void createEnrollment_validData_shouldSaveEnrollment_VerifyWithArgumentCaptor() {
        //ARRANGE
        String email = "paul@espe.edu.ec";
        double credits = 30.0;
        //Simular que no esta suspendido el estudiante
        when(academicControlClient.isStudentSuspended(email)).thenReturn(false);
        //Simular que el servicio de comfirmacion genera un codigo
        when(confirmationService.generateConfirmationCode()).thenReturn("CONF-5678");
        //Simular el comportamiento del repositorio
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(invocation ->
                invocation.getArgument(0));
        //ACT
        enrollmentService.registerStudent(email, credits);
        //ASSERT
        //Crear ArgumentCaptor para Enrollment
        ArgumentCaptor<Enrollment> enrollmentCaptor = ArgumentCaptor.forClass(Enrollment.class);
        //Verificar que se guardo la matricula y capturar el argumento
        verify(enrollmentRepository).save(enrollmentCaptor.capture());
        //Obtener la matricula capturada
        Enrollment captured = enrollmentCaptor.getValue();
        //Verificar los valores de la matricula capturada
        assertEquals(email, captured.getStudentEmail());
        assertEquals(credits, captured.getCredits());
        assertEquals("CREATED", captured.getStatus());
        //assertNotNull(captured.getId());


    }*/
}
