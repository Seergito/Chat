package servidor;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import servidor.Mensaje.TipoMensaje;

public class Compartida {

	private ArrayList<String> listanicks;
	private ArrayList<ObjectOutputStream> listastreamssalida;
	private JTextArea AServidor;

	public Compartida(JTextArea aServidor) {
		super();
		AServidor = aServidor;
		listanicks=new ArrayList<String>();
		listastreamssalida=new ArrayList<ObjectOutputStream>();
	}

	// Comprueba si el nick existe en la lista de nicks
	// Si el nick ya existe devuelve false, si no devuelve true y actualiza las listas
	public synchronized boolean comprobarnick(String nick,ObjectOutputStream salida) {
		if (listanicks.contains(nick)) return false; // el nick ya existe
		else {
			listanicks.add(nick);
			listastreamssalida.add(salida);
			return true;
		}
	}

	public synchronized void enviaratodos(Mensaje mensaje) {
		try {
			for (ObjectOutputStream salida : listastreamssalida) {
				salida.writeObject(mensaje);
				salida.flush();
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

	public synchronized void enviarnicks() {
		try {
			for (ObjectOutputStream salida : listastreamssalida) {
				String lista="";
				for (String nic : listanicks) {
					lista=lista+nic+"\n";
				}
				Mensaje mensaje=new Mensaje(lista, TipoMensaje.LISTANICKS);
				salida.writeObject(mensaje);
				salida.flush();
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
	
	public synchronized void desconectar(String nick,ObjectOutputStream flujosalida) {
		listanicks.remove(nick);
		listastreamssalida.remove(flujosalida);
	}

}


