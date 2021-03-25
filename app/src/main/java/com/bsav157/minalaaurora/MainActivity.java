package com.bsav157.minalaaurora;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // sara.correa@gmail.com
    // sara.correa1999

    // Variables de la vista Constrain Login
    private ConstraintLayout constraintLogin;
    private EditText constrainLoginEtCorreo,constrainLoginEtClave;
    private Button constrainLoginBtnIngresar;
    private TextView constrainLoginTvRegistrarse, salir;

    // Variables de la vista Constrain Menu Principal
    private ConstraintLayout constraintMenuPrincipal;
    private Button constrainMenuPrincipalBtnUpm001, constrainMenuPrincipalBtnUpm002, constrainMenuPrincipalBtnUpm003;

    // Variables de la vista Constrain Menu Upm
    private ConstraintLayout constrainMenuUpm;
    private TextView constrainMenuUpmTvRegresar, constrainMenuUpmTvTitulo;
    private Button constrainMenuUpmBtnAgregar;
    public static RecyclerView recyclerView;
    private ImageView imagenDescargandoMaquinas;

    // Otras variables
    private FirebaseAuth mAuth;
    private Extras extras;
    private SharedPreferences sharedpreferences;
    private String mypreference = "mypref";
    private String UPM = "none";
    public static ArrayList<Maquinas> maquinas;
    public static RecyclerVerMaquinas myAdapterRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initItems();
        rellenarDatosSesion();
    }

    public void initItems(){

        // Variables de la vista Constrain Login
        constraintLogin = findViewById(R.id.constrainLogin);
        constrainLoginEtCorreo = findViewById(R.id.constrain_login_et_correo);
        constrainLoginEtClave = findViewById(R.id.constrain_login_et_clave);
        constrainLoginBtnIngresar = findViewById(R.id.constrain_login_btn_ingresar);
        constrainLoginTvRegistrarse = findViewById(R.id.constrain_login_tv_registrarse);
        salir = findViewById(R.id.salir);
        salir.setOnClickListener(this);
        constrainLoginBtnIngresar.setOnClickListener(this);
        constrainLoginTvRegistrarse.setOnClickListener(this);

        // Variables de la vista Constrain Menu Principal
        constraintMenuPrincipal = findViewById(R.id.constrain_menu_principal);
        constrainMenuPrincipalBtnUpm001 = findViewById(R.id.constrain_menu_principal_btn_upm001);
        constrainMenuPrincipalBtnUpm002 = findViewById(R.id.constrain_menu_principal_btn_upm002);
        constrainMenuPrincipalBtnUpm003 = findViewById(R.id.constrain_menu_principal_btn_upm005);
        constrainMenuPrincipalBtnUpm001.setOnClickListener(this);
        constrainMenuPrincipalBtnUpm002.setOnClickListener(this);
        constrainMenuPrincipalBtnUpm003.setOnClickListener(this);

        // Variables de la vista Constrain Menu Upm
        constrainMenuUpm = findViewById(R.id.constrain_menu_upm);
        constrainMenuUpmTvRegresar = findViewById(R.id.constrain_menu_upm_tv_regresar);
        constrainMenuUpmTvTitulo = findViewById(R.id.constrain_menu_upm_titulo);
        constrainMenuUpmBtnAgregar = findViewById(R.id.constrain_menu_upm_btn_agregar);
        recyclerView = findViewById(R.id.recycler_ver_maquina);
        imagenDescargandoMaquinas = findViewById(R.id.imagen_descargando_maquinas);
        constrainMenuUpmTvRegresar.setOnClickListener(this);
        constrainMenuUpmBtnAgregar.setOnClickListener(this);

        // Otras variables
        extras = new Extras(this, new Interface() {
            @Override
            public void onClickMaquina(Maquinas maquina) {
            }

            @Override
            public void onCreateOrModify() {
                actualizaRecycler();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        mAuth.getInstance().signOut();

    }

    public void rellenarDatosSesion() {
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

        if (sharedpreferences.contains("correo")) {
            constrainLoginEtCorreo.setText(sharedpreferences.getString("correo", ""));
        }
        if (sharedpreferences.contains("clave")) {
            constrainLoginEtClave.setText(sharedpreferences.getString("clave", ""));
        }
    }

    public void guardarDatosSesion(String correo, String clave) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("correo", correo);
        editor.putString("clave", clave);
        editor.commit();
    }

    public void actualizaRecycler(){

        recyclerView.setVisibility(View.GONE);
        imagenDescargandoMaquinas.setBackgroundResource(R.drawable.descargando_maquinas);
        AnimationDrawable animationDrawable = (AnimationDrawable) imagenDescargandoMaquinas.getBackground();
        animationDrawable.start();
        imagenDescargandoMaquinas.setVisibility(View.VISIBLE);

        maquinas.clear();
        extras.descargarMaquinas(UPM);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                myAdapterRecycler = new RecyclerVerMaquinas( maquinas, getApplicationContext(), new Interface() {
                    @Override
                    public void onClickMaquina(Maquinas maquina) {
                        dialogVerMaquina(maquina);
                    }

                    @Override
                    public void onCreateOrModify() {
                    }
                });
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager( new LinearLayoutManager(getApplicationContext()));
                recyclerView.setAdapter(myAdapterRecycler);
                // Ocultamos imagen y mostramos recycler
                imagenDescargandoMaquinas.setVisibility(View.GONE);
                animationDrawable.stop();
                recyclerView.setVisibility(View.VISIBLE);
            }
        }, 4000);

    }

    public void dialogAgregarMaquina(){

        Dialog dialogAgregar = new Dialog(this);
        dialogAgregar.setContentView(R.layout.agregar_maquina);
        dialogAgregar.setCancelable(false);
        extras.tamañoDialog(dialogAgregar);
        EditText campoNombre = dialogAgregar.findViewById(R.id.nombre_maquina);
        EditText campoSerial = dialogAgregar.findViewById(R.id.serial_maquina);
        EditText campoModelo = dialogAgregar.findViewById(R.id.modelo_maquina);
        EditText campoHorometroActual = dialogAgregar.findViewById(R.id.horometro_actual);// Horometro Actual
        EditText campoProximoHorometro = dialogAgregar.findViewById(R.id.proximo_horometro);
        EditText campoHorometroDiario = dialogAgregar.findViewById(R.id.horometro_diario);
        EditText horasRestantes = dialogAgregar.findViewById(R.id.horas_restantes);
        Button guardarMaquina = dialogAgregar.findViewById(R.id.guardar_maquina);
        TextView cancelar = dialogAgregar.findViewById(R.id.cancelar);

        horasRestantes.setEnabled(false);
        guardarMaquina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ( !extras.campoVacio(campoNombre) || !extras.campoVacio(campoSerial) || !extras.campoVacio(campoModelo) ||
                      !extras.campoVacio(campoHorometroActual) || !extras.campoVacio(campoProximoHorometro) ||
                      !extras.campoVacio(campoHorometroDiario))
                    return;

                extras.guardarMaquina( campoNombre.getText().toString().trim(), campoSerial.getText().toString().trim(),
                                                    campoModelo.getText().toString().trim(), campoHorometroActual.getText().toString().trim(),
                                                    campoProximoHorometro.getText().toString().trim(), campoHorometroDiario.getText().toString().trim(),
                                                    UPM);

                dialogAgregar.dismiss();

            }
        });
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAgregar.dismiss();
            }
        });

        dialogAgregar.show();

    }

    public void dialogVerMaquina(Maquinas maquina){

        Dialog dialogVerMaquina = new Dialog(this);
        dialogVerMaquina.setContentView(R.layout.agregar_maquina);
        dialogVerMaquina.setCancelable(false);
        extras.tamañoDialog(dialogVerMaquina);

        // Enlazo elementos de la vista
        EditText campoNombre = dialogVerMaquina.findViewById(R.id.nombre_maquina);
        EditText campoSerial = dialogVerMaquina.findViewById(R.id.serial_maquina);
        EditText campoModelo = dialogVerMaquina.findViewById(R.id.modelo_maquina);
        EditText campoHorometro = dialogVerMaquina.findViewById(R.id.horometro_actual);
        EditText campoHorometroProximo = dialogVerMaquina.findViewById(R.id.proximo_horometro);
        EditText campoHorometroDiario = dialogVerMaquina.findViewById(R.id.horometro_diario);
        EditText horasRestantes = dialogVerMaquina.findViewById(R.id.horas_restantes);
        Button btnGuardar = dialogVerMaquina.findViewById(R.id.guardar_maquina);
        TextView cancelar = dialogVerMaquina.findViewById(R.id.cancelar);

        horasRestantes.setEnabled(false);
        // Creo y manipulo los listener del click
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogVerMaquina.dismiss();
            }
        });
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ( !extras.campoVacio(campoNombre) || !extras.campoVacio(campoSerial) || !extras.campoVacio(campoModelo) ||
                      !extras.campoVacio(campoHorometro) || !extras.campoVacio(campoHorometroProximo) ||
                      !extras.campoVacio(campoHorometroDiario))
                    return;

                if( campoNombre.getText().toString().equals(maquina.getNombre()) &&
                     campoSerial.getText().toString().equals(maquina.getSerial()) &&
                     campoModelo.getText().toString().equals(maquina.getModelo()) &&
                     campoHorometro.getText().toString().equals(maquina.getHorometroActual()) &&
                     campoHorometroProximo.getText().toString().equals( maquina.getHorometroProximo()) &&
                     campoHorometroDiario.getText().toString().equals( maquina.getHorometroDiario() ) ){
                    // Si no han habido cambios, entonces no es necesario guardar cambios
                    Toast.makeText(MainActivity.this, "No hay cambios para guardar", Toast.LENGTH_SHORT).show();
                    return;
                }

                maquina.setHorometroProximo(extras.stringCampo(campoHorometroProximo));
                maquina.setHorometroDiario( extras.stringCampo(campoHorometroDiario) );
                maquina.setModelo(extras.stringCampo(campoModelo));
                maquina.setNombre(extras.stringCampo(campoNombre));
                maquina.setHorometroActual(extras.stringCampo(campoHorometro));
                maquina.setSerial(extras.stringCampo(campoSerial));

                // Si han habido cambios entonces se procede a guardar los cambios
                extras.actualizaMaquina(maquina, UPM, dialogVerMaquina);

            }
        });

        // Aplico valores por defecto
        campoNombre.setText( maquina.getNombre() );
        campoSerial.setText( maquina.getSerial() );
        campoModelo.setText( maquina.getModelo() );
        campoHorometro.setText( maquina.getHorometroActual() );
        campoHorometroDiario.setText( maquina.getHorometroDiario() );
        campoHorometroProximo.setText( maquina.getHorometroProximo() );
        horasRestantes.setText( "" + maquina.getHorasRestantes() );

        dialogVerMaquina.show();

    }

    public void iniciarSesion(){
        constraintLogin.setVisibility(View.GONE);
        mAuth.signInWithEmailAndPassword(constrainLoginEtCorreo.getText().toString().trim(), constrainLoginEtClave.getText().toString().trim())
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            guardarDatosSesion(constrainLoginEtCorreo.getText().toString().trim(), constrainLoginEtClave.getText().toString().trim());

                            FirebaseUser user = mAuth.getCurrentUser();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    constraintMenuPrincipal.setVisibility(View.VISIBLE);
                                }
                            }, 500);

                        } else {
                            constraintLogin.setVisibility(View.VISIBLE);
                            Toast.makeText(MainActivity.this, "No se pudo iniciar sesion", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.constrain_login_btn_ingresar:

                if(!extras.isOnline()){
                    Toast.makeText(this, "SIN INTERNET", Toast.LENGTH_LONG).show();
                    return;
                }

                if(constrainLoginEtCorreo.getText().toString().isEmpty()){
                    constrainLoginEtCorreo.setError("Introduzca su correo");
                    return;
                }

                if (!extras.correoValido(constrainLoginEtCorreo.getText().toString().trim())) {
                    constrainLoginEtCorreo.setError("Formato de correo invalido");
                    return;
                }

                if(constrainLoginEtClave.getText().toString().isEmpty()){
                    constrainLoginEtClave.setError("Introduzca su clave");
                    return;
                }

                iniciarSesion();

                break;

            case R.id.constrain_login_tv_registrarse:

                break;

            case R.id.constrain_menu_principal_btn_upm001:
            case R.id.constrain_menu_principal_btn_upm002:
            case R.id.constrain_menu_principal_btn_upm005:
            case R.id.constrain_menu_principal_btn_upm006:
            case R.id.constrain_menu_principal_btn_upm007:
            case R.id.constrain_menu_principal_btn_upm009:
            case R.id.constrain_menu_principal_btn_upm010:
            case R.id.constrain_menu_principal_btn_upm074:
            case R.id.constrain_menu_principal_btn_upm079:

                // Animacion para avisar que se estan descargando datos
                imagenDescargandoMaquinas.setBackgroundResource(R.drawable.descargando_maquinas);
                AnimationDrawable animationDrawable = (AnimationDrawable) imagenDescargandoMaquinas.getBackground();
                animationDrawable.start();
                imagenDescargandoMaquinas.setVisibility(View.VISIBLE);

                constraintMenuPrincipal.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        constrainMenuUpm.setVisibility(View.VISIBLE);
                    }
                }, 500);

                if(view.getId() == R.id.constrain_menu_principal_btn_upm001){
                    constrainMenuUpmTvTitulo.setText("UPM 001");
                    UPM = "UPM 001";
                }

                if(view.getId() == R.id.constrain_menu_principal_btn_upm002){
                    constrainMenuUpmTvTitulo.setText("UPM 002");
                    UPM = "UPM 002";
                }

                if(view.getId() == R.id.constrain_menu_principal_btn_upm005){
                    constrainMenuUpmTvTitulo.setText("UPM 003");
                    UPM = "UPM 005";
                }

                if(view.getId() == R.id.constrain_menu_principal_btn_upm006){
                    constrainMenuUpmTvTitulo.setText("UPM 006");
                    UPM = "UPM 006";
                }

                if(view.getId() == R.id.constrain_menu_principal_btn_upm007){
                    constrainMenuUpmTvTitulo.setText("UPM 007");
                    UPM = "UPM 007";
                }

                if(view.getId() == R.id.constrain_menu_principal_btn_upm009){
                    constrainMenuUpmTvTitulo.setText("UPM 009");
                    UPM = "UPM 009";
                }

                if(view.getId() == R.id.constrain_menu_principal_btn_upm010){
                    constrainMenuUpmTvTitulo.setText("UPM 010");
                    UPM = "UPM 010";
                }

                if(view.getId() == R.id.constrain_menu_principal_btn_upm074){
                    constrainMenuUpmTvTitulo.setText("UPM 074");
                    UPM = "UPM 074";
                }

                if(view.getId() == R.id.constrain_menu_principal_btn_upm079){
                    constrainMenuUpmTvTitulo.setText("UPM 079");
                    UPM = "UPM 079";
                }

                extras.descargarMaquinas(UPM);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myAdapterRecycler = new RecyclerVerMaquinas( maquinas, getApplicationContext(), new Interface() {
                            @Override
                            public void onClickMaquina(Maquinas maquina) {
                                dialogVerMaquina(maquina);
                            }

                            @Override
                            public void onCreateOrModify() {
                            }
                        });
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager( new LinearLayoutManager(getApplicationContext()));
                        recyclerView.setAdapter(myAdapterRecycler);
                        // Ocultamos imagen y mostramos recycler
                        imagenDescargandoMaquinas.setVisibility(View.GONE);
                        animationDrawable.stop();
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }, 4000);

                break;

            case R.id.constrain_menu_upm_tv_regresar:

                UPM = "none";
                constrainMenuUpm.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        constraintMenuPrincipal.setVisibility(View.VISIBLE);
                    }
                }, 500);

                break;

            case R.id.constrain_menu_upm_btn_agregar:
                dialogAgregarMaquina();
                break;

            case R.id.salir:
                onBackPressed();
                break;

            default:
                break;

        }
    }

    @Override
    public void onBackPressed() {

        Dialog questionExit = new Dialog(this);
        questionExit.setContentView(R.layout.question);
        questionExit.setCancelable(false);
        extras.tamañoDialog(questionExit);
        TextView responesYes = questionExit.findViewById(R.id.response_one);
        TextView responesNo = questionExit.findViewById(R.id.response_two);

        responesYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                finish();
            }
        });
        responesNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionExit.dismiss();
            }
        });

        questionExit.show();
    }

    @Override
    protected void onDestroy() {
        mAuth.getInstance().signOut();
        super.onDestroy();
    }

}