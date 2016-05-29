package com.fym.core.web.filter.obj;

/**
 *
 * Created by fengy on 2016/2/18.
 */

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

public class JsonReadHttpServletRequest extends HttpServletRequestWrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonReadHttpServletRequest.class);

    private byte[] body;
//	private String uuid =   UUID.randomUUID().toString();

    public JsonReadHttpServletRequest(HttpServletRequest httpServletRequest) {
        super(httpServletRequest);
        // Read the request body and save it as a byte array
        InputStream is;
        try {
            is = super.getInputStream();
            body = IOUtils.toByteArray(is);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new ServletInputStreamImpl(new ByteArrayInputStream(body));
    }

    @Override
    public BufferedReader getReader() throws IOException {
        String enc = getCharacterEncoding();
        if (enc == null)
            enc = "UTF-8";
        return new BufferedReader(new InputStreamReader(getInputStream(), enc));
    }

    private class ServletInputStreamImpl extends ServletInputStream {

        private InputStream is;

        public ServletInputStreamImpl(InputStream is) {
            this.is = is;
        }

        public int read() throws IOException {
            return is.read();
        }

        public boolean markSupported() {
            return false;
        }

        public synchronized void mark(int i) {
            throw new RuntimeException(new IOException("mark/reset not supported"));
        }

        public synchronized void reset() throws IOException {
            throw new IOException("mark/reset not supported");
        }
    }

}

