package com.ninja.RestAPISpringBootWithDynamoDB.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.ScanResultPage;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.github.javafaker.Faker;
import com.ninja.RestAPISpringBootWithDynamoDB.entity.Report;
import com.ninja.RestAPISpringBootWithDynamoDB.entity.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock DynamoDBMapper mapper;
    @InjectMocks StudentService studentService;

// ? @InjectMocks does below thing //
//    @BeforeEach
//    void setUp() {
//        studentService = new StudentService(mapper);
//    }

    @Test
    void getStudents() {
        studentService.getStudents();
        verify(mapper).scan(eq(Student.class), any());
    }

    @Test
    void getStudent() {
        String testStudentId = "123";
        Student testStudent = new Student();
        testStudent.setId(testStudentId);

        // when/given is used to set the behaviour on the mock object
        when(mapper.load(testStudent)).thenReturn(testStudent);

        // Call the method under test
        Optional<Student> result = studentService.getStudent(testStudentId);

        // Verify the interactions and assertions
        verify(mapper, times(1)).load(testStudent); // Ensure that the load method was called once
        assertTrue(result.isPresent()); // Ensure that the result is present
        assertEquals(testStudent, result.get()); // Ensure that the result is as expected
        /*
        ? assertEquals calls object.equals internally so implementation of equalTo of Student class is responsible
        ? for comparing the objects
         */
    }
    @Test
    void updateStudent() {
        String studentId = "55";
        Student testStudent = new Student();
        testStudent.setId(studentId);

        studentService.deleteStudent(studentId);

        verify(mapper, times(1))
                .delete(testStudent);

        ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
        verify(mapper).save(captor.capture());
        assertEquals(captor.getValue(),testStudent);
    }

    @Test
    void deleteStudent() {
        Student student = new Student();
        String id = "53";
        student.setId(id);

        studentService.deleteStudent(id);

        ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
        verify(mapper).delete(captor.capture());
        assertEquals(captor.getValue(), student);

        verifyNoMoreInteractions(mapper);
    }

    @Test
    void addStudent() {
        Student student = demoStudentList().get(1);

        studentService.addStudent(student);

        // Argument captor is used to capture what value was passed in the method
        // so that we can verify that the Object which we initialized is the one
        // which is passed to the DAO or not
        ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
        verify(mapper).save(captor.capture());
        assertThat(captor.getValue()).isEqualTo(student);
    }

    @Test
    void pageQueryWithSize() {
        int pageNumber = 56;
        int pageSize = 5;

        // Mock Data
        List<Student> mockResults = demoStudentList();
        ScanResultPage<Student> page = new ScanResultPage<>();
        page.setResults(mockResults);

        // Set up the behavior for the subsequent iterations
        when(mapper.scanPage(eq(Student.class), any(DynamoDBScanExpression.class)))
                .thenReturn(page);

        // Perform the Test
        List<Student> result = studentService.pageQueryWithSize(pageNumber, pageSize);

        // Verify that scanPage is called the expected number of times
        verify(mapper, times(pageNumber)).scanPage(eq(Student.class), any(DynamoDBScanExpression.class));

        // Assertions
        assertFalse(mockResults.isEmpty());
        assertEquals(mockResults.size(), result.size());
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