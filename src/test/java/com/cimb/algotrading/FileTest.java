package com.cimb.algotrading;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

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
		
		List<TradeBean> list = fileHandler.read();
		
		assertEquals(100000, list.size());
		
	}

}
