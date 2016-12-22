package cl.ucn.disc.dam.proyecto.domain;

import cl.ucn.disc.dam.proyecto.Database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Pic
 *
 * @author Victor Araya Delgado
 * @version 20161111
 */
//LOMBOK
@Slf4j
//Constructor vacio publico
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        //Base de datos donde ira a parar
        database = Database.class,
        cachingEnabled = true,
        orderedCursorLookUp = true, // https://github.com/Raizlabs/DBFlow/blob/develop/usage2/Retrieval.md#faster-retrieval
        cacheSize = Database.CACHE_SIZE
)
public class Pic extends BaseModel {

    /**
     * Identificador unico
     */
    @Getter
    @Column
    @PrimaryKey(autoincrement = true)
    Long id;

    /**
     * Identificador del dispositivo
     */
    @Column
    @Getter
    String deviceId;

    /**
     * Fecha de la foto
     */
    @Column
    @Getter
    Long date;

    /**
     * URL de la foto
     */
    @Column
    @Getter
    String url;

    /**
     * Latitud
     */
    @Column
    @Getter
    Double latitude;

    /**
     * Longitud
     */
    @Column
    @Getter
    Double longitude;

    /**
     * Numero de likes
     */
    @Column
    @Getter
    Integer positive;

    /**
     * Numero de dis-likes
     */
    @Column
    @Getter
    Integer negative;

    /**
     * Numero de warnings
     */
    @Column
    @Getter
    Integer warning;

    /**
     * Imagen full size
     */
    @Column
    @Getter
    String imagen;
}
