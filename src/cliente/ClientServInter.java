package cliente;

import java.rmi.Remote;

//interfaz de nodo que tendran accesible el nodo central
public interface ClientServInter extends Remote {
    //aviso de que un amigo se ha conectado, se recibe su refRemota de interaccion entre nodos para qu le pueda mandar
    //mensajes, su nombre y un token que acompañara a los mensajes que intercambien para verificar su identidad
    void recibirAmigoConectado(ClientClientInter refRemota, String name, long token) throws java.rmi.RemoteException;

    //aviso de que un amigo se ha desconectado y por lo tanto deja de poder  recibir mensajes
    void recibirDesconexionAmigo(String amigo) throws java.rmi.RemoteException;

    //aviso de que tiene una solicitud de amistad pendiente de aceptar o rechazar
    void recibirSolicitud(String solicitante) throws java.rmi.RemoteException;
}
