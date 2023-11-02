package com.telegram.social_poster.Repositories;

import com.telegram.social_poster.Entities.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {
    UserEntity findUserEntityByUserId(String chatId);
}
