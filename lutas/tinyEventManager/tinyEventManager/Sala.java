package tinyEventManager;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class Sala implements Serializable{
	private String name;
	private int lotation;
	private List<Pessoa> pessoas;

	private static final long serialVersionUID = 1L;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getLotation() {
		return lotation;
	}
	public void setLotation(int lotation) {
		this.lotation = lotation;
	}

	public List<Pessoa> getPessoas() {
		return pessoas;
	}
	public void setPessoas(List<Pessoa> pessoas) {
		this.pessoas = pessoas;
	}
	
	public Sala(){
		
	}
	
	public Sala(String name, int lotation, List<Pessoa> pessoas){
		setName(name);
		setLotation(lotation);
		setPessoas(pessoas);
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
