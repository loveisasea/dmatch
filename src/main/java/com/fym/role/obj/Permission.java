package com.fym.role.obj;

import java.util.HashSet;
import java.util.Set;

//@JsonIgnoreProperties(value = { "seq", "group", "items", "roleids","isAdmin" })
public class Permission {
	/**
	 * 序号
	 */
	public Integer seq;


	/**
	 * 权限KEY
	 */
	public String key;


	/**
	 * 权限名称
	 */
	public String name;


	/**
	 * 权限描述
	 */
	public String description;


	/**
	 * 权限群组
	 */
	public String group;


	/**
	 * 是否超级管理员权限
	 */
	public Boolean isAdmin;

	/**
	 * 子权限
	 */
	public Set<String> items;


	public Permission() {
		this.items = new HashSet<String>();
	}

}
