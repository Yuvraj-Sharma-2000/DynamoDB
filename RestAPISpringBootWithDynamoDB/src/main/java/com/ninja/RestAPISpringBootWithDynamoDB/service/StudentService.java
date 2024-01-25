package com.ninja.RestAPISpringBootWithDynamoDB.service;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.ninja.RestAPISpringBootWithDynamoDB.entity.Report;
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
	private StudentRepository studentRepo;
	private static DynamoDBMapper mapper;

	public StudentService(@Qualifier("dynamoDBMapper") DynamoDBMapper mapper) {
		StudentService.mapper = mapper;
	}

	public Iterable<Student> getStudents() {
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression().withLimit(25);
		return mapper.scan(Student.class, scanExpression);
	}

	// Value = Name of the cache to use
	// Key = specific key to  be used to cache the data
	// unless = expression to filter what to cache
	@Cacheable(value = "DynamoDbCache", key="#id", unless = "#result.grade.equals('A')")
	public Optional<Student> getStudent(String id) {
		Student student = new Student();
		student.setId(id);

		return Optional.ofNullable(mapper.load(student));
	}

	public Student updateStudent(Student student, String id) {
		student.setId(id);
		mapper.delete(student);
		mapper.save(student);

		return student;
	}

	public boolean deleteStudent(String id) {
		Student student = new Student();
		student.setId(id);
		mapper.delete(student);

		return true;
	}
	
	public Student addStudent(Student student) {
		mapper.save(student);
		return student;
	}

	public List<Student> pageQueryWithSize(int pageNumber, int pageSize) {
		DynamoDBScanExpression pageScanExpression = new DynamoDBScanExpression()
				.withLimit(pageSize)
				.withExclusiveStartKey(null);
		Map<String, AttributeValue> lastEvaluatedKey = null;
		do {
			ScanResultPage<Student> page = mapper.scanPage(Student.class, pageScanExpression);
			lastEvaluatedKey = page.getLastEvaluatedKey();
			pageScanExpression.setExclusiveStartKey(lastEvaluatedKey);
		}while (--pageNumber>1);

		pageScanExpression.setExclusiveStartKey(lastEvaluatedKey);

		return mapper.scanPage(Student.class, pageScanExpression).getResults();
	}
}
