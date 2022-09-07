package servidor;

import cliente.ClientClientInter;
import cliente.ClientServInter;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServidorInter extends Remote {
    //el usuario con nombre name y contraseña password inicia sesion indicando que se va a comunicar con el servidor mediante refRemotaCs y con los nodos con refRemotaCc
    boolean conectar(ClientServInter refRemotaCs, ClientClientInter refRemotaCc, String name, String password) throws RemoteException;

    //el usuario con nombre name y contraseña password cierra sesion
    boolean desconectar(String name, String password) throws RemoteException;

    //se da de alta a un nuevo usuario con nombre name y contraseña password
    boolean crearCuenta(String name, String password) throws RemoteException;

    //el usuario solicitante con contraseña password envia una peticion de amistad a solicitado
    boolean solicitar(String solicitante, String password, String solicitado) throws RemoteException;

    //el usuario usuario con contraseña password acepta una peticion de amistad de futAmigo
    boolean aceptarSolicitud(String usuario, String password, String futAmigo) throws RemoteException;

    //el usuario usuario con contraseña password rechaza una peticion de amistad de solicitante
    boolean rechazarSolicitud(String usuario, String password, String solicitante) throws RemoteException;

    //el usuario usuario con contrasñea oldContra cambia su contraseña a newContra
    boolean cambiarContrasena(String usuario, String oldContra, String newContra) throws RemoteException;
}
