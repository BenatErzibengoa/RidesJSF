package eredua.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import businessLogic.BLFacade;
import eredua.domain.Ride;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

@Named("preziobean")
@SessionScoped
public class SearchByPriceBean implements Serializable {
	
	private static final long serialVersionUID = 1L; 
	
	private float selectedPrice;
    private List<Ride> foundRides;


	
    public SearchByPriceBean() {
    	foundRides = new ArrayList<>();
    }
    

    
    public void loadRides() {
        BLFacade facade = FacadeBean.getBusinessLogic();
        this.foundRides = new ArrayList<Ride>();
        List<Ride> seachRides = facade.getRidesByPrice(selectedPrice);
        if (seachRides.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage("Rides not found for these credentials"));
            	return;
        }
        System.out.println("Rides received by bean: " + seachRides);
        foundRides = seachRides;
        System.out.println("Rides saved at bean: " + foundRides);

    }
    
    public List<Ride> getFoundRides(){return foundRides;}
    
    public float getSelectedPrice() { return selectedPrice; }
    public void setSelectedPrice(float selectedPrice) { this.selectedPrice=selectedPrice; }
    
    
    
    
}
