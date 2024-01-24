import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LyngSat {
    public static final class Transponder {
        private String band;
        private int freq;
        private String pol;
        private int SR;
        private String FEC;
        private String kontinent;
        private String mod;
        private String format;
        private String provider;

        public String getBand() {
            return band;
        }

        public void setBand(String band) {
            this.band = band;
        }

        public int getFreq() {
            return freq;
        }

        public void setFreq(int freq) {
            this.freq = freq;
        }

        public String getPol() {
            return pol;
        }

        public void setPol(String pol) {
            this.pol = pol;
        }

        public int getSR() {
            return SR;
        }

        public void setSR(int SR) {
            this.SR = SR;
        }

        public String getFEC() {
            return FEC;
        }

        public void setFEC(String FEC) {
            this.FEC = FEC;
        }

        public String getKontinent() {
            return kontinent;
        }

        public void setKontinent(String kontinent) {
            this.kontinent = kontinent;
        }

        public String getMod() {
            return mod;
        }

        public void setMod(String mod) {
            this.mod = mod;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public List<Object> toArray() {
            List<Object> arr = new ArrayList<>();
            arr.add(getBand());
            arr.add(getFreq());
            arr.add(getPol());
            arr.add(getSR());
            arr.add(getFEC());
            arr.add(getKontinent());
            arr.add(getMod());
            arr.add(getFormat());
            arr.add(getProvider());
            return arr;
        }
    }

    private int norad;
    private int index;
    private String position;
    private String cospar;
    private String name;
    private String model;
    private Date launch;
    private List<Transponder> transponders = new ArrayList<>();

    public int getNorad() {
        return norad;
    }

    public void setNorad(int norad) {
        this.norad = norad;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCospar() {
        return cospar;
    }

    public void setCospar(String cospar) {
        this.cospar = cospar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Date getLaunch() {
        return launch;
    }

    public void setLaunch(Date launch) {
        this.launch = launch;
    }

    public List<Transponder> getTransponders() {
        return transponders;
    }

    public void setTransponders(List<Transponder> transponders) {
        this.transponders = transponders;
    }

    public void addTransponder(Transponder transponder) {
        this.transponders.add(transponder);
    }

    public List<Object> toArray() {
        List<Object> arr = new ArrayList<>();
        arr.add(getIndex());
        arr.add(getNorad());
        arr.add(getPosition());
        arr.add(getCospar());
        arr.add(getName());
        arr.add(getModel());
        arr.add(getLaunch());
        return arr;
    }
}
