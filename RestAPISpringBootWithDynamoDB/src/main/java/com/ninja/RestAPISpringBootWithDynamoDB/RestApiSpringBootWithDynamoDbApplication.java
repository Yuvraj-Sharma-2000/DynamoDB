package com.ninja.RestAPISpringBootWithDynamoDB;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.github.javafaker.Faker;
import com.ninja.RestAPISpringBootWithDynamoDB.entity.Report;
import com.ninja.RestAPISpringBootWithDynamoDB.entity.Student;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;


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
//		scan(mapper);
//		populateTable(mapper);
	}

	public static void scan(DynamoDBMapper mapper) {
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

	public static void pageQuery(DynamoDBMapper mapper) {
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

	public static void delete(DynamoDBMapper mapper) {
		// Single delete //
		Student s = new Student();
		s.setId("3");
		mapper.delete(s);

		// Batch delete //
//		mapper.batchDelete(s,s,s);
	}

	public static void query(DynamoDBMapper mapper) {
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

	public static void populateTable(DynamoDBMapper mapper){
		for (int i = 0; i < 100; i++) {
			Student s  = new Student();
			s.setId(String.valueOf(i));
			s.setGrade(faker.random().hex(1).toUpperCase());
			s.setFirstName(faker.name().firstName());
			s.setLastName(faker.name().lastName());
			s.setReport(Report.builder().
					reportId(String.valueOf(i*10)).
					university(faker.university().name()).
					branch(faker.educator().course()).
					percentage(faker.number().numberBetween(0,100)).
					status(i%2==0?"PASS":"FAIL").
					build());

			mapper.save(s);
		}
	}
	public static void save(DynamoDBMapper mapper) {
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

	public static void load(DynamoDBMapper mapper){
		// ------- Basic load -------- //
		Student student = new Student();
		student.setId("1");

		Student result = mapper.load(student);
		System.out.println(result);
	}
}