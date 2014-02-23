package com.cimb.algotrading.config.properties;

public interface ICalculatorProperties {
	
	@Value("input.file")
	String inputFile();
	
	@Value("ma.duration.trades")
	String numOfTrades();
	
	@Value("ma.duration.minutes")
	String numOfMinutes();
	
	@Value("macd.slow.period")
	String macdSlowPeriod();
	
	@Value("macd.fast.period")
	String macdFastPeriod();
	
	@Value("time.start")
	String getStartTime();
	
	@Value("time.end")
	String getEndTime();
	
}
