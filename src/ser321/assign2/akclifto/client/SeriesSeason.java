package ser321.assign2.akclifto.client;

import org.json.JSONObject;
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

    private String name;        // name of the TV show/series
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

    public SeriesSeason(String jsonString) {
        this(new JSONObject(jsonString));
    }

    /**
     * Explicit constructor call.
     * */
    public SeriesSeason(String name, String season, String imdbRating, String genre,
                        String posterLink, String plotSummary) {

        this.name = name;
        this.season = season;
        this.imdbRating = imdbRating;
        this.genre = genre;
        this.posterLink = posterLink;
        this.plotSummary = plotSummary;

    }

    /**
     * Method used to import JSON file from SeasonLibrary to construct in
     * Series Season object.
     * @param jsonObject : JSON object to transmit information about a series/season
     * @return void
     * @see SeasonLibrary
     * */
    public SeriesSeason(JSONObject jsonObject){

        try{

            this.name = jsonObject.getString("title");
            this.season = jsonObject.getString("seriesSeason");
            this.imdbRating = jsonObject.getString("imdbRating");
            this.genre = jsonObject.getString("genre");
            this.posterLink = jsonObject.getString("poster");
            this.plotSummary = jsonObject.getString("plotSummary");

            System.out.println("Added new entry for: " + season);

            //TODO add to episode list if season is the same



        } catch(Exception ex){
            System.out.println("Exception importing from JSON file: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /* All setters/getters */
    public void setTitle(String name) {
        this.name = name;
    }

    public String getTitle() {
        return this.name;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getSeason() {
        return this.season;
    }

    public void setImdbRating(String imdbRating) {
        this.imdbRating = imdbRating;
    }

    public String getImdbRating() {
        return this.imdbRating;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getGenre() {
        return this.genre;
    }

    public void setPosterLink(String posterLink) {
        this.posterLink = posterLink;
    }

    public String getPosterLink() {
        return this.posterLink;
    }

    public void setEpisodeList(List<Episode> episodeList) {
        this.episodeList = episodeList;
    }

    public List<Episode> getEpisodeList() {
        return this.episodeList;
    }

    public String getPlotSummary() {
        return this.plotSummary;
    }

    public void setPlotSummary(String plotSummary) {
        this.plotSummary = plotSummary;
    }

    public void setEpisode(Episode episode) {
        this.episode = episode;
    }

    public Episode getEpisode() {
        return this.episode;
    }


    public void addToEpisodeList(Episode episode) {

        if (episodeList.isEmpty()) {
            episodeList.add(episode);
            System.out.println(episode.getName() + " added to the Episode list.");
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
            System.out.println(episode.getName() + " added to the Episode list.");
        }
    }

    public String toJsonString() {
        String ret = "{}";
        try {
            ret = this.toJson().toString(0);
        } catch (Exception ex) {
            System.out.println("Exception in toJsonString: " + ex.getMessage());
        }
        return ret;
    }

    public JSONObject toJson() {

        JSONObject obj = new JSONObject();
        try {
            obj.put("title", name);
            obj.put("seriesSeason", season);
            obj.put("imdbRating", imdbRating);
            obj.put("genre", genre);
            obj.put("poster", posterLink);
            obj.put("plotSummary", plotSummary);
        } catch (Exception ex) {
            System.out.println("Exception in toJson: " + ex.getMessage());
        }
        return obj;
    }

    @Override
    public String toString() {

        return "Title: " + name +
                "\nSeason: " + season +
                "\nIMDB Rating: " + imdbRating +
                "\nGenre: " + genre +
                "\nPosterLink: " + posterLink +
                "\nPlot Summary: " + plotSummary + "\n";
    }

}
