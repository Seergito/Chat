package servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

import servidor.Mensaje.TipoMensaje;

public class Hilo extends Thread {

	private Socket socket;
	private Compartida compartida;
	private String nick;
	private ventanaServidor ventana;

	public Hilo(Socket socket, Compartida compartida,ventanaServidor ventana) {
		super();
		this.socket = socket;
		this.compartida = compartida;
		this.ventana=ventana;

	}

	public void run() {
		try {
			ObjectInputStream entrada=new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream salida=new ObjectOutputStream(socket.getOutputStream());
			boolean fin=false;
			while(!fin) {
				Mensaje recibido=(Mensaje) entrada.readObject();
				switch (recibido.getTipomensaje()) {
				case CONECTANDO:
					nick=(String) recibido.getMensaje();
					if (compartida.comprobarnick(nick, salida)) {
						Mensaje mensaje=new Mensaje( "Nick correcto",TipoMensaje.ACEPTADO);
						salida.writeObject(mensaje);
						salida.flush();
						ventana.getAreaServidor().append("Se ha conectado "+socket.getInetAddress()+" nick: "+nick+"\n");
						compartida.enviarnicks();
						Mensaje m=new Mensaje(nick+" se ha conectado..."+"\n", TipoMensaje.NORMAL);
						compartida.enviaratodos(m);
					}
					else {
						Mensaje mensaje=new Mensaje( "Nick incorrecto",TipoMensaje.NOACEPTADO);
						salida.writeObject(mensaje);
						salida.flush();
						fin=true;
					}
					break;
				case NORMAL:
					Mensaje mensaje=new Mensaje(nick+": "+recibido.getMensaje()+"\n", TipoMensaje.NORMAL);
					compartida.enviaratodos(mensaje);
					break;
				case DESCONEXION:
					Mensaje m=new Mensaje("DESCONEXIÓN ACEPTADA",TipoMensaje.DESCONEXIONACEPTADA);
					salida.writeObject(m);
					salida.flush();
					socket.close();
					
					ventana.getAreaServidor().append("Se ha desconectado "+socket.getInetAddress()+" nick: "+nick+"\n");
					compartida.desconectar(nick, salida);
					compartida.enviarnicks();
					Mensaje m2=new Mensaje(nick+" se ha desconectado..."+"\n",TipoMensaje.NORMAL);
					compartida.enviaratodos(m2);
					fin=true;
					break;
				default:
					fin=true;
					break;
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		finally {
			try {
				socket.close();
			} catch (IOException e) {e.printStackTrace();}
		}
	}


}
