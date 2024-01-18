package com.ninja.RestAPISpringBootWithDynamoDB.repository;

import org.socialsignin.spring.data.dynamodb.repository.DynamoDBCrudRepository;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ninja.RestAPISpringBootWithDynamoDB.entity.Student;

@EnableScan
@Repository
public interface StudentRepository extends DynamoDBCrudRepository<Student,String> {
	
}
