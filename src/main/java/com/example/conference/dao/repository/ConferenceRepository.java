package com.example.conference.dao.repository;

import com.example.conference.dao.document.Conference;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConferenceRepository extends MongoRepository<Conference, String> {
    Optional<Conference> findByRoom(String roomId);
}
