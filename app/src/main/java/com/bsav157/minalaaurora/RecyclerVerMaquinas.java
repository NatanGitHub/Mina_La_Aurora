package com.bsav157.minalaaurora;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class RecyclerVerMaquinas extends RecyclerView.Adapter<RecyclerVerMaquinas.ViewHolder> {

    private ArrayList<Maquinas> datos;
    private LayoutInflater mInflater;
    private Context context;
    private Extras extras;
    private Interface viewItemInterface;

    public RecyclerVerMaquinas(ArrayList<Maquinas> item, Context context, Interface viewItemInterface){
        this.mInflater= LayoutInflater.from(context);
        this.context=context;
        this.datos= item;
        this.viewItemInterface = viewItemInterface;
        extras = new Extras(context, null);
    }

    @Override
    public int getItemCount(){
        return datos.size();
    }

    @Override
    public RecyclerVerMaquinas.ViewHolder onCreateViewHolder(ViewGroup parent, int ViewType){
        //View view= mInflater.inflate(R.layout.lista_elemetos, null);
        View view= mInflater.from(parent.getContext()).inflate(R.layout.cards_recycler, parent,false);
        return  new RecyclerVerMaquinas.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerVerMaquinas.ViewHolder holder, final int position){
        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewItemInterface != null) {
                    viewItemInterface.onClickMaquina(datos.get(position));
                }
            }
        });
        holder.bindData(datos.get(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView nombreMaquina, orometroMaquina;
        ImageView estadoMaquina;
        CardView cv;

        ViewHolder(View ItemView){
            super(ItemView);
            cv = ItemView.findViewById(R.id.cardView);
            estadoMaquina = ItemView.findViewById(R.id.estado_maquina);
            nombreMaquina = ItemView.findViewById(R.id.card_view_nombre);
            orometroMaquina = ItemView.findViewById(R.id.card_view_orometro);
        }
        void bindData(final Maquinas item){
            nombreMaquina.setText(item.getNombre());
            orometroMaquina.setText(item.getHorometroActual());

            if ( (Long.parseLong(item.getHorasRestantes())) >= 80 )
                estadoMaquina.setBackgroundResource(R.drawable.ic_estado_ok);
            else
                estadoMaquina.setBackgroundResource(R.drawable.ic_estado_mal);

        }
    }
}