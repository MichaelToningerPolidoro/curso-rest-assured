package br.com.michael.desafios.testsrefact;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import br.com.michael.desafios.core.BaseTest;
import io.restassured.RestAssured;

public class ContasTest extends BaseTest {
	
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
	public void deveIncluirUmaContaComSucesso() {
		
		Map<String, String> conta = new HashMap<>();
		conta.put("nome", "Conta inserida");
		
		given()
			.body(conta)
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
		;
		
		conta.clear();
	}
	
	@Test
	public void deveAlterarContaComSucesso() {
		/*Put em /contas/:id
		 */
		Map<String, String> conta = new HashMap<>();
		conta.put("nome", "Conta alterada");
		
		int CONTA_ID = getIdContaPeloNome("Conta para alterar");
		
		given()
			.body(conta)
			.pathParam("id", CONTA_ID)
		.when()
			.put("/contas/{id}")
		.then()
			.statusCode(200)
			.body("nome", is(conta.get("nome")))
		;
		
		conta.clear();
	}
	
	@Test
	public void naoDeveIncluirContaComNomeRepetido() {
		Map<String, String> novaConta = new HashMap<>();
		novaConta.put("nome", "Conta mesmo nome");
		
		given()
			.body(novaConta)
		.when()
			.post("/contas")
		.then()
			.statusCode(400)
			.body("error", is("Já existe uma conta com esse nome!"))
		;
		
		novaConta.clear();
	}
	
	private static int getIdContaPeloNome(String nome) {
		return RestAssured.get("/contas?nome=" + nome)
				.then().extract().path("id[0]");
	}
}
