package com.fym.role.obj;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "seq" })
public class PermissionGroup {
	public Integer seq;
	public String name;//组名称
	public List<Permission> permissions; //权限列表


}
