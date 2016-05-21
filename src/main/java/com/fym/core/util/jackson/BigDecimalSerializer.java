package com.fym.core.util.jackson;

import java.io.IOException;
import java.math.BigDecimal;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class BigDecimalSerializer extends JsonSerializer<BigDecimal> {

	public final static java.text.DecimalFormat formatter = new java.text.DecimalFormat("#");

	public static String format(BigDecimal value) {
		if (value == null) {
			return null;
		}
		return formatter.format(value);
	}

	@Override
	public void serialize(BigDecimal value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {

		String str = BigDecimalSerializer.format(value);
		if (str == null) {
			jgen.writeString("");
		} else {
			jgen.writeString(str);
		}

	}

}
