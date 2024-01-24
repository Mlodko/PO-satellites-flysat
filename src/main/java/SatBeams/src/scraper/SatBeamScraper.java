package scraper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.util.*;

public class SatBeamScraper extends WebsiteData {

    protected static final Logger scraper = LogManager.getLogger();

    public SatBeamScraper() {
    }

    public List<SatBeam> ScrapeData(int rangeStart, int rangeEnd) throws Exception {

        // check connection
        Document document;
        try {
            Connection connect = Jsoup.connect("https://www.satbeams.com/satellites");
            document = connect.get();
        } catch (Exception er) {
            scraper.warn("Unable to connect!");
            scraper.error(er);
            return new ArrayList<>();
        }

        // check whether start is lower number than start
        if(rangeEnd < rangeStart){
            throw new Exception("Start of range can't have lower value than End of range");
        }else{
            rangeEnd++;
        }

        // check range end
        if(rangeEnd > 609 || rangeEnd <= 0){
            throw new Exception("End of range must be between 1 and 608");
        }

        // check rang start
        if(rangeStart <= 0 || rangeStart >= 609){
            throw new Exception("Start of range must be between 1 and 608");
        }else{
            rangeStart++;
        }

        List<SatBeam> Satellites = new ArrayList<>();
        int whichColumn = 0;
        String anim= "|/-\\"; // dla animacji ładowania
        int scrapedSatCount = 1;

        // iteracja przez każdy wiersz w podanym zakresie
        for (int i = rangeStart; i <= rangeEnd; i++) {
            whichColumn = 0;
            // animacja ładowania
            //String data = "\r  Scraped Sat's: " + scrapedSatCount + "   " +  anim.charAt(i % anim.length()) + "   \n";
            //System.out.write(data.getBytes());
            if(i == rangeEnd){System.out.println("\n");}

            // pobranie danych z pojedynczego wiersza
            String condition = "table > tbody > tr > td:nth-child(2) tr:nth-child(" + i + ") td";

            // sprawdzenie, czy nie wystąpił błąd podczas pobierania wiersza
            try{
                Elements trRow = document.select(condition);
                SatBeam tempSat = new SatBeam();

                // sprawdzenie, czy pobrany wiersz nie jest pusty
                if(!Objects.equals(trRow.text(), "")){

                    // obsługa danych z pojedynczego wiersza i zapisanie ich do obiektu
                    for (Element tr : trRow) {
                            switch (whichColumn){
                                case 0:
                                    try{
                                        tempSat.setId(Long.parseLong(tr.text()));
                                    }catch (Exception er) {
                                        scraper.warn("Can't scrape ID for Sat number " + Satellites.getLast().getId() + " : " + er.getMessage());
                                        tempSat.setId(null);
                                    }
                                    break;
                                case 1:
                                    tempSat.setPosition(tr.text());
                                    break;
                                case 2:
                                    tempSat.setStatus(tr.text());
                                    break;
                                case 3:
                                    tempSat.setSateliteName(tr.text());
                                    try{
                                        Elements aLinkHref = document.select("table > tbody > tr > td:nth-child(2) tr:nth-child(" + i + ") td > a");
//                                    System.out.println("Link: " + "https://www.satbeams.com" + aLinkHref.getFirst().attr("href"));
                                        String link = "https://www.satbeams.com" + aLinkHref.getFirst().attr("href");
                                        try{
                                            tempSat = this.GetAdditionalData(link, tempSat);
                                        }catch (Exception er){
                                            scraper.error("While getting additional data to Sat with id = " + tempSat.getId() + " error occured: " + er.getMessage());
                                        }
                                    }catch (Exception er){
                                        scraper.error("While scraping url to more detailed info about Sat error occured: " + er.getMessage());
                                    }
                                    break;
                                case 4:
                                    try{
                                        tempSat.setNorad(Integer.parseInt(tr.text()));
                                    }catch (Exception er){
                                        scraper.warn("Can't scrape Norad for Sat with id " + tempSat.getId() + " : "+ er.getMessage());
                                        tempSat.setNorad(0);
                                    }
                                    break;
                                case 5:
                                    tempSat.setCospar(tr.text());
                                    break;
                                case 6:
                                    tempSat.setSatelliteModel(tr.text());
                                    break;
                                case 7:
                                    tempSat.setOperator(tr.text());
                                    break;
                                case 8:
                                    tempSat.setLaunchSite(tr.text());
                                    break;
                                case 9:
                                    try{
                                        tempSat.setLaunchMass(Integer.parseInt(tr.text()));
                                    }catch (Exception er){
                                        scraper.warn("Can't scrape Launch Mass for Sat with id " + tempSat.getId() + ": "+ er.getMessage());
                                        tempSat.setLaunchMass(0);
                                    }
                                    break;
                                case 10:
                                    tempSat.setLaunchDate(tr.text());
                                    break;
                                case 11:
                                    break;
                                case 12:
                                    break;
                                case 13:
                                    tempSat.setComments(tr.text());
                                    break;
                            }
                            whichColumn++;

                        }
                        Satellites.add(tempSat);
                } else {
                    scraper.warn("Scraped line number " + (i + 1) + " is empty!");
                }
            }
            catch (Exception er){
                scraper.error("Problem Reading Info from Row " + (i + 1) + " : " + er.getMessage());
            }
        }

        scraper.info("Successfully scraped " + (scrapedSatCount-1) + " satellites!");
        return Satellites;
    }

