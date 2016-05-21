package com.fym.role.entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity
@Table(name = "rolepermission", indexes = {
        @Index(columnList = "permission"),
        @Index(columnList = "roleid")
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RolePermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    /**
     * 角色id
     */
    @Column
    public Integer roleid;


    /**
     * 权限
     */
    @Column(length = 255)
    public String permission;



}
