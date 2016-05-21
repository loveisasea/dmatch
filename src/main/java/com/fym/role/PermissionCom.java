package com.fym.role;

import com.fym.role.obj.Permission;
import com.fym.role.obj.PermissionGroup;
import com.fym.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fengy on 2016/2/1.
 * 启动时只读一次，不会修改
 */
@Component("permissionCom")
public class PermissionCom implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionCom.class);

    @Autowired
    private ServletContext servletContext;

    // Map<Permission.Item, List<Permission.Key>>
    private Map<String, List<String>> permissionItemCache = new ConcurrentHashMap<>();

    // Map<Permission.key,Permission>
    private Map<String, Permission> permissionCache = new ConcurrentHashMap<>();

    //permission.Item
    private Set<String> adminPermissions = new HashSet<String>();
    private Comparator<Permission> permissionComparator = new Comparator<Permission>() {

        @Override
        public int compare(Permission o1, Permission o2) {
            if (o1 == null || o1.seq == null) {
                return -1;
            } else if (o2 == null || o2.seq == null) {
                return 1;
            } else {
                if (o1.seq < o2.seq) {
                    return -1;
                } else if (o1.seq > o2.seq) {
                    return 1;
                } else {
                    return 0;
                }
            }

        }
    };

    public String getErrMsg(String permItem) {
        List<String> permissionKeys = this.getPermissionKeys(permItem);
        return "缺少其中一个权限<" + StringUtil.compact(permissionKeys) + ">";
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("开始初始化PermissionCom...");
        this.initCache(this.fetchPermissionsFromXML());
        LOGGER.info("初始化PermissionCom完毕");
    }

    public boolean isAdminPermItem(String perm) {
        return this.adminPermissions.contains(perm);
    }

    /**
     * 获取所有权限组，下面带权限，权限下面带子权限
     *
     * @return 所有权限组，下面带权限，权限下面带子权限
     */
    public List<PermissionGroup> getAllPermissions() {
        List<Permission> permissions = new LinkedList<>();
        for (Permission permission : this.permissionCache.values()) {
            permissions.add(permission);
        }
        Collections.sort(permissions, permissionComparator);

        Map<String, PermissionGroup> map = new HashMap<String, PermissionGroup>(permissions.size() * 2);

        for (Permission permission : permissions) {
            PermissionGroup pg0 = map.get(permission.group);
            if (pg0 == null) {
                if (permission.group != null && permission.group.length() > 0) {
                    pg0 = new PermissionGroup();
                    pg0.seq = (permission.seq);
                    pg0.name = (permission.group);
                    pg0.permissions = (new LinkedList<Permission>());
                    map.put(pg0.name, pg0);
                } else {
                    pg0 = map.get("其他");
                    if (pg0 == null) {
                        pg0 = new PermissionGroup();
                        pg0.seq = (Integer.MAX_VALUE);
                        pg0.name = ("其他");
                        pg0.permissions = (new LinkedList<Permission>());
                        map.put(pg0.name, pg0);
                    }
                }
            }
            Permission retPerm = new Permission();
            retPerm.key = (permission.key);
            retPerm.name = (permission.name);
            retPerm.group = (permission.group);
            retPerm.description = (permission.description);
            pg0.permissions.add(retPerm);
        }

        // 排序
        List<PermissionGroup> ret = new LinkedList<PermissionGroup>();
        for (PermissionGroup pg : map.values()) {
            ret.add(pg);
        }
        Collections.sort(ret, new Comparator<PermissionGroup>() {

            @Override
            public int compare(PermissionGroup o1, PermissionGroup o2) {
                if (o1 == null || o1.seq == null) {
                    return -1;
                } else if (o2 == null || o2.seq == null) {
                    return 1;
                } else {
                    if (o1.seq < o2.seq) {
                        return -1;
                    } else if (o1.seq > o2.seq) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }

        });
        return ret;
    }

    /**
     * 判断是否存在子权限
     *
     * @param permItem 子权限
     * @return true:有，false：无
     */
    public boolean permissionItemExist(String permItem) {
        return this.permissionItemCache.get(permItem) != null ? true : false;
    }

    public boolean permissionExists(String permissionKey) {
        return this.permissionCache.get(permissionKey) != null;
    }

    public Set<String> getAdminPermissions() {
        return Collections.unmodifiableSet(this.adminPermissions);
    }

    public List<String> getPermissionKeys(String permItem) {
        List<String> permissions = this.permissionItemCache.get(permItem);
        return Collections.unmodifiableList(permissions);
    }

    /**
     * 根据权限获取所有子权限
     *
     * @param permission 权限
     * @return 子权限列表
     */
    public Collection<String> getItems(String permission) {
        Permission permission2 = this.permissionCache.get(permission);
        if (permission2 == null) {
            return Collections.EMPTY_LIST;
        } else {
            return Collections.unmodifiableCollection(permission2.items);
        }
    }

    /**
     * 根据权限列表获取所有子权限
     *
     * @param permissions 权限列表
     * @return 子权限列表
     */
    public Collection<String> getItems(Collection<String> permissions) {
        if (permissions == null) {
            return Collections.EMPTY_LIST;
        }
        List<String> ret = new ArrayList<>(permissions.size() * 3);
        for (String permission : permissions) {
            ret.addAll(this.getItems(permission));
        }
        return ret;
    }

    //初始化permissionCache和permissionCache
    private void initCache(List<Permission> permissions) {
        this.permissionCache.clear();
        this.permissionItemCache.clear();
        this.adminPermissions.clear();
        for (Permission pmsn : permissions) {
            if (this.permissionCache.containsKey(pmsn.key)) {
                LOGGER.warn("警告：发现重复的permission：" + pmsn.key);
            } else {
                this.permissionCache.put(pmsn.key, pmsn);
                for (String item : pmsn.items) {
                    List<String> pf = this.permissionItemCache.get(item);
                    if (pf == null) {
                        pf = new ArrayList<String>();
                        this.permissionItemCache.put(item, pf);
                    }
                    if (!pf.contains(pmsn)) {
                        pf.add(pmsn.key);
                    }
                }
                if (pmsn.isAdmin) {
                    //添加超级管理员权限
                    this.adminPermissions.addAll(pmsn.items);
                }
            }
        }
    }

    // 从XML中读取权限信息
    private List<Permission> fetchPermissionsFromXML() throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        String filename = servletContext.getRealPath("/WEB-INF/") + "/permissions.xml";
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(filename);
            int seq = 0;
            NodeList groupNodeList = doc.getElementsByTagName("group");
            List<Permission> ret = new ArrayList<Permission>();
            for (int g = 0; g < groupNodeList.getLength(); g++) {
                Node groupValue = groupNodeList.item(g);
                Node groupNameItem = groupValue.getAttributes().getNamedItem("name");
                String groupName = "其他";
                if (groupNameItem != null) {
                    groupName = groupNameItem.getNodeValue();
                }
                for (int i = 0; i < groupValue.getChildNodes().getLength(); i++) {
                    try {
                        Node permissionValue = groupValue.getChildNodes().item(i);
                        if (permissionValue.getAttributes() == null) {
                            continue;
                        }
                        Permission pmsn = new Permission();
                        pmsn.group = groupName;
                        pmsn.seq = (seq++);
                        // 处理key
                        Node keyItem = permissionValue.getAttributes().getNamedItem("key");
                        if (keyItem == null) {
                            LOGGER.error("错误：相应的permission没有key");
                            continue;
                        }
                        pmsn.key = (keyItem.getNodeValue());

                        // 处理name
                        Node nameItem = permissionValue.getAttributes().getNamedItem("name");
                        if (nameItem == null) {
//                            LOGGER.error("错误：相应的permission没有name");
//                            continue;
                            pmsn.name = pmsn.key;
                        } else {
                            pmsn.name = (nameItem.getNodeValue());
                        }
                        // 处理description
                        Node descriptionItem = permissionValue.getAttributes().getNamedItem("description");
                        if (descriptionItem != null) {
                            pmsn.description = (descriptionItem.getNodeValue());
                        } else {
                            pmsn.description = "";
                        }

                        // 处理超级管理员权限
                        Node isAdminPtem = permissionValue.getAttributes().getNamedItem("isadmin");
                        if (isAdminPtem != null && (isAdminPtem.getNodeValue()).toLowerCase().equals("true")) {
                            pmsn.isAdmin = true;
                        } else {
                            pmsn.isAdmin = false;
                        }


                        // 注意，permission.key也会作为权限item
                        pmsn.items.add(pmsn.key);
                        ret.add(pmsn);
                        // 把子item加进去
                        NodeList childNodes = permissionValue.getChildNodes();
                        for (int j = 0; j < childNodes.getLength(); j++) {
                            Node item = childNodes.item(j);
                            if (item.getNodeName().equals("item")) {
                                String value = item.getNodeValue();
                                value = item.getTextContent();
                                if (!pmsn.items.contains(value)) {
                                    pmsn.items.add(value);
                                }
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.error("错误：读取文本资源过程中发生错误。");
                    }
                }
            }
            return ret;
        } catch (Exception e) {
            LOGGER.error("致命错误：无法读取文本资源 " + filename);
            throw e;
        }
    }
}
