package br.com.michael.aulas;

import org.junit.jupiter.api.Test;

import io.restassured.http.ContentType;

import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.*;

public class UserXMLTest {

	@Test
	public void trabalhandoComXML() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/usersXML/3")
		.then()
			.statusCode(200)
			.contentType(ContentType.XML)
			
			.rootPath("user")
			.body("name", is("Ana Julia"))
			.body("@id", is("3"))
			
			.rootPath("user.filhos")
			.body("name.size()", is(2))
			
			.detachRootPath("filhos")
			.body("filhos.name[0]", is("Zezinho"))
			
			.appendRootPath("filhos")
			.body("name", hasItems("Luizinho", "Zezinho"))
		;
	}
	
	@Test
	public void pesquisasAvancadasComXML() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/usersXML")
		.then()
			.statusCode(200)
			.contentType(ContentType.XML)
			.rootPath("users.user")
			.body("size()", is(3))
			.body("findAll{it.age.toInteger() <= 25}.size()", is(2))
			.body("@id", hasItems("1", "2", "3"))
			.body("find{it.age.toInteger() == 25}.name", is("Maria Joaquina"))
			.body("findAll{it.name.toString().contains('n')}.name", hasItems("Maria Joaquina", "Ana Julia"))
			.body("salary.find{it != null}", is("1234.5678"))
			.body("salary.find{it != null}.toFloat()", is(1234.5678f))
		;
	}
}
