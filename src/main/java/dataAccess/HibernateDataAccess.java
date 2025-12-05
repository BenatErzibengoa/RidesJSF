package dataAccess;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import configuration.UtilDate;
import eredua.JPAUtil;
import eredua.domain.Driver;
import eredua.domain.Profile;
import eredua.domain.Ride;
import eredua.domain.Traveller;
import eredua.factory.ProfileFactory;
import exceptions.RideAlreadyExistException;
import exceptions.RideMustBeLaterThanTodayException;

public class HibernateDataAccess {
	
    public HibernateDataAccess() {
    	
    }

    public List<String> getDepartCities() {
		EntityManager em = JPAUtil.getEntityManager();
		List<String> result = new ArrayList<String>();
		try {
			em.getTransaction().begin();
			TypedQuery<String> query = em.createQuery("SELECT DISTINCT r.from FROM Ride r ORDER BY r.from", String.class);
			result = query.getResultList();
			em.getTransaction().commit();
		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw e;
		} finally {
			em.close();
		}
		return result;
	}
	

    public List<String> getArrivalCities(String from) {
		EntityManager em = JPAUtil.getEntityManager();
		List<String> result = new ArrayList<String>();
		try {
			em.getTransaction().begin();
			TypedQuery<String> query = em.createQuery("SELECT DISTINCT r.to FROM Ride r WHERE r.from=:from ORDER BY r.to", String.class);
			query.setParameter("from", from);
			result = query.getResultList();
			em.getTransaction().commit();
		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw e;
		} finally {
			em.close();
		}
		return result;
	}
    
    
	 
    public Ride createRide(String from, String to, Date date, int nPlaces, float price, String driverEmail)
			throws RideAlreadyExistException, RideMustBeLaterThanTodayException {
		System.out.println(">> DataAccess: createRide=> from= "+from+" to= "+to+" driver="+driverEmail+" date "+date);
		EntityManager em = JPAUtil.getEntityManager();
		Ride ride = null;
		
		
	        // Option 1: Create a dummy driver for testing
	        Driver gid = new Driver(driverEmail, "Test Driver", "12345678");
	        em.persist(gid);
	        
	        // Option 2: Stop execution if this is strictly logic
	        // db.getTransaction().rollback();
	        // throw new RuntimeException("Driver not found: " + driverEmail);
		
		
		try {
			if (new Date().compareTo(date) > 0) {
				throw new RideMustBeLaterThanTodayException("Ride must be later than today!");
			}
			
			em.getTransaction().begin();
			Driver driver = em.find(Driver.class, driverEmail);
			
			if (driver.doesRideExists(from, to, date)) {
				em.getTransaction().commit(); // Bestela errorea eman eta gero ez da itxiko
				throw new RideAlreadyExistException("Ride already exists!");
			}
			ride = driver.addRide(from, to, date, nPlaces, price);
			em.persist(driver);
			em.getTransaction().commit();

		} catch (RideAlreadyExistException | RideMustBeLaterThanTodayException e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw e;
		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			e.printStackTrace();
			return null;
		} finally {
			em.close();
		}
		return ride;
	}
	

    public List<Ride> getRides(String from, String to, Date date) {
		System.out.println(">> DataAccess: getRides=> from= " + from + " to= " + to + " date " + date);
		EntityManager em = JPAUtil.getEntityManager();
		List<Ride> result = new ArrayList<Ride>();
		try {
			em.getTransaction().begin();
			TypedQuery<Ride> query = em.createQuery("SELECT r FROM Ride r WHERE r.from=:from AND r.to=:to AND r.date=:date", Ride.class);
			query.setParameter("from", from);
			query.setParameter("to", to);
			query.setParameter("date", date);
			result = query.getResultList();
			em.getTransaction().commit();
		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw e;
		} finally {
			em.close();
		}
		return result;
	}

