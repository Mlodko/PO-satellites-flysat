package flysat;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import org.jsoup.Jsoup;
import ParentClasses.Satellite;

public class HTMLParser {
    public static Optional<ArrayList<String>> GetSatelliteURLs() {
        Document document;

        // Try to connect, if can't return empty optional to be handled later
        // Why not null? Simple - I hate nulls, Optionals *force* you to properly handle the possibility of lack of data.
        try {
            document = Jsoup.connect("https://www.flysat.com/en/satellitelist").get();
        } catch (IOException e) {
            System.err.println("Couldn't pull data from https://www.flysat.com/en/satellitelist");
            return Optional.empty();
        }
        ArrayList<String> satelliteURLs = new ArrayList<>();

        // TODO parse HTML document to a list of URLs of specific satellites (for example https://www.flysat.com/en/satellite/nss-9)

        return Optional.of(satelliteURLs);
    }

    // We can't rely on other teams to use optionals so if we can't connect we return a null (;-;)
    public static Satellite parseSatelliteData(String url) {
        Satellite satellite = new Satellite();
        Document document;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            System.err.println("Couldn't pull data from " + url);
            return null;
        }

        // TODO parse HTML document

        return satellite;
    }
}
