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
public interface AuthorService {

    public Author getAuthorDetails(Long mid);

    public Author postAuthorByMid(Long mid);

    public Page<Author> getAuthor(Long mid, String text, Integer page, Integer pagesize);
}
