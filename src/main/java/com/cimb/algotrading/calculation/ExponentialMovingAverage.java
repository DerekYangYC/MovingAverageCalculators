/**
 * 
 */
package com.cimb.algotrading.calculation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
public class ExponentialMovingAverage implements ITechnicalAnalysis {

	private ICalculatorProperties properties;
	private double alpha;

	private final static SimpleDateFormat cdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private final Logger log = Logger.getLogger(getClass());

	public ExponentialMovingAverage(ICalculatorProperties properties) {
		this.properties = properties;
	}

	public Map<Integer, Double> calculate(List<TradeBean> tradeList) throws ParseException {

		if (properties.numOfMinutes().isEmpty()) {
			throw new IllegalArgumentException("Num of Minute is NOT set while calulating EMA...");
		}

		int num = Integer.parseInt(properties.numOfMinutes());
		double result = 0;
		Map<Integer, Double> map = new HashMap<Integer, Double>();

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

		int index = i;

		List<Integer> priceList = new ArrayList<Integer>();

		TradeBean tmpBean = tradeList.get(index);
		Calendar now = tmpBean.getTradeDateTime();
		Calendar lastMinute = (Calendar) tmpBean.getTradeDateTime().clone();
		lastMinute.add(Calendar.MINUTE, -m);

		while (index >= 0 && !now.before(lastMinute)) {

			tmpBean = tradeList.get(index);
			priceList.add(tmpBean.getPrice());

			if (index != 0) {
				now = tradeList.get(index - 1).getTradeDateTime();
			}

			index--;
		}

		double ema = 0.0;
		alpha = (double) 2 / (m + 1);

		for (int j = priceList.size() - 1; j >= 0; j--) {
			ema = alpha * priceList.get(j) + (1 - alpha) * ema;
		}

		if (log.isInfoEnabled()) {
			log.info("alpha: " + alpha + " EMA:" + ema);
		}

		return (double) ema / FileHandler.DECIMAL_NUM;
	}

}
