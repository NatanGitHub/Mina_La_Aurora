package com.bsav157.minalaaurora;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Extras {

    private Context context;
    private DatabaseReference referenciaBD;
    private ArrayList<Maquinas> maquinas = new ArrayList<>();
    private boolean estado = false;
    private Interface anInterface;

    public Extras(Context context, Interface recibeInterface) {
        this.context = context;
        anInterface = recibeInterface;
    }

    public boolean isOnline() {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }

        return false;

    }

    public void tamañoDialog(Dialog dialog) {
        Window window = dialog.getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout((int) (size.x * 0.95), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }

    public boolean correoValido(String correo){
        Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher mather = pattern.matcher(correo);
        if (!mather.find()) {
            return false;
        }
        return true;
    }

    public void guardarMaquina(String nombre, String serial, String modelo, String horometroActual, String proximoHorometro, String horometroDiario, String UPM){

        if(!isOnline()){
            Toast.makeText(context, "SIN INTERNET", Toast.LENGTH_LONG).show();
            return;
        }

        referenciaBD = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> datosMaquina = new HashMap<>();

        datosMaquina.put( "lastUpdate", obtenerFecha() );
        datosMaquina.put("nombre", nombre);
        datosMaquina.put("horometroProximo", proximoHorometro);
        datosMaquina.put("horasRestantes", restarDosString(horometroActual, proximoHorometro));
        datosMaquina.put("serial", serial);
        datosMaquina.put("modelo", modelo);
        datosMaquina.put("horometroActual", horometroActual);
        datosMaquina.put("horometroDiario", horometroDiario);
        referenciaBD.child(UPM).push().setValue(datosMaquina).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "MAQUINA AGREGADA", Toast.LENGTH_SHORT).show();
                anInterface.onCreateOrModify();
            }
        });

    }

    public void descargarMaquinas(String UPM){

        referenciaBD = FirebaseDatabase.getInstance().getReference();
        maquinas.clear();

        referenciaBD.child(UPM).addValueEventListener(new ValueEventListener() {
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
                MainActivity.maquinas = maquinas;

                /*Intent intent = new Intent(context, SegundoPlano.class);
                intent.putExtra("maquinas", maquinas);
                intent.putExtra("UPM", UPM);
                context.startService(intent);*/
            }
        }, 3500);

    }

    public void actualizaMaquina(Maquinas maquina, String UPM, Dialog dialog){

        if(!isOnline()){
            Toast.makeText(context, "SIN INTERNET", Toast.LENGTH_LONG).show();
            return;
        }

        referenciaBD = FirebaseDatabase.getInstance().getReference();

        referenciaBD.child(UPM).child(maquina.getKeyElemento()).setValue(maquina).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "MAQUINA ACTUALIZADA", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                anInterface.onCreateOrModify();
            }
        });

    }

    public boolean campoVacio(EditText campo){

        if(campo.getText().toString().isEmpty()){
            campo.setError("Campo Vacio");
            return false;
        }

        return true;

    }

    public String stringCampo(EditText campo){
        return campo.getText().toString().trim();
    }

    public String obtenerFecha(){
        Date date = new Date();
        String source;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        source = format.format(date);
        return source;
    }

    public String restarDosString(String one, String two){
        long numberOne, numberTwo;
        numberOne = Long.parseLong(one);
        numberTwo = Long.parseLong(two);
        return "" + (numberTwo - numberOne);
    }

    public boolean esHoy(String fecha){
        Date date = new Date();
        SimpleDateFormat datePattern = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date  = datePattern.parse(fecha);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DateUtils.isToday(date.getTime());
    }

}
