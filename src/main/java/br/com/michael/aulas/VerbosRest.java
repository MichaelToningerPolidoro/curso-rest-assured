package br.com.michael.aulas;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import br.com.michael.models.User;
import io.restassured.http.ContentType;

public class VerbosRest {
	
	@Test
	public void salvarUsuario() {
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body("{\"name\": \"José\", \"age\": \"15\"}")
		.when()
			.post("https://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(201)
			.body("id", notNullValue())
			.body("name", is("José"))
			.body("age", is(15))
		;
	}
	
	@Test
	public void salvarUsuarioUsandoUmMap() {
		Map<String, Object> param = new HashMap<>();
		param.put("name", "usuario MAP");
		param.put("age", 32);
		
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body(param)
		.when()
			.post("https://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(201)
			.body("id", notNullValue())
			.body("name", is("usuario MAP"))
			.body("age", is(32))
		;
	}
	
	@Test
	public void salvarUsuarioUsandoUmObjeto() {
		User user = new User("Usuario objeto", 20);
		
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body(user)
		.when()
			.post("https://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(201)
			.body("id", notNullValue())
			.body("name", is("Usuario objeto"))
			.body("age", is(20))
		;
	}
	
	@Test
	public void deveDeserializarObjetoQuandoSalvarUsuarioUsandoUmObjeto() {
		User user = new User("Usuario deserializado", 20);
		
		User insertedUser = given()
			.log().all()
			.contentType(ContentType.JSON)
			.body(user)
		.when()
			.post("https://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(201)
			.extract()
			.body()
			.as(User.class)
		;
		
		assertEquals(insertedUser, user);
		assertEquals(user.getName(), user.getName());
		assertEquals(user.getAge(), insertedUser.getAge());
	}
	
	@Test
	public void naoDeveSalvarUsuarioSemNome() {
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body("{ \"age\": \"15\"}")
		.when()
			.post("https://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(400)
			.body("id", notNullValue())
		;
	}
	
	@Test
	public void deveAlterarUsuario() {
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body("{ \"name\": \"usuario modificado\", \"age\": 15}")
		.when()
			.put("https://restapi.wcaquino.me/users/1")
		.then()
			.log().all()
			.statusCode(200)
			.body("id", notNullValue())
			.body("name", is("usuario modificado"))
			.body("age", is(15))
			.body("salary", is(1234.5678f))
		;
	}
	
	@Test
	public void customizarURL() {
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body("{ \"name\": \"usuario modificado\", \"age\": 15}")
			.pathParam("entity", "users")
			.pathParam("userId", 1)
		.when()
			.put("https://restapi.wcaquino.me/{entity}/{userId}")
		.then()
			.log().all()
			.statusCode(200)
			.body("id", notNullValue())
			.body("name", is("usuario modificado"))
			.body("age", is(15))
			.body("salary", is(1234.5678f))
		;
	}
	
	@Test
	public void deveRemoverUsuario() {
		given()
			.log().all()
			.contentType(ContentType.JSON)
		.when()
			.delete("https://restapi.wcaquino.me/users/1")
		.then()
			.log().all()
			.statusCode(204)
		;
	}
	
	@Test
	public void deveRemoverUsuarioInexistente() {
		given()
			.log().all()
			.contentType(ContentType.JSON)
		.when()
			.delete("https://restapi.wcaquino.me/users/1000")
		.then()
			.log().all()
			.statusCode(400)
			.body("error", is("Registro inexistente"))
		;
	}
}
