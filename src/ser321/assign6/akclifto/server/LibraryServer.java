package ser321.assign6.akclifto.server;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


/**
 * Copyright 2020 Adam Clifton (akclifto@asu.edu),
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Purpose: LibraryServer server implementation for the media player to manage
 * TV series information, which it pulls from omdbapi.com.  The Server is designed to
 * be distributed and used remotely.
 * <p>
 * Ser321 Principles of Distributed Software Systems
 *
 * @author Tim Lindquist (Tim.Linquist@asu.edu),
 * Software Engineering, CIDSE, IAFSE, ASU Poly
 * @author Adam Clifton akclifto@asu.edu
 * Software Engineering, ASU
 * @version April 2020
 */
public class LibraryServer implements Library, LibraryHelper {

    private Hashtable<String, SeriesSeason> libraryMap;
    private static final String fileName = "series.json";
    private List<SeriesSeason> seriesSeasonList; // List of SeriesSeason objects
    private static LibraryServer sLibrary = null;

    /**
     * Constructor used for tests.
     */
    public LibraryServer() {

        this.libraryMap = new Hashtable<>();
        this.seriesSeasonList = new ArrayList<>();
        //this.restoreLibraryFromFile(fileName);
    }

    /**
     * Construct Season Library, Singleton
     *
     * @return SeasonLibrary
     */
    public static LibraryServer getInstance() {

        try {
            if (sLibrary == null) {
                sLibrary = new LibraryServer();
                sLibrary.restoreLibraryFromFile(fileName);
            }
        } catch (Exception ex) {
            System.out.println("Exception in getInstance(): " + ex.getMessage());
            ex.printStackTrace();
        }
        return sLibrary;
    }

    @Override
    public LibraryServer getLibrary() {
        System.out.println("The Library has been sent to the client.");
        return sLibrary;
    }

    @Override
    public Hashtable<String, SeriesSeason> getLibraryMap() {
        return libraryMap;
    }


    @Override
    public String[] getSeriesSeasonTitles() {

        String[] result = new String[seriesSeasonList.size()];
        try {

            for (int i = 0; i < seriesSeasonList.size(); i++) {

                result[i] = seriesSeasonList.get(i).getTitle();
            }
        } catch (Exception ex) {
            System.out.println("exception in getTitles: " + ex.getMessage());
        }
        System.out.println("SeriesSeasonTitles request completed for client");
        return result;
    }

    @Override
    public List<SeriesSeason> getSeriesSeasonList() {

        if (seriesSeasonList.isEmpty()) {
            System.out.println("The series list is empty!");
        }

        for (SeriesSeason series : seriesSeasonList) {
            System.out.println(series.getTitle() + " - " + series.getSeason());
        }
        System.out.println("getSeriesSeasonList request completed for client.");
        return seriesSeasonList;
    }

    @Override
    public String getSeriesSeason() {

        StringBuilder res = new StringBuilder();
        System.out.println("Series in library: ");
        for (SeriesSeason ss : seriesSeasonList) {
            System.out.println(ss.getTitle() + ",  " + ss.getSeason());
            res.append(ss.getTitle()).append(", ").append(ss.getSeason());
        }
        System.out.println("getSeriesSeason request completed for client.");
        return res.toString();

    }

    @Override
    public SeriesSeason getSeriesSeason(String title, String season) {

        for (SeriesSeason series : seriesSeasonList) {
            if (series.getTitle().equalsIgnoreCase(title) && series.getSeason().equalsIgnoreCase(season)) {
                return series;
            }
        }
        System.out.println(title + " was not found in the SeriesSeason list!");
        return null;
    }

    @Override
    public SeriesSeason getSeriesSeason(String title) {

        for (SeriesSeason series : seriesSeasonList) {
            if (series.getTitle().equalsIgnoreCase(title)) {
                return series;
            }
        }
        System.out.println(title + " was not found in the SeriesSeason list!");
        return null;
    }

    @Override
    public int getLibrarySize() {
        return seriesSeasonList.size();
    }

    @Override
    public String jsonGetSeries(String seriesTitle) {

        for (SeriesSeason ss : seriesSeasonList) {
            if (ss.getTitle().equalsIgnoreCase(seriesTitle)) {
                return ss.toJSONString();
            }
        }
        System.out.println(seriesTitle + " was not found in the SeriesSeason list!");
        return null;
    }

    @Override
    public String jsonGetEpisode(String seriesTitle, String episodeName) {

        List<Episode> epList = getSeriesSeason(seriesTitle).getEpisodeList();
        for (Episode epi : epList) {
            if (epi.getName().equalsIgnoreCase(episodeName)) {
                return epi.toJSONString();
            }
        }
        System.out.println(episodeName + " was not found in list for " + seriesTitle);
        return null;
    }

