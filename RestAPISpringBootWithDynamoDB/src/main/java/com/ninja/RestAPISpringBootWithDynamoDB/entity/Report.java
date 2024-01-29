package com.ninja.RestAPISpringBootWithDynamoDB.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "Report")
public class Report {
    @DynamoDBHashKey(attributeName = "reportId")
    private String reportId;

    @DynamoDBAttribute
    private String university;

    @DynamoDBAttribute
    private String branch;

    @DynamoDBAttribute
    private int percentage;

    @DynamoDBAttribute
    private String status;
}
