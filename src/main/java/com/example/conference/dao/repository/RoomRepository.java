package com.example.conference.dao.repository;

import com.example.conference.dao.document.Room;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends MongoRepository<Room, String> {
    Optional<Room> findByIdAndSeatsCountIsGreaterThanEqual(String id, Integer seatsCount);

    List<Room> findBySeatsCountIsGreaterThanEqual(Integer seatsCount);
}
