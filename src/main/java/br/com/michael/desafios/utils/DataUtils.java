package br.com.michael.desafios.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DataUtils {

	public static String getDataComDiferencaDeDias(int quantDias) {
		Calendar data = Calendar.getInstance();
		data.add(Calendar.DAY_OF_MONTH, quantDias);
		
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		
		return format.format(data.getTime());
	}
}
