/**
 * 
 */
package com.cimb.algotrading.bean;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author DerekYang
 * 
 */
public class TradeBean {
	private String symbol;
	private int price;
	private int decimal;
	private int size;
	private Calendar tradeDateTime;

	public TradeBean(String symbol, int price, int decimal, int size, Calendar tradeDateTime) {
		this.symbol = symbol;
		this.price = price;
		this.decimal = decimal;
		this.size = size;
		this.tradeDateTime = tradeDateTime;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getDecimal() {
		return decimal;
	}

	public void setDecimal(int decimal) {
		this.decimal = decimal;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Calendar getTradeDateTime() {
		return tradeDateTime;
	}

	public void setTradeDateTime(Calendar tradeDateTime) {
		this.tradeDateTime = tradeDateTime;
	}

	@Override
	public String toString() {
		return "TradeBean [symbol=" + symbol + ", price=" + price + ", decimal=" + decimal
				+ ", size=" + size + ", time=" + tradeDateTime.getTime().toString() + "]";
	}

	public String toString(SimpleDateFormat sdf) {
		return "TradeBean [" + symbol + ", " + price + ", "
				+ sdf.format(tradeDateTime.getTime()) + ", " + decimal + ", " + size
				+ "]";
	}

}
