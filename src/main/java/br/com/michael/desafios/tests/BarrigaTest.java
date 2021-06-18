package br.com.michael.desafios.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import br.com.michael.desafios.core.BaseTest;
import br.com.michael.desafios.tests.models.Movimentacao;
import br.com.michael.desafios.utils.DataUtils;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;

@TestMethodOrder(OrderAnnotation.class)
public class BarrigaTest extends BaseTest {

	private static int MOVIMENTACAO_ID;
	
	private static String NAME_CONTA = "Conta" + System.nanoTime();
	private static String NAME_CONTA_ALTERADA = NAME_CONTA + " alterada";
	
	private static int CONTA_ID;
	
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
	}
	
	@Test
	@Order(1)
	public void t01_deveIncluirUmaContaComSucesso() {
		Map<String, String> conta = new HashMap<>();
		conta.put("nome", NAME_CONTA);
		
		CONTA_ID = given()
			.body(conta)
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
			.extract()
			.path("id")
		;
		
		conta.clear();
	}
	
	@Test
	@Order(2)
	public void t02_deveAlterarContaComSucesso() {
		Map<String, String> conta = new HashMap<>();
		conta.put("nome", NAME_CONTA_ALTERADA);
		
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
	@Order(3)
	public void t03_naoDeveIncluirContaComNomeRepetido() {
		Map<String, String> novaConta = new HashMap<>();
		novaConta.put("nome", NAME_CONTA_ALTERADA);
		
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
	
	@Test
	@Order(4)
	public void t04_deveInserirMovimentacaoComSucesso() {
		Movimentacao movimentacao = getMovimentacaoValida();
		
		MOVIMENTACAO_ID = given()
			.body(movimentacao)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)
			.extract()
			.path("id")
		;
	}
	
	@Test
	@Order(5)
	public void t05_deveValidarCamposObrigatoriosNaMovimentacao() {
		given()
			.body("{}") // mesma coisa que "nada"
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", hasSize(8))
			.body("msg", hasItems(
						"Data da Movimentação é obrigatório",
						"Data do pagamento é obrigatório",
						"Descrição é obrigatório",
						"Interessado é obrigatório",
						"Valor é obrigatório",
						"Valor deve ser um número",
						"Conta é obrigatório",
						"Situação é obrigatório"
					))
		;
	}
	
	@Test
	@Order(6)
	public void t06_naoDeveCadastrarMovimentacaoFutura() {
		Movimentacao movimentacao = new Movimentacao();
		movimentacao.setData_transacao(DataUtils.getDataComDiferencaDeDias(2));
		
		given()
			.body(movimentacao)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
		;
	}
	
	@Test
	@Order(7)
	public void t07_naoDeveRemoverUmaContaComMovimentacao() {
		given()
			.pathParam("id", CONTA_ID)
			
		.when()
			.delete("/contas/{id}")
		.then()
			.statusCode(500)
			.body("constraint", is("transacoes_conta_id_foreign"))
		;
	}
	
	@Test
	@Order(8)
	public void t08_deveCalcularSaldoDeContas() {
		given()
		.when()
			.get("/saldo")
		.then()
			.statusCode(200)
			.body("find{it.conta_id == "+ CONTA_ID +"}.saldo", is("100.00"))
		;
	}
	
	@Test
	@Order(9)
	public void t09_deveRemoverUmaMovimentacao() {
		/*Delete /transacoes/id 533373*/
		Map<String, Integer> transacao = new HashMap<>();
		transacao.put("id", 533373);
		
		given()
			.pathParam("id", MOVIMENTACAO_ID)
		.when()
			.delete("/transacoes/{id}")
		.then()
			.statusCode(204)
		;
		
		transacao.clear();
	}
	
	@Test
	@Order(10)
	public void t10_naoDeveAcessarAPISemToken() {
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
	
	private Movimentacao getMovimentacaoValida() {
		Movimentacao movimentacao = new Movimentacao();
		movimentacao.setConta_id(CONTA_ID);
		movimentacao.setDescricao("Descrição da movimentação");
		movimentacao.setEnvolvido("Envolvido na movimentação");
		movimentacao.setTipo("REC");
		movimentacao.setData_transacao("01/01/2000");
		movimentacao.setData_pagamento("10/05/2010");
		movimentacao.setValor(100f);
		movimentacao.setStatus(true); // conta paga ou nao
		
		return movimentacao;
	}
}
