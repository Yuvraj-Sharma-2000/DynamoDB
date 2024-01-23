package com.ninja.RestAPISpringBootWithDynamoDB.service;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.ninja.RestAPISpringBootWithDynamoDB.entity.Student;
import com.ninja.RestAPISpringBootWithDynamoDB.exception.EntityNotFoundException;
import com.ninja.RestAPISpringBootWithDynamoDB.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StudentService {
	
	@Autowired
	StudentRepository studentRepo;
	private static DynamoDBMapper mapper;

	public StudentService(@Qualifier("dynamoDBMapper") DynamoDBMapper mapper) {
		StudentService.mapper = mapper;
	}

	public Iterable<Student> getStudents() {
		return studentRepo.findAll();
	}

	// Value = Name of the cache to use
	// Key = specific key to  be used to cache the data
	// unless = expression to filter what to cache
	@Cacheable(value = "DynamoDbCache", key="#id", unless = "#result.grade.equals('A')")
	public Optional<Student> getStudent(String id) {
		Optional<Student> student = studentRepo.findById(id);
		return Optional.of(student)
				.orElseThrow(() -> new EntityNotFoundException("Student not found with the given ID"));
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
		if(!exists)
			throw new EntityNotFoundException("Student(id- " + id + ") Not Found !!");
		else
			studentRepo.deleteById(id);	
	}
	
	public Student addStudent(Student student) {
		return studentRepo.save(student);	
	}

	public List<Student> pageQueryWithSize(int pageNumber, int pageSize) {
		DynamoDBScanExpression pageScanExpression = new DynamoDBScanExpression()
				.withLimit(pageSize)
				.withExclusiveStartKey(null);
		Map<String, AttributeValue> lastEvaluatedKey;
		do {
			ScanResultPage<Student> page = mapper.scanPage(Student.class, pageScanExpression);
			lastEvaluatedKey = page.getLastEvaluatedKey();
			pageScanExpression.setExclusiveStartKey(lastEvaluatedKey);
		}while (--pageNumber>1);

		pageScanExpression.setExclusiveStartKey(lastEvaluatedKey);

		return mapper.scanPage(Student.class, pageScanExpression).getResults();
	}
}
