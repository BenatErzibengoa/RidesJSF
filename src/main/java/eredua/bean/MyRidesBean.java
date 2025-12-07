package eredua.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import businessLogic.BLFacade;
import eredua.domain.Ride;

@Named("myrides")
@ViewScoped
public class MyRidesBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Ride> futureRides;
    private List<Ride> pastRides;

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

    // Getters
    public List<Ride> getFutureRides() {
        return futureRides;
    }

    public List<Ride> getPastRides() {
        return pastRides;
    }
    
    // Método placeholder para la futura funcionalidad de valorar
    public void rateRide(Ride r) {
        System.out.println("Valorando ride: " + r.getRideNumber());
        // Aquí irá la lógica de valoración en el futuro
    }
}