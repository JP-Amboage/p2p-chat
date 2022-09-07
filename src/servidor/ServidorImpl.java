package servidor;

import cliente.ClientClientInter;
import cliente.ClientServInter;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Random;

public class ServidorImpl extends UnicastRemoteObject implements ServidorInter {
    private HashMap<String, Usuario> conectados;
    private AccesoBD accesoBD;
    private Random random;

    protected ServidorImpl(AccesoBD accesoBD) throws RemoteException {
        super();
        this.conectados = new HashMap<>();
        this.accesoBD = accesoBD;
        this.random = new Random();
    }

    @Override
    public boolean conectar(ClientServInter refRemotaCs, ClientClientInter refRemotaCc, String name, String password) throws RemoteException {
        if(refRemotaCs==null || refRemotaCc==null || name==null || password ==null) return false;
        if(conectados.get(name)!=null) return false;
        if(!accesoBD.existeUsuarioContrasena(name,password)) return false;
        Usuario usuario = new Usuario();
        usuario.setRefRemotaCs(refRemotaCs);
        usuario.setRefRemotaCc(refRemotaCc);
        usuario.setNombre(name);
        usuario.setContrasena(password);
        usuario.setAmigos(accesoBD.obtenerAmigos(name));
        this.conectados.put(name,usuario);
        Usuario aux;
        for (String amigo : usuario.getAmigos()){
            aux=this.conectados.get(amigo);
            if(aux!=null){
                long token = random.nextLong();
                refRemotaCs.recibirAmigoConectado(aux.getRefRemotaCc(), aux.getNombre(),token);
                aux.getRefRemotaCs().recibirAmigoConectado(refRemotaCc, name,token);
            }
        }
        for (String solicitante : this.accesoBD.obtenerSolicitudes(name)){
            refRemotaCs.recibirSolicitud(solicitante);
        }
        return true;
    }

    @Override
    public boolean desconectar(String name, String password) throws RemoteException {
        if(name==null || password==null) return false;
        Usuario usuario = conectados.get(name);
        if(usuario==null) return false;
        if(!password.equals(usuario.getContrasena())) return false;
        Usuario aux;
        for (String amigo : usuario.getAmigos()){
            aux=this.conectados.get(amigo);
            if(aux!=null){
                aux.getRefRemotaCs().recibirDesconexionAmigo(name);
            }
        }
        this.conectados.remove(name);
        System.out.println("Desconectado: "+ name);
        return true;
    }

    @Override
    public boolean crearCuenta(String name, String password) throws RemoteException {
        if(name==null || password==null) return false;
        return this.accesoBD.almacenarUsuario(name,password);
    }

    @Override
    public boolean solicitar(String solicitante, String password, String solicitado) throws RemoteException {
        if(solicitante==null || solicitado==null || password==null) return false;
        if(solicitante.equals(solicitado)) return false;
        Usuario usuario = this.conectados.get(solicitante);
        if(usuario==null) return false;
        if(!password.equals(usuario.getContrasena())) return false;
        if(usuario.getAmigos().contains(solicitado)) return false;
        if(this.accesoBD.existeSolicitud(solicitado,solicitante)) return false;
        if(!this.accesoBD.solicitar(solicitante,solicitado)) return false;
        usuario = this.conectados.get(solicitado);
        if(usuario!=null){
            usuario.getRefRemotaCs().recibirSolicitud(solicitante);
        }
        return true;
    }

    @Override
    public boolean aceptarSolicitud(String usuario, String password, String futAmigo) throws RemoteException {
        if(usuario==null || password==null || futAmigo==null) return false;
        Usuario user = this.conectados.get(usuario);
        if(user==null) return false;
        if(!password.equals(user.getContrasena())) return false;
        if(!this.accesoBD.existeSolicitud(futAmigo,usuario)) return false;
        if(!this.accesoBD.amigar(usuario,futAmigo)) return false;
        user.getAmigos().add(futAmigo);
        Usuario amigo = this.conectados.get(futAmigo);
        if(amigo!=null){
            amigo.getAmigos().add(usuario);
            long token = random.nextLong();
            amigo.getRefRemotaCs().recibirAmigoConectado(user.getRefRemotaCc(),usuario,token);
            user.getRefRemotaCs().recibirAmigoConectado(amigo.getRefRemotaCc(),futAmigo,token);
        }
        return true;
    }

    @Override
    public boolean rechazarSolicitud(String usuario, String password, String solicitante) throws RemoteException {
        if(solicitante==null || password==null || usuario==null) {
            System.out.println("Argumento nulo");
            return false;
        }
        Usuario user = this.conectados.get(usuario);
        if(user==null) {
            System.out.println("enviador no conectado");
            return false;
        }
        if(!password.equals(user.getContrasena())){
            System.out.println("contrasena incorrecta");
            return false;
        }
        return this.accesoBD.borrarSolicitud(solicitante,usuario);
    }

    @Override
    public boolean cambiarContrasena(String usuario, String oldContra, String newContra) throws RemoteException {
        System.out.println(newContra);
        System.out.println(oldContra);
        System.out.println(this.conectados.get(usuario).getContrasena());
        if(usuario==null || oldContra==null || newContra==null) return false;
        Usuario user = this.conectados.get(usuario);
        if(user==null) return false;
        if(!oldContra.equals(user.getContrasena())) return false;
        if(!this.accesoBD.cambiarContra(usuario,newContra)) return false;
        user.setContrasena(newContra);
        System.out.println(this.conectados.get(usuario).getContrasena());
        return true;
    }
}
