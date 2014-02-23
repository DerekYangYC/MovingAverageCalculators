/**
 * 
 */
package com.cimb.algotrading.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cimb.algotrading.calculation.ExponentialMovingAverage;
import com.cimb.algotrading.calculation.MacdIndicatorAnalysis;
import com.cimb.algotrading.calculation.SimpleMovingAverageAnalysis;
import com.cimb.algotrading.config.properties.ICalculatorProperties;
import com.cimb.algotrading.handler.FileHandler;

/**
 * @author DerekYang
 *
 */
@Configuration
public class CalculatorConfiguration {

	private @Autowired ICalculatorProperties properties;
	
	@Bean
	public FileHandler fileHandler(){
		return new FileHandler(properties);
	}
	
	@Bean
	public SimpleMovingAverageAnalysis simpleMovingAverage(){
		return new SimpleMovingAverageAnalysis(properties);
	}
	
	@Bean
	public ExponentialMovingAverage exponentialMovingAverage(){
		return new ExponentialMovingAverage(properties);
	}
	
	@Bean
	public MacdIndicatorAnalysis macdIndicatorAnalysis(){
		return new MacdIndicatorAnalysis();
	}
	
}
