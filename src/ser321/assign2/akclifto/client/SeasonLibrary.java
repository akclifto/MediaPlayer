package ser321.assign2.akclifto.client;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.*;


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
 * Purpose: SeasonLibrary is a class whose properties inidividual episodes from
 * a TV series that pulls information from omdbapi.com
 * <p>
 * Ser321 Principles of Distributed Software Systems
 *
 * @author Tim Lindquist (Tim.Linquist@asu.edu),
 * Software Engineering, CIDSE, IAFSE, ASU Poly
 * @author Adam Clifton akclifto@asu.edu
 * Software Engineering, ASU
 * @version March 2020
 */
public class SeasonLibrary implements Library {

    private HashMap<String, SeriesSeason> library;

    private static final String fileName = "series.json";
    private List<SeriesSeason> seriesSeasonList; // List of SeriesSeason objects
    private static SeasonLibrary sLibrary = null;

//for tests
    public SeasonLibrary() {

        this.library = new HashMap<>();
        this.seriesSeasonList = new ArrayList<>();
    }

    /**
     * Construct Season Library, Singleton
     * @return SeasonLibrary
     * */
    public static SeasonLibrary getInstance() {

        if (sLibrary == null) {
            sLibrary = new SeasonLibrary();
         //   sLibrary.restoreLibraryFromFile(fileName);
        }
        return sLibrary;
    }

    public SeasonLibrary getSeasonLibrary(){
        return this;
    }

    public int getlibrarySize(){
        return library.size();
    }

    public boolean removeSeason(String title) {

        return remove(title);
    }

    @Override
    public boolean add(SeriesSeason ss) {
        boolean result = false;
        System.out.println("Adding: " + ss.getTitle());
        try {
            library.put(ss.getTitle(), ss);
            result = true;
        } catch (Exception ex) {
            System.out.println("exception in add: " + ex.getMessage());
        }
        return result;
    }


    @Override
    public boolean remove(String mediaTitle) {
        boolean result = false;
        System.out.println("Removing " + mediaTitle);
        try {
            library.remove(mediaTitle);
            result = true;
        } catch (Exception ex) {
            System.out.println("exception in remove: " + ex.getMessage());
        }
        return result;
    }


    @Override
    public SeriesSeason get(String mediaTitle) {
        SeriesSeason result = null;
        try {
            result = library.get(mediaTitle);
        } catch (Exception ex) {
            System.out.println("exception in get: " + ex.getMessage());
        }
        return result;
    }


    @Override
    public String[] getTitles() {
        String[] result = null;
        try {
            Set<String> vec = library.keySet();
            result = vec.toArray(new String[]{});
        } catch (Exception ex) {
            System.out.println("exception in getTitles: " + ex.getMessage());
        }
        return result;
    }

    @Override
    public List<SeriesSeason> getSeriesSeason() {

        if (seriesSeasonList.isEmpty()) {
            System.out.println("The series list is empty!");
            return null;
        }

        for (SeriesSeason series : seriesSeasonList) {
            System.out.println(series.getTitle() + " Season " + series.getSeason());
        }
        return seriesSeasonList;
    }

    @Override
    public SeriesSeason getSeriesSeason(String title) {

        for (SeriesSeason series : seriesSeasonList) {
            if (series.getTitle().equalsIgnoreCase(title)) {
                System.out.println(title + "was found in the SeriesSeason list and returned.");
                return series;
            }
        }
        System.out.println(title + "was not found in the SeriesSeason list!");
        return null;
    }

    @Override
    public void addSeriesSeason() {

        //TODO
    }

    public void addSeriesSeason(SeriesSeason seriesSeason) {

        if (seriesSeasonList.isEmpty()) {
            seriesSeasonList.add(seriesSeason);
            System.out.println(seriesSeason.getTitle() + "was added to the Library list");
            return;
        }

        boolean flag = false;
        for (SeriesSeason value : seriesSeasonList) {
            if (value.getTitle().equals(seriesSeason.getTitle())) {
                flag = true;
                break;
            }
        }
        if (flag) {
            System.out.println("Episode already included in the Episode list.");
        } else {
            seriesSeasonList.add(seriesSeason);
            System.out.println(seriesSeason.getTitle() + " was added to the Library list");
        }
    }

    @Override
    public void removeSeriesSeason(String title) {

        if(seriesSeasonList.isEmpty()){
            System.out.println("SeriesSeason List is empty.");
            return;
        }

        for (SeriesSeason series : seriesSeasonList) {
            if (series.getTitle().equalsIgnoreCase(title)) {
                System.out.println(title + " was found and removed from the list.");
                seriesSeasonList.remove(series);
                break;
            }
        }
        System.out.println(title + "was not found in the list!");
    }

