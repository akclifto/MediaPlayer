package ser321.assign2.akclifto.client;

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
 * @version March 2020
 */
public class SeriesSeason {

    private String title;        // title of the TV show/series
    private String season;      // season number
    private String imdbRating;  // imdb rating
    private String genre;       // genre of the series
    private String posterLink;  // link to the movie poster
    private String plotSummary; // summary of the plot

    private Episode episode;    // episode object
    private List<Episode> episodeList; // List of Episodes

    public SeriesSeason() {
        //ctor
    }

    public SeriesSeason(String title, String season, String imdbRating, String genre,
                        String posterLink, String plotSummary) {

        this.title = title;
        this.season = season;
        this.imdbRating = imdbRating;
        this.genre = genre;
        this.posterLink = posterLink;
        this.plotSummary = plotSummary;
        episodeList = new ArrayList<>();
    }

    public SeriesSeason(String jsonString) {
        this(new JSONObject(jsonString));
    }

    /**
     * Method used to import JSON file from SeasonLibrary to construct in
     * Series Season object.
     * @param jsonObject : JSON object to transmit information about a series/season
     * @return void
     * @see SeasonLibrary
     * */
    public SeriesSeason(JSONObject jsonObject){

        episodeList = new ArrayList<>();
        try {
            title = jsonObject.getString("title");
            season = jsonObject.getString("season");
            imdbRating = jsonObject.getString("imdbRating");
            genre = jsonObject.getString("genre");
            posterLink = jsonObject.getString("posterLink");
            plotSummary = jsonObject.getString("plotSummary");

            //go through episode list array
            JSONArray episodes = jsonObject.getJSONArray("episodes");
            for (int i = 0; i < episodes.length(); i++) {
                Episode episode = new Episode(episodes.getJSONObject(i));
                episodeList.add(episode);
            }

            System.out.println("Added new entry for: " + season);

        } catch (Exception ex) {
            System.out.println("Exception importing from JSON file: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    /**if series added from JSON file, not URL Search*/
    public SeriesSeason(JSONObject jsonObject, String actionOption){
        
        episodeList = new ArrayList<>();
        if(actionOption.equalsIgnoreCase("search")){

            try{

                title = jsonObject.getString("title");
                season = jsonObject.getString("season");
                imdbRating = jsonObject.getString("imdbRating");
                genre = jsonObject.getString("genre");
                posterLink = jsonObject.getString("posterLink");
                plotSummary = jsonObject.getString("plotSummary");

                //go through episode list array
                JSONArray episodes = jsonObject.getJSONArray("episodes");
                for (int i = 0; i < episodes.length(); i++) {
                    Episode episode = new Episode(episodes.getJSONObject(i));
                    episodeList.add(episode);
                }

                System.out.println("Added new entry for: " + season);

            } catch(Exception ex){
                System.out.println("Exception importing from JSON file: " + ex.getMessage());
                ex.printStackTrace();
            }
        } else {
            addFromURL(jsonObject);
        }
    }

    /**if series added from URL search, not a JSON file*/
    private void addFromURL(JSONObject jsonObject) {

        //TODO
    }

    /* All getters */
    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }


    public String getSeason() {
        return this.season;
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
     * Methods to add a series episode to the episode list.
     * @param episode : episodes to be added to the list.
     * @return void
     * */
    public void addToEpisodeList(Episode episode) {

        if (episodeList.isEmpty()) {
            episodeList.add(episode);
            System.out.println(episode.getName() + " added to the Episode list for " + title);
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
            System.out.println(episode.getName() + " added to the Episode list for " + title);
        }
    }

    /**
     * Methods to display String in JSON file format output.
     * @return void
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
     * Methods to serialize data to JSON file
     * @return void
     * */
    public JSONObject toJson() {

        JSONObject series = new JSONObject();
        try {
            series.put("title", title);
            series.put("seriesSeason", season);
            series.put("imdbRating", imdbRating);
            series.put("genre", genre);
            series.put("poster", posterLink);
            series.put("plotSummary", plotSummary);

            JSONArray episodes = new JSONArray();
            for(int i = 0; i < episodeList.size(); i++) {
                episodes.put(i, episodeList.get(i).toJson());
            }
            series.put("episodes", episodes);

        } catch (Exception ex) {
            System.out.println("Exception in toJson: " + ex.getMessage());
        }
        return series;
    }

    /**
     * Method to print out the list of episodes.
     * @return void
     * */
    public void printEpisodes(){

        System.out.println("\nEpisodes for " + title + ": ");
        for(Episode e : getEpisodeList()) {
            e.print();
            System.out.println();
        }
    }


    /**
     * Override method to output String data to console
     * @return String of formatted console output.
     * */
    @Override
    public String toString() {

        return "Title: " + title +
                "\nSeason: " + season +
                "\nIMDB Rating: " + imdbRating +
                "\nGenre: " + genre +
                "\nPosterLink: " + posterLink +
                "\nPlot Summary: " + plotSummary + "\n";
    }

}
