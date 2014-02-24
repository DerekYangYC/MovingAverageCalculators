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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private final static SimpleDateFormat cdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public FileHandler(ICalculatorProperties properties) {
		this.properties = properties;
	}

	public Map<String, List<TradeBean>> read() throws IOException, ParseException {

		if (properties.inputFile().isEmpty()) {
			throw new IllegalArgumentException("InputFile is NOT set in the property config file!");
		}

		InputStream is = new FileInputStream(new File(properties.inputFile()));
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		List<TradeBean> rawList = new ArrayList<TradeBean>();
		List<TradeBean> minuteList = new ArrayList<TradeBean>();
		Map<String, List<TradeBean>> map = new HashMap<String, List<TradeBean>>();

		String line = reader.readLine();

		int minute = 0;
		int count = 0;
		int avgPrice = 0;
		int avgSize = 0;
		int avgTempPrice = 0;
		int avgTempSize = 0;
		String symbol = "";
		Calendar avgCal = Calendar.getInstance();

		while ((line = reader.readLine()) != null) {
			String[] tmp = line.split(",");
			
			symbol = tmp[1];
			
			Calendar tmpDateTime = Calendar.getInstance();
			tmpDateTime.setTime(sdf.parse(tmp[0] + " " + tmp[5]));

			int tmpMin = tmpDateTime.get(Calendar.MINUTE);
			if (count == 0) {
				minute = tmpMin;
				avgCal.setTime(cdf.parse(tmp[0] + " " + tmp[5]));
			}

			int tmpPrice = (int) Math.round(Double.parseDouble(tmp[2]) * DECIMAL_NUM);
			int tmpSize = Integer.parseInt(tmp[3]);

			rawList.add(new TradeBean(symbol, tmpPrice, DECIMAL_SIZE, tmpSize, tmpDateTime));

			if (tmpMin == minute) {
				avgPrice += tmpPrice;
				avgSize += tmpSize;
				count++;
			} else {
				double ap = avgPrice;
				double c = count;
				avgTempPrice = (int) Math.round((double) (ap / c));
				avgTempSize = avgSize / count;
				minuteList.add(new TradeBean(symbol, avgTempPrice, DECIMAL_SIZE, avgTempSize,
						avgCal));
				avgCal = Calendar.getInstance();
				avgCal.setTime(cdf.parse(tmp[0] + " " + tmp[5]));
				avgPrice = tmpPrice;
				avgSize = tmpSize;
				minute = tmpMin;
				count=1;
			}
		}
		//Add the last item in the text file
		minuteList.add(new TradeBean(symbol, avgTempPrice, DECIMAL_SIZE, avgTempSize, avgCal));

		reader.close();

		map.put("raw", rawList);
		map.put("minute", minuteList);
		return map;
	}

}
