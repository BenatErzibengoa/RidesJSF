package eredua.domain;

import java.io.Serializable;
import javax.persistence.Entity;

@Entity
public class Traveller extends Profile implements Serializable {

	private static final long serialVersionUID = 1L;

	public Traveller() {
		super();
	}

	public Traveller(String email, String name, String password) {
		super(email, name, password);
	}
	
}