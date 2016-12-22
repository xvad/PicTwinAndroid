package cl.ucn.disc.dam.proyecto.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import cl.ucn.disc.dam.proyecto.R;

public class ImagenActivity extends AppCompatActivity {
    @BindView(R.id.textFecha)
    TextView textFecha;

    @BindView(R.id.textLatitud)
    TextView textLatitud;

    @BindView(R.id.textLongitud)
    TextView textLongitud;

    @BindView(R.id.textLike)
    TextView textPositive;

    @BindView(R.id.textDislike)
    TextView textNegative;

    @BindView(R.id.textWarning)
    TextView textWarning;

    @BindView(R.id.imageView)
    TextView imagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagen);

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
