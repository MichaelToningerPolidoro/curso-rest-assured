package br.com.michael.aulas;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.*;

public class BuscasHTML {
	
	@BeforeAll
	public static void setup() {
		RestAssured.baseURI = "https://restapi.wcaquino.me/";
	}

	@Test
	public void deveFazerBuscasComHTML() {
		given()
			.log().all()
		.when()
			.get("v2/users?format=clean")
		.then()
			.log().all()
			.statusCode(200)
			.contentType(ContentType.HTML)
			.appendRootPath("html.body.div.table.tbody.")
			.body("tr.size()", is(3))
			.body("tr[1].td[2]", is("25"))
		;
	}
	
	@Test
	public void deveFazerBuscasComXpathEmHTML() {
		given()
			.log().all()
		.when()
			.get("v2/users?format=clean")
		.then()
			.log().all()
			.statusCode(200)
			.contentType(ContentType.HTML)
			.body(hasXPath("count(//table/td)"), is("4"))
		;
	}
}
