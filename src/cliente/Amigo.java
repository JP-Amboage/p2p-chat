package cliente;

import java.util.ArrayList;
import java.util.List;

public class Amigo {
    private ClientClientInter refRemota;
    private String nombre;
    private String chat;
    private long token;

    public Amigo(){
        nombre=new String();
        chat = new String();
    }
    public Amigo(ClientClientInter refRemota, String nombre, long token){
        this.refRemota=refRemota;
        this.nombre=nombre;
        this.token=token;
        this.chat=new String();
    }

    public void setChat(String chat){
        this.chat=chat;
    }
    public String getNombre(){
        return this.nombre;
    }
    public ClientClientInter getRefRemota(){return  this.refRemota;}
    public long getToken(){return this.token;}
    public String getChat(){return this.chat;}
}
