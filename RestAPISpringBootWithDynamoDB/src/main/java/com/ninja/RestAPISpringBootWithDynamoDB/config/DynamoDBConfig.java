package com.ninja.RestAPISpringBootWithDynamoDB.config;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import org.springframework.context.annotation.Primary;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableCaching
@EnableDynamoDBRepositories
	(basePackages = "com.ninja.RestAPISpringBootWithDynamoDB")
	public class DynamoDBConfig {

	    @Value("${amazon.dynamodb.endpoint}")
	    private String amazonDynamoDBEndpoint;

	    @Value("${amazon.aws.accesskey}")
	    private String amazonAWSAccessKey;

	    @Value("${amazon.aws.secretkey}")
	    private String amazonAWSSecretKey;

	    @Bean
	    public AmazonDynamoDB amazonDynamoDB(AWSCredentialsProvider awsCredentialsProvider) {
	        AmazonDynamoDB amazonDynamoDB
	            = AmazonDynamoDBClientBuilder.standard()
	            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(amazonDynamoDBEndpoint, "us-west-2"))
	            .withCredentials(awsCredentialsProvider).build();
	        return amazonDynamoDB;
	    }

	    @Bean
	    public AWSCredentialsProvider awsCredentialsProvider() {
	        return new AWSStaticCredentialsProvider(new BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey));
	    }

		@Bean
		@Primary
		public DynamoDBMapper dynamoDBMapper() {
			return new DynamoDBMapper(amazonDynamoDB(awsCredentialsProvider()),	DynamoDBMapperConfig.DEFAULT);
		}

		@Bean
		public CacheManager cacheManager() {
			// configure and return an implementation of Spring's CacheManager SPI
			SimpleCacheManager cacheManager = new SimpleCacheManager();
			cacheManager.setCaches(List.of(new ConcurrentMapCache("DynamoDbCache")));
			return cacheManager;
		}
}
