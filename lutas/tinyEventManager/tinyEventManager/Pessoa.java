package tinyEventManager;

import java.io.Serializable;

public class Pessoa implements Serializable{
	private String firstName;
	private String lastName;

	private static final long serialVersionUID = 1L;
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Pessoa() {
		
	}
	
	public Pessoa(String firstName, String lastName) {
		setFirstName(firstName);
		setLastName(lastName);
	}
	
	@Override
	public String toString() {
		return getFirstName()+ " " + getLastName();
	}
}
