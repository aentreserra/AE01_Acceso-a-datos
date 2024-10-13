package es.florida;

import javax.swing.*;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.awt.event.ActionEvent;
import javax.swing.JTree;

import java.text.SimpleDateFormat;
import javax.swing.JScrollPane;

import java.io.*;

import java.nio.file.*;
import java.util.List;
import java.text.Normalizer;
import java.util.regex.Pattern;


/**
 * Clase para mostrar la interfaz de usuario y sus respectivas funciones: cargar carpeta, buscar y remplazar
 * @author Adrià Entreserra
 * @version 1.0
 */
public class Vista extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTree directoryTree;
	private JTextField userSearchInput;
	private JCheckBox keySensitive;
	private JCheckBox keyAccent;
	private JTextField userReplaceInputFrom;
	private JTextField userReplaceInputTo;

	/**
	 * Metodo para arrancar la aplicación
	 * @param args Parametro pasados por consola
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Vista frame = new Vista();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	/**
	 * Componentes de la interfaz gráfica
	 */
	public Vista() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 534);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel primerPanel = new JPanel();
		primerPanel.setBorder(null);
		primerPanel.setBounds(10, 11, 414, 473);
		contentPane.add(primerPanel);
		primerPanel.setLayout(null);
		
		JButton btnNewButton_1 = new JButton("Seleccionar Carpeta");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectFolder();
			}
		});
		btnNewButton_1.setBounds(5, 36, 168, 23);
		primerPanel.add(btnNewButton_1);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(5, 277, 399, 185);
		primerPanel.add(scrollPane);
		
		directoryTree = new JTree();
		scrollPane.setViewportView(directoryTree);
		
		userSearchInput = new JTextField();
		userSearchInput.setBounds(6, 89, 303, 28);
		primerPanel.add(userSearchInput);
		userSearchInput.setColumns(10);
		
		JButton btnNewButton_2 = new JButton("Buscar");
		btnNewButton_2.setBounds(316, 92, 89, 23);
		primerPanel.add(btnNewButton_2);
		
		JLabel lblNewLabel = new JLabel("Busca en el contenido");
		lblNewLabel.setBounds(16, 72, 157, 14);
		primerPanel.add(lblNewLabel);
		
		JLabel lblPrimeroCargaUn = new JLabel("Primero carga un directorio");
		lblPrimeroCargaUn.setBounds(15, 16, 185, 14);
		primerPanel.add(lblPrimeroCargaUn);
		
		userReplaceInputFrom = new JTextField();
		userReplaceInputFrom.setBounds(33, 174, 373, 23);
		primerPanel.add(userReplaceInputFrom);
		userReplaceInputFrom.setColumns(10);
		
		userReplaceInputTo = new JTextField();
		userReplaceInputTo.setBounds(33, 208, 373, 23);
		primerPanel.add(userReplaceInputTo);
		userReplaceInputTo.setColumns(10);
		
		JButton btnNewButton_3 = new JButton("Remplazar");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				replaceWords();
			}
		});
		btnNewButton_3.setBounds(287, 242, 119, 23);
		primerPanel.add(btnNewButton_3);
		
		JLabel lblReemplazar = new JLabel("Reemplazar");
		lblReemplazar.setBounds(16, 153, 124, 14);
		primerPanel.add(lblReemplazar);
		
		JLabel lblDe = new JLabel("DE:");
		lblDe.setBounds(12, 178, 33, 14);
		primerPanel.add(lblDe);
		
		JLabel lblA = new JLabel("A:");
		lblA.setBounds(12, 212, 33, 14);
		primerPanel.add(lblA);
		
		keySensitive = new JCheckBox("Key Sensitive");
		keySensitive.setBounds(6, 119, 134, 23);
		
		primerPanel.add(keySensitive);
		
		keyAccent = new JCheckBox("Acentos");
		keyAccent.setBounds(147, 119, 134, 23);
		primerPanel.add(keyAccent);
		
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				search();
			}
		});
	}
	
	private File selectedFolder;
	

	/**
	 * Función para seleccionar la carpeta con una UI selectora
	 */
	private void selectFolder() {
		JFileChooser userFolder = new JFileChooser();
		userFolder.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int response = userFolder.showOpenDialog(this);
		if (response == JFileChooser.APPROVE_OPTION) {
			selectedFolder = userFolder.getSelectedFile();
			getAllFiles();
		}
	}
	

	/**
	 * Función para crear un árbol con todos los archivos y carpetas en la carpeta seleccionada
	 */
	private void getAllFiles() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(selectedFolder.getName());
		createFileTree(selectedFolder, root);
		DefaultTreeModel treeModel = new DefaultTreeModel(root);
		directoryTree.setModel(treeModel);
		
		openAllTree(directoryTree);
	}
	
	/**
	 * Función para crear el arbol de los subdirectorios y los archivos de los subdirectorios
	 * @param folder Carpeta a analizar
	 * @param node Nodo a unir
	 */
	private void createFileTree(File folder, DefaultMutableTreeNode node) {
		File[] files = folder.listFiles();
		if (files != null) {
			for (File file : files) {
				DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(formatName(file));
				node.add(childNode);
				if (file.isDirectory()) {
					createFileTree(file, childNode);
				}
			}
		}
	}
	
	/**
	 * Función para formatear el nombre del arhivo decha, hora y peso
	 * @param tempFile Archivo para obtener los datos necesarios
	 * @return String Resultado del nombre compuesto
	 */
	private String formatName(File tempFile) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
		String lastMod = sdf.format(tempFile.lastModified());
		
		String fileSize = String.format("%.1f KB", (double) tempFile.length() / 1024);
		
		return tempFile.getName() + " (" + fileSize + " - " + lastMod + ")"; 
	} 
	
	/**
	 * Función para buscar dento de la carpeta y subdirectorios seleccionados
	 */
	private void search() {
		if (selectedFolder != null) {
			DefaultMutableTreeNode root = new DefaultMutableTreeNode(selectedFolder.getName());
			createFileTreeCounter(selectedFolder, root);
			DefaultTreeModel treeModel = new DefaultTreeModel(root);
			directoryTree.setModel(treeModel);
			
			openAllTree(directoryTree);
		} else {
			JOptionPane.showMessageDialog(this, "Debes de seleccionar una carpeta primero");
		}
	}
	
	/**
	 * Función para crear el arbol de la busqueda para añadir las coincidencias
	 * @param folder Carpeta que analizar
	 * @param node Nodo a unir
	 */
	private void createFileTreeCounter(File folder, DefaultMutableTreeNode node) {
		File[] files = folder.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(file.getName());
					node.add(childNode);
					createFileTreeCounter(file, childNode);
				} else {
					if (isReadableFile(file)) {
						int counter = searchFile(file);
						DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(file.getName() + " (" + counter + " coincidencias)");
						node.add(childNode);
					} else {
						DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(file.getName() + " (0 coincidencias)");
						node.add(childNode);
					}
				}				
			}
		}
	}
	
	/**
	 * Función para saber si un archivo es leíble o no
	 * @return boolean Devuelve si el archivo es txt o java
	 */
	private boolean isReadableFile (File file) {
		return file.getName().endsWith(".txt") || file.getName().endsWith(".java");
	}
	
	/**
	 * Función para porcesar los archivos leíbles y poder ver cuantas repeticiones tiene
	 * @param file Archivo que queremos analizar
	 * @return integer Resultado encontrado de repeticiones
	 */
	private int searchFile (File file) {
		int count = 0;
		try {
			List<String> lines = Files.readAllLines((Path.of(file.getPath())));
			
			for (String line : lines) {
				String finalLine = line;
				
				if (!keyAccent.isSelected()) {
	                finalLine = removeAccents(line);
	            }
				
				if (!keySensitive.isSelected()) {
					if (finalLine.toLowerCase().contains(userSearchInput.getText().toLowerCase())) {
						count++;
					}
				} else {
					if (finalLine.contains(userSearchInput.getText())) {
						count++;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return count;
	}
	
	/**
	 * Función para substituir palabras en los archivos editables
	 */
	private void replaceWords() {
		if (selectedFolder != null) {
			replaceWordsDirectory(selectedFolder);
		} else {
			JOptionPane.showMessageDialog(this, "Debes de seleccionar una carpeta primero");
		}
	}
	
	/**
	 * Función para iterar en los archivos de una carpeta 
	 * @param folder Carpeta a procesar
	 */
	private void replaceWordsDirectory(File folder) {
		File[] files = folder.listFiles();
		
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					replaceWordsDirectory(file);
				} else if (file.getName().endsWith(".txt")) {
					processFile(file);
				}
			}
		}
		getAllFiles();
	}
	
	/**
	 * Función para porocesar los arhivos leíbles y editables para substituir palabras
	 * @param file Archivo a procesar
	 */
	private void processFile(File file) {
		 try {
            List<String> lines = Files.readAllLines(Path.of(file.getPath()));
            StringBuilder modifiedContent = new StringBuilder();

            for (String line : lines) {
                String modLine = line.replace(userReplaceInputFrom.getText(), userReplaceInputTo.getText());
                modifiedContent.append(modLine).append("\n");
            }

            File modFile = new File(file.getParent(), "MOD_" + file.getName());
            FileWriter fileWriter = new FileWriter(modFile);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            
            writer.write(modifiedContent.toString());
            
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * Función para eliminar los accentos de una palabra
	 * @param text Texto a pocesar
	 * @return String Texo procesado
	 */
	private String removeAccents (String text) {
		String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
	    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
	    return pattern.matcher(normalized).replaceAll("");
	}
	
	/**
	 * Función para abrir todos los nodos del JTree
	 * @param tree Árbol a abrir los nodos
	 */
	private void openAllTree(JTree tree) {
	    int row = 0;
	    while (row < tree.getRowCount()) {
	        tree.expandRow(row);
	        row++;
	    }
	}
 }
