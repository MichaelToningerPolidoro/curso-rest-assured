package br.com.michael.desafios.testsrefact;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import br.com.michael.desafios.core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;

public class AuthTest extends BaseTest {
	
	@BeforeAll
	public static void login() {
		Map<String, String> login = new HashMap<>();
		login.put("email", "michael@tp");
		login.put("senha", "MTP123");
		
		String TOKEN = "JWT " + given()
			.body(login)
		.when()
			.post("/signin")
		.then()
			.statusCode(200)
			.extract().path("token");
		
		login.clear();
		
		RestAssured.requestSpecification.header("Authorization", TOKEN);
		
		//Resetar status das contas e transações
		RestAssured.get("/reset").then().statusCode(200);
	}
	
	@Test
	public void naoDeveAcessarAPISemToken() {
		FilterableRequestSpecification req = 
				(FilterableRequestSpecification) RestAssured.requestSpecification;
		
		req.removeHeader("Authorization");
		
		given()
		.when()
			.get("/contas")
		.then()
			.statusCode(401);
		;
	}
}
