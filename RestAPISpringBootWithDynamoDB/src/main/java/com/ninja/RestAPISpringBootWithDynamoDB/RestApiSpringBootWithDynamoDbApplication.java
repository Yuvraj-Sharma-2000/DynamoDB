package com.ninja.RestAPISpringBootWithDynamoDB;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.github.javafaker.Faker;
import com.ninja.RestAPISpringBootWithDynamoDB.entity.Report;
import com.ninja.RestAPISpringBootWithDynamoDB.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SpringBootApplication
public class RestApiSpringBootWithDynamoDbApplication {

	private static DynamoDBMapper mapper;
	static Faker faker;

	public RestApiSpringBootWithDynamoDbApplication(@Qualifier("dynamoDBMapper") DynamoDBMapper mapper) {
		RestApiSpringBootWithDynamoDbApplication.mapper = mapper;
	}
	public static void main(String[] args) {
		faker = new Faker();
		SpringApplication.run(RestApiSpringBootWithDynamoDbApplication.class, args);
//		load(mapper);
//		save(mapper);
//		query(mapper);
//		delete(mapper);
//		pageQuery(mapper);
		scan(mapper);
	}

	private static void scan(DynamoDBMapper mapper) {
		// Create a DynamoDBScanExpression with filters on the "grade" and "firstName" attributes
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();

		// Build a map for the filter expressions
		Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
		expressionAttributeValues.put(":gradeVal", new AttributeValue().withS("A"));
		expressionAttributeValues.put(":firstNameVal", new AttributeValue().withS("Ram"));

		// Set the filter expressions
		scanExpression
				.withFilterExpression("grade = :gradeVal AND firstName = :firstNameVal")
				.withExpressionAttributeValues(expressionAttributeValues);

		// Perform the scan operation with DynamoDBScanExpression
		PaginatedScanList<Student> scanResult = mapper.scan(Student.class, scanExpression);

		scanResult.forEach(System.out::println);
	}

	private static void pageQuery(DynamoDBMapper mapper) {
		Student s = new Student();
		s.setId("1");
		DynamoDBQueryExpression<Student> queryExpression =
				new DynamoDBQueryExpression<Student>()
						.withHashKeyValues(s)
						.withLimit(2);
		QueryResultPage<?> resultPage = mapper.queryPage(Student.class, queryExpression);

		System.out.println(resultPage.getResults());
		System.out.println(resultPage.getLastEvaluatedKey());

	}

	private static void delete(DynamoDBMapper mapper) {
		// Single delete //
		Student s = new Student();
		s.setId("3");
		mapper.delete(s);

		// Batch delete //
//		mapper.batchDelete(s,s,s);
	}

	private static void query(DynamoDBMapper mapper) {
		// Normal query //
		Student s = new Student();
		s.setId("1");

		DynamoDBQueryExpression<Student> queryExpression =
				new DynamoDBQueryExpression<Student>()
						.withHashKeyValues(s)
//						.withIndexName("grade")      if using GSI
//						.withConsistentRead(false)   to avoid stale data of secondary table
						.withLimit(10);
		List<Student> studentList = mapper.query(Student.class, queryExpression);

		studentList.forEach(System.out::println);
	}

	private static void save(DynamoDBMapper mapper) {
		// Basic save //
		Student s  = new Student();
		s.setId("1");
		s.setGrade("B");
		s.setFirstName("Vicky");
		s.setLastName("Tomar");
		s.setReport(Report.builder().
				reportId("5").
				university("SSTC").
				branch("CSE").
				percentage(95).
				status("PASS").
				build());

		mapper.save(s);

		// Batch save //
//		mapper.batchSave(s,s,s);
	}

	private static void load(DynamoDBMapper mapper){
		// ------- Basic load -------- //
		Student student = new Student();
		student.setId("1");

		Student result = mapper.load(student);
		System.out.println(result);
	}
}