    public SatBeam GetAdditionalData(String url, SatBeam sat) throws Exception {
        SatBeam tempSat = sat;
        Document document;

        try {
            Connection connect = Jsoup.connect(url).timeout(10*10000);
            document = connect.get();
        } catch (Exception er) {
            throw new Exception("Unable to connect! Error: " + er);
        }

        String condition = "div#table_wrap table tbody > tr:nth-child(2) > td:first-child";
        Elements items = document.select(condition);
        String[] toAnalyze = items.toString().split("(\\<br\\>|\\<td\\>)\\<b\\>*[a-z A-Z0-9:()]*\\<\\/b\\>");

        int whichLine = 0;
        for (String oneItem : toAnalyze){

            String value;

            if(oneItem.contains("<a href")){
                String[] item = oneItem.split("\">");
                item = item[1].split("</a>");
                value = item[0];
            }else{
                value = oneItem;
            }

            switch (whichLine){
                case  9:
                    if (value.contains("&nbsp;")){
                        tempSat.setLaunchVehicle(null);
                    }else{
                        tempSat.setLaunchVehicle(value);
                    }
                    break;
                case 12:
                    if (value.contains("&nbsp;")){
                        tempSat.setManufacturer(null);
                    }else{
                        tempSat.setManufacturer(value);
                    }
                    break;
                case 15:
                    if (value.contains("&nbsp;")){
                        tempSat.setExpectedLifetime(null);
                    }else{
                        tempSat.setExpectedLifetime(value.split("<br></td>")[0]);
                    }
                    break;
            }

            whichLine++;
        }
        if(Objects.equals(tempSat.getStatus(), "active")){
            // getting url to satellite transmitters
            condition = "div#table_wrap table tbody > tr:nth-child(2) > td:last-child";
            items = document.select(condition);
            toAnalyze = items.toString().split("(\\<br\\>|\\<td\\>)\\<b\\>*[a-z A-Z0-9:()]*\\<\\/b\\>");
            String transmittersUrl = "";

            whichLine = 0;
            for (String oneItem : toAnalyze){
                String value;
                switch (whichLine){
                    case  3:
                        value = oneItem.split("</b> <a href=\"")[1];
                        value = value.split("\">list")[0];
                        transmittersUrl = "https://www.satbeams.com"+value;
                        break;
                }
                whichLine++;
            }

            try{
                ArrayList<WebsiteData.Satellite.Transmitter> transmitters = getTransmitters(transmittersUrl);
                tempSat.setTransmitters(transmitters);
            }catch (Exception er){
                scraper.warn("Error while getting data about Transmitters for sat with id: " + tempSat.getId() + ", error: "+ er);
            }
        }else{
//            System.out.println("Doesn't have transmitters");
        }

        return tempSat;
    }

    public ArrayList<WebsiteData.Satellite.Transmitter> getTransmitters(String url) throws Exception {
        Document document;

//        System.out.println("Scraping Transmitters");

        try {
            Connection connect = Jsoup.connect(url).timeout(10*10000);
            document = connect.get();
        } catch (Exception er) {
            throw new Exception("Unable to connect! Error: " + er);
        }

        String condition = "td.group_td";
        Elements items = document.select(condition);
        ArrayList<WebsiteData.Satellite.Transmitter> transmitters = new ArrayList<>();

        WebsiteData.Satellite tempSat = new Satellite();
        for(Element item: items){
            WebsiteData.Satellite.Transmitter tempTransmitter = tempSat.new Transmitter();
//            System.out.println(item.text());

            String[] values = item.text().split("\\) ");
            String[] leftValues = values[0].split(" ");
            String[] rightValues = values[1].split(" ");


//            // get band
//            String band = leftValues[1];
//            System.out.println("Band: " + band);

            // get frequency
            try{
                tempTransmitter.setFrequency(Float.parseFloat(leftValues[2]));
            }catch (Exception er){
                scraper.warn("Cant scrape freq: " + er.getMessage());
            }

            try{
                // get polarisation
                tempTransmitter.setPolarisation(leftValues[3].charAt(0));
            }catch (Exception er){
                scraper.warn("Can't scrape polarisation: " + er.getMessage());
            }


            //get SR
            try {
                tempTransmitter.setSymbolRate(Integer.parseInt(leftValues[4]));
            }catch (Exception er){
                scraper.warn("Can't scrape Symbol Rate: " + er.getMessage());
            }

            try{
                //get FEC
                tempTransmitter.setFec(leftValues[5]);
            }catch (Exception er){
                scraper.warn("Can't scrape SR: " + er.getMessage());
            }


            try{
                //get beam
                tempTransmitter.setBeam(rightValues[0]);
            }catch (Exception er){
                scraper.warn("Can't scrape Beam: " + er.getMessage());
            }

            try{
                //get modulation
                tempTransmitter.setModulation(rightValues[1]);
            }catch (Exception er){
                scraper.warn("Can't scrape modulation: " + er.getMessage());
            }

            try{
                //get format
                tempTransmitter.setStandard(rightValues[2]);
            }catch (Exception er){
                scraper.warn("Can't scrape standard: " +er.getMessage());
            }


            transmitters.add(tempTransmitter);

//            System.out.println(tempTransmitter);
        }
        return transmitters;
    }



    // translating SatBeam Objects List to WebsiteData.Satellites Objects List
    public ArrayList<WebsiteData.Satellite> SatBeamListToSatellites(List<SatBeam> satBeams) throws ParseException {
        ArrayList<WebsiteData.Satellite> satellites = new ArrayList<WebsiteData.Satellite>();

        for(SatBeam satBeam : satBeams){
            satellites.add(satBeam.toSatellite());
        }

        return  satellites;
    }
}
