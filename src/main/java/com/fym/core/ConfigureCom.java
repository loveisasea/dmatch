package com.fym.core;

import com.fym.core.util.jackson.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Owned by Planck System
 * Created by fengy on 2016/4/26.
 * 配置
 */
@Component
public class ConfigureCom implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigureCom.class);


    private String filename  ;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private ObjectMapper objectMapper;

    private Map<String, Object> map = new HashMap<>();


    @Override
    public void afterPropertiesSet() throws IOException {
        this.filename = this.servletContext.getRealPath("/WEB-INF/") + "/configure.json";
        this.map = this.objectMapper.readValue(new File(this.filename), this.map.getClass());
    }

    public Object get(String key) {
        return this.map.get(key);
    }

    public void set(String key, Object value) throws IOException {
        this.map.put(key, value);
        this.objectMapper.writeValue(new File(this.filename), this.map);
    }
}
 
