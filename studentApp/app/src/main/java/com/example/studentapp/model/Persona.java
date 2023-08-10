package com.example.studentapp.model;
public class Persona {
    private String cedula;
    private String celular;
    private String nombre;
    private String apellido;
    private String correo;
    private String direccion;
    private String carrera;
    private int nivelCarrera;
    private String fotoUrl;
    private String pdfUrl;
    private String audioUrl;

    // Constructor
    public Persona(String cedula, String celular, String nombre, String apellido,
                   String correo, String direccion, String carrera, int nivelCarrera,
                   String fotoUrl, String pdfUrl, String audioUrl) {
        this.cedula = cedula;
        this.celular = celular;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.direccion = direccion;
        this.carrera = carrera;
        this.nivelCarrera = nivelCarrera;
        this.fotoUrl = fotoUrl;
        this.pdfUrl = pdfUrl;
        this.audioUrl = audioUrl;
    }

    // Constructor y métodos setters...

    // Getters
    public String getCedula() {
        return cedula;
    }

    public String getCelular() {
        return celular;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getCarrera() {
        return carrera;
    }

    public int getNivelCarrera() {
        return nivelCarrera;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }
}
