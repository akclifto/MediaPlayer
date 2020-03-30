package ser321.assign2.akclifto.client;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
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

    private HashMap<String, SeriesSeason> libraryMap;
    private static final String fileName = "series.json";
    private List<SeriesSeason> seriesSeasonList; // List of SeriesSeason objects
    private static SeasonLibrary sLibrary = null;

    /**
     * Constructor used for tests.
     * */
    public SeasonLibrary() {

        this.libraryMap = new HashMap<>();
        this.seriesSeasonList = new ArrayList<>();
    }

    /**
     * Construct Season Library, Singleton
     * @return SeasonLibrary
     * */
    public static SeasonLibrary getInstance() {

        if (sLibrary == null) {
            sLibrary = new SeasonLibrary();
            sLibrary.restoreLibraryFromFile(fileName);
        }
        return sLibrary;
    }

    /*All setters/getters*/
    public SeasonLibrary getSeasonLibrary(){
        return this;
    }
    public int getlibrarySize(){
        return libraryMap.size();
    }

//    @Override
//    public SeriesSeason get(String mediaTitle) {
//        SeriesSeason result = null;
//        try {
//            result = libraryMap.get(mediaTitle);
//        } catch (Exception ex) {
//            System.out.println("exception in get: " + ex.getMessage());
//        }
//        return result;
//    }


    @Override
    public String[] getSeriesSeasonTitles() {

        String[] result = new String[seriesSeasonList.size()];
        try {

            for(int i = 0; i < seriesSeasonList.size(); i++) {

                result[i] = seriesSeasonList.get(i).getTitle();
            }
        } catch (Exception ex) {
            System.out.println("exception in getTitles: " + ex.getMessage());
        }
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
        return seriesSeasonList;
    }

    @Override
    public String getSeriesSeason() {

        StringBuilder res= new StringBuilder();
        System.out.println("Series in library: ");
        for(SeriesSeason ss : seriesSeasonList){
            System.out.println(ss.getTitle() + ",  " + ss.getSeason());
            res.append(ss.getTitle()).append(", ").append(ss.getSeason());
        }
        return res.toString();

    }

    @Override
    public SeriesSeason getSeriesSeason(String title, String season) {

        for (SeriesSeason series : seriesSeasonList) {
            if (series.getTitle().equalsIgnoreCase(title) && series.getSeason().equalsIgnoreCase(season)) {
                System.out.println(title + " was found in the SeriesSeason list and returned.");
                return series;
            }
        }
        System.out.println(title + " was not found in the SeriesSeason list!");
        return null;
    }

    @Override
    public SeriesSeason getSeriesSeason(String title){

        for (SeriesSeason series : seriesSeasonList) {
            if (series.getTitle().equalsIgnoreCase(title)) {
                System.out.println(title + " was found in the SeriesSeason list and returned.");
                return series;
            }
        }
        System.out.println(title + " was not found in the SeriesSeason list!");
        return null;
    }


    @Override
    public void addSeriesSeason(SeriesSeason seriesSeason) {

        try {
            if (seriesSeasonList.isEmpty()) {
                seriesSeasonList.add(seriesSeason);
                System.out.println(seriesSeason.getTitle() + " was added to the Library list for " + seriesSeason.getTitle());
                return;
            }
            boolean flag = false;
            for (SeriesSeason value : seriesSeasonList) {
                if (value.getSeason().equals(seriesSeason.getSeason())) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                System.out.println("Series already included in the Library list.");
            } else {
                libraryMap.put(seriesSeason.getTitle(), seriesSeason);
                seriesSeasonList.add(seriesSeason);
                System.out.println(seriesSeason.getTitle() + " was added to the Library list for " + seriesSeason.getTitle());
            }
        } catch(Exception ex){
            System.out.println("Exception adding series to library: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    @Override
    public boolean removeSeriesSeason(String title) {

        if(seriesSeasonList.isEmpty()){
            System.out.println("SeriesSeason List is empty.");
            return false;
        }

        for (SeriesSeason series : seriesSeasonList) {
            if (series.getTitle().equalsIgnoreCase(title)) {
                System.out.println(title + " was found and removed from the list.");
                libraryMap.remove(title);
                seriesSeasonList.remove(series);
                return true;
            }
        }
        System.out.println(title + "was not found in the list!");
        return false;
    }

    @Override
    public void saveLibraryToFile(String fileName) {

        System.out.println("\nSaving current library to file: " + fileName);
        JSONObject jsonSeries = constructJSON();
        try(PrintWriter out = new PrintWriter(fileName)){
            out.println(jsonSeries.toString(4));

       // System.out.println(jsonSeries.toString(4));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper method to serialize a file for JSON output.
     * @return JSONArray for write output.
     * */
    private JSONObject constructJSON() {

        JSONObject master = new JSONObject();
        JSONArray seriesArr = new JSONArray();
//        System.out.println("List size: " + seriesSeasonList.size());
        try{
            JSONObject series;
            for(SeriesSeason ss : seriesSeasonList) {
                series = ss.toJson();
                seriesArr.put(series);
            }
            master.put("library", seriesArr);

        } catch(Exception ex) {
            System.out.println("Exception in ConstructJSON: " + ex.getMessage());
            ex.printStackTrace();
        }
        //System.out.println(master.toString(4));
        return master;
    }


    @Override
    public boolean restoreLibraryFromFile(String filename) {

        try {
            clearLibrary();
            this.libraryMap = new HashMap<>();
            this.seriesSeasonList = new ArrayList<>();
            initialize(filename);
            return true;
        } catch(Exception ex){
            ex.printStackTrace();
            return false;
        }

    }

    /**
     * Helper method to load JSON file from libraryMap.
     * @param fileName : path-to or name-of JSON file
     * @return void
     * */
    private void initialize(String fileName){

        try{

            String content = Files.readString(Paths.get(fileName));
            JSONObject jsonRoot = new JSONObject(content);
//            System.out.println(jsonRoot.toString(2));

            JSONArray libArr = jsonRoot.getJSONArray("library");
            for(int i = 0; i < libArr.length(); i++){
                JSONObject series = libArr.getJSONObject(i).getJSONObject("series");
//                System.out.println(libArr.getJSONObject(i).toString());
                SeriesSeason ss = new SeriesSeason(series);
                addSeriesSeason(ss);  //add to library map and list
            }
        } catch(Exception ex) {
            System.out.println("Exception loading JSON file: " +ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Helper method to clear the library and list
     * @return void.
     * */
    private void clearLibrary(){

        libraryMap.clear();
        seriesSeasonList.clear();
    }


    /**
     * Helper method to print everything in the library. Used for debugging.
     * @return void.
     * */
    public void printAll(){

        System.out.println("\nPRINTING SERIES SEASON LIST CONTENTS: ");
        if(seriesSeasonList.isEmpty()){
            System.out.println("List is empty");
            return;
        }

        String sep = "";
        for(SeriesSeason ss : seriesSeasonList){
            System.out.print(sep + ss.toJSONString());
            sep = ", ";
        }
    }

}