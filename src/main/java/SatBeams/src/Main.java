import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import scraper.SatBeam;
import scraper.SatBeamScraper;
import scraper.WebsiteData;

import java.util.List;
import java.util.Scanner;

public class Main {
    protected static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        try {
            // Set the range for scraping
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter Range Start (ID of first Sat to scrape; min 1): ");
            int rangeStart = scanner.nextInt();
            System.out.print("Enter Range End (ID of last Sat to scrape; max 608): ");
            int rangeEnd = scanner.nextInt();

            // Perform scraping
            SatBeamScraper scraper = new SatBeamScraper();
            List<SatBeam> SatelliteList = scraper.ScrapeData(rangeStart, rangeEnd);
            // Print scraped data
            for (SatBeam satellite : SatelliteList) {
                System.out.println(satellite.toString());
            }

        } catch (Exception e) {
            logger.error("Error occurred: ", e);
        }
    }
}
