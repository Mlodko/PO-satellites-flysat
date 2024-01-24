import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LyngsatParser {
    public static ArrayList<LyngSat> parse() {
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

    public static void main(String[] args) {
        try {
            LyngSatParser parser = new LyngSatParser();
            ArrayList<LyngSat> result = parser.parse();

            /*for (LyngSat lyngSat : result) {
                System.out.println("Satellite Name: " + lyngSat.getName() + " Satellite Position: " + lyngSat.getPosition());

                for (LyngSat.Transponder transponder : lyngSat.getTransponders()) {
                    System.out.println("Freq: " + transponder.getFreq() + " Pol: " + transponder.getPol() + ", Format: " + transponder.getFormat() + ", Mod: " + transponder.getMod() + ", SR: " + transponder.getSR() + ", FEC: " + transponder.getFEC());
                }

                System.out.println("-------------");
            }*/
        } catch (Exception e) {
            System.err.println("General Error: " + e.getMessage());
        }
    }
}