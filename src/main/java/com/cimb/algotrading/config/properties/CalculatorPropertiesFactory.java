/**
 * 
 */
package com.cimb.algotrading.config.properties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Properties;

/**
 * @author DerekYang
 * 
 */
public final class CalculatorPropertiesFactory {

	public static ICalculatorProperties create() throws FileNotFoundException, IOException {
		String configFile = System.getProperty("calculator.properties", "config/calculator.properties");
		
		return create(new FileInputStream(configFile));	
	}

	public static ICalculatorProperties create(FileInputStream is) throws IOException {

		ClassLoader loader = ICalculatorProperties.class.getClassLoader();
		Class<?>[] interfaces = new Class[] { ICalculatorProperties.class };

		final Properties properties = new Properties();
		properties.load(is);

		return (ICalculatorProperties) Proxy.newProxyInstance(loader, interfaces,
				new PropertyMapper(properties));
	}

	public static final class PropertyMapper implements InvocationHandler {

		private final Properties properties;

		public PropertyMapper(Properties properties) {
			this.properties = properties;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

			final Value value = method.getAnnotation(Value.class);

			if (value == null)
				return null;
			
			String property = properties.getProperty(value.value());
			
			if(property == null) return null;
			
			final Class<?> returns = method.getReturnType();
			if(returns.isPrimitive()){
				if(returns.equals(int.class)) return (Integer.valueOf(property));
			}

			return property;
		}

	}

}
