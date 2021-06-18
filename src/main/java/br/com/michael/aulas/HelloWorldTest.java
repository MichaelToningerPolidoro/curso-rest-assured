package br.com.michael.aulas;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.request;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;


public class HelloWorldTest {

	@Test
	public void teste1() {
		Response response = request(Method.GET, "http://restapi.wcaquino.me/ola");
		
		assertEquals(response.getBody().asString(), "Ola Mundo!", "A resposta deveria ser: ");
		assertEquals(response.statusCode(), 200, "O Status code deve ser 200");
	}
	/*
	@Test
	public void devoConhecerOutrasFormasRestAssured() {
		RestAssured.get("url").then().statusCode(200);
	}
	*/
	
	@Test
	public void modoFluente() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/ola")
		.then()
			.statusCode(is(200))
			.contentType(ContentType.TEXT.withCharset("utf-8"));
	}
	
	@Test
	public void conhecendoMathchersHamcrest() {
		
		//assertThat(10, Matchers.isA(Integer.class));
		//assertThat(10, Matchers.isA(Double.class));
		//assertThat(10, Matchers.gratherThan(10)));
		//assertThat(lista, contains(1, 2, 3));
		// anyOf() -> varios casos dentro para usar com is() -> is("maria", anyOf("Joao", "aaa"))
	}
	
	@Test
	public void validarBody() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/ola")
		.then()
			.statusCode(200)
			.body(is("Ola Mundo!"))
			.body(containsString("Mundo"))
			.body(is(notNullValue()));
	}
}
