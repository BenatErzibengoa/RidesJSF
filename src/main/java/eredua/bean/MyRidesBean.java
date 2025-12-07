package eredua.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.primefaces.PrimeFaces;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import businessLogic.BLFacade;
import eredua.domain.Ride;

@Named("myrides")
@ViewScoped
public class MyRidesBean implements Serializable {

    private static final long serialVersionUID = 1L;

    //Rides erakusteko
    private List<Ride> futureRides;
    private List<Ride> pastRides;
    
    //Balorazioak
    private Ride rideToRate;
    private int ratingStars;
    private String ratingDescription;

    @Inject
    private UserBean user;

    public MyRidesBean() {
        futureRides = new ArrayList<>();
        pastRides = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        if (user.isLoggedIn()) {
            loadRides();
        }
    }

    private void loadRides() {
        BLFacade facade = FacadeBean.getBusinessLogic();
        List<Ride> allRides = new ArrayList<>();
        String email = user.getEmail();

        if (user.isDriver()) {
            allRides = facade.getRidesByDriver(email);
        } else if (user.isTraveler()) {
            allRides = facade.getRidesByTraveller(email);
        }

        Date today = new Date();
        futureRides.clear();
        pastRides.clear();

        for (Ride r : allRides) {
            if (r.getDate().after(today)) {
                futureRides.add(r);
            } else {
                pastRides.add(r);
            }
        }
    }
    
    public void openRateDialog(Ride ride) {
        this.rideToRate = ride;
        this.ratingStars = 0; 
        this.ratingDescription = ""; 
    }

    public void submitRating() {
        if (rideToRate != null && user.isLoggedIn()) {
            BLFacade facade = FacadeBean.getBusinessLogic();
            try {
                facade.storeRating(rideToRate, user.getEmail(), ratingStars, ratingDescription);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Rating saved successfully!"));
                PrimeFaces.current().executeScript("PF('rateDialog').hide();");
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not save rating."));
                e.printStackTrace();
            }
        }
    }
    
    
    public List<Ride> getFutureRides() {
        return futureRides;
    }

    public List<Ride> getPastRides() {
        return pastRides;
    }
    
    public Ride getRideToRate() { return rideToRate; }
    public void setRideToRate(Ride rideToRate) { this.rideToRate = rideToRate; }
    public int getRatingStars() { return ratingStars; }
    public void setRatingStars(int ratingStars) { this.ratingStars = ratingStars; }
    public String getRatingDescription() { return ratingDescription; }
    public void setRatingDescription(String ratingDescription) { this.ratingDescription = ratingDescription; }
    
}