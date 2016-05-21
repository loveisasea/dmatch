package com.fym.core.dao;

import com.fym.core.ConfigureCom;
import com.fym.core.util.DateUtil;
import com.fym.core.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * Owned by Planck System
 * Created by fengy on 2016/4/26.
 * 数据库备份工具
 */
@Component
public class DBCom implements InitializingBean, ApplicationListener<ContextRefreshedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBCom.class);
    ;
    private Boolean InitDB_required = false;
    private String InitDB_Cmd = "";
    private Boolean Backup_required = true;
    private String Backup_Cmd = "";

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private ConfigureCom configureCom;

    @Autowired
    private DataSource dataSource;


    public void backup() {
        if (this.Backup_required) {
            Format format = new SimpleDateFormat("yyyyMMdd_HHmmss");

            final String cmd = String.format(this.Backup_Cmd,
                    this.servletContext.getRealPath(""),
                    format.format(DateUtil.getCurrent()));
            Process p = null;
            try {
                p = Runtime.getRuntime().exec(cmd);
                String line = null;
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream(), "UTF-8"));
                while ((line = br.readLine()) != null) {
                    LOGGER.error(line);
                }
                br = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"));
                while ((line = br.readLine()) != null) {
                    LOGGER.info(line);
                }

                if (p.waitFor() != 0) {
                    LOGGER.error("备份失败：" + cmd);

                } else {
                    LOGGER.info("备份成功：" + cmd);
                }
            } catch (Exception e) {
                LOGGER.error("备份中发生异常:" + cmd);
                e.printStackTrace();
            }
        }
    }


    public void restore() {
        if (this.InitDB_required) {
            LOGGER.info("数据库需要初始化");

            //清除数据
            JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);

            try {
                File file = new File(this.servletContext.getRealPath("/WEB-INF/sql/cntdelete.sql"));
                String content = FileUtil.getContent(file);
                List<Map<String, Object>> list = jdbcTemplate.queryForList(content);
                StringBuilder sen2 = new StringBuilder(1024 * 8);
                for (int i = 0; i < list.size(); i++) {
                    sen2.append((String) list.get(i).get("sen"));
                    if (i < list.size() - 1) {
                        sen2.append(" union ");
                    }
                }
                List<Map<String, Object>> list2 = jdbcTemplate.queryForList(sen2.toString());
                for (Map<String, Object> map : list2) {
                    String delexe = (String) map.get("delexe");
                    jdbcTemplate.execute(delexe);
                }

            } catch (IOException e) {
                LOGGER.error("清除数据中发生错误");
                e.printStackTrace();
            }
            LOGGER.info("已清除原有数据");

            final String cmd = String.format(this.InitDB_Cmd, this.servletContext.getRealPath(""));
            Process p = null;

            try {
                p = Runtime.getRuntime().exec(cmd);
                String line = null;
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream(), "UTF-8"));
                while ((line = br.readLine()) != null) {
                    LOGGER.error(line);
                }
                br = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"));
                while ((line = br.readLine()) != null) {
                    LOGGER.info(line);
                }
                if (p.waitFor() != 0) {
                    LOGGER.error("恢复失败：" + cmd);

                } else {
                    LOGGER.info("恢复成功：" + cmd);
                }
            } catch (Exception e) {
                LOGGER.error("恢复中发生异常:" + cmd);
                e.printStackTrace();
            }
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            this.InitDB_required = (Boolean) this.configureCom.get("InitDB_required");
            this.Backup_required = (Boolean) this.configureCom.get("Backup_required");
            this.InitDB_Cmd = (String) this.configureCom.get("InitDB_Cmd");
            this.Backup_Cmd = (String) this.configureCom.get("Backup_Cmd");
        } catch (Exception e) {
            LOGGER.error("无法初始化备份数据库的配置");
            e.printStackTrace();
        }
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.restore();
    }
}
 
