package com.example.conference.dao.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Participant {
    @Transient
    public static final String SEQUENCE_NAME = "users_sequence";

    @Field(name = "Id")
    private String id;

    @NotNull
    @Size(min = 3, max = 25)
    @Field(name = "FirstName")
    private String firstName;

    @NotNull
    @Size(min = 3, max = 25)
    @Field(name = "LastName")
    private String lastName;

    @NotNull
    @Min(18)
    @Field(name = "Age")
    private Integer age;

    @Field(name = "Gender")
    private String gender;

    @Field(name = "Residence")
    private String residence;

    @NotNull
    @Field(name = "CompanyName")
    private String companyName;

    @Field(name = "Specialization")
    private String specialization;

    @Field(name = "Motivation")
    private String motivation;

    @NotNull
    @Field(name = "InvitedBy")
    private String invitedBy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant that = (Participant) o;
        return Objects.equals(id, that.id) && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName)
                && Objects.equals(age, that.age) && Objects.equals(gender, that.gender) && Objects.equals(residence, that.residence)
                && Objects.equals(companyName, that.companyName) && Objects.equals(specialization, that.specialization)
                && Objects.equals(motivation, that.motivation) && Objects.equals(invitedBy, that.invitedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, age, gender, residence, companyName, specialization, motivation, invitedBy);
    }
}
