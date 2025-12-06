package eredua.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import businessLogic.BLFacade;
import eredua.domain.Driver;
import exceptions.RideAlreadyExistException;
import exceptions.RideMustBeLaterThanTodayException;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("createride")
@SessionScoped
public class CreateRideBean implements Serializable {

	private String departCity;
	private String arrivalCity;
	private int numSeats;
	private float price;	
	private Date rideDate;
	
	private Driver driver;
	
	 @Inject 
	    private UserBean user;
	
	public CreateRideBean() {
        // Beharrezkoa
    }

	public String createRide() {
        try {
            BLFacade facade = FacadeBean.getBusinessLogic();
            facade.createRide(departCity, arrivalCity, rideDate, numSeats, price, user.getEmail());

            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Ride created successfully: " + departCity + " -> " + arrivalCity));
            
            System.out.println("Orain, horrela geratzen dira departcities: " + facade.getDepartCities());
            this.departCity = "";
            this.arrivalCity = "";
            this.numSeats = 0;
            this.price = 0;
            this.rideDate = null;

            return ""; 

        } catch (RideMustBeLaterThanTodayException e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Date must be later than today"));
            return null;
        } catch (RideAlreadyExistException e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ride already exists."));
            return null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unexpected error: " + e.getMessage()));
            return null;
        }
    }
	

	public Date getMinDate() {
	    Calendar c = Calendar.getInstance();
	    c.setTime(new Date());
	    c.add(Calendar.DATE, 1); 
	    return c.getTime(); // Biharko eguna itzuli
	}
	
	public String getDepartCity() {
		return departCity;
	}

	public void setDepartCity(String departCity) {
		this.departCity = departCity;
	}

	public String getArrivalCity() {
		return arrivalCity;
	}

	public void setArrivalCity(String arrivalCity) {
		this.arrivalCity = arrivalCity;
	}

	public int getNumSeats() {
		return numSeats;
	}

	public void setNumSeats(int numSeats) {
		this.numSeats = numSeats;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}
	
	public Date getRideDate() {
        return rideDate;
    }

    public void setRideDate(Date rideDate) {
        this.rideDate = rideDate;
    }

	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}
	
	
	
}
