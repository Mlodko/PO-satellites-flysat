private List<StockEntry> fetchDataFromWeb() {
        List<StockEntry> scrapedData = new ArrayList<>();

        try {
            final String url = "https://www.biznesradar.pl/gielda/akcje_gpw";
            final Document document = Jsoup.connect(url).get();

            for (Element row : document.select("table.qTableFull tr")) {
                if (row.select("td:nth-of-type(1)").text().equals("") ||
                		row.select("td:nth-of-type(2)").text().equals("")) {
                    continue;
                } else {
                  // jedynie trzeba zobaczyc do czego sie odnosi u nas "td:nth-of-type(1)", itp
                  // czasami tabelki na stronach są chujowo zrobione i nie mają wartości w niektórych polach, 
                  // wtedy dodać kolejny || row.select("td:nth-of-type(x)").text().equals("")) do pierwszego if'a
                    final String tempProfil = row.select("td:nth-of-type(1)").text();
                    final String profil = tempProfil.replace(" ", "");
                    final String czas = row.select("td:nth-of-type(2)").text();
                    String kurs = row.select("td.bvalue:nth-of-type(3)").text();
                    if (kurs.equals("")) {
                        kurs = row.select("td:nth-of-type(3)").text();
                    }
                    
                    final String tempZmiana = row.select("td:nth-of-type(5)").text();
                    final String zmiana = tempZmiana.replace("+", "").replace("(", "").replace(")", "").replace("%", "");
                    final String tempWolumen = row.select("td:nth-of-type(10)").text();
                    final String wolumen = tempWolumen.replace(" ", "");
                    final String tempObrot = row.select("td:nth-of-type(11)").text();
                    final String obrot = tempObrot.replace(" ", "");

                    StockEntry entry = new StockEntry(profil, czas, Double.parseDouble(kurs), 
                    		Double.parseDouble(zmiana), Integer.parseInt(wolumen), Integer.parseInt(obrot));
                    
                    scrapedData.add(entry);
                }
            }

            String sortBy = loadConfig().getProperty("sort.criteria");
            if (sortBy == null) sortBy = "0";
            sortList(scrapedData, sortBy);

        } catch (IOException ex) {
            logger.error("Error fetching data", ex);
            // Handle the case when the data retrieval is complete
            SwingUtilities.invokeLater(() -> displayData(scrapedData));
        }

        return scrapedData;
    }
