package com.jannchie.biliob.security;

import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.utils.UserUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Jannchie
 */
@Component
public class UserAuthenticationProvider implements AuthenticationProvider {


    private static final Logger logger = LogManager.getLogger();
    private final MongoTemplate mongoTemplate;
    private final BCryptPasswordEncoder bcryptPasswordEncoder;

    @Autowired
    public UserAuthenticationProvider(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.bcryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        String name = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();

        User user = UserUtils.getPasswdAndRole(name);
        if (user == null) {
            logger.error("凭证过期");
            throw new BadCredentialsException(ResultEnum.LOGIN_FAILED.getMsg());
        }
        String encodedPassword = new Md5Hash(password, user.getName()).toHex();
        if (encodedPassword.equals(user.getPassword())) {
            mongoTemplate.updateFirst(Query.query(
                    new Criteria()
                            .orOperator(Criteria.where("name").is(name), Criteria.where("mail").is(name))),
                    Update.update("password", bcryptPasswordEncoder.encode(password)), User.class);
            user.setPassword(bcryptPasswordEncoder.encode(password));
        }
        if (bcryptPasswordEncoder.matches(password, user.getPassword())) {
            Collection<GrantedAuthority> authorityCollection = new ArrayList<>();
            authorityCollection.add(new SimpleGrantedAuthority(user.getRole()));
            return new UsernamePasswordAuthenticationToken(name, password, authorityCollection);
        }
        throw new BadCredentialsException(ResultEnum.LOGIN_FAILED.getMsg());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}