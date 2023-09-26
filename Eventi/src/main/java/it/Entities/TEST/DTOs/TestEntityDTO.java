package it.Entities.TEST.DTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestEntityDTO {

    @MongoId
    private ObjectId id;
    private String stringa;
    private Integer intero;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date data;
    private GeoJsonPoint location;
    private List<String> array;
}
