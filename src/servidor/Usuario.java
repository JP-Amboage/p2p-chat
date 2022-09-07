package servidor;

import cliente.ClientClientInter;
import cliente.ClientServInter;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class Usuario {
    private ClientServInter refRemotaCs;
    private ClientClientInter refRemotaCc;
    private String nombre;
    private String contrasena;
    private ArrayList<String> amigos;

    public Usuario(){
        this.contrasena=new String();
        this.nombre=new String();
        this.amigos= new ArrayList<>();
    }
    public void setRefRemotaCs( ClientServInter refRemota) throws RemoteException {
        this.refRemotaCs=refRemota;
    }
    public void setRefRemotaCc( ClientClientInter refRemota) throws RemoteException {
        this.refRemotaCc=refRemota;
    }

    public void setNombre(String nombre){
        this.nombre=nombre;
    }
    public void setContrasena(String contrasena){
        this.contrasena= contrasena;
    }
    public void setAmigos(ArrayList<String> amigos){
        this.amigos= amigos;
    }
    public ClientServInter getRefRemotaCs(){
        return this.refRemotaCs;
    }
    public ClientClientInter getRefRemotaCc(){
        return this.refRemotaCc;
    }

    public String getNombre() {
        return this.nombre;
    }
    public String getContrasena(){
        return this.contrasena;
    }
    public ArrayList<String> getAmigos(){
        return this.amigos;
    }

}
