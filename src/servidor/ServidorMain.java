package servidor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServidorMain {
    public static void main(String[] args) {
        InputStreamReader is = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(is);
        String portNum="3333", registryURL;
        try{
            AccesoBD accesoBD= new AccesoBD();
            int puerto = Integer.parseInt(portNum);
            startRegistry(puerto);
            ServidorImpl obj = new ServidorImpl(accesoBD);
            registryURL = "rmi://localhost:" + portNum + "/p2pCentral";
            Naming.rebind(registryURL, obj);
            System.out.println("Servidor registrado.  Registro actualmente contiene:");
            // lista los nombres actualmente en el registro
            listRegistry(registryURL);
            System.out.println("Servidor Listo.");
        }// end try
        catch (Exception e) {
            System.out.println("Exception en Servidor.main: " + e);
        } // end catch
    } // end main
    private static void startRegistry(int puerto) throws RemoteException {
        try{
            Registry registry = LocateRegistry.getRegistry(puerto);
            registry.list();
        }catch (RemoteException e){
            System.out.println("Registro RMI no puede ser alocado en el puerto " + puerto);
            Registry registry = LocateRegistry.createRegistry(puerto);
            System.out.println("Registro RMI creado en el puerto " + puerto);
        }
    }

    private static void listRegistry(String registryURL) throws RemoteException, MalformedURLException {
        System.out.println("Registro " + registryURL + " contiene: ");
        String [ ] nombres = Naming.list(registryURL);
        for (String nombre : nombres) System.out.println(nombre);
    }
}
