package cliente;

import java.io.ObjectInputStream;
import javax.swing.JOptionPane;

import servidor.Mensaje;

public class Hilo extends Thread{

	private ObjectInputStream flujoentrada;
	private ventanaCliente ventana;

	public Hilo(ObjectInputStream flujoentrada, ventanaCliente ventana) {
		super();
		this.flujoentrada = flujoentrada;
		this.ventana = ventana;
	}

	public void mostrar() {
		ventana.getLabelservidor().setVisible(false);
		ventana.getCampoServidor().setVisible(false);
		ventana.getLabelPuerto().setVisible(false);
		ventana.getCampoPuerto().setVisible(false);
		ventana.getLabelNick().setVisible(false);
		ventana.getCampoNick().setVisible(false);
		ventana.getBotonConectar().setVisible(false);
		
		ventana.getPanelChat().setVisible(true);
		ventana.getAreaChat().setVisible(true);
		ventana.getBotonDesconectar().setVisible(true);
		ventana.getLabelConectados().setVisible(true);
		ventana.getPanelConectados().setVisible(true);
		ventana.getAreaConectados().setVisible(true);
		ventana.getBotonEnviar().setVisible(true);
		ventana.getCampoEnviar().setVisible(true);
		
		ventana.setTitle("Cliente chat: nick "+ventana.getNick());
	}
	
	public void conectar() {
		
		ventana.getLabelservidor().setVisible(false);
		ventana.getCampoServidor().setVisible(false);
		ventana.getLabelPuerto().setVisible(false);
		ventana.getCampoPuerto().setVisible(false);
		ventana.getLabelNick().setVisible(false);
		ventana.getCampoNick().setVisible(false);
		ventana.getBotonConectar().setVisible(false);
		
		ventana.getPanelChat().setVisible(true);
		ventana.getAreaChat().setVisible(true);
		ventana.getBotonDesconectar().setVisible(true);
		ventana.getLabelConectados().setVisible(true);
		ventana.getPanelConectados().setVisible(true);
		ventana.getAreaConectados().setVisible(true);
		ventana.getBotonEnviar().setVisible(true);
		ventana.getCampoEnviar().setVisible(true);
		
			
		
	}
	
	
	public void desconectar() {
		ventana.getLabelservidor().setVisible(true);
		ventana.getCampoServidor().setVisible(true);
		ventana.getLabelPuerto().setVisible(true);
		ventana.getCampoPuerto().setVisible(true);
		ventana.getLabelNick().setVisible(true);
		ventana.getCampoNick().setVisible(true);
		ventana.getBotonConectar().setVisible(true);
		
		ventana.getPanelChat().setVisible(false);
		ventana.getAreaChat().setVisible(false);
		ventana.getBotonDesconectar().setVisible(false);
		ventana.getLabelConectados().setVisible(false);
		ventana.getPanelConectados().setVisible(false);
		ventana.getAreaConectados().setVisible(false);
		ventana.getBotonEnviar().setVisible(false);
		ventana.getCampoEnviar().setVisible(false);
		
		ventana.setTitle("Cliente chat: nick "+ventana.getNick());
	}
	
	public void run() {
		boolean fin=false;
		while(!fin) {
			try {
				Mensaje recibido=(Mensaje) flujoentrada.readObject();
				switch (recibido.getTipomensaje()) {
				case NOACEPTADO:
					JOptionPane.showMessageDialog(null, recibido.getMensaje());
					fin=true;
					break;
				case ACEPTADO:
					JOptionPane.showMessageDialog(null, recibido.getMensaje());
					ventana.getBotonDesconectar().setVisible(true);
					ventana.setConectado(true);
					mostrar();	
					conectar();
					break;
				case LISTANICKS:
					ventana.getAreaConectados().setText("");
					ventana.getAreaConectados().setText(recibido.getMensaje().toString());
					break;
				
				case NORMAL:
					ventana.getAreaChat().append(recibido.getMensaje().toString());
					break;
				
				case DESCONEXIONACEPTADA:
					ventana.getSocket().close();
					ventana.setConectado(false);
					desconectar();
					ventana.setTitle("Cliente chat: Conexion");
					fin=true;
					break;
				}

			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, e.getMessage());
			} 
		}
	}

}
