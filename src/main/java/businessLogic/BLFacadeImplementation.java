package businessLogic;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;


import dataAccess.HibernateDataAccess;
import eredua.domain.Profile;
import eredua.domain.Rating;
import eredua.domain.Ride;
import exceptions.RideMustBeLaterThanTodayException;
import exceptions.RideAlreadyExistException;

/*
	It implements the business logic as a web service.
*/
public class BLFacadeImplementation  implements BLFacade {
	HibernateDataAccess dbManager;

	public BLFacadeImplementation()  {		
		System.out.println("Creating BLFacadeImplementation instance");
		    dbManager=new HibernateDataAccess();
		    dbManager.initializeDB();
	}
	
    public BLFacadeImplementation(HibernateDataAccess da)  {
		System.out.println("Creating BLFacadeImplementation instance with HibernateDataAccess parameter");
		dbManager=da;	
		System.out.println("Initialazing DB with data...");
	    dbManager.initializeDB();
	}
    
    public List<String> getDepartCities(){		
		List<String> departLocations=dbManager.getDepartCities();				
		return departLocations;
    	
    }

    public List<String> getDestinationCities(String from){		
		 List<String> targetCities=dbManager.getArrivalCities(from);				
		return targetCities;
	}
	 
   public Ride createRide( String from, String to, Date date, int nPlaces, float price, String driverEmail ) throws RideMustBeLaterThanTodayException, RideAlreadyExistException{
	   	Ride ride=dbManager.createRide(from, to, date, nPlaces, price, driverEmail);		
		return ride;
   };
	
	public List<Ride> getRides(String from, String to, Date date){
		List<Ride>  rides=dbManager.getRides(from, to, date);
		return rides;
	}

	public List<Date> getThisMonthDatesWithRides(String from, String to, Date date){
		List<Date>  dates=dbManager.getThisMonthDatesWithRides(from, to, date);
		return dates;
	}
	
    public Profile register(String email, String name, String password, String type) {
    	Profile p = dbManager.register(email, name, password, type);
    	return p;
    }
    
    public Profile login(String email, String password) {
    	Profile p = dbManager.login(email, password);
    	return p;
    }
    
    public boolean erreserbatu(Ride ride, String email) {
    	return dbManager.erreserbatu(ride, email);
    }
    
    public List<Ride> getRidesByDriver(String driverEmail) {
    	List<Ride> rs = dbManager.getRidesByDriver(driverEmail);
        return rs;
    }

    public List<Ride> getRidesByTraveller(String travellerEmail) {
    	List<Ride> rs = dbManager.getRidesByTraveller(travellerEmail);
        return rs;
    }

    public Rating storeRating(Ride ride, String travellerEmail, int stars, String description) {
    	Rating r = dbManager.storeRating(ride, travellerEmail, stars, description);
        return r;
    }
	 
	 public void initializeBD(){
		dbManager.initializeDB();
	}

}