    @Override
    public String getSeriesTitle(String title) {

        //System.out.println("Processed getSeriesTitle for " + title + " for client.");
        return getSeriesSeason(title).getTitle();
    }

    @Override
    public String getSeriesImdbRating(String title) {

        return getSeriesSeason(title).getImdbRating();
    }

    @Override
    public String getGenre(String title) {

        return getSeriesSeason(title).getGenre();
    }

    @Override
    public String getPosterLink(String title) {

        return getSeriesSeason(title).getPosterLink();
    }

    @Override
    public String getSummary(String title) {

        return getSeriesSeason(title).getPlotSummary();
    }

    @Override
    public String[] getEpisodeTitles(String title) {

        return getSeriesSeason(title).getEpisodeTitles();
    }

    @Override
    public int getEpisodeListSize(String title) {

        return getSeriesSeason(title).getEpisodeList().size();

    }

    @Override
    public boolean checkSeries(String title) {

        try {
            for (SeriesSeason ss : seriesSeasonList) {
                if (ss.getTitle().equalsIgnoreCase(title)) {
                    return false;
                }
            }
        } catch (Exception ex) {
            System.out.println("Exception in checkSeries: " + ex.getMessage());
        }
        return true;
    }

    @Override
    public String getEpisodeName(String parent, String node) {

        return getSeriesSeason(parent).getEpisode(node).getName();
    }

    @Override
    public String getEpisodeImdb(String parent, String node) {

        return getSeriesSeason(parent).getEpisode(node).getImdbRating();
    }

    @Override
    public String getEpisodeSummary(String parent, String node) {

        System.out.println("Processed information for " + node + " and " + parent + " for client.");
        return getSeriesSeason(parent).getEpisode(node).getEpSummary();
    }