    @Override
    public boolean saveLibraryToFile(String fileName) {

        System.out.println("\nSaving current library to file.");
        JSONObject jsonSeries = constructSaveFile();
        try(PrintWriter out = new PrintWriter(fileName)){
            out.println(jsonSeries.toString(4));

            System.out.println(jsonSeries.toString(4));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public void constructJSON() {




    }

    /**
     * Helper method to serialize a file for JSON output.
     * @return JSONArray for write output.
     * */
    private JSONObject constructSaveFile() {

        JSONObject seriesSeasons = new JSONObject();
        JSONArray seriesRoot = new JSONArray();
        JSONObject libraryRoot = new JSONObject();
        try {

            Set<String> keys = library.keySet();

            //set the root JSONArray
            for(String key : keys) {
                //construct the next JSON file level: series/seasons
                JSONObject seriesJSON = new JSONObject();
                seriesJSON.put("title", library.get(key).getTitle());
                seriesJSON.put("seriesSeason", library.get(key).getSeason());
                seriesJSON.put("imdbRating", library.get(key).getImdbRating());
                seriesJSON.put("genre", library.get(key).getGenre());
                seriesJSON.put("poster", library.get(key).getPosterLink());
                seriesJSON.put("plotSummary", library.get(key).getPlotSummary());

                //construct the inner most JSON file level: episodes
                JSONArray episodes = new JSONArray();

                for(int i = 0; i < library.get(key).getEpisodeList().size(); i++) {
                    Episode episode = library.get(key).getEpisodeList().get(i);
                    JSONObject epiJSON = new JSONObject();
                    epiJSON.put("name", episode.getName());
                    epiJSON.put("imdbRating", episode.getImdbRating());
                    episodes.put(epiJSON);
                }

                //link the episodes with the within series level
                seriesJSON.put("episodes", episodes);
                //link the series array with the object
                seriesRoot.put(seriesJSON);
                //link with root level
 //               seriesSeasons.put("series", seriesRoot);
                libraryRoot.put("library", seriesRoot);
            }

        } catch(Exception ex) {
            System.out.println("Exception saving the file to library: " + ex.getMessage());
            ex.printStackTrace();
        }

        return libraryRoot;
    }



    @Override
    public boolean restoreLibraryFromFile(String filename) {

        try {
            clearLibrary();
            this.library = new HashMap<>();
            seriesSeasonList = new ArrayList<>();
            initialize(filename);
            return true;
        } catch(Exception ex){
            ex.printStackTrace();
            return false;
        }

    }

    public boolean loadHistory(String fileName) {

        clearLibrary();

//        try{
//            String content = Files.readString(Paths.get(fileName));
//            JSONObject jsonSeries = new JSONObject(content);
//            System.out.println(jsonSeries.toString(2));
//
//            JSONObject json = jsonSeries.getJSONObject("library");
//            JSONArray seriesArray = json.getJSONArray("series");
//            for(int i = 0; i < jsonSeries.length(); i++){
//                JSONObject obj = jsonSeries.getJSONObject(i);
//                library.put(series.getString("title"), new SeriesSeason(series));
//
//
//
//                //construct the inner most JSON file level: episodes
//                JSONArray episodes = new JSONArray();
//
//
//
//            }
//        } catch(Exception ex) {
//            System.out.println("Exception loading JSON file: " +ex.getMessage());
//            ex.printStackTrace();
//            return false;
//        }
          return true;
    }

    /**
     * Helper method to load JSON file from library.
     * @param fileName : path-to or name-of JSON file
     * @return void
     * */
    public void initialize(String fileName){

        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName);
            if (is == null) {
                is = new FileInputStream(new File(fileName));
            }

            JSONObject media = new JSONObject(new JSONTokener(is));

            Iterator<String> it = media.keys();
            while (it.hasNext()) {
                String mediaTitle = it.next();
                JSONObject aMed = media.optJSONObject(mediaTitle);
                if (aMed != null) {
                    SeriesSeason ss = new SeriesSeason(aMed);
                    library.put(mediaTitle, ss);
                    seriesSeasonList.add(ss);
                }
            }
        } catch (Exception ex) {
            System.out.println("Exception reading " + fileName + ": " + ex.getMessage());
        }
    }

    public void clearLibrary(){
        library.clear();
    }


    public void printAll(){

        String sep = "";
        for(SeriesSeason ss : seriesSeasonList){
            System.out.print(sep + ss.toJSONString());
            sep = ", ";
        }
    }


}