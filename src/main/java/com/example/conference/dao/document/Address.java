package com.example.conference.dao.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @NotNull
    @Field(name = "AddressLine1")
    private String addressLine1;

    @NotNull
    @Field(name = "AddressLine2")
    private String addressLine2;

    @Field(name = "IPAddressRange")
    private String ipAddressRange;

    @Field(name = "Timezone")
    private String timezone;

    @NotNull
    @Field(name = "Latitude")
    private Double latitude;

    @NotNull
    @Field(name = "Longitude")
    private Double longitude;
}
