package cl.ucn.disc.dam.proyecto.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.support.v7.widget.Toolbar;

import cl.ucn.disc.dam.proyecto.ImageListAdapter;
import cl.ucn.disc.dam.proyecto.R;
import cl.ucn.disc.dam.proyecto.domain.Pic;
import cl.ucn.disc.dam.proyecto.domain.Twin;
import cl.ucn.disc.dam.proyecto.util.DeviceUtils;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.extern.slf4j.Slf4j;

/**
 * Pagina principal.
 *
 * @author Victor Araya Delgado
 * @version 20151022
 */
@Slf4j
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.list)
    ListView dataList;

    private boolean camera;

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


        //boton flotante
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getUriPicture());
                startActivityForResult(cameraIntent, CAMERA_REQUEST);

                String deviceID = DeviceUtils.getDeviceId(getApplicationContext());
                Long date = getDate();
                Double[] latlon = getPosicion();
                Double latitud = latlon [0];
                Double longitud = latlon[1];

                {
                    FlowManager.init(new FlowConfig.Builder(getApplicationContext())
                            .openDatabasesOnInit(true)
                            .build());

                    log.debug("DB initialized ");
                }


                Pic pic = Pic.builder()
                        .deviceId(deviceID)
                        .latitude(latitud)
                        .longitude(longitud)
                        .date(date)
                        .url(getUriPicture().toString())
                        .positive(0)
                        .negative(0)
                        .warning(0)
                        .build();
                        log.debug("Se creo el pic");

                //commit
                pic.save();


                Pic pic1 = Pic.builder()
                        .deviceId(deviceID)
                        .latitude(latitud)
                        .longitude(longitud)
                        .negative(0)
                        .positive(0)
                        .warning(0)
                        .date(date)
                        .url(getUriPicture().toString())
                        .build();
                pic1.save();

                Twin nue = Twin.builder()
                        .local(pic)
                        .remote(pic1)
                        .build();

                nue.save();
                log.debug("Se agregó el twin a la DB");


            }
        });

    }

    public Uri getUriPicture(){
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureName = getPictureName();
        File imageFile = new File(pictureDirectory, pictureName);
        return Uri.fromFile(imageFile);// variable con uri direccion
    }
    @Override
    protected void onStart() {
        super.onStart();
        ArrayList<Twin> arrayTwin = new ArrayList<>();
        {
            FlowManager.init(new FlowConfig.Builder(getApplicationContext())
                    .openDatabasesOnInit(true)
                    .build());

        }
        {

            List<Twin> twins = SQLite.select().
                    from(Twin.class).queryList();

            for(int i = twins.size()-1; i >= 0 ; i--){
                String log = "ID:" + twins.get(i).getLocal().getId() + " date: "+twins.get(i).getLocal().getDate()
                        + " ,Image: " + twins.get(i).getLocal().getUrl();

                // Writing Contacts to log
                Log.d("Result: ", log);
                // add contacts data in arrayList
                arrayTwin.add(twins.get(i));

            }

            final ImageListAdapter adapter = new ImageListAdapter(this, arrayTwin);
            dataList.setAdapter(adapter);

        }
    }

    /**
     * Metodo que retirna un array con la latitud y longitus
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
        return "PicTwin"+timeStamp+".jpg";

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

        if(resultCode == RESULT_OK){
            if (requestCode == CAMERA_REQUEST){

            }
        }


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
