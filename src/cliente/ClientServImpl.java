package cliente;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class ClientServImpl extends UnicastRemoteObject implements ClientServInter {

    private Controller gui;

    protected ClientServImpl() throws RemoteException {
        super();
    }
    protected ClientServImpl(Controller gui) throws RemoteException {
        super();
        this.gui=gui;
    }

    @Override
    public void recibirSolicitud(String solicitante) throws RemoteException {
        gui.getSolicitudes().add(solicitante);
        gui.mostrarNotificacion(solicitante+ " quiere ser su amigo!");
    }


    @Override
    public void recibirAmigoConectado(ClientClientInter refRemota, String name, long token ) throws RemoteException{
        if(refRemota==null || name==null) return;
        if(gui.getConectadosMap().containsKey(name)) return;
        Amigo amigo = new Amigo(refRemota,name,token);
        gui.getConectadosMap().put(name, amigo);
        gui.getConectados().add(amigo);
        gui.mostrarNotificacion(name + " se ha conectado.");
    }

    @Override
    public void recibirDesconexionAmigo(String amigo) throws RemoteException{
        if(amigo==null)return;
        if(!gui.getConectadosMap().containsKey(amigo)) return;
        this.gui.getConectados().remove(gui.getConectadosMap().get(amigo));
        this.gui.getConectadosMap().remove(amigo);
        this.gui.mostrarNotificacion(amigo + " se acaba de desconectar");
    }
}
