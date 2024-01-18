package com.ninja.RestAPISpringBootWithDynamoDB.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName="Student")
public class Student {

	@DynamoDBHashKey(attributeName = "id")
	private String id;
	
	@DynamoDBAttribute
	private String firstName;
	
	@DynamoDBAttribute
	private String lastName;
	
	@DynamoDBAttribute
	private String grade;

	@DynamoDBAttribute(attributeName = "report")
	@DynamoDBTypeConverted(converter = ReportTranslator.class)
	private Report report;
}
