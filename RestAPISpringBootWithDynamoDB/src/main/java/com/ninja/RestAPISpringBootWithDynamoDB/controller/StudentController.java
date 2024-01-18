package com.ninja.RestAPISpringBootWithDynamoDB.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ninja.RestAPISpringBootWithDynamoDB.entity.Student;
import com.ninja.RestAPISpringBootWithDynamoDB.service.StudentService;

@RestController
public class StudentController {

	@Autowired
	StudentService studentService;

    @GetMapping("message")
    public String message(){
        return "SUCCESS";
    }
	@GetMapping("/students")
    public ResponseEntity<Iterable<Student>> getStudents() {
        return ResponseEntity.ok(studentService.getStudents());
    }
	
	@GetMapping("/student/{id}")
    public ResponseEntity<Optional<Student>> getStudent(@PathVariable String id) {
        return ResponseEntity.ok(studentService.getStudent(id));
    }
	
	@PostMapping("/student")
    public ResponseEntity<Student> addStudent(@RequestBody Student student ) {
        return  ResponseEntity.ok(studentService.addStudent(student));
    }
	
	@PutMapping("/student/{id}")
    public ResponseEntity<Student> updateStudent(@RequestBody Student student, @PathVariable String id) {
        return  ResponseEntity.ok(studentService.updateStudent(student,id));
    }
	
	@DeleteMapping("/student/{id}")
    public ResponseEntity<Map<String, String>> deleteStudent(@PathVariable String id) {
		Map<String, String> responseMap = new TreeMap<String, String>(Collections.reverseOrder());
		studentService.deleteStudent(id);
		String msg = "The record has been deleted !!";
		responseMap.put("student_ id", id);
		responseMap.put("message_response", msg);
		return ResponseEntity.status(HttpStatus.OK).body(responseMap);
    }
}

