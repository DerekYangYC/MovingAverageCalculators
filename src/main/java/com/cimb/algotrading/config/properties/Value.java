package com.cimb.algotrading.config.properties;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author DerekYang
 * 
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Value {
	
	String value();
	
}
