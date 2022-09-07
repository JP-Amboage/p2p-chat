package cliente;

import com.sun.javafx.application.PlatformImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import servidor.ServidorInter;

import java.rmi.Naming;
import java.rmi.RemoteException;

public class Main extends Application {
    //controlador de la ventana gestiona el envio y la recepcion de mensajes
    private Controller controlador;
    private String HOST_NAME = "localhost";
    private String PORT_NUM = "3333";

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Obtencion de la referencia remota del servidor central
        String registryURL = "rmi://" + HOST_NAME + ":" + PORT_NUM + "/p2pCentral";
        // encontramos el objeto remoto y lo casteamos a la interfaz
        ServidorInter servidor = (ServidorInter) Naming.lookup(registryURL);

        //Inicio de interfaz grafica
        //localizamos fxml con la descripción de la ventana grafica
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        //cargamos el fxml
        Parent root = loader.load();
        //obtenemos el controlador
        this.controlador = loader.getController();
        //le pasamos la referencia remota del servidor\
        this.controlador.setServidor(servidor);
        //establecemos el titulo de la ventana
        primaryStage.setTitle("App Mensajeria P2P - P4 Garcia Amboage");
        //dimensiones de aprtida de la ventana
        primaryStage.setScene(new Scene(root, 700, 400));
        primaryStage.setResizable(false);
        //mostramos la ventana
        primaryStage.show();
    }

    @Override
    public void stop() throws RemoteException { //se ejecuta al cerrar la ventana grafica
        //cerramos la conexion
        this.controlador.cerrarConexion();
        //PlatformImpl.tkExit();
        Platform.exit();
        //Imprimimos un mensaje por pantalla
        System.out.println("Cerrado");
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
