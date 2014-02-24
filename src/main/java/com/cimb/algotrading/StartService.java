/**
 * 
 */
package com.cimb.algotrading;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Logger;
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
public class StartService {

	private final static Logger log = Logger.getLogger(StartService.class);

	private AnnotationConfigApplicationContext context;
	private ICalculatorProperties properties;
	private int choice;

	private int userInput() {
		System.out.println("Please select the following moving averages: ");
		System.out.println("1)  Simple moving average over the last n trades "
				+ " | 2)  Simple moving average over the last m minutes "
				+ " | 3)  An Exponential Moving Average " + " | 4) The MACD indicator");
		System.out.print("Please select a number: ");

		Scanner reader = new Scanner(System.in);
		choice = reader.nextInt();

		while (choice < 1 || choice > 4) {
			System.out.println("Please enter again, or press ctrl+c to exist: ");
			choice = reader.nextInt();
		}

		reader.close();
		return choice;
	}

	private void start() throws FileNotFoundException, IOException, ParseException {
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

		if (choice == 1) {
			sma.calculateForTrades(rawList);
		} else if (choice == 2) {
			sma.calculateForMinute(minuteList);
		} else if (choice == 3) {
			ema.calculate();
		} else {

		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		StartService service = new StartService();
		service.userInput();

		try {
			service.start();
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		} catch (ParseException e) {
			log.error(e.getMessage());
		}

	}

}
