package com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class OrderIdUtils {
	private static Map<Integer, String> orderIdToString = new HashMap<>();
	private static Map<String, Integer> stringToOrderId = new HashMap<>();

	static {
		for (final Field field : OrderIds.class.getDeclaredFields()) {
			final String name = field.getName();
			try {
				final Object value = field.get(null);
				if (value instanceof Integer) {
					final Integer orderId = (Integer) value;
					orderIdToString.put(orderId, name);
					stringToOrderId.put(name, orderId);
				}
			}
			catch (final IllegalArgumentException e) {
				e.printStackTrace();
			}
			catch (final IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public static int getOrderId(final String orderIdString) {
		final Integer orderId = stringToOrderId.get(orderIdString.toLowerCase());
		if (orderId == null) {
			return 0;
		}
		return orderId;
	}

	public static String getStringFromOrderId(final Integer orderId) {
		return orderIdToString.get(orderId);
	}
}
