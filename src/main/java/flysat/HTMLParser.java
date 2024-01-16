package flysat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import ParentClasses.Satellite;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HTMLParser {
    private static Optional<ArrayList<String>> getSatelliteURLs() {
        Document document;

        // Try to connect, if can't - return empty optional to be handled later
        // Why not null? Simple - I hate nulls, Optionals *force* you to properly handle the possibility of lack of data.
        try {
            document = Jsoup.connect("https://www.flysat.com/en/satellitelist").get();
        } catch (IOException e) {
            System.err.println("Couldn't pull data from https://www.flysat.com/en/satellitelist");
            return Optional.empty();
        }
        ArrayList<String> satelliteURLs = new ArrayList<>();

        /* TODO
            Parse HTML document to a list of URLs of specific satellites
            (for example https://www.flysat.com/en/satellite/nss-9)
            After pulling just put the urls in satelliteURLs ArrayList
         */
        return Optional.of(satelliteURLs);
    }
    
    private static Optional<Satellite> parseSatelliteData(String url) {
        Document document;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            System.err.println("Couldn't pull data from " + url);
            return Optional.empty();
        }
        Satellite satellite = new Satellite();

        /* TODO
            Parse HTML document to Satellite object's fields. (Including Transponder(s) ;-;)
            What can we pull:
            Satellite:
             - [x] Name(s)
             - [x] Orbital position
            Transponder(s):
             - [ ] Name
             - [ ] Frequency
             - [ ] Polarization
             - [ ] Standard
             - [ ] Encoding
             - [ ] SR
             - [ ] FEC
         */

        Elements nameHeader =  document.select("body > table:nth-of-type(2) > tbody > tr > td");
        System.out.println(nameHeader);
        Optional<Element> header = Optional.ofNullable(nameHeader.select("b").first());
        System.out.println(header);

        if(header.isEmpty()) return Optional.empty();

        String headerString = header.get().text();
        String[] nameAndPosition = headerString.split("@");

        // Satellite name parsing
            // TODO For now I use the whole thing as a single name, have to figure out how to split it
            ArrayList<String> names = new ArrayList<>();
            names.add(nameAndPosition[0].strip());
            satellite.names = names.toArray(String[]::new);

        // Orbital position parsing
            String[] splitPositionString = nameAndPosition[1].strip().split("°");
            // Convention
            // * West is (-)
            // * East is (+)
            satellite.orbital_position = Float.parseFloat(splitPositionString[0].strip());
            if(splitPositionString[1].strip().equals("W")) {
                satellite.orbital_position *= -1;
            }

        return Optional.of(satellite);
    }

    public static @Nullable Satellite[] getSatellites() {
        Optional<ArrayList<String>> urls = getSatelliteURLs();

        return urls.map(urlList -> urlList.stream()             // If urls is not empty, create a stream from the list of URLs
                        .map(HTMLParser::parseSatelliteData)    // Apply parseSatelliteData() to each URL in the stream
                        .filter(Optional::isPresent)            // Remove any Optionals that are empty
                        .map(Optional::get)                     // Transform each Optional<Satellite> in the stream into a Satellite
                        .toArray(Satellite[]::new))             // Collect the Satellite objects in the stream into a new array
                        .orElse(null);                          // If urls was empty, return null
    }

    public static void main(String[] args) {
        // TODO This method is just for debugging. After we're done we should delete it.
        parseSatelliteData("https://flysat.com/public/en/satellite/intelsat-32e-sky-brasil-1");
    }
}
