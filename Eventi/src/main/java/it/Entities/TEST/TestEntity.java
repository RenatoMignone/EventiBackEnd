package it.Entities.TEST;

import it.Entities.TEST.DTOs.TestEntityDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("TEST")
public class TestEntity {

    @MongoId
    private ObjectId id;

    @Field(name = "PROVA_STRINGA")
    private String stringa;

    @Field(name = "PROVA_INTEGER")
    private Integer intero;

    @Field(name = "PROVA_DATA")
    private Date data;

    @Field(name = "PROVA_LOCATION")
    private GeoJsonPoint location;

    @Field(name = "PROVA_ARRAY_STRINGHE")
    private List<String> array;

    public void setAll(TestEntityDTO test){
        this.stringa=test.getStringa();
        this.intero=test.getIntero();
        this.data=test.getData();
        this.location=test.getLocation();
        this.array=test.getArray();
    }
}
