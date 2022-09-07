package cliente;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import servidor.ServidorInter;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Controller {

    private ClientServImpl clientS;
    private ClientClientImpl clientC;

    private ServidorInter servidor;

    private String nombre;
    private String contrasena;
    private HashMap<String,Amigo> conectadosMap;
    private ObservableList<Amigo> conectados;
    private ObservableList<String> solicitudes;
    private Amigo contactoSeleccionado;
    private String solicitudSeleccionada;

    @FXML
    private TabPane tabPane;
    //tab1
    @FXML
    private TextField nombreTextField;
    @FXML
    private TextField contrasenaTextField;
    @FXML
    private Button iniciarButton;
    @FXML
    private Button registrarseButton;

    //tab2
    @FXML
    private TableView<Amigo> contactosTable;
    @FXML
    private TableColumn<Amigo, String> contactosColumn;
    @FXML
    private TextArea mensajesTextArea;
    @FXML
    private TextField mensajeTextField;
    @FXML
    private Button enviarMensajeButton;

    //tab2
    @FXML
    private TableView<String> solicitudesTable;
    @FXML
    private TableColumn<String, String> solicitudesColumn;
    @FXML
    private Button aceptarButton;
    @FXML
    private Button rechazarButton;
    @FXML
    private TextField amigoTextField;
    @FXML
    private Button solicitarButton;
    @FXML
    private TextField nuevaContrasenaTextField;
    @FXML
    private Button cambiarButton;

    //notificaciones
    @FXML
    private TextArea notificacionesTextArea;
    @FXML
    private Button limpiarButton;
    @FXML
    private Label nameLabel;

    public ObservableList<String> getSolicitudes(){
        return this.solicitudes;
    }
    public ObservableList<Amigo> getConectados(){
        return this.conectados;
    }
    public Amigo getContactoSeleccionado(){
        return this.contactoSeleccionado;
    }

    public void actualizarMensajesTextArea() {
        if(this.contactoSeleccionado!=null){
            this.mensajesTextArea.setText(this.contactoSeleccionado.getChat());
        }else {
            this.mensajesTextArea.setText("");
        }
    }

    @FXML
    private void initialize() {
        this.conectadosMap = new HashMap<>();
        //configuramos la tabla de contactos conectados
        contactosColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        this.conectados = FXCollections.observableArrayList();
        contactosTable.setItems(this.conectados);
        // Listen for selection changes and show the person details when changed.
        contactosTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> cambiarConversacion(newValue));

        //configuramos la tabla de solicitudes recibidas
        solicitudesColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));
        this.solicitudes = FXCollections.observableArrayList();
        solicitudesTable.setItems(this.solicitudes);
        solicitudesTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> cambiarSolicitud(newValue));
    }

    public void setServidor(ServidorInter servidor) {
        this.servidor = servidor;
    }
    public HashMap<String,Amigo> getConectadosMap() {return this.conectadosMap;}

    public void mostrarNotificacion(String notificacion) {
        notificacionesTextArea.setText(notificacionesTextArea.getText()+ "$>"+notificacion + "\n\n");
    }

    public void mostrarAviso(String aviso) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("AVISO!");
        alert.setHeaderText(null);
        alert.setContentText(aviso);
        alert.showAndWait();
    }

    //acciones pestana 1
    public void iniciarButtonClicked() throws RemoteException {
        //no hay un usuario logeado
        if (this.clientC != null) {
            this.mostrarAviso("Ya ha iniciado sesion. Reinicie la app para cambiar de usuario");
            return;
        }
        //los campos se cubireron correctamente
        this.nombre = nombreTextField.getText();
        this.contrasena = contrasenaTextField.getText();
        if (nombre.isBlank() || contrasena.isBlank()) {
            this.mostrarAviso("Rellene ambos campos nombre y contraseña");
            return;
        }
        //creamos los dos clientes
        this.clientC = new ClientClientImpl(this);
        this.clientS = new ClientServImpl(this);

        //conexion con el servidor
        if (this.servidor.conectar(clientS,clientC, nombre, contrasena)) {
            nombreTextField.setText("");
            tabPane.getSelectionModel().select(1);
            nameLabel.setText(nombre);
        } else {
            this.mostrarAviso("No se ha podido conectar\nEs posible que el usuario y contrasena no sean correctos.");
            this.clientS=null;
            this.clientC=null;
        }
        contrasenaTextField.setText("");
    }
    public void registrarseButtonClicked() throws RemoteException {
        //no hay un usuario logeado
        if (this.clientC != null) {
            this.mostrarAviso("Ya ha iniciado sesion. Reinicie la app para crear un usuario");
            return;
        }
        //los campos se cubireron correctamente
        this.nombre = nombreTextField.getText();
        this.contrasena = contrasenaTextField.getText();
        if (nombre.isBlank() || contrasena.isBlank()) {
            this.mostrarAviso("Rellene ambos campos nombre y contraseña");
            return;
        }

        //conexion con el servidor
        if (this.servidor.crearCuenta(nombre, contrasena)) {
            this.mostrarAviso("Usuario registrado\nIntroduzca credenciales para iniciar sesion");
        } else {
            this.mostrarAviso("No se ha podido registrar\nEs posible que el nombre que ha elegido ya este en uso");
            nombreTextField.setText("");
        }
        contrasenaTextField.setText("");
    }

    //acciones pestana 2
    public void enviarMensajeButtonClicked() throws RemoteException {
        //no hay un usuario logeado
        if (this.clientC == null) {
            this.mostrarAviso("Debe iniciar sesion para enviar mensajes");
            return;
        }
        //no hay un contacto seleccionado
        if (this.contactoSeleccionado == null) {
            this.mostrarAviso("Debe elegir un contacto para enviar mensajes");
            return;
        }
        String mensaje = mensajeTextField.getText();
        //no hay mensaje
        if(mensaje.isBlank()) return;

        //enviamos el mensaje al amigo
        mensaje = "| " + this.nombre + " |- " + mensaje + "\n ~" + this.fecha()+"\n\n";
        if(this.contactoSeleccionado.getRefRemota().recibirMensaje(mensaje,this.nombre,this.contactoSeleccionado.getToken())){
            String chat = contactoSeleccionado.getChat()+mensaje;
            this.contactoSeleccionado.setChat(chat);
            this.mensajesTextArea.setText(chat);
            mensajeTextField.setText("");
        }else {
            this.mostrarAviso("El destinatario ha rechazado el mensaje");
        }
    }

    private void cambiarConversacion(Amigo amigo){
        this.contactoSeleccionado = amigo;
        this.actualizarMensajesTextArea();
    }

    //tab 3
    public void aceptarButtonClicked() throws RemoteException{
        //no hay un usuario logeado
        if (this.clientC == null) {
            this.mostrarAviso("Debe iniciar sesion para aceptar solicitudes");
            return;
        }
        //no hay un contacto seleccionado
        if (this.solicitudSeleccionada == null) {
            this.mostrarAviso("Debe elegir una solicitud para aceptar");
            return;
        }
        //enviamos la aceptacion al servidor
        if(this.servidor.aceptarSolicitud(this.nombre,this.contrasena,this.solicitudSeleccionada)){
            this.mostrarAviso("Solicitud aceptada con exito");
            this.solicitudes.remove(solicitudSeleccionada);
        }else {
            this.mostrarAviso("No se ha podido acpetar la solicitud");
        }

    }
    public void rechazarButtonClicked() throws RemoteException{
        //no hay un usuario logeado
        if (this.clientC == null) {
            this.mostrarAviso("Debe iniciar sesion para rechazar solicitudes");
            return;
        }
        //no hay un contacto seleccionado
        if (this.solicitudSeleccionada == null) {
            this.mostrarAviso("Debe elegir una solicitud para rechazar");
            return;
        }
        //enviamos la aceptacion al servidor
        if(this.servidor.rechazarSolicitud(this.nombre,this.contrasena,this.solicitudSeleccionada)){
            this.mostrarAviso("Solicitud rechazada con exito");
            this.solicitudes.remove(solicitudSeleccionada);
        }else {
            this.mostrarAviso("No se ha podido rechazar la solicitud");
        }

    }
    public void solicitarButtonClicked() throws RemoteException{
        //no hay un usuario logeado
        if (this.clientC == null) {
            this.mostrarAviso("Debe iniciar sesion para enviar solicitudes");
            return;
        }
        //no hay un contacto seleccionado
        String solicitado = amigoTextField.getText();
        if (solicitado.isBlank()){
            this.mostrarAviso("Debe introducir alguien a quien solicitar");
            return;
        }
        //enviamos la aceptacion al servidor
        if(this.servidor.solicitar(this.nombre,this.contrasena,solicitado)){
            this.mostrarAviso("Solicitud enviada con exito");
        }else {
            this.mostrarAviso("No se ha podido enviar la solicitud\n"+
                    "Es probale que se deba a:\n"+
                    solicitado+" ya tiene una solicitud tuya pendiente\n"+
                    solicitado+" ya es su amigo\n"+
                    "No existe el usuario " +solicitado);
        }
        amigoTextField.setText("");
    }
    public void cambiarButtonClicked() throws RemoteException{
        //no hay un usuario logeado
        if (this.clientC == null) {
            this.mostrarAviso("Debe iniciar sesion para cambiar la contraseña");
            return;
        }
        //no hay un contacto seleccionado
        String newContra = nuevaContrasenaTextField.getText();
        if (newContra.isBlank()){
            this.mostrarAviso("Debe introducir una nueva contraseña");
            return;
        }
        //enviamos la aceptacion al servidor
        if(this.servidor.cambiarContrasena(this.nombre,this.contrasena,newContra)){
            this.mostrarAviso("Contrasña cambiada con exito");
            this.contrasena=newContra;
        }else {
            this.mostrarAviso("No se ha podido  cambiar la contraseña");
        }
        nuevaContrasenaTextField.setText("");
    }

    private void cambiarSolicitud(String solicitud){
        this.solicitudSeleccionada=solicitud;
    }

    public void LimpiarButtonClicked(){
        notificacionesTextArea.setText("");
    }

    //funcion auxiliar para obtener la fecha actual formateada
    private String fecha() {
        //creamos un formato horas minutos y segundos
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        //obtenemos la fecha actual la formateamos y la devolvemos
        return dateFormat.format(new Date());
    }


    public void cerrarConexion() throws RemoteException {
        if(servidor == null || clientC == null || clientS == null) return;
        this.servidor.desconectar(nombre,contrasena);
        UnicastRemoteObject.unexportObject(clientC,false);
        UnicastRemoteObject.unexportObject(clientS,false);
        System.out.println("Desconectado");
    }
}
