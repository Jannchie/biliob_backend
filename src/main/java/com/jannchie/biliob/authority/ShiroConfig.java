package com.jannchie.biliob.authority;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.ShiroHttpSession;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author jannchie
 */
@Configuration
public class ShiroConfig {

	@Bean
	public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		// 必须设置 SecurityManager
		shiroFilterFactoryBean.setSecurityManager(securityManager);

		// setLoginUrl 如果不设置值，默认会自动寻找Web工程根目录下的"/login.jsp"页面 或 "/login" 映射
		shiroFilterFactoryBean.setLoginUrl("/api/no-login");
		// 设置无权限时跳转的 url;
		shiroFilterFactoryBean.setUnauthorizedUrl("/api/no-role");

		// 设置拦截器
		Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
		// 允许发起登录请求
		filterChainDefinitionMap.put("/api/login", "anon");

		// 允许随意查看author信息
		filterChainDefinitionMap.put("/api/author/**", "anon");

		// 允许随意查看video信息
		filterChainDefinitionMap.put("/api/video/**", "anon");

		// 允许发起注册请求
		filterChainDefinitionMap.put("/api/user", "anon");

		// 允许用户查看用户信息
		filterChainDefinitionMap.put("/api/user/**", "roles[普通用户]");

		// 管理员，需要角色权限 “admin”
		filterChainDefinitionMap.put("/api/admin/**", "roles[admin]");

		// 其余接口一律拦截
		// 主要这行代码必须放在所有权限设置的最后，不然会导致所有 url 都被拦截
		filterChainDefinitionMap.put("/**", "authc");

		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

		return shiroFilterFactoryBean;
	}

	/**
	 * 注入 securityManager 安全管理器
	 */
	@Bean
	public DefaultWebSecurityManager securityManager() {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		// 设置realm.
		securityManager.setRealm(userRealm());
		securityManager.setSessionManager(sessionManager());
		return securityManager;
	}

	/**
	 * 自定义身份认证 realm;
	 * <p>
	 * <p>必须写这个类，并加上 @Bean 注解，目的是注入 UserRealm， 否则会影响 CustomRealm类 中其他类的依赖注入
	 * <p>
	 * <p>在realm中获取到用户的安全数据,传入安全管理器securityManager中进行验证
	 */
	@Bean
	public UserRealm userRealm() {
		UserRealm userRealm = new UserRealm();
		userRealm.setAuthenticationCachingEnabled(false);
		return userRealm;
	}

	@Bean
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	@Bean
	public DefaultWebSessionManager sessionManager() {
		Cookie cookie = new SimpleCookie(ShiroHttpSession.DEFAULT_SESSION_ID_NAME);
		cookie.setMaxAge(60 * 60 * 24 * 7);
		cookie.setHttpOnly(true);
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		sessionManager.setSessionIdCookie(cookie);
		return sessionManager;
	}
}
