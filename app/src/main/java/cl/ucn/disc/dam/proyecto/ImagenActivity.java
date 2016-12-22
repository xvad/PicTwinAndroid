package cl.ucn.disc.dam.proyecto;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ImagenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagen);

        //Inicializamos
        TextView textFecha = (TextView) findViewById(R.id.textFecha);
        TextView textLatitud = (TextView) findViewById(R.id.textLatitud);
        TextView textLongitud = (TextView) findViewById(R.id.textLongitud);
        TextView textPositive = (TextView) findViewById(R.id.textLike);
        TextView textNegative = (TextView) findViewById(R.id.textDislike);
        TextView textWarning = (TextView) findViewById(R.id.textWarning);
        ImageView imagen = (ImageView) findViewById(R.id.imageView);

        //se inicializa el Bundle para sacar datos
        final Bundle bundle = getIntent().getExtras();

        //Se muestran los datos del pic seleccionados
        textFecha.setText("Fecha: "+bundle.getLong("date")+"");
        textLatitud.setText("Latitude: "+bundle.getDouble("latitude")+"");
        textLongitud.setText("Longitude: "+bundle.getDouble("longitude")+"");
        textPositive.setText("("+bundle.getInt("positive")+")");
        textNegative.setText("("+bundle.getInt("negative")+")");
        textWarning.setText("("+bundle.getInt("warning")+")");

        //Mostramos la imagen seleccionada un poco más grande
        Picasso.with(this)
                .load(bundle.getString("url"))
                .rotate(90)
                .resize(600,600)
                .centerCrop()
                .into(imagen);
    }
    public void finalizar(View view) {
        finish();
    }


}
