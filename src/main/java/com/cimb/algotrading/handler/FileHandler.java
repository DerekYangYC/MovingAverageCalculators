/**
 * 
 */
package com.cimb.algotrading.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.cimb.algotrading.bean.TradeBean;
import com.cimb.algotrading.config.properties.ICalculatorProperties;

/**
 * @author DerekYang
 * 
 */
public final class FileHandler {

	private ICalculatorProperties properties;

	private final static int DECIMAL_SIZE = 3;
	public final static int DECIMAL_NUM = (int) Math.pow(10, DECIMAL_SIZE);

	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	public FileHandler(ICalculatorProperties properties) {
		this.properties = properties;
	}

	public List<TradeBean> read() throws IOException, ParseException {

		if (properties.inputFile().isEmpty()) {
			throw new IllegalArgumentException("InputFile is NOT set in the property config file!");
		}

		InputStream is = new FileInputStream(new File(properties.inputFile()));
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		List<TradeBean> returnList = new ArrayList<TradeBean>();

		String line = reader.readLine();

		while ((line = reader.readLine()) != null) {
			String[] tmp = line.split(",");

			Calendar tmpDateTime = Calendar.getInstance();
			tmpDateTime.setTime(sdf.parse(tmp[0] + " " + tmp[5]));

			int tmpPrice = (int) Math.round(Double.parseDouble(tmp[2]) * DECIMAL_NUM);
			int tmpSize = Integer.parseInt(tmp[3]);

			returnList.add(new TradeBean(tmp[1], tmpPrice, DECIMAL_SIZE, tmpSize, tmpDateTime));
		}

		reader.close();
		return returnList;
	}

}
