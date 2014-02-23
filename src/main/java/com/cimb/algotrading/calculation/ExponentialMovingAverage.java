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
	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	private final static SimpleDateFormat cdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public ExponentialMovingAverage(ICalculatorProperties properties) {
		this.properties = properties;
	}

	public double calculate(List<TradeBean> tradeList) throws ParseException {
		double result = 0;
		Map<Integer, Double> map = new HashMap<Integer, Double>();

		if (properties.numOfMinutes().isEmpty()) {
			throw new IllegalArgumentException("Num of Minute is NOT set while calulating EMA...");
		}
		int num = Integer.parseInt(properties.numOfMinutes());
		
		if(properties.getStartTime().isEmpty() && properties.getEndTime().isEmpty()){
			
			int index=0;
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
			
		}else if (!properties.getStartTime().isEmpty() && !properties.getEndTime().isEmpty()){
			
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
			
		}else{
			throw new IllegalArgumentException(
					"Missing StartTime or EndTime in properties config file.");
		}
		
		return 0;
	}

	public double calculateLastMinutes(List<TradeBean> tradeList, int i, int m) {

		int count = 0;
		int minute = 1;
		int index = i;
		
		double numOfPeriod = m;
		
		List<Double> priceList = new ArrayList<Double>();

		TradeBean bean = tradeList.get(index);

		Calendar now = bean.getTradeDateTime();
		Calendar last = (Calendar) bean.getTradeDateTime().clone();
		last.add(Calendar.MINUTE, -m);

		Calendar later = (Calendar) bean.getTradeDateTime().clone();
		later.add(Calendar.MINUTE, -minute);
		
		String timeKey = sdf.format(now.getTime());
		Map<String,Double> tmpMap = new HashMap<String, Double>();
		
		double EMA = 0.00;
		alpha = (double)(2 / (numOfPeriod + 1));
		
		System.out.println(count+","+alpha);
		
		System.out.println("now:" + sdf.format(now.getTime()) + "; later:"
				+ sdf.format(later.getTime())+"; last:"+sdf.format(last.getTime()));
		
		int tmpSum = 0;
		while(index>=0 && !later.before(last)){
			
			if(!now.before(later)){
				count++;
				tmpSum+=bean.getPrice();
				index--;
				bean=tradeList.get(index);
				now = bean.getTradeDateTime();
			}else{
				System.out.println("now:" + sdf.format(now.getTime()) + "; later:"
						+ sdf.format(later.getTime()));
				
				tmpMap.put(timeKey, (double)tmpSum/count);
				priceList.add((double)tmpSum/count);
				timeKey = sdf.format(later.getTime());
				later.add(Calendar.MINUTE, -minute);
				count=1;
				tmpSum=bean.getPrice();
				index--;
				bean=tradeList.get(index);
				now = bean.getTradeDateTime();
				
			}
			
			
		}
		
		for(int j=priceList.size()-1;j>=0;j--){
			EMA = alpha*priceList.get(j)+(1-alpha)*EMA;
		}
		
		System.out.println("Alpha: "+alpha+" EMA:"+EMA);
		

		return (double) (EMA / FileHandler.DECIMAL_NUM);
	}

	/*
	 * private double calculateWithTrades(List<TradeBean> tradeList, Calendar
	 * start, Calendar end) {
	 * 
	 * int count = 0; int num = Integer.parseInt(properties.numOfTrades());
	 * List<TradeBean> tmpList = new ArrayList<TradeBean>();
	 * 
	 * Calendar tmpStart = start; Calendar tmpEnd = Calendar.getInstance();
	 * tmpEnd.add(Calendar.MINUTE, num);
	 * 
	 * 
	 * for (TradeBean bean : tradeList) { if (!bean.getTime().before(start) &&
	 * !bean.getTime().after(end)) { if (count > num) break; tmpList.add(bean);
	 * count++; } }
	 * 
	 * double EMA = 0.00; alpha = 2/(count+1); for(TradeBean bean : tmpList){
	 * EMA = alpha*bean.getPrice()+(1-alpha)*EMA; }
	 * 
	 * return EMA/100; }
	 */

}
