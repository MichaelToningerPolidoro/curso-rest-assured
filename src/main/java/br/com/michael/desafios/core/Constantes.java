package br.com.michael.desafios.core;

import io.restassured.http.ContentType;

public interface Constantes {
	String APP_BASE_URL = "https://barrigarest.wcaquino.me";
	int APP_PORT = 443;
	String APP_BASE_PATH = "";  // v1/algo, v2/algo
	
	ContentType APP_CONTENT_TYPE = ContentType.JSON;
	
	Long MAX_TIMEOUT = 6000L;
}
