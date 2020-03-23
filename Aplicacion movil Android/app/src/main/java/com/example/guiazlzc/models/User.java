package com.example.guiazlzc.models;
/*
 * Echo por: Updown Systems
 * Programado por: Jorge Luis Pérez Medina
 */
import com.google.firebase.database.IgnoreExtraProperties;

// [COMIENZO user_class]
@IgnoreExtraProperties
public class User {
    public String nombreJefe;
    public String localidad;
    public String categoria;
    public String telefono;
    public String ciudad;
    public String direccion;
    public String nombreNegocio;
    public String email;
    public String sesion;

    public User() {
        // Constructor predeterminado requerido para llamadas a DataSnapshot.getValue(User.class)
    }

    public User(String nombreJefe, String localidad, String categoria, String telefono, String ciudad, String direccion, String nombreNegocio, String email,String sesion) {
        this.nombreJefe = nombreJefe;
        this.localidad = localidad;
        this.categoria = categoria;
        this.email = email;
        this.telefono = telefono;
        this.ciudad = ciudad;
        this.direccion = direccion;
        this.nombreNegocio = nombreNegocio;
        this.sesion=sesion;
    }

}
// [FIN user_class]