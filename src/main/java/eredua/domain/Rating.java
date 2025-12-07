package eredua.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Entity
public class Rating implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    private Ride ride;

    @ManyToOne
    private Traveller traveller;

    private int stars;
    private String description;

    public Rating() {
        super();
    }

    public Rating(Ride ride, Traveller traveller, int stars, String description) {
        this.ride = ride;
        this.traveller = traveller;
        this.stars = stars;
        this.description = description;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Ride getRide() { return ride; }
    public void setRide(Ride ride) { this.ride = ride; }
    public Traveller getTraveller() { return traveller; }
    public void setTraveller(Traveller traveller) { this.traveller = traveller; }
    public int getStars() { return stars; }
    public void setStars(int stars) { this.stars = stars; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}