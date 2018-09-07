package jordan.spproject.helper;

/**
 * Created by hyungiko on 9/1/18.
 */

public class DataModelProfile {
    String profileFeature;
    String profileContent;

    public DataModelProfile(String profileFeature, String profileContent) {
        this.profileFeature=profileFeature;
        this.profileContent=profileContent;
    }

    public String getProfileFeature() {
        return profileFeature;
    }

    public String getProfileContent() {
        return profileContent;
    }
}
