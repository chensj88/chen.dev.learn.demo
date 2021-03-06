package org.chen.spring.security5.boot.security;

import lombok.extern.slf4j.Slf4j;
import org.chen.spring.security5.boot.domain.SysPermission;
import org.chen.spring.security5.boot.service.CustomUserDetailServiceImpl;
import org.chen.spring.security5.boot.service.SysPermissionService;
import org.chen.spring.security5.boot.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 在使用hasPermission使用了自定义的permission表达式，所以需要提供自定义的解析方法
 *
 * @author chensj
 * @date 2019-09-12 11:55
 */
@Slf4j
@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {
    @Autowired
    private SysPermissionService permissionService;
    @Autowired
    private SysRoleService roleService;
    @Autowired
    private CustomUserDetailServiceImpl userDetailService;

    /**
     * 通过 Authentication 取出登录用户的所有 Role
     * 遍历每一个 Role，获取到每个Role的所有 Permission
     * 遍历每一个 Permission，只要有一个 Permission 的 url 和传入的url相同，且该 Permission 中包含传入的权限，返回 true
     * 如果遍历都结束，还没有找到，返回false
     *
     * @param authentication   认证信息
     * @param targetUrl        访问 url
     * @param targetPermission 访问 url 权限
     * @return boolean
     */
    @Override
    public boolean hasPermission(Authentication authentication, Object targetUrl, Object targetPermission) {
        String username =  authentication.getPrincipal().toString();
        // 获得loadUserByUsername()方法的结果
        User user = (User) userDetailService.loadUserByUsername(username);
        // 获得loadUserByUsername()中注入的角色
        Collection<GrantedAuthority> authorities = user.getAuthorities();

        log.info("targetUrl:[{}] , permission: [{}]", targetUrl, targetPermission);
        // 遍历用户所有角色
        for (GrantedAuthority authority : authorities) {
            String roleName = authority.getAuthority();
            Integer roleId = roleService.selectByName(roleName).getId();
            // 得到角色所有的权限
            List<SysPermission> permissionList = permissionService.listByRoleId(roleId);

            // 遍历permissionList
            for (SysPermission sysPermission : permissionList) {
                // 获取权限集
                List permissions = sysPermission.getPermissions();
                // 如果访问的Url和权限用户符合的话，返回true
                if (targetUrl.equals(sysPermission.getUrl())
                        && permissions.contains(targetPermission)) {
                    return true;
                }
            }

        }

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String s, Object o) {
        return false;
    }
}
