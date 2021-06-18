package br.com.michael.desafios.core;

import org.junit.jupiter.api.BeforeAll;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;

import static org.hamcrest.Matchers.*;

public class BaseTest implements Constantes{


	@BeforeAll
	public static void setup() {
		RestAssured.baseURI = APP_BASE_URL;
		RestAssured.basePath = APP_BASE_PATH;
		RestAssured.port = APP_PORT;
		
		RequestSpecBuilder recBuilder = new RequestSpecBuilder()
				.setContentType(APP_CONTENT_TYPE);
		
		ResponseSpecBuilder respBuilder = new ResponseSpecBuilder()
				.expectResponseTime(lessThan(MAX_TIMEOUT));
		
		RestAssured.requestSpecification = recBuilder.build();
		RestAssured.responseSpecification = respBuilder.build();
		
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		
		System.out.println("Setup realizado!");
	}
}
