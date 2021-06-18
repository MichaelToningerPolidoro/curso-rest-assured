package br.com.michael.desafios.testsrefact;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import br.com.michael.desafios.core.BaseTest;
import io.restassured.RestAssured;

public class SaldoTest extends BaseTest {
	
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
	public void deveCalcularSaldoDeContas() {
		/*get /saldo  obs ele retorna apenas saldo de 
		 * contas com movimentacao*/
		int contaID = getIdContaPeloNome("Conta para saldo");
		
		given()
		.when()
			.get("/saldo")
		.then()
			.statusCode(200)
			.body("find{it.conta_id == "+ contaID +"}.saldo", is("534.00"))
		;
	}
	
	private static int getIdContaPeloNome(String nome) {
		return RestAssured.get("/contas?nome=" + nome)
				.then().extract().path("id[0]");
	}
}
