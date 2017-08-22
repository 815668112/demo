package com.example.demo.service;

import com.example.demo.entity.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2017/8/20.
 */
@Service
public interface UserService extends PagingAndSortingRepository<User, Long> {

}
