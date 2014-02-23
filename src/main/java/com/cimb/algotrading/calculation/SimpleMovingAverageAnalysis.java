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
	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	private final static SimpleDateFormat cdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public SimpleMovingAverageAnalysis(ICalculatorProperties properties) {
		this.properties = properties;
	}

	public double calculate(List<TradeBean> tradeList, int choice) {

		double result = 0;

		try {
			if (choice == 1) {
				calculateForTrades(tradeList);
			} else {
				calculateForMinute(tradeList);
			}
		} catch (ParseException e) {
			log.error(e.getMessage());
		}
		return result;
	}

	private Map<Integer, Double> calculateForTrades(List<TradeBean> tradeList)
			throws ParseException {

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

	private Map<Integer, Double> calculateForMinute(List<TradeBean> tradeList)
			throws ParseException {

		double result = 0;
		Map<Integer, Double> map = new HashMap<Integer, Double>();

		if (properties.numOfMinutes().isEmpty()) {
			throw new IllegalArgumentException("Num of Minute is NOT set while calulating...");
		}
		int num = Integer.parseInt(properties.numOfMinutes());

		if (properties.getStartTime().isEmpty() && properties.getEndTime().isEmpty()) {

			int index = 0;
			Calendar c = tradeList.get(0).getTradeDateTime();
			c.add(Calendar.MINUTE, num);

			for (TradeBean bean : tradeList) {
				if (!bean.getTradeDateTime().before(c)) {
					index++;
					break;
				}
			}
			result = calculateLastMinutes(tradeList, index, num);
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
		System.out.println(tmpSum);
		return (double) tmpSum / (count * FileHandler.DECIMAL_NUM);
	}

	public double calculateLastMinutes(List<TradeBean> tradeList, int i, int m) {

		int tmpSum = 0;
		int index = i;
		int count = 0;

		TradeBean tmpBean = tradeList.get(index);

		Calendar now = tmpBean.getTradeDateTime();
		Calendar last = (Calendar) tmpBean.getTradeDateTime().clone();
		last.add(Calendar.MINUTE, -m);

		System.out.println("now:" + sdf.format(now.getTime()) + "; last:"
				+ sdf.format(last.getTime()));

		while (index >= 0 && !now.before(last)) {
			tmpSum += tmpBean.getPrice();
			count++;
			index--;
			tmpBean = tradeList.get(index);
			now = tmpBean.getTradeDateTime();
		}
		System.out.println(tmpSum);
		return (double) tmpSum / (count * FileHandler.DECIMAL_NUM);
	}

}
