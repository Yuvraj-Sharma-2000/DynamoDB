package com.ninja.RestAPISpringBootWithDynamoDB.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.beans.Customizer;

public class ReportTranslator implements DynamoDBTypeConverter<String,Report> {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convert(Report object) {
        try {
            return mapper.writeValueAsString(object);
        }catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Report unconvert(String object) {
        try {
            return mapper.readValue(object, Report.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
