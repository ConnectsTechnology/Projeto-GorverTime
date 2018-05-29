package lass.govertime.Presidentes;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lass.govertime.R;


/**
 * Created by Nailson on 12/05/2018.
 */

public class FragmentNoticia extends Fragment {

    View view;

    public FragmentNoticia() {
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.noticia, container, false);
        String valor = getActivity().getIntent().getStringExtra("anuncio_id");
       // Toast.makeText(getContext(), "Codigo do Politico " + valor,Toast.LENGTH_LONG).show();
        return view;
    }

}
