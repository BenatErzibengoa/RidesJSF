package eredua.bean; 
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.primefaces.event.SelectEvent;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
    
import businessLogic.BLFacade;
import eredua.domain.Profile;
import eredua.domain.Ride;


@Named("queryrides")
@ViewScoped  //Datuak bizirik soilik uneko pantailan
public class QueryRidesBean implements Serializable {

    private String selectedOrigin;
    private String selectedDestination;
    private Date selectedDate;

    private List<String> departCities;
    private List<String> destinationCities;
    private List<Ride> foundRides;

    private BLFacade facadeBL;
    
    @Inject 
    private UserBean user;

    public QueryRidesBean() {
    	departCities = new ArrayList<>();
    	destinationCities = new ArrayList<>();
    	foundRides = new ArrayList<>();
    }
    
    @PostConstruct
    public void init() {
        try {
            facadeBL = FacadeBean.getBusinessLogic();
            if (facadeBL != null) {
                departCities = facadeBL.getDepartCities();
            	System.out.println("Depart cities loaded: " + departCities.toString());
            } else {
                System.out.println("Error: FacadeBean returned null. There is no business logic");
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", "Business Logic not initialized"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    public String searchRides() {
        if (selectedOrigin != null && selectedDestination != null && selectedDate != null) {
            
            foundRides = facadeBL.getRides(selectedOrigin, selectedDestination, selectedDate);
            
            if (foundRides.isEmpty()) {
                // Rides ez --> errore mezua
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage("Rides not found for these credentials"));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Please select origin, destination and date.", null));
        }
        
        return null; // Errorea --> ez egin ezer
    }

    public void onOriginChange() {
        if (selectedOrigin != null && !selectedOrigin.isEmpty()) {
            destinationCities = facadeBL.getDestinationCities(selectedOrigin);
        }
        foundRides = null; // Bestela, taula garbitu
    }
    
    public void onDateSelect(SelectEvent<Date> event) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage("Selected date: " + event.getObject().toString()));
    }

	public Date getMinDate() {
	    Calendar c = Calendar.getInstance();
	    c.setTime(new Date());
	    c.add(Calendar.DATE, 1); 
	    return c.getTime(); // Biharko eguna itzuli
	}
	
	
	public void erreserbatu(Ride r) {
        FacesContext context = FacesContext.getCurrentInstance();
        
        if (!user.isLoggedIn() || user.getUser() == null) {
        	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                "Error", "User not logged in. Cannot proceed with booking."));
            return;
        }
        
        Profile loggedInUser = user.getUser();

        try {
            facadeBL = FacadeBean.getBusinessLogic();
            boolean success = facadeBL.erreserbatu(r, loggedInUser.getEmail());
            if (success) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 
                    "Success", "Ride successfully booked!"));
                searchRides(); 
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Error", "Could not book the ride"));
            }
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, 
                "Error", "Server error"));
            e.printStackTrace();
        }
    }


    public String getSelectedOrigin() { return selectedOrigin; }
    public void setSelectedOrigin(String selectedOrigin) { this.selectedOrigin = selectedOrigin; }

    public String getSelectedDestination() { return selectedDestination; }
    public void setSelectedDestination(String selectedDestination) { this.selectedDestination = selectedDestination; }

    public Date getSelectedDate() { return selectedDate; }
    public void setSelectedDate(Date selectedDate) { this.selectedDate = selectedDate; }

    public List<String> getDepartCities() { return departCities; }
    public void setDepartCities(List<String> departCities) { this.departCities = departCities; }

    public List<String> getDestinationCities() { return destinationCities; }
    public void setDestinationCities(List<String> destinationCities) { this.destinationCities = destinationCities; }

    public List<Ride> getFoundRides() { return foundRides; }
    public void setFoundRides(List<Ride> foundRides) { this.foundRides = foundRides; }
}