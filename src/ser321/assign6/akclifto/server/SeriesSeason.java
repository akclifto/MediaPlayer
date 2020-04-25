package ser321.assign6.akclifto.server;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
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
 * Purpose: SeriesSeason is a class whose properties describe a TV show series
 * that pulls information from omdbapi.com
 * <p>
 * Ser321 Principles of Distributed Software Systems
 *
 * @author Tim Lindquist (Tim.Linquist@asu.edu),
 * Software Engineering, CIDSE, IAFSE, ASU Poly
 * @author Adam Clifton akclifto@asu.edu
 * Software Engineering, ASU
 * @version April 2020
 */
public class SeriesSeason {

    private String title;       // title of the TV show/series
    private String seriesSeason;// seriesSeason number
    private String imdbRating;  // imdb rating
    private String genre;       // genre of the series
    private String posterLink;  // link to the movie poster
    private String plotSummary; // summary of the series plot
    private List<Episode> episodeList; // List of Episodes


    /**
     * Explicit constructor call.
     * */
    public SeriesSeason(String title, String seriesSeason, String imdbRating, String genre,
                        String posterLink, String plotSummary) {

        this.title = title;
        this.seriesSeason = seriesSeason;
        this.imdbRating = imdbRating;
        this.genre = genre;
        this.posterLink = posterLink;
        this.plotSummary = plotSummary;
        episodeList = new ArrayList<>();
    }


    /**
     * Method used to import JSON file from SeasonLibrary to construct in
     * Series Season object.
     * @param jsonObject : JSON object to transmit information about a series/seriesSeason
     * @return void
     * @see LibraryServer
     * */
    public SeriesSeason(JSONObject jsonObject){

        episodeList = new ArrayList<>();

        try{

            this.title = jsonObject.getString("title");
            this.seriesSeason = jsonObject.getString("seriesSeason");
            this.imdbRating = jsonObject.getString("imdbRating");
            this.genre = jsonObject.getString("genre");
            this.posterLink = jsonObject.getString("poster");
            this.plotSummary = jsonObject.getString("plotSummary");

            //go through episode list array
            JSONArray episodes = jsonObject.getJSONArray("episodes");
            for (int i = 0; i < episodes.length(); i++) {
                Episode episode = new Episode(episodes.getJSONObject(i));
                this.addToEpisodeList(episode);
            }
            System.out.println("Added new entry for: " + title);

        } catch(Exception ex){
            System.out.println("Exception importing from JSON file: " + ex.getMessage());
            ex.printStackTrace();
        }

    }


    /**
     * Check if series added from JSON file, or URL Search.  Pass accordingly
     * @param jsonObject :  object to be added to seriesSeason class
     * @param actionOption : check is URL "search" or JSON file "add".
     * */
    public SeriesSeason(JSONObject jsonObject, String actionOption){

        if(actionOption.equalsIgnoreCase("Add")){  //TODO---
            new SeriesSeason(jsonObject);
        } else {
            addFromURL(jsonObject);
        }
    }


    /**if series added from URL search, not a JSON file*/
    private void addFromURL(JSONObject jsonObject) {

        //TODO
    }

    /* All setters/getters */
    public String getTitle() {
        return this.title;
    }

    public String getSeason() {
        return this.seriesSeason;
    }

    public String getImdbRating() {
        return this.imdbRating;
    }

    public String getGenre() {
        return genre;
    }

    public String getPosterLink() {
        return this.posterLink;
    }

    public List<Episode> getEpisodeList() {
        return this.episodeList;
    }

    public String getPlotSummary() {
        return this.plotSummary;
    }

    /**
     * Method to get list of of Episode title names.
     * @return String array of title names for a given series.
     * */
    public String[] getEpisodeTitles(){

        if(episodeList.isEmpty()){

            return null;
        }

        String[] result = new String[episodeList.size()];
        try {

            for(int i = 0; i < episodeList.size(); i++) {

                result[i] = episodeList.get(i).getName();
            }
        } catch (Exception ex) {
            System.out.println("exception in getTitles: " + ex.getMessage());
        }
        return result;
    }

