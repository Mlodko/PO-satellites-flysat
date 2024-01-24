package scraper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class WebsiteData implements Serializable{

    private ArrayList<Satellite> satellites;

    public WebsiteData() {}

    public ArrayList<Satellite> getSatellites() {
        return satellites;
    }

    public WebsiteData setSatellites(ArrayList<Satellite> satellites) {
        this.satellites = satellites;
        return this;
    }


    public static class Satellite implements Serializable {

        //for tests
        @Override
        public String toString() {
            String transmittersString = "null";

            if(this.transmitters != null){
                transmittersString = "   - transmitter= \n";

                for(Transmitter transmitter : this.transmitters){
                    transmittersString = transmittersString +
                            "         - Freq.: " + transmitter.getFrequency() + ", " +
                            "Polarisation: " + transmitter.getPolarisation() + ", " +
                            "Beam: " + transmitter.getBeam() + ", " +
                            "Standard: " + transmitter.getStandard() + ", " +
                            "Modulation: " + transmitter.getModulation() + ", " +
                            "Symbol Rate: " + transmitter.getSymbolRate() + ", " +
                            "Fec: " + transmitter.getFec() + "\n";
                }
            }

            return "Satellite{" + "\n" +
                    "   - norad = " + norad + "\n" +
                    "   - names = '" + names + "'\n" +
                    "   - operator = '" + operator + "'\n" +
                    "   - status = '" + status + "'\n" +
                    "   - orbitalPosition = " + orbitalPosition + "\n" +
                    "   - actualPosition = " + actualPosition + "\n" +
                    "   - launchDate = " + launchDate + "\n" +
                    "   - launchSite = '" + launchSite + "'\n" +
                    "   - launchMass = " + launchMass + "\n" +
                    "   - launchVehicle = '" + launchVehicle + "'\n" +
                    "   - satelliteManufacturer = '" + satelliteManufacturer + "'\n" +
                    "   - satelliteModel = '" + satelliteModel + "'\n" +
                    "   - satelliteExpectedLifetime = " + satelliteExpectedLifetime + "\n" +
                    transmittersString + '}';
        }

        //        TODO: Class need to be public otherwise we will not be able to access it from other packages
        private Long id;
        private int norad;
        private ArrayList<String> names;
        private String operator;
        private String status;
        private float orbitalPosition; // number in degrees, negative  = W, positive = E
        private float actualPosition; // as above

        // TODO: I propose changing launchDate to string as well as actual position

        private Date launchDate;
        private String launchSite;
        private double launchMass;
        private String launchVehicle;
        private String satelliteManufacturer;
        private String satelliteModel;
        private String satelliteExpectedLifetime;
        public ArrayList<Transmitter> transmitters = new ArrayList<>();

        public Satellite() {}
        public Satellite(int norad,  ArrayList<String> names, String operator, String status, float orbitalPosition,
                         float actualPosition, Date launchDate, String launchSite, double launchMass, String launchVehicle,
                         String satelliteManufacturer, String satelliteModel, String satelliteExpectedLifetime,
                         ArrayList<Transmitter> transmitters) {
            this.norad = norad;
            this.names = names;
            this.operator = operator;
            this.status = status;
            this.orbitalPosition = orbitalPosition;
            this.actualPosition = actualPosition;
            this.launchDate = launchDate;
            this.launchSite = launchSite;
            this.launchMass = launchMass;
            this.launchVehicle = launchVehicle;
            this.satelliteManufacturer = satelliteManufacturer;
            this.satelliteModel = satelliteModel;
            this.satelliteExpectedLifetime = satelliteExpectedLifetime;
            this.transmitters = transmitters;
        }
        public Long getId() {
            return id;
        }

        public int getNorad() {
            return norad;
        }

        public ArrayList<String> getNames() {
            return names;
        }

        public String getOperator() {
            return operator;
        }

        public String getStatus() {
            return status;
        }

        public float getOrbitalPosition() {
            return orbitalPosition;
        }

        public float getActualPosition() {
            return actualPosition;
        }

        public Date getLaunchDate() {
            return launchDate;
        }

        public String getLaunchSite() {
            return launchSite;
        }

        public double getLaunchMass() {
            return launchMass;
        }

        public String getLaunchVehicle() {
            return launchVehicle;
        }

        public String getSatelliteManufacturer() {
            return satelliteManufacturer;
        }

        public String getSatelliteModel() {
            return satelliteModel;
        }

        public String getSatelliteExpectedLifetime() {
            return satelliteExpectedLifetime;
        }

        public ArrayList<Transmitter> getTransmitters() {
            return transmitters;
        }
        public Satellite setId(Long id) {

            this.id = id;
            return this;
        }

        public Satellite setNorad(int norad) {
            this.norad = norad;
            return this;
        }

        public Satellite setNames(ArrayList<String> names) {
            this.names = names;
            return this;
        }

        public Satellite setOperator(String operator) {
            this.operator = operator;
            return this;
        }

        public Satellite setStatus(String status) {
            this.status = status;
            return this;
        }

        public Satellite setOrbitalPosition(float orbitalPosition) {
            this.orbitalPosition = orbitalPosition;
            return this;
        }

        public Satellite setActualPosition(float actualPosition) {
            this.actualPosition = actualPosition;
            return this;
        }

        public Satellite setLaunchDate(Date launchDate) {
            this.launchDate = launchDate;
            return this;
        }

        public Satellite setLaunchSite(String launchSite) {
            this.launchSite = launchSite;
            return this;
        }

        public Satellite setLaunchMass(double launchMass) {
            this.launchMass = launchMass;
            return this;
        }

        public Satellite setLaunchVehicle(String launchVehicle) {
            this.launchVehicle = launchVehicle;
            return this;
        }

        public Satellite setSatelliteManufacturer(String satelliteManufacturer) {
            this.satelliteManufacturer = satelliteManufacturer;
            return this;
        }

        public Satellite setSatelliteModel(String satelliteModel) {
            this.satelliteModel = satelliteModel;
            return this;
        }

        public Satellite setSatelliteExpectedLifetime(String satelliteExpectedLifetime) {
            this.satelliteExpectedLifetime = satelliteExpectedLifetime;
            return this;
        }

        public Satellite setTransmitters(ArrayList<Transmitter> transmitters) {
            this.transmitters = transmitters;
            return this;
        }

        public Satellite addName(String name) {
            this.names.add(name);
            return this;
        }

        public Satellite addTransmitter(Transmitter transmitter) {
            this.transmitters.add(transmitter);
            return this;
        }



        public class Transmitter implements Serializable{

            private Float frequency = 0.0F;
            private Character polarisation;
            private String beam;
            private String standard;
            private String modulation;
            private Integer symbolRate = 0;
            private String fec; // probably not a good idea to write 2/3 as a float due to precision loss

            public Transmitter() {}
            public Transmitter(Float frequency, Character polarisation, String beam, String standard,
                               String modulation, Integer symbolRate, String fec) {
                this.frequency = frequency;
                this.polarisation = polarisation;
                this.beam = beam;
                this.standard = standard;
                this.modulation = modulation;
                this.symbolRate = symbolRate;
                this.fec = fec;
            }

            public Float getFrequency() {
                return frequency;
            }

            public Character getPolarisation() {
                return polarisation;
            }

            public String getBeam() {
                return beam;
            }

            public String getStandard() {
                return standard;
            }

            public String getModulation() {
                return modulation;
            }

            public Integer getSymbolRate() {
                return symbolRate;
            }

            public String getFec() {
                return fec;
            }

            public Transmitter setFrequency(Float frequency) {
                this.frequency = frequency;
                return this;
            }

            public Transmitter setPolarisation(Character polarisation) {
                this.polarisation = polarisation;
                return this;
            }

            public Transmitter setBeam(String beam) {
                this.beam = beam;
                return this;
            }

            public Transmitter setStandard(String standard) {
                this.standard = standard;
                return this;
            }

            public Transmitter setModulation(String modulation) {
                this.modulation = modulation;
                return this;
            }

            public Transmitter setSymbolRate(Integer symbolRate) {
                this.symbolRate = symbolRate;
                return this;
            }

            public Transmitter setFec(String fec) {
                this.fec = fec;
                return this;
            }

            @Override
            public String toString() {
                return "frequency=" + frequency +
                        ", polarisation=" + polarisation +
                        ", symbolRate=" + symbolRate +
                        ", fec='" + fec + '\'' +
                        ", beam='" + beam + '\'' +
                        ", modulation='" + modulation + '\'' +
                        ", standard='" + standard + '\'';
            }
        }

    }

}
