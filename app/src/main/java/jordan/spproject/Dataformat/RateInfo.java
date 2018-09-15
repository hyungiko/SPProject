package jordan.spproject.Dataformat;

/**
 * Created by hyungiko on 9/15/18.
 */

public class RateInfo {
    public String email;
    public String name;
    public double rate;
    public double count;

    public RateInfo(String email, String name, double rate, double count) {
        this.email = email;
        this.name = name;
        this.rate = rate;
        this.count = count;
    }
}
