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
    final static String SATELLITE_LIST_URL = "https://www.flysat.com/en/satellitelist";
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
        ArrayList<String> satelliteURLs = new ArrayList<>();

        /* TODO
            Parse HTML document to a list of URLs of specific satellites
            (for example https://www.flysat.com/en/satellite/nss-9)
            After pulling just put the urls in satelliteURLs ArrayList
         */

        /* link parsing */
        Set<String> linkSet = new LinkedHashSet<>();
        return Optional.of(satelliteURLs);

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
		satelliteURLs.addAll(linkSet);
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
