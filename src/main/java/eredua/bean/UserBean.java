package eredua.bean;

import java.io.Serializable;
import eredua.domain.Profile;
import eredua.domain.Traveller;
import eredua.domain.Driver;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

@Named("user")
@SessionScoped
public class UserBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Profile user;

    public boolean isLoggedIn() {
        return user != null;
    }

    public boolean isDriver() {
        return user != null && (user instanceof Driver);
    }

    public boolean isTraveler() {
        return user != null && (user instanceof Traveller);
    }
    
    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        this.user = null;
        return "main?faces-redirect=true";
    }

    public Profile getUser() {
        return user;
    }

    public void setUser(Profile user) {
        this.user = user;
    }
}