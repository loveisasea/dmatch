package com.fym.core.util.jackson;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

public class BigDecimal1dSerializer extends JsonSerializer<BigDecimal> {

	public final static java.text.DecimalFormat formatter = new java.text.DecimalFormat("#.0");

	public static String format(BigDecimal value) {
		if (value == null) {
			return null;
		}
		return formatter.format(value);
	}

	@Override
	public void serialize(BigDecimal value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {

		String str = BigDecimal1dSerializer.format(value);
		if (str == null) {
			jgen.writeNull();
		} else {
			jgen.writeString(str);
		}

	}

}
