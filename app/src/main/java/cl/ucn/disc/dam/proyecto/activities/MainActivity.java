package cl.ucn.disc.dam.proyecto.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.support.v7.widget.Toolbar;

import cl.ucn.disc.dam.proyecto.ImageListAdapter;
import cl.ucn.disc.dam.proyecto.R;
import cl.ucn.disc.dam.proyecto.WebService;
import cl.ucn.disc.dam.proyecto.domain.Pic;
import cl.ucn.disc.dam.proyecto.domain.Twin;
import cl.ucn.disc.dam.proyecto.util.DeviceUtils;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Pagina principal.
 *
 * @author Victor Araya Delgado
 * @version 20151022
 */
@Slf4j
public class MainActivity extends AppCompatActivity {

    String path = "";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.list)
    ListView dataList;

    private static final int CAMERA_REQUEST = 1;


    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Cuchillo con mantequilla !
        ButterKnife.bind(this);

        //inicializacion de la base de datos
        {
            FlowManager.init(new FlowConfig.Builder(getApplicationContext())
                    .openDatabasesOnInit(true)
                    .build());
        }


        actualizarImagenes();

        //boton flotante
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String pictureName = getPictureName();
                File imageFile = new File(pictureDirectory, pictureName);
                path = Uri.fromFile(imageFile).getPath();

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                startActivityForResult(cameraIntent, CAMERA_REQUEST);


            }
        });

    }


    public void actualizarImagenes(){
        ArrayList<Twin> arrayTwin = new ArrayList<>();
        //Retornamos la lista de twins
        List<Twin> twins = SQLite.select().from(Twin.class).queryList();

        for(int i = twins.size()-1; i >= 0 ; i--){
            // Vamos agregando al Twin
            arrayTwin.add(twins.get(i));
        }

        //instanciamos el adapter para agregarle el array de pic
        final ImageListAdapter adapter = new ImageListAdapter(this, arrayTwin);
        dataList.setAdapter(adapter);
    }

    /**
     * Metodo que retirna un array con la latitud y longitud
     * @return
     */
    private Double[] getPosicion(){
        LocationManager locationManager = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);
        final Double[] salida = new Double[2];

        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        Double longitud;
        Double latitud;
        if(isGPSEnabled){
            try {
                Criteria criteria = new Criteria();
                String provider = locationManager.getBestProvider(criteria, false);
                Location location = locationManager.getLastKnownLocation(provider);

                if(location != null){
                    longitud = location.getLongitude();
                    latitud = location.getLatitude();
                    salida[0]=latitud;
                    salida[1]=longitud;

                }else{
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            Double longitud = location.getLongitude();
                            Double latitud = location.getLatitude();
                            salida[0]=latitud;
                            salida[1]=longitud;

                        }

                        @Override
                        public void onStatusChanged(String s, int i, Bundle bundle) {

                        }

                        @Override
                        public void onProviderEnabled(String s) {

                        }

                        @Override
                        public void onProviderDisabled(String s) {

                        }
                    });
                }


            }catch (SecurityException e){
                e.printStackTrace();
            }

        }

        return salida;
    }

    /**
     * Metodo que retorna la fecha en formato long
     * @return
     */
    private Long getDate(){
        SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
        Long timeStamp =Long.parseLong(date.format(new Date()));
        return timeStamp;
    }

    /**
     * metodo que genera un nombre a la imagen
     * @return
     */
    private String getPictureName() {

        SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timeStamp = date.format(new Date());
        return "Pic"+timeStamp+".jpg";

    }

    /**
     * Activity result de la camara fotografica
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //en caso de que la foto efectivamente se saque
        if(resultCode == RESULT_OK){
            if (requestCode == CAMERA_REQUEST){
                MediaScannerConnection.scanFile(this,
                        new String[]{this.path}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                            }
                        });

                //Parametros del pic
                String deviceID = DeviceUtils.getDeviceId(getApplicationContext());
                Long date = getDate();
                Double[] latlon = getPosicion();
                Double latitud = latlon [0];
                Double longitud = latlon[1];
                Bitmap bm = BitmapFactory.decodeFile(this.path);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                byte[] b = baos.toByteArray();
                String encodedImage = Base64.encodeToString(b,Base64.DEFAULT);

                Log.e("EL PATH!:",path);

                //Objeto Pic
                Pic pic = Pic.builder()
                        .deviceId(deviceID)
                        .latitude(latitud)
                        .longitude(longitud)
                        .date(date)
                        .url(path)
                        .positive(0)
                        .negative(0)
                        .warning(0)
                        .imagen(encodedImage)
                        .build();
                log.debug("Se creo el pic");
                twinServer(pic);

            }
        }


    }

    private void twinServer(final Pic pic){


            //Metodo para el post
            WebService.Factory.getInstance().sendPic(pic).enqueue(new Callback<Twin>() {

                //Respuesta positiva desde el servidor
                @Override
                public void onResponse(Call<Twin> call, Response<Twin> response) {

                    //Obtenemos el pic remoto
                    final Pic picRemoto = response.body().getRemote();
                    Log.e("PicRemoto!",picRemoto.getUrl());
                    picRemoto.save();

                    //Obtenemos el pic local
                    final Pic picLocal = response.body().getLocal();
                    Log.e("Pic!",picLocal.getUrl());
                    picLocal.save();

                    //agregamos los pic local y remoto en un Twin
                    final Twin twin = Twin.builder()
                            .local(picLocal)
                            .remote(picRemoto)
                            .build();

                    twin.save();
                    actualizarImagenes();
                }

                @Override
                public void onFailure(Call<Twin> call, Throwable t) {
                    log.debug("Sin Conexion");
                }

            });
    }


    /**
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
