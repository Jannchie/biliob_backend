package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.exception.AuthorAlreadyFocusedException;
import com.jannchie.biliob.exception.UserAlreadyFavoriteAuthorException;
import com.jannchie.biliob.model.Author;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.repository.AuthorRepository;
import com.jannchie.biliob.service.AuthorService;
import com.jannchie.biliob.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author jannchie
 */
@Service
public class AuthorServiceImpl implements AuthorService {
    private static final Logger logger = LogManager.getLogger(VideoServiceImpl.class);

    private final AuthorRepository respository;
    private UserService userService;

    @Autowired
    public AuthorServiceImpl(AuthorRepository respository, UserService userService) {
        this.respository = respository;
        this.userService = userService;
    }

    @Override
    public Author getAuthorDetails(Long mid) {
        return respository.findByMid(mid);
    }

    @Override
    public void postAuthorByMid(Long mid)
            throws AuthorAlreadyFocusedException, UserAlreadyFavoriteAuthorException {
        User user = userService.addFavoriteAuthor(mid);
        logger.info(mid);
        logger.info(user.getName());
        if (respository.findByMid(mid) != null) {
            throw new AuthorAlreadyFocusedException(mid);
        }
        respository.save(new Author(mid));
    }

    @Override
    public Page<Author> getAuthor(Long mid, String text, Integer page, Integer pagesize) {
        if (!(mid == -1)) {
            logger.info(mid);
            return respository.searchByMid(
                    mid, PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "data.0.fans")));
        } else if (!Objects.equals(text, "")) {
            logger.info(text);
            return respository.search(
                    text, PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "data.0.fans")));
        } else {
            logger.info("查看所有UP主列表");
            return respository.findAllByDataIsNotNull(
                    PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "data.0.fans")));
        }
    }
}
