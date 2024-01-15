package flysat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import ParentClasses.Satellite;

public class HTMLParser {
    private static Optional<ArrayList<String>> getSatelliteURLs() {
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
    
    private static Optional<Satellite> parseSatelliteData(String url) {
        Document document;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            System.err.println("Couldn't pull data from " + url);
            return Optional.empty();
        }
        Satellite satellite = new Satellite();

        // TODO parse HTML document

        return Optional.of(satellite);
    }

    public static Satellite[] @Nullable getSatellites() {
        Optional<ArrayList<String>> urls = getSatelliteURLs();

        return urls.map(urlList -> urlList.stream()             // If urls is not empty, create a stream from the list of URLs
                        .map(HTMLParser::parseSatelliteData)    // Apply parseSatelliteData() to each URL in the stream
                        .filter(Optional::isPresent)            // Remove any Optionals that are empty
                        .map(Optional::get)                     // Transform each Optional<Satellite> in the stream into a Satellite
                        .toArray(Satellite[]::new))             // Collect the Satellite objects in the stream into a new array
                        .orElse(null);                          // If urls was empty, return null
    }
}
