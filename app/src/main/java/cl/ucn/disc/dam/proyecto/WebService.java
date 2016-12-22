package cl.ucn.disc.dam.proyecto;

import cl.ucn.disc.dam.proyecto.domain.Pic;
import cl.ucn.disc.dam.proyecto.domain.Twin;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Body;

/**
 * Created by intermex on 21/12/2016.
 */

public interface WebService {
    //direccion del servidor
    String BASE_URL = "http://192.168.43.188:8181/";

    @POST("pic/enviar")
    Call<Twin> sendPic(@Body Pic pic);

    @GET("pic/buscar")
    Call<Pic> getPic(@Body Integer id);

    /**
     * Metodo necesario para gestionar Retrofit
     */
    class Factory{
        private  static WebService service;

        public static WebService getInstance(){
            if(service==null){
                Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL).build();
                WebService service = retrofit.create(WebService.class);
                return service;
            }else{
                return service;
            }
        }

    }
}
