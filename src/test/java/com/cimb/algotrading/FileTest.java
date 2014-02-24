package com.cimb.algotrading;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.cimb.algotrading.bean.TradeBean;
import com.cimb.algotrading.config.properties.CalculatorPropertiesFactory;
import com.cimb.algotrading.config.properties.ICalculatorProperties;
import com.cimb.algotrading.handler.FileHandler;

@RunWith(JUnit4.class)
public class FileTest {

	@Test
	public void testRead() throws FileNotFoundException, IOException, ParseException {

		ICalculatorProperties properties = CalculatorPropertiesFactory.create();

		FileHandler fileHandler = new FileHandler(properties);

		Map<String, List<TradeBean>> map = fileHandler.read();

		List<TradeBean> rawList = map.get("raw");
		List<TradeBean> minuteList = map.get("minute");

		for(TradeBean b : minuteList){
			System.out.println(b.toString(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
		}
		
		assertEquals(100000, rawList.size());
		assertEquals(1826, minuteList.size());
	}

}
