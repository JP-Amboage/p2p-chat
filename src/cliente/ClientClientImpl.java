package cliente;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class ClientClientImpl extends UnicastRemoteObject implements ClientClientInter {
    private Controller gui;
    protected ClientClientImpl(Controller gui) throws RemoteException {
        super();
        this.gui=gui;
    }
    @Override
    public Boolean recibirMensaje(String mensaje, String enviador, long token) throws RemoteException{
        if(mensaje==null || enviador ==null) return false;
        Amigo amigo = gui.getConectadosMap().get(enviador);
        if(amigo == null) return false;
        if(amigo.getToken()!=token) return false;
        amigo.setChat(amigo.getChat()+mensaje);
        if(amigo.equals(gui.getContactoSeleccionado())){
            gui.actualizarMensajesTextArea();
        }
        gui.mostrarNotificacion("Nuevo mensaje de: " + enviador);

        return true;
    }
}
