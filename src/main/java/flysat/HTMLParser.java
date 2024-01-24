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

        // Lyngsat

        try {
            LyngSatParser parser = new LyngSatParser();
            ArrayList<LyngSat> Lyngsats = parser.parse();
        } catch (Exception e) {
            System.err.println("Lyngsat: General Error: " + e.getMessage());
        }

        return Optional.of(satellite);
    }

    // Lyngsat

    public static ArrayList<LyngSat> parseLyngsat() {
        ArrayList<LyngSat> lyngSats = new ArrayList<>();

        final String urlEurope = "https://www.lyngsat.com/europe.html";
        final String urlAsia = "https://www.lyngsat.com/asia.html";
        final String urlAmerica = "https://www.lyngsat.com/america.html";
        final String urlAtlantic = "https://www.lyngsat.com/atlantic.html";

        String[] urls = {urlEurope, urlAsia, urlAmerica, urlAtlantic};

        for (String url : urls) {
            try {
                String currentUrl = url;
                final Document document = Jsoup.connect(currentUrl).timeout(60 * 1000).get();
                String previousPosition = "";
                String previousName = "";

                int endIndex = document.select("td > table > tbody > tr > td > table > tbody > tr").size() - 2;

                for (Element row : document.select("td > table > tbody > tr > td > table > tbody > tr").subList(0, endIndex)) {
                    try {
                        String position = row.select("td:nth-of-type(1)").text();
                        String name = row.select("td:nth-of-type(2)").text();

                        name = name.replaceAll("\\(.*?\\)", "").trim();

                        if (position.matches("^\\d.*$")) {
                            lyngSats.add(setLyngSat(position, name));
                            previousPosition = position;
                            previousName = name;
                            System.out.println("Satellite data successfully retrieved: " + name);
                        } else if (!previousPosition.isEmpty()) {
                            position = position.replaceAll("\\(.*?\\)", "").trim();
                            lyngSats.add(setLyngSat(previousPosition, position));
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing satellite information: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.err.println("Error fetching data from: " + url);
            }
        }

        //transponders
        for (int i = 0; i < lyngSats.size(); i++) {
            LyngSat lyngSat = lyngSats.get(i);

            String transponderUrl = "";

            try {
                transponderUrl = "https://www.lyngsat.com/" + lyngSat.getName().replace(" ", "-").replace("Ă„", "A").replace("ĂĽ", "u").replace("'", "").replace("/", "-") + ".html";

                final Document document = Jsoup.connect(transponderUrl).timeout(60 * 1000).get();

                String regex = "^[0-9]{4,5}? [A-Z]?";

                Pattern pattern = Pattern.compile(regex);

                for (Element row : document.select(".bigtable > tbody > tr > td > table > tbody > tr ")) {
                    try {
                        final String row1 = row.select("td:nth-of-type(1)").text();
                        final String row2 = row.select("td:nth-of-type(2)").text();

                        Matcher matcher = pattern.matcher(row1);

                        if (matcher.find()) {
                            String word1Row1 = "0";
                            String word2Row1 = "None";

                            String word1Row2 = "None";
                            String word2Row2 = "None";
                            String word3Row2 = "0";
                            String word4Row2 = "None";

                            String[] wordsRow1 = row1.split("\\s+");
                            word1Row1 = wordsRow1[0];
                            word2Row1 = wordsRow1[1];

                            String[] wordsRow2 = row2.split("\\s+");

                            for (int j = 0; j < Math.min(wordsRow2.length, 5); j++) {
                                if (wordsRow2[j].startsWith("DVB-") || wordsRow2[j].startsWith("JSDB-") || wordsRow2[j].startsWith("MPEG") || wordsRow2[j].startsWith("DSS") || wordsRow2[j].startsWith("Digicipher")) {
                                    if (wordsRow2[j].startsWith("Digicipher")) {
                                        word1Row2 = wordsRow2[j] + " " + wordsRow2[j + 1];
                                    } else {
                                        word1Row2 = wordsRow2[j];
                                    }
                                }
                                if (wordsRow2[j].matches("\\d+")) {
                                    word3Row2 = wordsRow2[j];
                                }
                                if (wordsRow2[j].contains("/")) {
                                    word4Row2 = wordsRow2[j];
                                }
                                if (wordsRow2[j].contains("PSK")) {
                                    word2Row2 = wordsRow2[j];
                                }
                            }

                            String[] str = {word1Row1, word2Row1, word1Row2, word2Row2, word3Row2, word4Row2};

                            lyngSat.addTransponder(setTransponder(str));
                            System.out.println("Transponder data successfully retrieved for satellite: " + lyngSat.getName());
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing transponder information: " + e.getMessage());
                    }
                }
                lyngSats.set(i, lyngSat);
            } catch (IOException e) {
                System.err.println("Error fetching transponder data from: " + transponderUrl);
            }
        }
        return lyngSats;
    }
    public static LyngSat.Transponder setTransponder(String[] str) {
        LyngSat.Transponder transponder = new LyngSat.Transponder();

        try {
            transponder.setFreq(Integer.parseInt(str[0]));
            transponder.setPol(str[1]);
            transponder.setFormat(str[2]);
            transponder.setMod(str[3]);
            transponder.setSR(Integer.parseInt(str[4]));
            transponder.setFEC(str[5]);
            // tylko tyle jest informacji
            transponder.setBand("None");
            transponder.setKontinent("None");
            transponder.setProvider("None");
        } catch (NumberFormatException e) {
            System.err.println("Error parsing transponder data: " + e.getMessage());
        }
        return transponder;
    }

    public static LyngSat setLyngSat(String Position, String Name) {
        LyngSat lyngSat = new LyngSat();
        lyngSat.setPosition(Position);
        lyngSat.setName(Name);
        // tylko tyle jest informacji
        lyngSat.setIndex(0);
        lyngSat.setNorad(0);
        lyngSat.setCospar("None");
        lyngSat.setModel("None");
        lyngSat.setLaunch(new Date(0, 0, 0));

        return lyngSat;
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
