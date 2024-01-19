package com.ninja.RestAPISpringBootWithDynamoDB;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.github.javafaker.Faker;
import com.ninja.RestAPISpringBootWithDynamoDB.entity.Report;
import com.ninja.RestAPISpringBootWithDynamoDB.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;


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
		delete(mapper);
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
		s.setId("2");
		s.setGrade("S");
		s.setFirstName("Rotten");
		s.setLastName("tom");
		s.setReport(Report.builder().
				reportId("33").
				university("LAS").
				branch("BS").
				percentage(40).
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




