package com.bsav157.minalaaurora;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SegundoPlano extends Service {

    private ArrayList<Maquinas> maquinas = new ArrayList<>();
    private DatabaseReference referenciaBD;
    private String UPM;
    private String UpmTotales[] = {"UPM 001","UPM 002","UPM 005","UPM 006","UPM 007", "UPM 009",
                                                    "UPM 010","UPM 074","UPM 079"};
    private Extras extras = new Extras(this, null);

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate(){
        super.onCreate();
    }

    public void onStart(Intent intent, int startId){

        referenciaBD = FirebaseDatabase.getInstance().getReference();
        maquinas = (ArrayList<Maquinas>) intent.getSerializableExtra("maquinas");
        UPM = intent.getStringExtra("UPM");
        descargarUpm(0);

        //this.stopSelf();

    }

    public void onDestroy(){
        super.onDestroy();
        System.out.println("El servicio a Terminado");
    }

    public void actualizaProducto(int upm){

        Long temporal;

        for ( Maquinas maquina : maquinas ) {

            if( !extras.esHoy( maquina.getLastUpdate() ) ){
                temporal = Long.parseLong( maquina.getHorometroActual() );
                temporal += (Long.parseLong( maquina.getHorometroDiario() ));
                maquina.setHorometroActual( String.valueOf(temporal) );
                // Ya le dimos nuevo valor al horometro actual, falta cambiar las horas restantes
                temporal = Long.parseLong( maquina.getHorometroProximo() );
                temporal -= (Long.parseLong( maquina.getHorometroActual() ));
                maquina.setHorasRestantes( String.valueOf(temporal) );

                if(!extras.isOnline())
                    continue;// Si en el preciso momento no hay internet, denegamos el envio

                referenciaBD.child(UpmTotales[upm]).child(maquina.getKeyElemento()).setValue(maquina);

            }
        }

        if(upm == 8)// Modificar esto si se añaden upms
            this.stopSelf();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                descargarUpm(upm + 1);
            }
        }, 1000);

    }

    public void descargarUpm(int upm){

        maquinas.clear();

        referenciaBD.child(UpmTotales[upm]).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for( DataSnapshot datos :  snapshot.getChildren()){
                    Maquinas p = new Maquinas();
                    p.setKeyElemento( datos.getKey() );
                    p.setModelo( datos.child("modelo").getValue().toString() );
                    p.setNombre( datos.child("nombre").getValue().toString() );
                    p.setLastUpdate( datos.child("lastUpdate").getValue().toString() );
                    p.setHorometroActual( datos.child("horometroActual").getValue().toString() );
                    p.setHorometroProximo( datos.child("horometroProximo").getValue().toString() );
                    p.setHorometroDiario( datos.child("horometroDiario").getValue().toString() );
                    p.setHorasRestantes( datos.child("horasRestantes").getValue().toString() );
                    p.setKeyElemento( datos.getKey() );
                    p.setSerial( datos.child("serial").getValue().toString() );
                    maquinas.add(p);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                actualizaProducto(upm);
            }
        }, 3500);

    }

}
