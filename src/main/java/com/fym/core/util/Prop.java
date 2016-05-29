package com.fym.core.util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by fengy on 2016/1/28.
 */
public class Prop extends PropertyPlaceholderConfigurer {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(Prop.class);
    private Map<String, String> ctxPropertiesMap;
    private boolean hasUpdateSet = false;

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
        super.processProperties(beanFactoryToProcess, props);
        if (!hasUpdateSet) {
            try {
                // hasUpdateSet = true;
                // Field declaredField =
                // PropertiesLoaderSupport.class.getDeclaredField("locations");
                // declaredField.setAccessible(true);
                // Resource[] resources = (Resource[]) declaredField.getList(this);
                // for (Resource r : resources) {
                // PropertyConfigurator.configureAndWatch(r.getFilename(), 60 *
                // 1000);
                // }

            } catch (Exception e) {
                LOGGER.error("fails to set property dynamic read. Inner exception:" + e.getMessage());
            }
        }
        ctxPropertiesMap = new HashMap<String, String>(props.size());
        for (Object key : props.keySet()) {
            String keyStr = key.toString();
            String value = props.getProperty(keyStr);
            ctxPropertiesMap.put(keyStr, value);
        }
    }

    public String get(String name) {
        if (ctxPropertiesMap.containsKey(name)) {
            return StringUtils.trim(ctxPropertiesMap.get(name));
        }
        else {
            LOGGER.error("获取属性失败：" + name);
            return null;
        }
    }

    public Map<String,String> get(){
        Map<String,String> ret = new HashMap<String,String>();
        ret.putAll(this.ctxPropertiesMap);
        return ret;
    }

}