    @Override
    public boolean addEpisode(String series, String episode) {

        Episode epi = getSeriesSeason(series).getEpisode(episode);
        try {
            this.getSeriesSeason(series).addToEpisodeList(epi);
        } catch (Exception ex) {
            System.out.println("Exception adding episode to series: " + ex.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public synchronized boolean removeEpisode(String series, String episode) {

        return getSeriesSeason(series).removeEpisode(episode);
    }

    @Override
    public synchronized boolean checkEpisodes(String title) {

        return getSeriesSeason(title).checkEpisodes();
    }


    @Override
    public void addSeriesSeason(SeriesSeason seriesSeason) {

        try {
            if (seriesSeasonList.isEmpty()) {
                seriesSeasonList.add(seriesSeason);
//                System.out.println(seriesSeason.getTitle() + " was added to the Library list for " + seriesSeason.getTitle());
                return;
            }
            boolean flag = false;
            for (SeriesSeason value : seriesSeasonList) {
                if (value.getSeason().equalsIgnoreCase(seriesSeason.getSeason())
                        && value.getTitle().equalsIgnoreCase(seriesSeason.getTitle())) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                System.out.println("Series already included in the Library list.");
            } else {
                libraryMap.put(seriesSeason.getTitle(), seriesSeason);
                seriesSeasonList.add(seriesSeason);
                System.out.println("Request completed: "
                        + seriesSeason.getTitle() + " was added to the Library list for ");
            }
        } catch (Exception ex) {
            System.out.println("Exception adding series to library: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    @Override
    public synchronized boolean removeSeriesSeason(String title) {

        if (seriesSeasonList.isEmpty()) {
            System.out.println("SeriesSeason List is empty.");
            return false;
        }

        for (SeriesSeason series : seriesSeasonList) {
            if (series.getTitle().equalsIgnoreCase(title)) {
                //  System.out.println(title + " was found and removed from the list for client.");
                libraryMap.remove(title);
                seriesSeasonList.remove(series);
                return true;
            }
        }
        System.out.println(title + "was not found in the list!");
        return false;
    }


    @Override
    public synchronized boolean saveLibraryToFile(String fileName) {

        //System.out.println("\nSaving current library to file for client: " + fileName);
        JSONObject jsonSeries = constructJSON();
        try (PrintWriter out = new PrintWriter(fileName)) {
            out.println(jsonSeries.toString(4));

//        System.out.println(jsonSeries.toString(4));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        System.out.println("Request completed: " + fileName + " saved for client.");
        return true;
    }

    /**
     * Helper method to serialize a file for JSON output.
     *
     * @return JSONArray for write output.
     */
    private JSONObject constructJSON() {

        JSONObject master = new JSONObject();
        JSONArray seriesArr = new JSONArray();

        try {
            JSONObject series;
            for (SeriesSeason ss : seriesSeasonList) {
                series = ss.toJson();
                seriesArr.put(series);
            }
            master.put("library", seriesArr);

        } catch (Exception ex) {
            System.out.println("Exception in ConstructJSON: " + ex.getMessage());
            ex.printStackTrace();
        }
        //System.out.println(master.toString(4));
        return master;
    }


    @Override
    public synchronized boolean restoreLibraryFromFile(String filename) {

        boolean flag;
        try {
            clearLibrary();
            flag = initializeLibrary(filename);
            System.out.println("Request completed: library " + filename + " been restored for client.");
        } catch (Exception ex) {
            System.out.println("Exception restoring library: " + ex.getMessage());
            flag = false;
        }
        return flag;
    }

    /**
     * Helper method to load JSON file from libraryMap.
     *
     * @param fileName : path-to or name-of JSON file
     * @return true if JSON initialized correctly, false otherwise.
     */
    private boolean initializeLibrary(String fileName) {

        try {

            String content = Files.readString(Paths.get(fileName));
            JSONObject jsonRoot = new JSONObject(content);
            //System.out.println(jsonRoot.toString(2));

            JSONArray libArr = jsonRoot.getJSONArray("library");
            for (int i = 0; i < libArr.length(); i++) {
                JSONObject series = libArr.getJSONObject(i).getJSONObject("series");

                SeriesSeason ss = new SeriesSeason(series);
                addSeriesSeason(ss);  //add to library map and list
            }
        } catch (Exception ex) {
            System.out.println("Exception loading JSON file: " + ex.getMessage());
            return false;
        }
        return true;
    }


    @Override
    public synchronized boolean parseURLtoJSON(String jsonSeries, String jsonEpisodes) {

        try {
            //shared or cross-data points
            String seriesSeason, plotSummary;

            //set root objects
            JSONObject seriesRoot = new JSONObject(jsonSeries);
            JSONObject epiRoot = new JSONObject(jsonEpisodes);

            //set objects to store information
            JSONObject seriesObj = new JSONObject();
            JSONArray epiArray = new JSONArray();

            //add shared information
            seriesSeason = epiRoot.getString("Season");
            plotSummary = seriesRoot.getString("Plot");
            //add series information
            seriesObj.put("title", seriesRoot.getString("Title") + " - Season " + seriesSeason);
            seriesObj.put("genre", seriesRoot.getString("Genre"));
            seriesObj.put("plotSummary", seriesRoot.getString("Plot"));
            seriesObj.put("poster", seriesRoot.getString("Poster"));
            seriesObj.put("imdbRating", seriesRoot.getJSONArray("Ratings").getJSONObject(0).getString("Value"));
            seriesObj.put("seriesSeason", seriesSeason);

            //add episode information
            JSONArray epiIter = epiRoot.getJSONArray("Episodes");
            for (int i = 0; i < epiIter.length(); i++) {

                JSONObject epiObj = new JSONObject();
                JSONObject epi = epiIter.getJSONObject(i);
                epiObj.put("epSummary", plotSummary);
                epiObj.put("name", epi.getString("Title"));
                epiObj.put("imdbRating", epi.getString("imdbRating"));
                epiArray.put(epiObj);
            }

            seriesObj.put("episodes", epiArray);
            refreshLibrary(seriesObj);

        } catch (Exception ex) {
            System.out.println("Exception in parseURLtoJSON: " + ex.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Private helper methods to update library after adding new series Season from API search.
     *
     * @param jsonObject : JSONObject containing seriesSeason data
     */
    private void refreshLibrary(JSONObject jsonObject) {

        SeriesSeason ss = new SeriesSeason(jsonObject);
        addSeriesSeason(ss);
    }


    /**
     * Helper method to clear the library and list
     *
     * @return void.
     */
    private void clearLibrary() {

        libraryMap.clear();
        seriesSeasonList.clear();
        this.libraryMap = new Hashtable<>();
        this.seriesSeasonList = new ArrayList<>();

    }


    @Override
    public void printAll() {

        System.out.println("\nPRINTING SERIES SEASON LIST CONTENTS: ");
        if (seriesSeasonList.isEmpty()) {
            System.out.println("List is empty");
            return;
        }

        String sep = "";
        for (SeriesSeason ss : seriesSeasonList) {
            System.out.print(sep + ss.toJSONString());
            sep = ", ";
        }
    }


//    /**
//     * Main method to initialize server and library.
//     * @param args : arguments for hostId and regPort number.
//     * */
//    public static void main(String[] args) {
//
//        try{
//            String hostId = "localHost";
//            String regPort = "8888";
//            if(args.length >= 2) {
//                hostId = args[0];
//                regPort= args[1];
//            }
//            Library obj = LibraryServer.getInstance();
//            Naming.rebind("rmi://"+hostId+":"+regPort+"/LibraryServer", obj);
//            System.out.println("Server bound in registry as: "+
//                    "rmi://"+hostId+":"+regPort+"/LibraryServer");
//            System.out.println("Server Ready");
//        } catch (Exception ex) {
//            System.out.println("Exception initializing server: " + ex.getMessage());
//            ex.printStackTrace();
//        }
//    }

}