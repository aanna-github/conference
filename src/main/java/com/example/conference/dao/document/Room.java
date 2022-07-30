package com.example.conference.dao.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Room {
    @Id
    @Field(targetType = FieldType.OBJECT_ID)
    private String id;

    @NotNull
    @Field(name = "Number")
    private Integer number;

    @NotNull
    @Field(name = "SeatsCount")
    private Integer seatsCount;

    @NotNull
    @Field(name = "Floor")
    private Integer floor;

    @NotNull
    @Field(name = "Address")
    private Address address;

    @Field(name = "RoomAvailability")
    private List<RoomAvailability> roomAvailability = new ArrayList<>();
}
