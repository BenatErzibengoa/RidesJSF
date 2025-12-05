package eredua.factory;

import eredua.domain.Driver;
import eredua.domain.Profile;
import eredua.domain.Traveller;

public class ProfileFactory {

    public static Profile createProfile(String type, String email, String name, String password) {
        if (type == null) {
            return null;
        }
        if (type.equals("driver")) {
            return new Driver(email, name, password);
        } else if (type.equals("traveller")) {
            return new Traveller(email, name, password);
        } else {
        	System.out.println("Erabiltzaile mota ezezaguna. Traveller sortzen...");
            return new Traveller(email, name, password); 
        }
    }
}