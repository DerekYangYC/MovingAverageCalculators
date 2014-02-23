/**
 * 
 */
package com.cimb.algotrading;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.cimb.algotrading.bean.TradeBean;
import com.cimb.algotrading.calculation.ExponentialMovingAverage;
import com.cimb.algotrading.calculation.SimpleMovingAverageAnalysis;
import com.cimb.algotrading.config.CalculatorConfiguration;
import com.cimb.algotrading.config.properties.CalculatorPropertiesFactory;
import com.cimb.algotrading.config.properties.ICalculatorProperties;
import com.cimb.algotrading.handler.FileHandler;

/**
 * @author DerekYang
 *
 */
@RunWith(JUnit4.class)
public class CalculationTest {

	private AnnotationConfigApplicationContext context;
	private ICalculatorProperties properties;
	private final Logger log = Logger.getLogger(getClass());
	
//	private DecimalFormat df = new DecimalFormat("000.000");
	
	@Test
	public void testSimpleMA() throws FileNotFoundException, IOException, ParseException{
		
		properties = CalculatorPropertiesFactory.create();
		context = new AnnotationConfigApplicationContext();
		context.getBeanFactory().registerSingleton("properties", properties);
		context.register(CalculatorConfiguration.class);
		context.refresh();
		
		FileHandler fileHandler = context.getBean(FileHandler.class);
		SimpleMovingAverageAnalysis sma = context.getBean(SimpleMovingAverageAnalysis.class);
		ExponentialMovingAverage ema = context.getBean(ExponentialMovingAverage.class);
		
		List<TradeBean> list = fileHandler.read();
		
		System.out.println(sma.calculateLastTrades(list, 3, 4));
		
//		System.out.println(sma.calculateLastMinutes(list, 712, 1));
		
//		System.out.println(ema.calculateLastMinutes(list, 517, 3));
	}
	
	
}
