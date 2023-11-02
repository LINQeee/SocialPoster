package com.telegram.social_poster.Repositories;

import com.telegram.social_poster.Entities.VideoEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends CrudRepository<VideoEntity, String> {
    VideoEntity findVideoEntityByUserId(String userId);
}
