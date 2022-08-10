package com.example.conference.dao.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.BsonTimestamp;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Conference {
    @Id
    @Field(targetType = FieldType.OBJECT_ID)
    private String id;

    @NotNull
    @Field(name = "RequestedSeatsCount")
    private Integer requestedSeatsCount;

    @NotNull
    @Field(name = "Status")
    private String status;

    @NotNull
    @Field(name = "EventDate")
    private BsonTimestamp eventDate;

    @Field(name = "Participants")
    private Set<Participant> participants = new LinkedHashSet<>();

    @DBRef
    @Field(name = "Room")
    private Room room;

    @Size(min = 7, max = 100)
    @Field(name = "Description")
    private String description;
}
