package com.jannchie.biliob.service;

import com.jannchie.biliob.model.Author;
import com.jannchie.biliob.repository.AuthorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class AuthorService {
    private static final Logger logger = LoggerFactory.getLogger(AuthorService.class);
    private final AuthorRepository respository;

    @Autowired
    public AuthorService(AuthorRepository respository) {
        this.respository = respository;
    }

    public Author getAuthorDetails(Long mid) {
        return respository.findByMid(mid);
    }

    public Author postAuthorByMid(Long mid) {
        return respository.save(new Author(mid));
    }

    public Page<Author> getAuthor(Long mid, String text, Integer page, Integer pagesize) {
        if (!(mid == -1)) {
            logger.info("[GET]searchByMid");
            return respository.searchByMid(mid, PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "data.0.fans")));
        } else if (!Objects.equals(text, "")) {
            logger.info("[GET]search");
            return respository.search(text, PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "data.0.fans")));
        } else {
            logger.info("[GET]findAll");
            return respository.findAll(PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "data.0.fans")));
        }
    }
}
