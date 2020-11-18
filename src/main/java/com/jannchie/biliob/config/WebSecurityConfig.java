package com.jannchie.biliob.config;

import com.jannchie.biliob.security.JwtFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author Jannchie
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
                // .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/api/user/activation-code").permitAll()
                .antMatchers("/api/user/password").permitAll()
                .antMatchers("/api/user").permitAll()
                .antMatchers("/api/user/check-in").permitAll()
                .antMatchers("/api/monitor/**").hasAnyAuthority("管理员", "站长", "系统测试员", "普通研究员", "管理研究员", "高级研究员", "特权研究员", "系统测试员")
                .antMatchers("/api/admin/**").hasAnyAuthority("管理员", "站长", "系统测试员")
                .antMatchers("/api/user/**").hasAnyAuthority("普通用户", "管理员", "普通研究员", "管理研究员", "高级研究员", "特权研究员", "系统测试员", "站长")
                .antMatchers(HttpMethod.POST, "/api/agenda").hasAnyAuthority("高级研究员", "管理研究员", "特权研究员", "系统测试员", "站长")
                .antMatchers(HttpMethod.POST, "/api/agenda/{id}/close").hasAnyAuthority("系统测试员", "站长")
                .antMatchers(HttpMethod.POST, "/api/agenda/{id}/duplicate").hasAnyAuthority("系统测试员", "站长")
                .antMatchers(HttpMethod.POST, "/api/agenda/{id}/doing").hasAnyAuthority("系统测试员", "站长")
                .antMatchers(HttpMethod.POST, "/api/agenda/{id}/done").hasAnyAuthority("系统测试员", "站长")
                .antMatchers("/api/author/**").hasAnyAuthority("系统测试员", "站长")
                .antMatchers("/api/video/**").hasAnyAuthority("系统测试员", "站长")
                .antMatchers("/api/user/video").hasAnyAuthority("系统测试员", "站长")
                .antMatchers("/api/user/author").hasAnyAuthority("系统测试员", "站长")
                .antMatchers("/api/damn-you").hasAnyAuthority("系统测试员", "站长")
                .antMatchers("/api/**").permitAll()
                .anyRequest().authenticated()
                .and().logout().permitAll().and()
                .addFilter(new JwtFilter(authenticationManager()));
    }
}