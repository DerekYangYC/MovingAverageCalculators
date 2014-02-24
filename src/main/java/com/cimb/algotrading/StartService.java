/**
 * 
 */
package com.cimb.algotrading;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.cimb.algotrading.bean.TradeBean;
import com.cimb.algotrading.calculation.ExponentialMovingAverage;
import com.cimb.algotrading.calculation.MacdIndicatorAnalysis;
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
		MacdIndicatorAnalysis macd = context.getBean(MacdIndicatorAnalysis.class);

		Map<String, List<TradeBean>> map = fileHandler.read();
		List<TradeBean> rawList = map.get("raw");
		List<TradeBean> minuteList = map.get("minute");

		if (choice == 1) {
			printResult(sma.calculateForTrades(rawList), 1);
		} else if (choice == 2) {
			printResult(sma.calculateForMinute(minuteList), 2);
		} else if (choice == 3) {
			printResult(ema.calculate(minuteList), 3);
		} else {
			printResult(macd.calculate(minuteList), 4);
		}
	}

	private void printResult(Map<Integer, Double> map, int choice) {
		if (map.size() == 1) {
			Integer key = 0;
			Double value = 0.0;
			for (Entry<Integer, Double> e : map.entrySet()) {
				key = e.getKey();
				value = e.getValue();
			}

			switch (choice) {
			case 1:
				System.out.format("The Simple moving average over the last %d trades is: %.3f %n",
						key, value);
				break;
			case 2:
				System.out.format("The Simple moving average over the last %d minutes is: %.3f %n",
						key, value);
				break;
			case 3:
				System.out.format(
						"The Exponential Moving Average over the last %d minutes is: %.3f %n", key,
						value);
				break;
			case 4:
				System.out.format("The MACD is: %.3f", value);
				break;
			}

		} else {

			switch (choice) {
			case 1:
				System.out
						.println("The Simple moving average over the trades within the start time and end time are:");
				break;

			case 2:
				System.out
						.println("The Simple moving average over the minutes within the start time and end time are:");
				break;
			case 3:
				System.out
						.println("The Exponential Moving Average over the minutes within the start time and end time are:");
				break;
			case 4:
				System.out
						.println("The Exponential Moving Average over the minutes within the start time and end time are:");
				break;
			}

			for (Entry<Integer, Double> e : map.entrySet()) {
				if (log.isInfoEnabled())
					log.info("ema key:" + e.getKey());
				System.out.format(" %.3f  %n", e.getValue());
			}
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
