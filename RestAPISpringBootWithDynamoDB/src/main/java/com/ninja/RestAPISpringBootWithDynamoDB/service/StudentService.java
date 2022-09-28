package com.ninja.RestAPISpringBootWithDynamoDB.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ninja.RestAPISpringBootWithDynamoDB.entity.Student;
import com.ninja.RestAPISpringBootWithDynamoDB.exception.EntityNotFoundException;
import com.ninja.RestAPISpringBootWithDynamoDB.repository.StudentRepository;

@Service
public class StudentService {
	
	@Autowired
	StudentRepository studentRepo;

	public Iterable<Student> getStudents() {
		return studentRepo.findAll();
	}

	public Optional<Student> getStudent(String id) {
		Optional<Student> student = studentRepo.findById(id);
		if (student.isPresent()) {
            return student;
        }
		else
			throw new EntityNotFoundException("Student not found with the given ID");	
	}

	public Student updateStudent(Student student, String id) {
		boolean exists = studentRepo.existsById(id);
		if(!exists) {
			throw new EntityNotFoundException("Student(id- " + id + ") Not Found !!");
		}
		else
			student.setId(id);
			return studentRepo.save(student);
	}

	public void deleteStudent(String id) {
		boolean exists = studentRepo.existsById(id);
		if(!exists) {
			throw new EntityNotFoundException("Student(id- " + id + ") Not Found !!");
		}
		else
			studentRepo.deleteById(id);	
	}
	
	public Student addStudent(Student student) {		
		return studentRepo.save(student);	
	}

}
