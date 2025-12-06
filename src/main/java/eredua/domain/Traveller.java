package eredua.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;

@Entity
public class Traveller extends Profile implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ManyToMany(mappedBy = "travellers", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private List<Ride> bookedRides = new ArrayList<>();

	public Traveller() {
		super();
	}

	public Traveller(String email, String name, String password) {
		super(email, name, password);
	}
	
	public List<Ride> getBookedRides() {
		return bookedRides;
	}

	public void setBookedRides(List<Ride> bookedRides) {
		this.bookedRides = bookedRides;
	}
	
	public void addRide(Ride ride) {
		if (!this.bookedRides.contains(ride)) {
			this.bookedRides.add(ride);
			if (!ride.getTravellers().contains(this)) {
				ride.addTraveller(this);
			}
		}
	}
	
    public void removeRide(Ride ride) {
        if (this.bookedRides.contains(ride)) {
            this.bookedRides.remove(ride);
            if (ride.getTravellers().contains(this)) {
                ride.getTravellers().remove(this);
            }
        }
    }
	
}