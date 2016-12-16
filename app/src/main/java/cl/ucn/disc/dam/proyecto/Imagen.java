package cl.ucn.disc.dam.proyecto;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import cl.ucn.disc.dam.proyecto.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by Victor on 10-12-16.
 */

@Slf4j
public class Imagen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datos);

        TextView textFecha = (TextView) findViewById(R.id.textFecha);
        TextView textLatitud = (TextView) findViewById(R.id.textLatitud);
        ImageView imagen = (ImageView) findViewById(R.id.imageView);

        final Bundle bundle = getIntent().getExtras();

        //textFecha.setText("Fecha:   "+bundle.getString("fecha"));
        //textLatitud.setText("Latitud:   "+bundle.getString("latitud"));
        Picasso.with(this)
                .load(bundle.getString("url"))
                .resize(600,600)
                .centerCrop()
                .into(imagen);
    }
    public void finalizar(View view) {
        finish();
    }

}