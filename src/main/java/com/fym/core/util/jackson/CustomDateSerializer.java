package com.fym.core.util.jackson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

 
public class CustomDateSerializer extends JsonSerializer<Date> {

	public final static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		String formattedDate = format.format(value);
		jgen.writeString(formattedDate);
	}
}
