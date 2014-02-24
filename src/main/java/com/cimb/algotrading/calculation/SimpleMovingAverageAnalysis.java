/**
 * 
 */
package com.cimb.algotrading.calculation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.cimb.algotrading.bean.TradeBean;
import com.cimb.algotrading.config.properties.ICalculatorProperties;
import com.cimb.algotrading.handler.FileHandler;

/**
 * @author DerekYang
 * 
 */
public class SimpleMovingAverageAnalysis implements ITechnicalAnalysis {

	private ICalculatorProperties properties;

	private final Logger log = Logger.getLogger(getClass());

	private final static SimpleDateFormat cdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public SimpleMovingAverageAnalysis(ICalculatorProperties properties) {
		this.properties = properties;
	}

	public Map<Integer, Double> calculateForTrades(List<TradeBean> tradeList) throws ParseException {

		double result = 0;
		Map<Integer, Double> map = new HashMap<Integer, Double>();

		if (properties.numOfTrades().isEmpty()) {
			throw new IllegalArgumentException("Num of Trades is NOT set while calulating...");
		}
		int num = Integer.parseInt(properties.numOfTrades());

		if (properties.getStartTime().isEmpty() && properties.getEndTime().isEmpty()) {

			result = calculateLastTrades(tradeList, num - 1, num);
			map.put(num, result);
		} else if (!properties.getStartTime().isEmpty() && !properties.getEndTime().isEmpty()) {

			int index = 0;
			Calendar start = Calendar.getInstance();
			start.setTime(cdf.parse(properties.getStartTime()));
			Calendar end = Calendar.getInstance();
			end.setTime(cdf.parse(properties.getEndTime()));

			for (TradeBean bean : tradeList) {
				if (!bean.getTradeDateTime().before(start) && !bean.getTradeDateTime().after(end)) {
					result = calculateLastTrades(tradeList, index, num);
					map.put(index, result);
				}
				index++;
			}

		} else {
			throw new IllegalArgumentException(
					"Missing StartTime or EndTime in properties config file.");
		}

		return map;
	}

	public Map<Integer, Double> calculateForMinute(List<TradeBean> tradeList) throws ParseException {

		double result = 0;
		Map<Integer, Double> map = new HashMap<Integer, Double>();

		if (properties.numOfMinutes().isEmpty()) {
			throw new IllegalArgumentException("Num of Minute is NOT set while calulating...");
		}
		int num = Integer.parseInt(properties.numOfMinutes());

		if (properties.getStartTime().isEmpty() && properties.getEndTime().isEmpty()) {

			result = calculateLastMinutes(tradeList, num, num);
			map.put(num, result);

		} else if (!properties.getStartTime().isEmpty() && !properties.getEndTime().isEmpty()) {

			int index = 0;
			Calendar start = Calendar.getInstance();
			start.setTime(cdf.parse(properties.getStartTime()));
			Calendar end = Calendar.getInstance();
			end.setTime(cdf.parse(properties.getEndTime()));

			for (TradeBean bean : tradeList) {
				if (!bean.getTradeDateTime().before(start) && !bean.getTradeDateTime().after(end)) {
					result = calculateLastMinutes(tradeList, index, num);
					map.put(index, result);
				}
				index++;
			}

		} else {
			throw new IllegalArgumentException(
					"Missing StartTime or EndTime in properties config file.");
		}

		return map;

	}

	public double calculateLastTrades(List<TradeBean> tradeList, int i, int n) {

		if (tradeList.size() == 0 || tradeList == null) {
			throw new IllegalArgumentException("Empty tradeList while calculating last n trades");
		}

		if (i < 0 || i >= tradeList.size()) {
			throw new IllegalArgumentException(
					"Too large or too small index for calculateLastTrades()");
		}

		if (n < 1) {
			throw new IllegalArgumentException("Trades Number can NOT be less than 1");
		}

		int tmpSum = 0;
		int index = i;
		int count = 0;

		// handling the case of insufficient last n trades
		while (index >= 0 && count < n) {
			TradeBean tmpBean = tradeList.get(index);
			tmpSum += tmpBean.getPrice();

			count++;
			index--;
		}

		if (log.isInfoEnabled()) {
			log.info("Calculating trade-tmpSum: " + tmpSum + " count:" + count);
		}

		return (double) tmpSum / (count * FileHandler.DECIMAL_NUM);
	}

	public double calculateLastMinutes(List<TradeBean> tradeList, int i, int m) {

		if (tradeList.size() == 0 || tradeList == null) {
			throw new IllegalArgumentException("Empty tradeList while calculating last m minutes");
		}

		if (i < 0 || i >= tradeList.size()) {
			throw new IllegalArgumentException(
					"Too large or too small index for calculateLastMinutes()");
		}

		if (m < 1) {
			throw new IllegalArgumentException("Minutes can NOT be less than 1");
		}

		int tmpSum = 0;
		int count = 0;
		int index = i;

		TradeBean tmpBean = tradeList.get(index);
		Calendar now = tmpBean.getTradeDateTime();
		Calendar lastMinute = (Calendar) tmpBean.getTradeDateTime().clone();
		lastMinute.add(Calendar.MINUTE, -m);

		while (index >= 0 && !now.before(lastMinute)) {

			tmpBean = tradeList.get(index);
			tmpSum += tmpBean.getPrice();

			if (index != 0) {
				now = tradeList.get(index - 1).getTradeDateTime();
			}
			count++;
			index--;
		}

		if (log.isInfoEnabled()) {
			log.info("Calculating minute-tmpSum: " + tmpSum + " count:" + count);
		}

		return (double) tmpSum / (count * FileHandler.DECIMAL_NUM);
	}

}