    /**
     * Helper method to check if episode list is empty
     * @return true if episode list is not empty, false otherwise.
     * */
    public boolean checkEpisodes() {

        return(!episodeList.isEmpty());
    }


    /**
     * Method to retrieve an episode from the episodeList
     * @param name : name of the episode
     * @return Episode if found, null if not found.
     * */
    public Episode getEpisode(String name) {

        for(Episode epi : episodeList) {
            if(epi.getName().equalsIgnoreCase(name)){
                return epi;
            }
        }
            System.out.println("Episode " + name + " not found in list");
            return null;
    }


    /**
     * Methods to add a series episode to the episode list.
     * @param episode : episodes to be added to the list.
     * @return void
     * */
    public void addToEpisodeList(Episode episode) {

        if (episodeList.isEmpty()) {
            episodeList.add(episode);
//            System.out.println(episode.getName() + " added to the Episode list for " + title + ", " + seriesSeason);
            return;
        }

        boolean flag = false;
        for (Episode value : episodeList) {
            if (value.getName().equals(episode.getName())) {
                flag = true;
                break;
            }
        }
        if (flag) {
            System.out.println("Episode already included in the Episode list.");
        } else {
            episodeList.add(episode);
//            System.out.println(episode.getName() + " added to the Episode list for " + title + ", " + seriesSeason);
        }
    }


    /**
     * Method to remove episode from list of series episodes.
     * @param title : title of episode to remove.
     * @return true if episode is remove from list, false otherwise.
     * */
    public boolean removeEpisode(String title){

        if(episodeList.isEmpty()){
            System.out.println("Episode List is empty.");
            return false;
        }

        for (Episode epi : episodeList) {
            if (epi.getName().equalsIgnoreCase(title)) {
                System.out.println(title + " was found and removed from the list.");
                episodeList.remove(epi);
                return true;
            }
        }
        //System.out.println(title + " was not found in the list!");
        return false;
    }

    /**
     * Methods to display String data in JSON file format for write output.
     * @return String of formatted SeriesSeason data.
     * */
    public String toJSONString() {

        String ret = "{}";
        try {
            ret = this.toJson().toString(4);
        } catch (Exception ex) {
            System.out.println("Exception in toJsonString: " + ex.getMessage());
        }
        return ret;
    }

    /**
     * Methods to serialize data to JSON file.
     * @return void
     * */
    public JSONObject toJson() {

        JSONObject series = new JSONObject();
        JSONObject seriesRoot = new JSONObject();

        try {
            series.put("title", title);
            series.put("seriesSeason", seriesSeason);
            series.put("imdbRating", imdbRating);
            series.put("genre", genre);
            series.put("poster", posterLink);
            series.put("plotSummary", plotSummary);

            JSONArray episodes = new JSONArray();
            for(int i = 0; i < episodeList.size(); i++) {
                episodes.put(i, episodeList.get(i).toJson());
            }
            series.put("episodes", episodes);
            seriesRoot.put("series", series);

        } catch (Exception ex) {
            System.out.println("Exception in toJson: " + ex.getMessage());
            ex.printStackTrace();
        }
        return seriesRoot;
    }


    /**
     * Debugging method to print out the list of episodes.
     * @return void
     * */
    public void printEpisodes(){

        System.out.println("\n" + title + ": ");
        for(Episode e : getEpisodeList()) {
            System.out.println(e.toString());
        }
        System.out.println();
    }


    /**
     * Override method to output String data to console
     * @return String of formatted console output.
     * */
    @Override
    public String toString() {

        return "Title: " + title +
                "\nSeason: " + seriesSeason +
                "\nIMDB Rating: " + imdbRating +
                "\nGenre: " + genre +
                "\nPosterLink: " + posterLink +
                "\nPlot Summary: " + plotSummary + "\n";
    }

}
