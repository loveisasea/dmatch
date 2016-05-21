package com.fym.core.util.jackson;

import com.fym.core.err.OpResult;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomDateDeserializer extends JsonDeserializer<Date> {

	public static final Logger LOGGER = LoggerFactory.getLogger(CustomDateTimeDeserializer.class);
	public final static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		Date date   = null;
		try {
			date = format.parse(jp.getText());
		} catch (ParseException e) {
			e.printStackTrace();
			LOGGER.error(OpResult.STR_INVALID+"解释json时发生错误"+e.getMessage());;
		}
		return date;

	}

}
