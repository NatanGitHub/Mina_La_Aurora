package com.bsav157.minalaaurora;

import java.io.Serializable;

public class Maquinas implements Serializable {

    private String modelo;
    private String nombre;
    private String horometroActual;// Horometro Actual
    private String serial;
    private String keyElemento;
    private String horometroProximo;
    private String horometroDiario;
    private String horasRestantes;
    private String lastUpdate;

    public Maquinas() {

    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getHorometroDiario() {
        return horometroDiario;
    }

    public void setHorometroDiario(String horometroDiario) {
        this.horometroDiario = horometroDiario;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getHorometroActual() {
        return horometroActual;
    }

    public void setHorometroActual(String horometroActual) {
        this.horometroActual = horometroActual;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getKeyElemento() {
        return keyElemento;
    }

    public void setKeyElemento(String keyElemento) {
        this.keyElemento = keyElemento;
    }

    public String getHorometroProximo() {
        return horometroProximo;
    }

    public void setHorometroProximo(String horometroProximo) {
        this.horometroProximo = horometroProximo;
    }

    public String getHorasRestantes() {
        return horasRestantes;
    }

    public void setHorasRestantes(String horasRestantes) {
        this.horasRestantes = horasRestantes;
    }
}
