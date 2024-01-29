package com.ninja.RestAPISpringBootWithDynamoDB.controller;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.ScanResultPage;
import com.github.javafaker.Faker;
import com.ninja.RestAPISpringBootWithDynamoDB.entity.Report;
import com.ninja.RestAPISpringBootWithDynamoDB.entity.Student;
import com.ninja.RestAPISpringBootWithDynamoDB.service.StudentService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {
    @InjectMocks
    private StudentController controller;
    @Mock
    private StudentService service;

    @Test
    void getStudents() {

        List<Student> list = List.of(
                new Student("1","Raju",null,null,null)
        );

        when(service.getStudents()).thenReturn(list);

        ResponseEntity<Iterable<Student>> response = controller.getStudents();
        verify(service).getStudents();
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.hasBody());
        assertEquals(response.getBody(), list);
    }

    @Test
    void getStudent() {
        String id = "12";
        Student student = new Student();
        student.setId(id);

        when(service.getStudent(id)).thenReturn(Optional.of(student));

        ResponseEntity<Optional<Student>> response = controller.getStudent(id);
        Optional<Student> responseBody = response.getBody();
        assert Objects.requireNonNull(responseBody).isPresent();
        assertEquals(responseBody.get(), student);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void addStudent() {
        // Mock Data
        Student student = new Student();
        student.setId("49");
        student.setGrade("A");
        student.setFirstName("Popeye");
        student.setLastName("Batch");

        // Set mocking behavior
        /*
            ? This means that whenever the "addStudent" method of the service is
            ? called with any Student object as an argument,it will always
            ? return the specific student object you created earlier.
         */
        when(service.addStudent(any(Student.class))).thenReturn(student);

        // Perform the test
        ResponseEntity<Student> response = controller.addStudent(student);

        ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
        verify(service).addStudent(captor.capture());
        assertThat(captor.getValue()).isEqualTo(student);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void updateStudent() {
        String id = "2";
        Student student = new Student();
        student.setId(id);

        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        when(service.updateStudent(student,id)).thenReturn(student);

        ResponseEntity<Student> response = controller.updateStudent(student, id);
        verify(service).updateStudent(studentCaptor.capture(), idCaptor.capture());

        assertEquals(id,idCaptor.getValue());
        assertEquals(student, studentCaptor.getValue());
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.hasBody());
        assertEquals(response.getBody(), student);
    }

    @Test
    void deleteStudent() {
        String id = "2";

        when(service.deleteStudent(id)).thenReturn(true);
        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);

        ResponseEntity<?> response = controller.deleteStudent(id);
        verify(service).deleteStudent(idCaptor.capture());

        assertEquals(200,response.getStatusCodeValue());
        assertEquals(id, idCaptor.getValue());
    }

    @Test
    void pageQuery() {
        int pageNumber = 56;
        int pageSize = 5;
        ArgumentCaptor<Integer> pageNumberCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> pageSizeCaptor = ArgumentCaptor.forClass(Integer.class);

        // Mock Data
        List<Student> mockResults = demoStudentList();

        // Set up the behavior Mock
        when(service.pageQueryWithSize(pageNumber,pageSize)).thenReturn(mockResults);

        // Perform the Test
        ResponseEntity<?> response = controller.pageQuery(pageNumber, pageSize);

        verify(service).pageQueryWithSize(pageNumberCaptor.capture(), pageSizeCaptor.capture());

        // Assertions
        assertEquals(pageNumberCaptor.getValue(), pageNumber);
        assertEquals(pageSizeCaptor.getValue(), pageSize);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(response.getBody(), mockResults);
    }
    private List<Student> demoStudentList() {
        Faker f = new Faker();
        List<Student> studentList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Student s  = new Student();
            s.setId(String.valueOf(i));
            s.setGrade(f.random().hex(1).toUpperCase());
            s.setFirstName(f.name().firstName());
            s.setLastName(f.name().lastName());
            s.setReport(Report.builder().
                    reportId(String.valueOf(i*10)).
                    university(f.university().name()).
                    branch(f.educator().course()).
                    percentage(f.number().numberBetween(0,100)).
                    status(i%2==0?"PASS":"FAIL").
                    build());
            studentList.add(s);
        }
        return studentList;
    }
}