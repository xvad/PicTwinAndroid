package cl.ucn.disc.dam.proyecto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cl.ucn.disc.dam.proyecto.domain.Pic;
import cl.ucn.disc.dam.proyecto.domain.Pic_Table;
import cl.ucn.disc.dam.proyecto.domain.Twin;

/**
 * Created by intermex on 17/11/2016.
 */

public class ImageListAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<Twin> twin;

    //Constructor del adapter
    public ImageListAdapter(Activity activity, ArrayList<Twin> twin) {
        this.activity = activity;
        this.twin=twin;
    }

    @Override
    public int getCount() {
        return twin.size();
    }

    @Override
    public Twin getItem(int i) {
        return twin.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    /**
     * Metodo que entrega la vista del list
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (null == convertView) {
            LayoutInflater inf = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inf.inflate(R.layout.list, null);
        }
        final Pic picLocal = twin.get(position).getLocal();
        Pic picRemoto = twin.get(position).getRemote();

        final ImageButton imageIzquerda = (ImageButton)row.findViewById(R.id.imageLocal);
        final ImageButton imageDerecha = (ImageButton)row.findViewById(R.id.imageRemota);

        String[] path1 = picLocal.getUrl().split("file://");
        String[] path2 = picRemoto.getUrl().split("file://");
        File file1 = new File(path1[1]);
        Uri pic1= Uri.fromFile(file1);

        File file2 = new File(path2[1]);
        Uri pic2= Uri.fromFile(file2);

        Picasso
                .with(this.activity)
                .load(pic1)
                .resize(200,200)
                .centerCrop()
                .transform(new CircleTransform())
                .into(imageIzquerda);

        Picasso
                .with(this.activity)
                .load(pic2)
                .resize(200,200)
                .centerCrop()
                .transform(new CircleTransform())
                .into(imageDerecha);

        imageIzquerda.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.e("id pic",toString().valueOf(getItem(position).getLocal().getId()));
                verImagen(getItem(position).getLocal());
            }
        });


        return row;
    }
    /*
    Metodod encrgado de enviar los datos de la imagen a un nuevo Activity
     */
    public void verImagen(Pic pic){
        Intent intent = new Intent(this.activity, Imagen.class);
        intent.putExtra("url",pic.getUrl());
        activity.startActivity(intent);
    }


}
