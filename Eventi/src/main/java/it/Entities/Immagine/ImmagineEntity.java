package it.Entities.Immagine;

import ch.qos.logback.classic.html.UrlCssBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("Immagini")
@Builder
public class ImmagineEntity {

    @MongoId
    @Field(targetType = FieldType.OBJECT_ID)
    private String id;

    @Field(name = "ID_EVENTO")
    private String idevento;

    @Field(name = "URL")
    private String nome;

    @Field(name = "FORMATO")
    private String type;

    @Field(name = "IMAGE_DATA")
    private byte[] imageData;
}
