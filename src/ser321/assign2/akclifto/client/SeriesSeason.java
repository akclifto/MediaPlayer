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
    private List<String> genre;       // genre of the series
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
     * Method used to import JSON file from SeasonLibrary to construct in
     * Series Season object.
     * @param jsonObject : JSON object to transmit information about a series/season
     * @return void
     * @see SeasonLibrary
     * */
    public SeriesSeason(JSONObject jsonObject){

        try{

            genre = new ArrayList<>();
            JSONArray jsonArray = jsonObject.getJSONArray("genre");
            for(int i = 0; i < jsonArray.length();i++) {
               genre.add(jsonArray.get(i).toString());
            }
            this.title = jsonObject.getString("title");
            this.season = jsonObject.getString("seriesSeason");
            this.imdbRating = jsonObject.getString("imdbRating");
            this.posterLink = jsonObject.getString("poster");
            this.plotSummary = jsonObject.getString("plotSummary");

            System.out.println("Added new entry for: " + season);

            //TODO add to episode list if season is the same



        } catch(Exception ex){
            System.out.println("Exception importing from JSON file: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**if series added from JSON file, not URL Search*/
    public SeriesSeason(JSONObject jsonObject, String actionOption){

        JSONObject data = jsonObject;

        if(actionOption.equalsIgnoreCase("search")){

            try{

                genre = new ArrayList<>();
                JSONArray jsonArray = jsonObject.getJSONArray("genre");
                for(int i = 0; i < jsonArray.length();i++) {
                    genre.add(jsonArray.get(i).toString());
                }

                this.title = jsonObject.getString("title");
                this.season = jsonObject.getString("seriesSeason");
                this.imdbRating = jsonObject.getString("imdbRating");
                this.posterLink = jsonObject.getString("poster");
                this.plotSummary = jsonObject.getString("plotSummary");

                System.out.println("Added new entry for: " + season);

                //TODO add to episode list if season is the same

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

    /* All setters/getters */
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

    public List<String> getGenre() {
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
            obj.put("title", title);
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

//    @Override
//    public String toString() {
//
//        return "Title: " + title +
//                "\nSeason: " + season +
//                "\nIMDB Rating: " + imdbRating +
//                "\nGenre: " + genre +
//                "\nPosterLink: " + posterLink +
//                "\nPlot Summary: " + plotSummary + "\n";
//    }

}
