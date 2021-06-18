package br.com.michael.aulas;

import org.junit.jupiter.api.Test;

import io.restassured.http.ContentType;
import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;

public class AuthTestsUsandoJWT {

	@Test
	public void deveFazerAutenticacaoComTokenJWT() {
		//login / receber token
		Map<String, String> login = new HashMap<>();
		login.put("email", "michael@tp");
		login.put("senha", "MTP123");
		
		String tokenJWT = "JWT " + given()
			.log().all()
			.body(login)
			.contentType(ContentType.JSON)
		.when()
			.post("http://barrigarest.wcaquino.me/signin")
		.then()
			.log().all()
			.statusCode(200)
			.body("token", notNullValue())
			.extract().path("token")
		;
		
		login.clear();
		
		// Buscar contas
		given()
			.log().all()
			.header("Authorization", tokenJWT)
			.contentType(ContentType.JSON)
		.when()
			.get("https://seubarriga.wcaquino.me/contas")
		.then()
			.log().all()
			.statusCode(200)
			.contentType(ContentType.JSON)
			.body("id", notNullValue())
			.body("nome", hasItem("Conta teste"))
			.body("visivel", hasItem(true))
			.body("usuario_id", notNullValue())
		;
	}
	
	@Test
	public void deveAcessarAplicacaoWeb() {
		String cookie = given()
			.log().all()
			.formParam("email", "michael@tp")
			.formParam("senha", "MTP123")
			.contentType(ContentType.URLENC.withCharset("UTF-8"))
		.when()
			.post("https://seubarriga.wcaquino.me/logar")
		.then()
			.log().all()
			.statusCode(200)
			.extract().header("set-cookie")
		;
		
		cookie = cookie.split("=")[1].split(":")[0];
		
		String body = given()
			.log().all()
		.when()
			.get("https://seubarriga.wcaquino.me/contas")
		.then()
			.log().all()
			.statusCode(200)
			.body("html.body.table.tbody.tr[0].td[0]", is("Conta teste"))
			.extract().body().asString()
		;
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, body);
		System.out.println(xmlPath.getString("html.body.table.tbody.tr[0].td[0]"));
	}
}
