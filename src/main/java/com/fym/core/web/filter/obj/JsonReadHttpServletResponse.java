package com.fym.core.web.filter.obj;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * Created by fengy on 2016/2/18.
 */
public class JsonReadHttpServletResponse extends HttpServletResponseWrapper {

    private PrintWriter writer;
    private ByteOutputStream output;


    public byte[] getBytes() {
        return output.getBytes();
    }

    public JsonReadHttpServletResponse(HttpServletResponse response) {
        super(response);
        output = new ByteOutputStream();
        writer = new PrintWriter(output);
    }

    @Override
    public PrintWriter getWriter() {
        return writer;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return output;
    }

    static class ByteOutputStream extends ServletOutputStream {

        private ByteArrayOutputStream bos = new ByteArrayOutputStream();

        @Override
        public void write(int b) throws IOException {
            bos.write(b);
        }

        public byte[] getBytes() {
            return bos.toByteArray();
        }
    }
}

