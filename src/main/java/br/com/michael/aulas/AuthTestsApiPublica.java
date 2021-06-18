package br.com.michael.aulas;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class AuthTestsApiPublica {
	// URL pública do Star Wars -> https://swapi.dev/
	
	@BeforeAll
	public static void setup() {
		RestAssured.baseURI = "https://swapi.dev/api/";
	}
	
	@Test
	public void deveValidarPeople() {
		given()
			.log().all()
		.when()
			.get("people/1/")
		.then()
			.log().all()
			.statusCode(200)
			.contentType(ContentType.JSON)
			.body("name", is("Luke Skywalker"))
			.body("height.toInteger()", is(172))
			.body("mass.toInteger()", is(77))
			.body("hair_color", containsStringIgnoringCase("blond"))
			.body("skin_color", is("fair"))
			.body("eye_color", not("green"))
			.body("birth_year", endsWithIgnoringCase("BBY"))
			.body("homeworld", endsWith("planets/1/"))
			.body("films", hasSize(4))
			.body("species", hasSize(0))
			.body("vehicles", contains("http://swapi.dev/api/vehicles/14/", "http://swapi.dev/api/vehicles/30/"))
			.body("starships", hasItem("http://swapi.dev/api/starships/12/"))
			.body("created", containsString("2014-12-09"))
			.body("edited", notNullValue())
			.body("url", is("http://swapi.dev/api/people/1/"))
		;
	}
	
	@Test
	public void deveValidarPlanet() {
		given()
			.log().all()
		.when()
			.get("planets/1/")
		.then()
			.log().all()
			.statusCode(200)
			.contentType(ContentType.JSON)
			.body("url", is("http://swapi.dev/api/planets/1/"))
			.body("name", is("Tatooine"))
			.body("rotation_period.toInteger()", is(23))
			.body("orbital_period.toInteger()", not(lessThan(300)))
			.body("diameter.toInteger()", greaterThan(10000))
			.body("climate", is("arid"))
			.body("gravity", containsString("standard"))
			.body("terrain", not(is("forest")))
			.body("surface_water", is("1"))
			.body("population.toInteger()", not(is(lessThan(15000))))
			.body("residents", hasSize(greaterThan(3)))
			.body("films.size()", is(5))
			.body("created", notNullValue())
			.body("edited", notNullValue())
		;
	}
	
	@Test
	public void deveValidarStarship() {
		given()
			.log().all()
		.when()
			.get("starships/9/")
		.then()
			.log().all()
			.statusCode(200)
			.contentType(ContentType.JSON)
			.body("url", is("http://swapi.dev/api/starships/9/"))
			.body("name", is("Death Star"))
			.body("model", containsString("DS-1"))
			.body("manufacturer", containsStringIgnoringCase("Imperial Department of Military Research"))
			.body("cost_in_credits", is("1000000000000"))
			.body("length.toInteger()", not(is(lessThan(100000))))
			.body("max_atmosphering_speed", notNullValue())
			.body("crew.replace(',', '').toInteger()", is(342953))
			.body("passengers.replace(',', '').toInteger()", is(843342))
			.body("cargo_capacity", is("1000000000000"))
			.body("consumables", is("3 years"))
			.body("hyperdrive_rating.toFloat()", is(4.0f))
			.body("MGLT.toInteger()", is(10))
			.body("starship_class", containsString("Deep Space"))
			.body("pilots", hasSize(0))
			.body("films", hasSize(1))
		;
	}
	
	@Test
	public void naoDeveEncontrarStarship() {
		given()
			.log().all()
		.when()
			.get("starships/1/")
		.then()
			.log().all()
			.statusCode(404)
			.contentType(ContentType.JSON)
			.body("detail", containsStringIgnoringCase("not found"))
		;
	}
}
