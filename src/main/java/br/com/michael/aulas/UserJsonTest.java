package br.com.michael.aulas;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class UserJsonTest {
	
	private static RequestSpecification requestEspecification;
	private static ResponseSpecification responseEspecification;
	
	@BeforeAll
	public static void setup() {
		RestAssured.baseURI = "http://restapi.wcaquino.me";
		//RestAssured.port = 80;
		//RestAssured.basePath = "/v2/";
		System.out.println("Setup realizado");
		
		RequestSpecBuilder reqBuilder = new RequestSpecBuilder();
		//reqBuilder.log(LogDetail.ALL);
		requestEspecification = reqBuilder.build();
		
		responseEspecification = new ResponseSpecBuilder()
				.expectStatusCode(200)
				.build();
		
		//Definir por padrão as specificações, atruibuindo-as nos atributos
		//estáticos da classe RestAssured
		RestAssured.requestSpecification = requestEspecification;
		//RestAssured.responseSpecification = responseEspecification;
	}

	@Test
	public void verificacaoPrimeiroNivelJSON() {
		
		given()
		.when()
			.get("/users/1")
		.then()
			//.statusCode(200)
			.spec(responseEspecification)
			.body("id", is(1))
			.body("name", containsStringIgnoringCase("Silva"))
			.body("age", greaterThan(10));
	}
	
	@Test
	public void verificacaoPrimeiroNivelJSONOutraForma() {
		// Tirando do jsone  colocando direto nas assertivas, com response
		
		Response response = 
				RestAssured.request(Method.GET, "/users/1");
		
		//path	
		assertEquals(1, Integer.valueOf(response.path("id").toString()));
		assertEquals(1, Integer.valueOf(response.path("%s", "id").toString()));
		
		//Json path
		//Passa o corpo todo como um texto
		JsonPath jpath = new JsonPath(response.asString());
		assertEquals(1, jpath.getInt("id"));
		
		// Outra forma (from)
		
		int id = JsonPath.from(response.asString()).getInt("id");
		assertEquals(1, id);
	}
	
	@Test
	public void verificacaoSegundoNivelJSON() {
		given()
		.when()
			.get("/users/2")
		.then()
			.spec(responseEspecification)
			.body("name", containsStringIgnoringCase("Joaquina"))
			.body("endereco.rua", is("Rua dos bobos"));
	}
	
	@Test
	public void verificacaoSegundoNivelJSON2() {
		given()
		.when()
			.get("/users/3")
		.then()
			.spec(responseEspecification)
			.body("name", containsStringIgnoringCase("Ana"))
			.body("filhos", hasSize(2))
			.body("filhos[0].name", is("Zezinho"))
			.body("filhos[1].name", is("Luizinho"))
			.body("filhos.name", hasItem("Zezinho"))
			;
	}
	
	@Test
	public void deveRetornarErroUsuarioInexistente() {
		given()
		.when()
			.get("/users/131312312")
		.then()
			.statusCode(404)
			.body("error", is("Usuário inexistente"))
		;
	}
	
	@Test
	public void listaNaRaiz() {
		given()
		.when()
			.get("/users")
		.then()
			.spec(responseEspecification)
			// Utiliza o "$" ou "" para demostrar que está buscando na Raiz
			.body("$", hasSize(3))
			.body("name", hasItems("João da Silva", "Maria Joaquina", "Ana Júlia"))
			.body("age[1]", is(25))
			.body("filhos.name", hasItem(Arrays.asList("Zezinho", "Luizinho")))
			.body("salary", contains(1234.5678f, 2500, null))
		;
	}
	
	@Test
	public void verificacoesAvancadas() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/users")
		.then()
			.spec(responseEspecification)
			.body("$", hasSize(3))
			.body("age.findAll{it <= 25}.size()", is(2))
			.body("age.findAll{it <= 25 && it >= 20}.size()", is(2))
			.body("findAll{it.age <= 25 && it.age > 20}.name", hasItem("Maria Joaquina"))
			.body("findAll{it.age >= 25}[0].name", is("João da Silva"))
			.body("find{it.age <=25}.name", is("Maria Joaquina"))
			.body("findAll{it.name.contains('n')}.name", hasItems("Maria Joaquina", "Ana Júlia"))
			.body("findAll{it.name.length() > 10}.name", hasItems("Maria Joaquina", "João da Silva"))
			.body("name.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
			.body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
			.body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}.toArray()"
					, allOf(arrayContaining("MARIA JOAQUINA")))
			.body("age.collect{it * 2}", hasItems(60, 50, 40))
			.body("id.max()", is(3))
			.body("salary.min()", is(1234.5678f))
			.body("salary.findAll{it != null}.sum()", closeTo(3734.5678f, 0.1))
		;
	}
	
	@Test
	public void unindoJsonPathComJava() {
		ArrayList<String> names = given()
		.when()
			.get("http://restapi.wcaquino.me/users")
		.then()
			.spec(responseEspecification)
			.extract().path("name.findAll{it.startsWith('Maria')}")
			// Codigo abaixo representa codigo acima
			//.body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}.toArray()"
			//		, allOf(arrayContaining("MARIA JOAQUINA")))
		;
		
		assertEquals(1, names.size());
		assertTrue(names.get(0).equalsIgnoreCase("maria joaquina"));
		assertEquals(names.get(0).toUpperCase(), "MARIA JOAQUINA");
	}
}
