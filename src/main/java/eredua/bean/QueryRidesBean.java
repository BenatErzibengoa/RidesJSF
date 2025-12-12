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
import eredua.domain.Driver;
import eredua.domain.Profile;
import eredua.domain.Rating;
import eredua.domain.Ride;
import eredua.domain.Traveller;


@Named("queryrides")
@ViewScoped  //Datuak bizirik soilik uneko pantailan
public class QueryRidesBean implements Serializable {

	//Rides bilaketa
    private String selectedOrigin;
    private String selectedDestination;
    private Date selectedDate;

    private List<String> departCities;
    private List<String> destinationCities;
    private List<Ride> foundRides;
    
    //Balorazioak
    private Driver selectedDriver;
    private List<Rating> driverRatings;

    private BLFacade facadeBL;
    
    //Mapa
    private Ride selectedRideForMap;

    
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
            List<Ride> rides = facadeBL.getRides(selectedOrigin, selectedDestination, selectedDate);
            if(user.isLoggedIn() && user.getUser() != null && user.isTraveler()) {
            	String currentUserEmail = user.getEmail();
            }
        	foundRides = rides;

            if (rides.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage("Rides not found for these credentials"));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Please select origin, destination and date.", null));
        }
        return null; 
    }
    
    public boolean isRideBookedByCurrentUser(Ride ride) {
        if (user == null || !user.isLoggedIn() || user.getUser() == null) {
            return false;
        }
        String currentUserEmail = user.getEmail();
        if (ride.getTravellers() != null) {
            for (Traveller t : ride.getTravellers()) {
                if (t.getEmail().equals(currentUserEmail)) {
                    return true; 
                }
            }
        }
        return false;
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
        	context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
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
	
	public void loadDriverInfo(Driver driver) {
        this.selectedDriver = driver;
        BLFacade facade = FacadeBean.getBusinessLogic();
        this.driverRatings = facade.getRatingsByDriver(driver.getEmail());
    }
	
	public double getAverageRating() {
	    if (driverRatings == null || driverRatings.isEmpty()) return 0.0;
	    double sum = 0.0;
	    for (Rating r : driverRatings) {
	        sum += r.getStars();
	    }
	    return Math.round((sum / driverRatings.size()) * 10.0) / 10.0; 
	}

	public String googleMapsUrl() {
	    if (selectedRideForMap == null) return "";
	    String origin = selectedRideForMap.getFrom().replace(" ", "+"); //Zuriuneak '+'-rengatik aldatu. Horrela, URL bat sortzen dugu
	    String destination = selectedRideForMap.getTo().replace(" ", "+");
	    return "https://www.google.com/maps?q=" + origin + "+to+" + destination + "&output=embed";
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
    
    public Driver getSelectedDriver() { return selectedDriver; }
    public void setSelectedDriver(Driver selectedDriver) { this.selectedDriver = selectedDriver; }
    public List<Rating> getDriverRatings() { return driverRatings; }
    public void setDriverRatings(List<Rating> driverRatings) { this.driverRatings = driverRatings;}
    
    public Ride getSelectedRideForMap() { return selectedRideForMap; }
    public void setSelectedRideForMap(Ride r) { this.selectedRideForMap = r; }
    
}