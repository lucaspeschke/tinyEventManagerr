package tinyEventManager;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;


class Gui {
	private static class Data implements Serializable{
		public List<Pessoa> pessoas = new ArrayList<>();
		public List<Sala> salasEvento = new ArrayList<>();
		public List<Sala> salasCafe = new ArrayList<>();
		
		public Data(){
			
		}
		
		public Data(List<Pessoa> pessoas, List<Sala> salasEvento, List<Sala> salasCafe){
			this.pessoas = pessoas;
			this.salasEvento = salasEvento;
			this.salasCafe = salasCafe;
		}
	}
	
	private static Data data = new Data();
	
	private static final String filepath="data.db";
	
	private static void importData(JFrame frame){
		try {
			FileInputStream fileIn = new FileInputStream(filepath);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
 
            Object obj = objectIn.readObject();
 
            objectIn.close();
			data = (Data) obj;
        } catch (Exception ex) {
        	JOptionPane.showMessageDialog(frame, "Erro ao ler os dados:\n"+ ex.toString());
        }
	}
	
	private static void saveData(JFrame frame){
		
		try {
            FileOutputStream fileOut = new FileOutputStream(filepath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(data);
            objectOut.close();
        } catch (Exception ex) {
        	JOptionPane.showMessageDialog(frame, "Erro ao salvar os dados:\n"+ ex.toString());
        }
	}
	
	private static void eraseData(JFrame frame){
		try {
            FileOutputStream fileOut = new FileOutputStream(filepath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(new Data(new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
            objectOut.close();
            importData(frame);
        } catch (Exception ex) {
        	JOptionPane.showMessageDialog(frame, "Erro ao apagar os dados:\n"+ ex.toString());
        }
	}
	
	private static Boolean verificarLotacao(JFrame frame){
		if (data.salasEvento.isEmpty()) {
			JOptionPane.showMessageDialog(frame, "Não há sala de evento cadastrada. \n Por favor, cadastre antes de novos usuários.");
			frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			return false;
		}else if (data.salasCafe.isEmpty()) {
			JOptionPane.showMessageDialog(frame, "Não há sala de café cadastrada. \n Por favor, cadastre antes de novos usuários.");
			frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			return false;
		}
        
        Boolean flag = false;
		for (Sala sala : data.salasEvento) {
			if (sala.getPessoas().size() < sala.getLotation()) {
				flag = true;
				break;
			}
		}
		if (!flag) {
			JOptionPane.showMessageDialog(frame, "Todas as salas de evento estão lotadas!");
			frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			return false;
		}
		
		flag = false;
		for (Sala sala : data.salasCafe) {
			if (sala.getPessoas().size() < sala.getLotation()) {
				flag = true;
				break;
			}
		}
		if (!flag) {
			JOptionPane.showMessageDialog(frame, "Todas as salas de cafe estão lotadas!");
			frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			return false;
		}
		
		return true;
	}
	
	public static void pessoaCadastroGUI(){		
        JFrame pessoaCadastro = new JFrame("Tiny Event Manager");
        pessoaCadastro.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        pessoaCadastro.setSize(400, 200);
        pessoaCadastro.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width  - pessoaCadastro.getSize().width) / 2, (Toolkit.getDefaultToolkit().getScreenSize().height - pessoaCadastro.getSize().height) / 2);
        pessoaCadastro.setVisible(true);
        
        JPanel panel = new JPanel();
        
        JLabel label = new JLabel("Nome");
        JTextField tf = new JTextField(20);
        panel.add(label);
        panel.add(tf);
        
        
        JPanel panel2 = new JPanel();
        JLabel label2 = new JLabel("Sobrenome");
        JTextField tf2 = new JTextField(20); 
        panel2.add(label2); 
        panel2.add(tf2);
        
        JPanel buttonsPanel = new JPanel();
        JButton cadButton = new JButton("Cadastrar");
        cadButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!verificarLotacao(pessoaCadastro)) return;
				if (tf.getText().isEmpty() || tf2.getText().isEmpty()) {
					JOptionPane.showMessageDialog(pessoaCadastro, "Erro: Campos nome e sobrenome são obrigatórios.");
				}else {				
					Pessoa pessoa = new Pessoa(tf.getText(), tf2.getText());
					
					int salaMenorSize = Integer.MAX_VALUE, salaMaiorSize = Integer.MIN_VALUE;
					Sala salaMenor = data.salasEvento.get(0);
					for (Sala sala : data.salasEvento) {
						if (sala.getPessoas().size() > salaMaiorSize) {
							salaMaiorSize = sala.getPessoas().size();
						}
						if (sala.getPessoas().size() < salaMenorSize) {
							salaMenorSize = sala.getPessoas().size();
						}				
						if (salaMenor.getLotation() <= salaMenor.getPessoas().size() || (sala.getLotation() > sala.getPessoas().size() && sala.getPessoas().size() < salaMenor.getPessoas().size())) {
							salaMenor = sala;
						}
					}
					
					if (salaMaiorSize - salaMenorSize == 1 && salaMenor.getPessoas().size() == salaMenorSize) {
						salaMenor.getPessoas().add(pessoa);
						
						Sala salaMenorCafe = data.salasCafe.get(0);
						for (Sala sala : data.salasCafe) {
							if (salaMenorCafe.getLotation() <= salaMenorCafe.getPessoas().size() || (sala.getLotation() > sala.getPessoas().size() && sala.getPessoas().size() < salaMenorCafe.getPessoas().size())) {
								salaMenorCafe = sala;
							}
						}
	
						salaMenorCafe.getPessoas().add(pessoa);
	
						data.pessoas.add(pessoa);
						JOptionPane.showMessageDialog(pessoaCadastro, "Pessoa cadastrada com sucesso!");
						saveData(pessoaCadastro);	
					} else{
						JOptionPane.showMessageDialog(pessoaCadastro, "Não pode-se cadastrar novas pessoas para que não se tenham salas com lotações muito diferentes.");
						pessoaCadastro.dispatchEvent(new WindowEvent(pessoaCadastro, WindowEvent.WINDOW_CLOSING));
						return;
					}
				}
			}
		});
        JButton cancelButton = new JButton("Cancelar");
    	cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				pessoaCadastro.dispatchEvent(new WindowEvent(pessoaCadastro, WindowEvent.WINDOW_CLOSING));			
			}
		});
        buttonsPanel.add(cadButton);
        buttonsPanel.add(cancelButton);

        pessoaCadastro.getContentPane().add(BorderLayout.NORTH, panel);
        pessoaCadastro.getContentPane().add(BorderLayout.CENTER, panel2);
        pessoaCadastro.getContentPane().add(BorderLayout.SOUTH, buttonsPanel);
        
        if (!verificarLotacao(pessoaCadastro)) return;
	}

	public static void salaCadastroGUI(Boolean isCoffe){
		
	    JFrame salaCadastro = new JFrame("Tiny Event Manager");
	    salaCadastro.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	    salaCadastro.setSize(400, 200);
	    salaCadastro.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width  - salaCadastro.getSize().width) / 2, (Toolkit.getDefaultToolkit().getScreenSize().height - salaCadastro.getSize().height) / 2);
	    salaCadastro.setVisible(true);
	    
	    JPanel panel = new JPanel();
	    
	    JLabel label = new JLabel("Nome");
	    JTextField tf = new JTextField(20);
	    panel.add(label);
	    panel.add(tf);
	    
	    
	    JPanel panel2 = new JPanel();
	    JLabel label2 = new JLabel("Lotação");
	    JTextField tf2 = new JTextField(6);
	    panel2.add(label2); 
	    panel2.add(tf2);
	    
	    JPanel buttonsPanel = new JPanel();
	    JButton cadButton = new JButton("Cadastrar");
	    cadButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (tf.getText().isEmpty() || tf2.getText().isEmpty()) {
					JOptionPane.showMessageDialog(salaCadastro, "Erro: Campos nome e lotação são obrigatórios.");
				}else {
					Sala sala;
					try {
						sala = new Sala(tf.getText(), Integer.parseInt(tf2.getText()), new ArrayList<Pessoa>());
						if (isCoffe){
							data.salasCafe.add(sala);
							
						}else {
							data.salasEvento.add(sala);
						}
					} catch (Exception ee) {
						JOptionPane.showMessageDialog(salaCadastro, "Campo Lotação deve ser um valor numérico inteiro.");
					}
					JOptionPane.showMessageDialog(salaCadastro, "Sala cadastrada com sucesso!");
					saveData(salaCadastro);
				}
			}
		});
	    JButton cancelButton = new JButton("Sair");
	    cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				salaCadastro.dispatchEvent(new WindowEvent(salaCadastro, WindowEvent.WINDOW_CLOSING));			
			}
		});
	    buttonsPanel.add(cadButton);
	    buttonsPanel.add(cancelButton);
	
	    salaCadastro.getContentPane().add(BorderLayout.NORTH, panel);
	    salaCadastro.getContentPane().add(BorderLayout.CENTER, panel2);
	    salaCadastro.getContentPane().add(BorderLayout.SOUTH, buttonsPanel);
	    
	    if (isCoffe && data.salasCafe.size() > 1){
	    	JOptionPane.showMessageDialog(salaCadastro, "Limite máximo de duas salas de café.");
	    	salaCadastro.dispatchEvent(new WindowEvent(salaCadastro, WindowEvent.WINDOW_CLOSING));
			return;
	    }
	}
	
	public static void salaConsultaGUI(Boolean isCoffe){
        JFrame pessoaConsulta = new JFrame("Tiny Event Manager");
        pessoaConsulta.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        pessoaConsulta.setSize(400, 200);
        pessoaConsulta.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width  - pessoaConsulta.getSize().width) / 2, (Toolkit.getDefaultToolkit().getScreenSize().height - pessoaConsulta.getSize().height) / 2);
        pessoaConsulta.setVisible(true);

        JPanel centerPanel = new JPanel();
        JScrollPane scroll = new JScrollPane(centerPanel);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        JPanel gridView = new JPanel(new GridLayout(0, 1, 10, 20));
        
        for (Sala sala : ((isCoffe) ? data.salasCafe : data.salasEvento)) {
            JLabel label = new JLabel(sala.toString()+" - "+"Lotação Máxima/Lotação = "+sala.getLotation()+"/"+sala.getPessoas().size());
            gridView.add(label);
            
            if (isCoffe){
            	for (Pessoa pessoa : sala.getPessoas()) {
                	JLabel pessoaLabel = new JLabel(pessoa.getFirstName()+" "+pessoa.getLastName());
                	gridView.add(pessoaLabel);
    			}
            	gridView.add(new JLabel("========"));
            	continue;
            }
            
            gridView.add(new JLabel("-- Pessoas no primeiro turno --"));
            for (Pessoa pessoa : sala.getPessoas()) {
            	JLabel pessoaLabel = new JLabel(pessoa.getFirstName()+" "+pessoa.getLastName());
            	gridView.add(pessoaLabel);
			}
            gridView.add(new JLabel("-- Pessoas no segundo turno --"));
            int metadeNaSala = data.pessoas.size()/data.salasEvento.size();
        	int indiceSala = data.salasEvento.indexOf(sala);
        	
        	Sala salaAnterior = data.salasEvento.get((indiceSala==0)? data.salasEvento.size()-1 : indiceSala-1);
        	for (int i = 0; i < metadeNaSala; i++) {
        		JLabel pessoaLabel = new JLabel(salaAnterior.getPessoas().get(i).getFirstName()+" "+salaAnterior.getPessoas().get(i).getLastName());
            	gridView.add(pessoaLabel);
			}
            
            for (int i = metadeNaSala; i < sala.getPessoas().size(); i++) {            	
            	JLabel pessoaLabel = new JLabel(sala.getPessoas().get(i).getFirstName()+" "+sala.getPessoas().get(i).getLastName());
            	gridView.add(pessoaLabel);
    		}
            
            gridView.add(new JLabel("========="));
		}
        
        centerPanel.add(gridView);
        
        
        pessoaConsulta.getContentPane().add(BorderLayout.CENTER, scroll);
	}

	
	public static void pessoaConsultaGUI(){
        JFrame pessoaConsulta = new JFrame("Tiny Event Manager");
        pessoaConsulta.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        pessoaConsulta.setSize(400, 200);
        pessoaConsulta.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width  - pessoaConsulta.getSize().width) / 2, (Toolkit.getDefaultToolkit().getScreenSize().height - pessoaConsulta.getSize().height) / 2);
        pessoaConsulta.setVisible(true);

        JPanel centerPanel = new JPanel();
        JScrollPane scroll = new JScrollPane(centerPanel);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        JPanel gridView = new JPanel(new GridLayout(0, 1, 10, 20));
        
    	for (int i = 0; i < data.pessoas.size(); i++) {
        	Sala salaEvento1 = null;
        	int indicePessoa = -1;
        	for (Sala sala : data.salasEvento) {
        		indicePessoa = sala.getPessoas().indexOf(data.pessoas.get(i));
        		if (indicePessoa!=-1){
        			salaEvento1 = sala; 
        			break;
        		}
			}
        	if (salaEvento1 == null) {
        		JOptionPane.showMessageDialog(pessoaConsulta, "Não há sala de evento cadastrada para o usuário "+data.pessoas.get(i).toString()+".");
    			pessoaConsulta.dispatchEvent(new WindowEvent(pessoaConsulta, WindowEvent.WINDOW_CLOSING));
    			return;
        	}
        	
        	int metadeNaSala = data.pessoas.size()/data.salasEvento.size();
        	
        	Sala segundaSala;
        	if(indicePessoa < metadeNaSala){
        		int j = (data.salasEvento.indexOf(salaEvento1)+1 >= data.salasEvento.size()) ? 0 : data.salasEvento.indexOf(salaEvento1)+1;
        		segundaSala = data.salasEvento.get(j);
        	}else {
        		segundaSala = salaEvento1;
    		}
            JLabel label = new JLabel(data.pessoas.get(i).toString() + " --- Primeiro turno: Sala "+salaEvento1.toString()+"; Segundo turno: Sala "+segundaSala.toString());
            gridView.add(label);
		}
        
        centerPanel.add(gridView);
        
        
        pessoaConsulta.getContentPane().add(BorderLayout.CENTER, scroll);
	}
	
    public static void main(String args[]) {

        JFrame frame = new JFrame("Tiny Event Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width  - frame.getSize().width) / 2, (Toolkit.getDefaultToolkit().getScreenSize().height - frame.getSize().height) / 2);
        
        JMenuBar mb = new JMenuBar();
        JMenu m1 = new JMenu("Cadastro");
        JMenu m2 = new JMenu("Consulta");
        mb.add(m1);
        mb.add(m2);
        JMenuItem m11 = new JMenuItem("Cadastro de pessoas");
        m11.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
		        pessoaCadastroGUI();
			}
		});
        JMenuItem m12 = new JMenuItem("Cadastro de salas de evento");
    	m12.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
		        salaCadastroGUI(false);
			}
		});
        JMenuItem m13 = new JMenuItem("Cadastro de espaço de café");
        m13.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
		        salaCadastroGUI(true);
			}
		});
        m1.add(m11);
        m1.add(m12);
        m1.add(m13);

        JMenuItem m21 = new JMenuItem("Consulta de pessoas");
        m21.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
		        pessoaConsultaGUI();
			}
		});
        JMenuItem m22 = new JMenuItem("Consulta de salas de evento");
        m22.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				salaConsultaGUI(false);
			}
		});
        JMenuItem m23 = new JMenuItem("Consulta de espaço de café");
    	m23.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				salaConsultaGUI(true);
			}
		});
        m2.add(m21); 
        m2.add(m22);
        m2.add(m23);
        
        JPanel panel = new JPanel(); 
        JButton send = new JButton("Apagar dados");
        send.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {	
				int response = JOptionPane.showConfirmDialog(null, "Todos os dados serão apagados.\n Deseja continuar?", "Confirm",
	                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
	            if (response == JOptionPane.YES_OPTION) {
	              eraseData(frame);
	            }
			}
		});
        
        panel.add(send);

        JTextArea ta = new JTextArea();

        frame.getContentPane().add(BorderLayout.SOUTH, panel);
        frame.getContentPane().add(BorderLayout.NORTH, mb);
        frame.getContentPane().add(BorderLayout.CENTER, ta);
        frame.setVisible(true);
        
        importData(frame);
    }
}