package flysat;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.Date;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ParentClasses.Transponder;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import ParentClasses.Satellite;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HTMLParser {
    final static String SATELLITE_LIST_URL = "https://www.flysat.com/en/satellitelist";
    final static int THREAD_POOL_SIZE = 128;

    // We have to introduce concurrency, each parseSatelliteData() call takes about a second,
    // 0.6 s of which is taken by `document = Jsoup.connect(url).get();`
    private static List<CompletableFuture<Optional<Satellite>>> parseSatellitesAsync(ArrayList<String> urls) {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        List<CompletableFuture<Optional<Satellite>>> futures = urls.stream()
                        .map(url -> CompletableFuture.supplyAsync(() -> parseSatelliteData(url), executor))
                        .toList();

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOf.join();
        executor.shutdown();
        return futures;
    }


    private static Optional<ArrayList<String>> getSatelliteURLs() {
        Document document;

        // Try to connect, if can't - return empty optional to be handled later
        // Why not null? Simple - I hate nulls, Optionals *force* you to properly handle the possibility of lack of data.
        try {
            document = Jsoup.connect(SATELLITE_LIST_URL).get();
        } catch (IOException e) {
            System.err.println("Couldn't pull data from " + SATELLITE_LIST_URL);
            return Optional.empty();
        }

        /* DONE
            Parse HTML document to a list of URLs of specific satellites
            (for example https://www.flysat.com/en/satellite/nss-9)
            After pulling just put the urls in satelliteURLs ArrayList
         */

        /* link parsing */
        Set<String> linkSet = new LinkedHashSet<>();

        int count = 0;

		for (org.jsoup.nodes.Element link: document.select("center table tbody tr td table tbody tr td:nth-of-type(2) a")) {
			String linkString = link.attr("href");
			if (linkString.equals("https://flysat.com/public/en/satellite/eutelsat-33e-moving-west")) {
				break;
			}
			linkSet.add(linkString);
			
//			System.out.println(linkString);
			count++;
		}
		
		System.out.println(count);

	    	linkSet.remove("http://www.alpsat.com/");
        ArrayList<String> satelliteURLs = new ArrayList<>(linkSet);
		return Optional.of(satelliteURLs);
    }

    private static Optional<Transponder[]> getTranspondersOfSatellite(Document document) {
        Elements coloredTrs = document.select("body > table > tbody > tr[bgcolor='#79bcff']");
        ArrayList<Transponder> transponders = new ArrayList<>();
        for (Element tr : coloredTrs) {
            
        }
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
        Element header = nameHeader.select("b").first();

        String headerString = header.text();
        //     ^^^^^^^^^^^^
        //     Like "name @ position ° W/E"

        String[] nameAndPosition = headerString.split("@");

        // Satellite name parsing
            satellite.names = extractNames(nameAndPosition[0].strip());

        // Orbital position parsing
            String[] splitPositionString = nameAndPosition[1].strip().split("°");
            //                             ^^^^^^^^^^^^^^^^^^
            //                             Like "106.5 ° E"

            // Convention
            // * West is (-)
            // * East is (+)
            satellite.orbital_position = Float.parseFloat(splitPositionString[0].strip());
            if(splitPositionString[1].strip().equals("W")) {
                satellite.orbital_position *= -1;
            }
        System.out.println("Done processing satellite from " + url);
        return Optional.of(satellite);
    }

    // Separate names from formats: "name1", "name1 (name2)", "name1 / name2" into an array
    private static String[] extractNames(String unformattedName) {
        String[] names;

        if (unformattedName.contains("(")) {
            // Format: "name1 (name2)"
            names = unformattedName.split("\\s*\\(\\s*|\\s*\\)\\s*");
        } else if (unformattedName.contains("/")) {
            // Format: "name1/name2"
            names = unformattedName.split("\\s*/\\s*");
        } else {
            // Format: Only one name
            names = new String[]{unformattedName};
        }

        return names;
    }

    public static @Nullable Satellite[] getSatellites() {
        Optional<ArrayList<String>> urls = getSatelliteURLs();
        if (urls.isEmpty())
            return new Satellite[0];
        System.out.println("Satellites to process: " + urls.get().size());
        List<CompletableFuture<Optional<Satellite>>> futures =  parseSatellitesAsync(urls.get());
        Satellite[] satellites = futures.stream().map(CompletableFuture::join)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toArray(Satellite[]::new);
        return satellites;
    }

    public static void main(String[] args) {
        getSatellites();
    }
}
