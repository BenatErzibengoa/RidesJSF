package eredua.bean;

import java.io.Serializable;

import businessLogic.BLFacade;
import eredua.domain.Profile;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpSession;


@Named("login") 
@ViewScoped
public class LoginBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String email; 
    private String password;


    public String login() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            BLFacade facade = FacadeBean.getBusinessLogic();

            Profile profile = facade.login(email, password);

            if (profile != null) {
                HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
                session.setAttribute("user", profile); 
                return "main?faces-redirect=true"; 
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Incorrect credentials"));
                this.password = null; 
                return null;
            }
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", "Server error"));
            e.printStackTrace();
            return null;
        }
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}