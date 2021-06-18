package br.com.michael.desafios.testsrefact;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import br.com.michael.desafios.core.BaseTest;
import br.com.michael.desafios.tests.models.Movimentacao;
import br.com.michael.desafios.utils.DataUtils;
import io.restassured.RestAssured;

public class MovimentacaoTest extends BaseTest {
	
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
	public void deveInserirMovimentacaoComSucesso() {
		/*Post /transacoes com 
		 * conta_id, usuario_id, descricao,
		 * envolvido, tipo(DESC, REC),
		 * data_transacao(dd/MM/yyyy),
		 * valor(0.00f), status(true, false)*/
		Movimentacao movimentacao = getMovimentacaoValida();
		
		given()
			.body(movimentacao)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)
			.body("descricao", is("Descrição da movimentação"))
			.body("envolvido", is("Envolvido na movimentação"))
		;
	}
	
	@Test
	public void deveValidarCamposObrigatoriosNaMovimentacao() {
		/*Post /transacoes com campos insulficientes*/
		
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
	public void naoDeveCadastrarMovimentacaoFutura() {
		/*Post /transacao onde data transacao nao pode estar
		 * à frente da data atual*/
		
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
	public void naoDeveRemoverUmaContaComMovimentacao() {
		/*Delete /contas/id*/
		given()
			.pathParam("id", getIdContaPeloNome("Conta com movimentacao"))
			
		.when()
			.delete("/contas/{id}")
		.then()
			.statusCode(500)
			.body("constraint", is("transacoes_conta_id_foreign"))
		;
	}
	
	@Test
	public void deveRemoverUmaMovimentacao() {
		/*Delete /transacoes/id 533373*/
		int movimentacaoID = 
				getIdMovimentacaoPelaDescricao("Movimentacao para exclusao");
		
		given()
			.pathParam("id", movimentacaoID)
		.when()
			.delete("/transacoes/{id}")
		.then()
			.statusCode(204)
		;
	}
	
	private Movimentacao getMovimentacaoValida() {
		Movimentacao movimentacao = new Movimentacao();
		movimentacao.setConta_id(getIdContaPeloNome("Conta para movimentacoes"));
		//movimentacao.setUsuario_id(0000);
		movimentacao.setDescricao("Descrição da movimentação");
		movimentacao.setEnvolvido("Envolvido na movimentação");
		movimentacao.setTipo("REC");
		movimentacao.setData_transacao("01/01/2000");
		movimentacao.setData_pagamento("10/05/2010");
		movimentacao.setValor(100f);
		movimentacao.setStatus(true); // conta paga ou nao
		
		return movimentacao;
	}
	
	private static int getIdContaPeloNome(String nome) {
		return RestAssured.get("/contas?nome=" + nome)
				.then().extract().path("id[0]");
	}
	
	private static int getIdMovimentacaoPelaDescricao(String descricao) {
		return RestAssured.get("/transacoes?descricao=" + descricao)
				.then().extract().path("id[0]");
	}
}
