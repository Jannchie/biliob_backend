package com.jannchie.biliob.config;

import com.jannchie.biliob.utils.IpHandlerInterceptor;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置跨域
 *
 * @author jannchie
 */
@Configuration
public class ConfigService {
    private final IpHandlerInterceptor ipHandlerInterceptor;

    @Autowired
    public ConfigService(IpHandlerInterceptor ipHandlerInterceptor) {
        this.ipHandlerInterceptor = ipHandlerInterceptor;
    }

    @Bean
    public WebMvcConfigurer myConfigure() {
        return new WebMvcConfigurer() {
            @Bean
            public TomcatContextCustomizer sameSiteCookiesConfig() {
                return context -> {
                    final Rfc6265CookieProcessor cookieProcessor = new Rfc6265CookieProcessor();
                    cookieProcessor.setSameSiteCookies(SameSiteCookies.NONE.getValue());
                    context.setCookieProcessor(cookieProcessor);
                };
            }

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                        .addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(3600);
            }

            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(ipHandlerInterceptor);
            }

        };
    }
}
