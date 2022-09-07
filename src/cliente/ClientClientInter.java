package cliente;

import java.rmi.Remote;

//interfaz de nodo que tendran accesible sus nodos amigos
public interface ClientClientInter extends Remote {
    //se recibe un mensaje que manda enviador junto con un token de verifiacion de identidad
    Boolean recibirMensaje(String mensaje, String enviador, long token) throws java.rmi.RemoteException;
}
