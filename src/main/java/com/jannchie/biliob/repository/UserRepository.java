package com.jannchie.biliob.repository;

import com.jannchie.biliob.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * @author jannchie
 */
@Repository
public interface UserRepository
        extends MongoRepository<User, ObjectId>, PagingAndSortingRepository<User, ObjectId> {

    /**
     * 判断用户名是否已存在
     *
     * @param name 用户名
     * @return 0代表不存在，1代表已存在
     */
    Integer countByName(String name);

    /**
     * 通过名称查询用户
     *
     * @param name 用户名
     * @return 用户对象
     */
    User findByName(String name);

    /**
     * 获取用户信息（不包括密码）
     *
     * @param name 用户名
     * @return 用户对象
     */
    @Query(value = "{name:?0}", fields = "{password:0}")
    User getUserInfo(String name);

    /**
     * get user slice
     *
     * @param pageable page information
     * @return the slice of user
     */
    @Query(fields = "{name:0, password: 0, ip: 0,  mail:0, favoriteAid: 0, favoriteMid: 0}")
    Slice<User> findTopUserByOrderByExp(Pageable pageable);
}
