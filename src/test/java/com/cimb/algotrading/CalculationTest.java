/**
 * 
 */
package com.cimb.algotrading;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
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
//	private final Logger log = Logger.getLogger(getClass());

	// private DecimalFormat df = new DecimalFormat("000.000");

	@Test
	public void testSimpleMA() throws FileNotFoundException, IOException, ParseException {

		properties = CalculatorPropertiesFactory.create();
		context = new AnnotationConfigApplicationContext();
		context.getBeanFactory().registerSingleton("properties", properties);
		context.register(CalculatorConfiguration.class);
		context.refresh();

		FileHandler fileHandler = context.getBean(FileHandler.class);
		SimpleMovingAverageAnalysis sma = context.getBean(SimpleMovingAverageAnalysis.class);
		ExponentialMovingAverage ema = context.getBean(ExponentialMovingAverage.class);

		Map<String, List<TradeBean>> map = fileHandler.read();
		List<TradeBean> rawList = map.get("raw");
		List<TradeBean> minuteList = map.get("minute");

		Assert.assertEquals(67.82, sma.calculateLastTrades(rawList, 20, 7),0.0001);
		Assert.assertEquals(67.79, sma.calculateLastTrades(rawList, 7, 20),0.0001);
		
		Assert.assertEquals(67.8345, sma.calculateLastMinutes(minuteList, 20, 3),0.0001);
		Assert.assertEquals(67.864, sma.calculateLastMinutes(minuteList, 3, 20),0.0001);
		
		Assert.assertEquals(59.3845,ema.calculateLastMinutes(minuteList, 2, 3),0.0001);
	}

}
