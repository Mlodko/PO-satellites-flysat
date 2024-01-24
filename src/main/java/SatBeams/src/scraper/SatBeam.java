package scraper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.select.Evaluator;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SatBeam {
    protected static final Logger satbeam = LogManager.getLogger();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMM-yyyy", Locale.ENGLISH); //FORMAT STRING TO DATE


    //    DateTimeFormatter formatter = DateTimeFormatter.ofPattern ( "dd-MMM-yyyy" , Locale.ENGLISH );
    private Long id;

    private String position;

    private String status;

    private String sateliteName;

    private int norad;

    private String cospar;

    private String satelliteModel;

    private String operator;

    private String launchSite;

    private int launchMass;

    private String launchDate;

    private String launchVehicle;

    private String manufacturer;

    private String expectedLifetime;

    private String comments;

    private ArrayList<WebsiteData.Satellite.Transmitter> transmitters;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getSateliteName() {
        return sateliteName;
    }

    public void setSateliteName(String sateliteName) {
        this.sateliteName = sateliteName;
    }

    public int getNorad() {
        return norad;
    }

    public void setNorad(int norad) {
        this.norad = norad;
    }

    public String getCospar() {
        return cospar;
    }

    public void setCospar(String cospar) {
        this.cospar = cospar;
    }

    public String getSatelliteModel() {
        return satelliteModel;
    }

    public void setSatelliteModel(String satelliteModel) {
        this.satelliteModel = satelliteModel;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getLaunchSite() {
        return launchSite;
    }

    public void setLaunchSite(String launchSite) {
        this.launchSite = launchSite;
    }

    public int getLaunchMass() {
        return launchMass;
    }

    public void setLaunchMass(int launchMass) {
        this.launchMass = launchMass;
    }

    public String getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(String launchDate) {
        this.launchDate = launchDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLaunchVehicle() {
        return launchVehicle;
    }

    public void setLaunchVehicle(String launchVehicle) {
        this.launchVehicle = launchVehicle;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getExpectedLifetime() {
        return expectedLifetime;
    }

    public void setExpectedLifetime(String expectedLifetime) {
        this.expectedLifetime = expectedLifetime;
    }

    public ArrayList<WebsiteData.Satellite.Transmitter> getTransmitters() {
        return transmitters;
    }

    public void setTransmitters(ArrayList<WebsiteData.Satellite.Transmitter> transmitters) {
        this.transmitters = transmitters;
    }

    @Override
    public String toString() {
            String transmittersInfo = "";
            if(transmitters != null) {
                transmittersInfo = transmittersInfo + "   -transmitter=\n";
                for(WebsiteData.Satellite.Transmitter transmitter : transmitters) {

                    transmittersInfo += transmitter.toString() + "\n";
                }
            }
            return "SatBeam{" + "\n" +
            "   - id=" + id + "\n" +
            "   - position='" + position + '\'' + "\n" +
            "   - status='" + status + '\'' + "\n" +
            "   - sateliteName='" + sateliteName + '\'' + "\n" +
            "   - norad=" + norad + "\n" +
            "   - cospar='" + cospar + '\'' + "\n" +
            "   - satelliteModel='" + satelliteModel + '\'' + "\n" +
            "   - operator='" + operator + '\'' + "\n" +
            "   - launchSite='" + launchSite + '\'' + "\n" +
            "   - launchMass=" + launchMass + "\n" +
            "   - launchDate='" + launchDate + '\'' + "\n" +
            "   - launchVehicle='" + launchVehicle + '\'' + "\n" +
            "   - manufacturer='" + manufacturer + '\'' + "\n" +
            "   - expectedLifetime='" + expectedLifetime + '\'' + "\n" +
            "   - comments='" + comments + '\'' + '}' + "\n"  +
            transmittersInfo;
    }

    public WebsiteData.Satellite toSatellite() throws ParseException {

        // TODO: Can't translate position to 'actual position' and 'date' in WebsiteData because of type conflict
        //       Normal Date format have to many deatils better idea is using simpler format


        WebsiteData.Satellite satellite = new WebsiteData.Satellite();

        satellite.setNorad(this.norad);
        ArrayList<String> names = new ArrayList<String>();
        names.add(this.sateliteName);
        satellite.setNames(names);
        satellite.setOperator(this.operator);
        satellite.setStatus(this.status);
        try{
            String[] position = this.position.split(" ");
            if(this.position.contains("E")){
                satellite.setOrbitalPosition(Float.parseFloat(position[0]));
            }else{
                satellite.setOrbitalPosition(Float.parseFloat(position[0]) * (-1));
            }
        }catch (Exception er){
            satbeam.error("Error while converting position for Sat with ID: " + this.id);
            satellite.setOrbitalPosition((float)0.0);
        }
        try{
            LocalDate dateTime = LocalDate.parse(this.launchDate, formatter);
            Date date =  Date.from(dateTime.atStartOfDay(ZoneId.systemDefault()).toInstant()); // to other format, more details
            satellite.setLaunchDate(date);
        }catch (Exception er){
            satbeam.warn("Problem with converting string to date for Sat " + this.id + " : " + er);
            satellite.setLaunchDate(null);
        }
        satellite.setLaunchSite(this.launchSite);
        satellite.setLaunchMass(this.launchMass);
        satellite.setLaunchVehicle(this.launchVehicle);
        satellite.setSatelliteManufacturer(this.manufacturer);
        satellite.setSatelliteModel(this.satelliteModel);
        satellite.setSatelliteExpectedLifetime(this.expectedLifetime);
        satellite.setTransmitters(this.transmitters);

        return satellite;
    }

    public WebsiteData.Satellite updateSatelliteBySatBeam(WebsiteData.Satellite websiteDataSat) throws ParseException {

        // TODO: Can't translate position to 'actual position' and 'date' in WebsiteData because of type conflict
        //       Normal Date format have to many deatils better idea is using simpler format

        WebsiteData.Satellite satellite = websiteDataSat;

        satellite.setNorad(this.norad);
        ArrayList<String> names = new ArrayList<String>();
        names.add(this.sateliteName);
        satellite.setNames(names);
        satellite.setOperator(this.operator);
        satellite.setStatus(this.status);
        try{
            String[] position = this.position.split(" ");
            if(this.position.contains("E")){
                satellite.setOrbitalPosition(Float.parseFloat(position[0]));
            }else{
                satellite.setOrbitalPosition(Float.parseFloat(position[0]) * (-1));
            }
        }catch (Exception er){
            satbeam.error("Error while converting position Sat: " + names.getFirst());
        }
        try{
            LocalDate dateTime = LocalDate.parse(this.launchDate, formatter);
            Date date =  Date.from(dateTime.atStartOfDay(ZoneId.systemDefault()).toInstant()); // to other format, more details
            satellite.setLaunchDate(date);
        }catch (Exception er){
            satbeam.warn("Problem with converting string to date for Sat " + this.id + " : " + er);
            satellite.setLaunchDate(null);
        }
        satellite.setLaunchSite(this.launchSite);
        satellite.setLaunchMass(this.launchMass);
        satellite.setLaunchVehicle(this.launchVehicle);
        satellite.setSatelliteManufacturer(this.manufacturer);
        satellite.setSatelliteModel(this.satelliteModel);
        satellite.setSatelliteExpectedLifetime(this.expectedLifetime);
        satellite.setTransmitters(this.transmitters);

        return satellite;
    }



}
