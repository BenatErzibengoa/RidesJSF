package eredua.bean;

import java.io.Serializable;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped; // Importante para formularios AJAX
import jakarta.inject.Named;

import businessLogic.BLFacade;
import eredua.domain.Profile;

@Named("register")
@ViewScoped
public class RegisterBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String username;
	private String password;
	private String userType; 
	private String name;
	private String surname;
	private String email;
	private String phone;

	public RegisterBean() {
	}


	public String register() {
		try {
			BLFacade facade = FacadeBean.getBusinessLogic();


			Profile p = facade.register(this.email, this.name, this.password, userType);

			if (p != null) {
				return "login?faces-redirect=true";
			} else {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Errorea", 
								"Email-a jada sisteman erregistratuta dago"));
				return null; 
			}

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Errorea", 
							"Zerbitzariko errorea"));
			return null;
		}
	}

	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
}