    public List<Date> getThisMonthDatesWithRides(String from, String to, Date date) {
		System.out.println(">> DataAccess: getEventsMonth");
		EntityManager em = JPAUtil.getEntityManager();
		List<Date> result = new ArrayList<Date>();
		try {
			em.getTransaction().begin();
			
			Date firstDayMonthDate = UtilDate.firstDayMonth(date);
			Date lastDayMonthDate = UtilDate.lastDayMonth(date);

			TypedQuery<Date> query = em.createQuery("SELECT DISTINCT r.date FROM Ride r WHERE r.from=:from AND r.to=:to AND r.date BETWEEN :startDate AND :endDate", Date.class);

			query.setParameter("from", from);
			query.setParameter("to", to);
			query.setParameter("startDate", firstDayMonthDate);
			query.setParameter("endDate", lastDayMonthDate);
			
			result = query.getResultList();
			em.getTransaction().commit();
		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw e;
		} finally {
			em.close();
		}
		return result;
	}
    
    
    
    //Register
    public Profile register(String email, String name, String password, String type) {
		EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Profile existitzenDa = em.find(Profile.class, email);
            
            if (existitzenDa != null) {
                System.out.println("Erabiltzailea existitzen da! " + email);
                return null;
            }

            Profile newUser = ProfileFactory.createProfile(type, email, name, password);

            em.persist(newUser);
            em.getTransaction().commit();
            System.out.println("Ondo gorde da: " + email + " como " + type);
            
            return newUser;

        } catch (Exception e) {
            if (em.getTransaction() != null && em.getTransaction().isActive()) {
            	em.getTransaction().rollback();
            }
            e.printStackTrace();
            return null;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
    
    // Login
    public Profile login(String email, String password) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Profile p = em.find(Profile.class, email);
            if (p != null && p.getPassword().equals(password)) {
                return p;
            } else {
                System.out.println("Kredentzial desegokiak: Logina ez da gauzatu");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
    
    

    public void initializeDB() {
		EntityManager em = JPAUtil.getEntityManager();
		try {
			em.getTransaction().begin();

			Calendar today = Calendar.getInstance();

			int month = today.get(Calendar.MONTH);
			int year = today.get(Calendar.YEAR);
			if (month == 12) {
				month = 1;
				year += 1;
			}

			// Create drivers
			Driver driver1 = new Driver("driver1@gmail.com", "Aitor Fernandez", "a");
			Driver driver2 = new Driver("driver2@gmail.com", "Ane Gaztañaga", "a");
			Driver driver3 = new Driver("driver3@gmail.com", "Test driver", "a");
			
			Traveller traveller1 = new Traveller("traveller1@gmail.com", "Beñat Ercibengoa", "b");
			Traveller traveller2 = new Traveller("traveller2@gmail.com", "Jon Portu", "b");
			Traveller traveller3 = new Traveller("traveller2@gmail.com", "Jon Elizetxea", "b");


			// Create rides
			driver1.addRide("Donostia", "Bilbo", UtilDate.newDate(year, month, 15), 4, 7);
			driver1.addRide("Donostia", "Gazteiz", UtilDate.newDate(year, month, 6), 4, 8);
			driver1.addRide("Bilbo", "Donostia", UtilDate.newDate(year, month, 25), 4, 4);

			driver1.addRide("Donostia", "Iruña", UtilDate.newDate(year, month, 7), 4, 8);

			driver2.addRide("Donostia", "Bilbo", UtilDate.newDate(year, month, 15), 3, 3);
			driver2.addRide("Bilbo", "Donostia", UtilDate.newDate(year, month, 25), 2, 5);
			driver2.addRide("Eibar", "Gasteiz", UtilDate.newDate(year, month, 6), 2, 5);

			driver3.addRide("Bilbo", "Donostia", UtilDate.newDate(year, month, 14), 1, 3);

			em.persist(driver1);
			em.persist(driver2);
			em.persist(driver3);

			em.getTransaction().commit();
			System.out.println("Db initialized");
		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			e.printStackTrace();
		} finally {
			em.close();
		}
	}
	
}
