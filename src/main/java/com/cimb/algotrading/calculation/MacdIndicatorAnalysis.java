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

/**
 * @author DerekYang
 * 
 */
public class MacdIndicatorAnalysis implements ITechnicalAnalysis {

	private ExponentialMovingAverage ema;
	private ICalculatorProperties properties;

	private final static SimpleDateFormat cdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private final Logger log = Logger.getLogger(getClass());

	public MacdIndicatorAnalysis(ExponentialMovingAverage ema, ICalculatorProperties properties) {
		this.ema = ema;
		this.properties = properties;
	}

	public Map<Integer, Double> calculate(List<TradeBean> tradeList) throws ParseException {

		if (properties.macdSlowPeriod().isEmpty() || properties.macdFastPeriod().isEmpty()) {
			throw new IllegalArgumentException("Parameters for MACD are NOT set correctly...");
		}

		Map<Integer, Double> map = new HashMap<Integer, Double>();

		double macdValue = 0.00;
		double fastEma = 0.00;
		double slowEma = 0.00;
		int slowPeriod = Integer.parseInt(properties.macdSlowPeriod());
		int fastPeriod = Integer.parseInt(properties.macdFastPeriod());

		if (properties.getStartTime().isEmpty() && properties.getEndTime().isEmpty()) {

			fastEma = ema.calculateLastMinutes(tradeList, slowPeriod - 1, fastPeriod);
			slowEma = ema.calculateLastMinutes(tradeList, slowPeriod - 1, slowPeriod);

			if(log.isInfoEnabled()) log.info("Single macd: fast EMA:"+fastEma+" ,slowEma:"+slowEma);
			
			macdValue = fastEma - slowEma;
			
			if (log.isInfoEnabled()) log.info("Single macd: " + macdValue);
			
			map.put(0,macdValue);
			
		} else if (!properties.getStartTime().isEmpty() && !properties.getEndTime().isEmpty()) {

			int index = 0;
			Calendar start = Calendar.getInstance();
			start.setTime(cdf.parse(properties.getStartTime()));
			Calendar end = Calendar.getInstance();
			end.setTime(cdf.parse(properties.getEndTime()));

			for (TradeBean bean : tradeList) {
				if (!bean.getTradeDateTime().before(start) && !bean.getTradeDateTime().after(end)) {

					fastEma = ema.calculateLastMinutes(tradeList, index, fastPeriod);
					slowEma = ema.calculateLastMinutes(tradeList, index, slowPeriod);

					macdValue = fastEma - slowEma;

					if (log.isInfoEnabled()) log.info("Continuous macd: " + macdValue);

					map.put(index, macdValue);
				}
				index++;
			}

		} else {
			throw new IllegalArgumentException(
					"Missing StartTime or EndTime in properties config file.");
		}

		return map;

	}
}
