package cliente;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import servidor.Compartida;
import servidor.Mensaje;
import servidor.Mensaje.TipoMensaje;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ventanaCliente extends JFrame {

	private JPanel contentPane;
	private JTextField campoServidor;
	private JTextField campoPuerto;
	private JTextField campoNick;
	private JTextField campoEnviar;
	private JLabel labelservidor;
	private JLabel labelPuerto;
	private JLabel labelNick;
	private JLabel labelConectados;
	private JTextArea areaChat;
	private JTextArea areaConectados;
	private JButton botonEnviar;
	private JButton botonDesconectar;
	private JButton botonConectar;
	private int PUERTO;
	private String servidor;
	private String nick;
	private Socket socket;
	private ObjectOutputStream flujosalida;
	private ObjectInputStream flujoentrada;
	private JScrollPane panelChat;
	private JScrollPane panelConectados;
	private Hilo hilo;
	private boolean conectado;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ventanaCliente frame = new ventanaCliente();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void conectar() {

		try {
			servidor=campoServidor.getText();
			PUERTO=Integer.valueOf(campoPuerto.getText());
			nick=campoNick.getText();
			socket=new Socket(servidor,PUERTO);
			flujosalida=new ObjectOutputStream(socket.getOutputStream());
			flujosalida.writeObject(new Mensaje(nick, TipoMensaje.CONECTANDO));
			flujosalida.flush();
			flujoentrada=new ObjectInputStream(socket.getInputStream());

			hilo=new Hilo(flujoentrada,getthis());
			hilo.start();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void enviar_mensaje() {
		try {
			flujosalida.writeObject(new Mensaje(campoEnviar.getText(),TipoMensaje.NORMAL));
			flujosalida.flush();
			campoEnviar.setText("");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** 
	 * Create the frame.
	 */
	public ventanaCliente() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

				try {
					if(conectado) {
						flujosalida.writeObject(new Mensaje("Desconectar", TipoMensaje.DESCONEXION));
						flujosalida.flush();
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});

		setTitle("Cliente chat: Conexion");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 869, 544);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		labelservidor = new JLabel("Servidor:");
		labelservidor.setFont(new Font("Tahoma", Font.BOLD, 11));
		labelservidor.setBounds(10, 24, 59, 20);
		contentPane.add(labelservidor);

		labelPuerto = new JLabel("Puerto:");
		labelPuerto.setFont(new Font("Tahoma", Font.BOLD, 11));
		labelPuerto.setBounds(257, 27, 80, 20);
		contentPane.add(labelPuerto);

		labelNick = new JLabel("Nick:");
		labelNick.setFont(new Font("Tahoma", Font.BOLD, 11));
		labelNick.setBounds(460, 24, 80, 20);
		contentPane.add(labelNick);

		campoServidor = new JTextField();
		campoServidor.setBounds(79, 24, 159, 20);
		contentPane.add(campoServidor);
		campoServidor.setColumns(10);

		campoPuerto = new JTextField();
		campoPuerto.setBounds(324, 24, 86, 20);
		contentPane.add(campoPuerto);
		campoPuerto.setColumns(10);

		campoNick = new JTextField();
		campoNick.setBounds(510, 24, 159, 20);
		contentPane.add(campoNick);
		campoNick.setColumns(10);

		botonConectar = new JButton("Conectar");
		botonConectar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		botonConectar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					servidor=campoServidor.getText();
					PUERTO=Integer.valueOf(campoPuerto.getText());
					nick=campoNick.getText().trim();  //CORRIGE LOS ESPACIADOS
					if (nick.equals("")) JOptionPane.showMessageDialog(null, "Nick vacio"); //CONDICION NICK VACIO
					else {
						socket =new Socket(servidor,PUERTO);
						flujosalida=new ObjectOutputStream(socket.getOutputStream());
						flujosalida.writeObject(new Mensaje(nick,TipoMensaje.CONECTANDO));
						flujosalida.flush();
						flujoentrada=new ObjectInputStream(socket.getInputStream());


						Hilo hilo=new Hilo(flujoentrada, getthis());
						hilo.start();
						areaChat.setText("");
					}

				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
				} 

			}


		});
		botonConectar.setBounds(696, 23, 118, 23);
		contentPane.add(botonConectar);

		botonDesconectar = new JButton("Desconectar");
		botonDesconectar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		botonDesconectar.setVisible(false);
		botonDesconectar.addMouseListener(new MouseAdapter() {		//CLICK DESCONEXIÓN
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					flujosalida.writeObject(new Mensaje("Desconectar", TipoMensaje.DESCONEXION));
					flujosalida.flush();
					botonDesconectar.setVisible(false);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});

		botonDesconectar.setBounds(696, 66, 118, 23);
		contentPane.add(botonDesconectar);

		labelConectados = new JLabel("Conectados");
		labelConectados.setVisible(false);
		labelConectados.setFont(new Font("Tahoma", Font.BOLD, 11));
		labelConectados.setBounds(717, 113, 80, 20);
		contentPane.add(labelConectados);

		campoEnviar = new JTextField();
		campoEnviar.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==10) {
					try {
						flujosalida.writeObject(new Mensaje(campoEnviar.getText(),TipoMensaje.NORMAL));
						flujosalida.flush();
						campoEnviar.setText("");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		campoEnviar.setVisible(false);
		campoEnviar.setBounds(10, 440, 671, 20);
		contentPane.add(campoEnviar);
		campoEnviar.setColumns(10);

		botonEnviar = new JButton("Enviar");
		botonEnviar.addMouseListener(new MouseAdapter() { //CLICK ENVIAR MENSAJE 
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					flujosalida.writeObject(new Mensaje(campoEnviar.getText(),TipoMensaje.NORMAL));
					flujosalida.flush();
					campoEnviar.setText("");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});

		botonEnviar.setVisible(false);
		botonEnviar.setBounds(696, 439, 89, 23);
		contentPane.add(botonEnviar);

		panelChat = new JScrollPane();
		panelChat.setVisible(false);
		panelChat.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panelChat.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		panelChat.setBounds(20, 11, 648, 421);
		contentPane.add(panelChat);

		areaChat = new JTextArea();
		panelChat.setViewportView(areaChat);
		areaChat.setEditable(false);

		panelConectados = new JScrollPane();
		panelConectados.setVisible(false);
		panelConectados.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		panelConectados.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panelConectados.setBounds(696, 144, 132, 284);
		contentPane.add(panelConectados);

		areaConectados = new JTextArea();
		panelConectados.setViewportView(areaConectados);
		areaConectados.setEditable(false);

		//CORREGIR BOTON ENCENDER   * CORREGIDO
		//BOTON DESCONECTAR AL CONECTARSE  * CORREGIDO
		//FORMATEAR EL CHAT AL RECONECTARSE *CORREGIDO
		//BOOLEANO CONECTADO  *CORREGIDO
		//WINDOW CLOSING SERVIDOR  * NO HACER
		//CORREGIR TITULO * CORREGIDO
		//IMPLEMENTAR INTRO EN MENSAJE *CORREGIDO
		//OCULTAR LA VENTANA SERVIDOR	* CORREGIDO

	}

	public void ventanaConexion() {
		campoEnviar.setVisible(false);
		botonEnviar.setVisible(false);
		panelConectados.setVisible(false);
		panelChat.setVisible(false);
		botonDesconectar.setVisible(false);
		labelConectados.setVisible(false);

		labelservidor.setVisible(true);
		labelPuerto.setVisible(true);
		labelNick.setVisible(true);

		campoServidor.setVisible(true);
		campoPuerto.setVisible(true);
		campoNick.setVisible(true);

	}

	public void ventanaDesconexion() {
		campoEnviar.setVisible(false);
		botonEnviar.setVisible(false);
		panelConectados.setVisible(false);
		panelChat.setVisible(false);
		botonDesconectar.setVisible(false);
		labelConectados.setVisible(false);

		labelservidor.setVisible(true);
		labelPuerto.setVisible(true);
		labelNick.setVisible(true);

		campoServidor.setVisible(true);
		campoPuerto.setVisible(true);
		campoNick.setVisible(true);
	}

	private ventanaCliente getthis() {
		return this;
	}

	public JTextField getCampoServidor() {
		return campoServidor;
	}

	public JTextField getCampoPuerto() {
		return campoPuerto;
	}

	public JTextField getCampoNick() {
		return campoNick;
	}

	public JTextField getCampoEnviar() {
		return campoEnviar;
	}

	public JLabel getLabelservidor() {
		return labelservidor;
	}

	public JLabel getLabelPuerto() {
		return labelPuerto;
	}

	public JLabel getLabelNick() {
		return labelNick;
	}

	public JLabel getLabelConectados() {
		return labelConectados;
	}

	public JTextArea getAreaChat() {
		return areaChat;
	}

	public JTextArea getAreaConectados() {
		return areaConectados;
	}

	public JButton getBotonEnviar() {
		return botonEnviar;
	}

	public JButton getBotonDesconectar() {
		return botonDesconectar;
	}

	public JButton getBotonConectar() {
		return botonConectar;
	}

	public int getPUERTO() {
		return PUERTO;
	}

	public String getServidor() {
		return servidor;
	}

	public String getNick() {
		return nick;
	}

	public Socket getSocket() {
		return socket;
	}

	public ObjectOutputStream getFlujosalida() {
		return flujosalida;
	}

	public ObjectInputStream getFlujoentrada() {
		return flujoentrada;
	}

	public JScrollPane getPanelChat() {
		return panelChat;
	}

	public JScrollPane getPanelConectados() {
		return panelConectados;
	}

	public boolean isConectado() {
		return conectado;
	}

	public void setConectado(boolean conectado) {
		this.conectado = conectado;
	}



}
