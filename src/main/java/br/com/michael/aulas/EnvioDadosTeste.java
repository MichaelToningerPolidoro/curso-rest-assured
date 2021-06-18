package br.com.michael.aulas;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.*;

public class EnvioDadosTeste {
	
	@BeforeAll
	public static void setup() {
		RestAssured.baseURI = "https://restapi.wcaquino.me/";
	}

	@Test
	public void deveEnviarValorViaQuery() {
		given()
		.when()
			.log().all()
			.get("v2/users?format=json")
		.then()
			.log().all()
			.statusCode(200)
			.contentType(ContentType.JSON)
		;
	}
	
	@Test
	public void deveEnviarValorViaQueryViaParametro() {
		given()
			.log().all()
			.queryParam("format", "json")
			.accept(ContentType.JSON)
		.when()
			.get("v2/users")
		.then()
			.log().all()
			.statusCode(200)
			.contentType(ContentType.JSON)
			.contentType(containsString("utf-8"))
		;
	}
	
	@Test
	public void deveEnviarValorViaQueryViaHeader() {
		given()
			.log().all()
			.queryParam("format", "json")
		.when()
			.get("v2/users")
		.then()
			.log().all()
			.statusCode(200)
			.contentType(ContentType.JSON)
			.contentType(containsString("utf-8"))
		;
	}
}
