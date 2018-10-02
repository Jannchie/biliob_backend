package com.jannchie.biliob.service;

import com.jannchie.biliob.model.Author;
import com.jannchie.biliob.repository.AuthorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


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

    public Page<Author> getAuthor(Integer page, Integer pagesize) {
        return respository.findAll(PageRequest.of(page, pagesize));
    }
}
