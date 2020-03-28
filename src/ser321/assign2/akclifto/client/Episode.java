package ser321.assign2.akclifto.client;

import org.json.JSONObject;
import org.json.JSONString;

import java.io.Serializable;

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
 * Purpose: Episode is a class whose properties inidividual episodes from
 * a TV series that pulls information from omdbapi.com
 * <p>
 * Ser321 Principles of Distributed Software Systems
 *
 * @author Adam Clifton akclifto@asu.edu
 * Software Engineering, ASU
 * @version March 2020
 */
public class Episode implements JSONString, Serializable {

    private String name;        // name of episode
    private String imdbRating;  // imdb rating

    public Episode(){}


    /**
     * Explicit Constructor
     * */
    public Episode(String name, String imdbRating){

        this.name = name;
        this.imdbRating = imdbRating;
    }


    /**
     * JSONObject Constructor
     * */
    public Episode(JSONObject jsonObj){

        this.name = jsonObj.getString("title");
        this.imdbRating= jsonObj.getString("imdbRating");
    }

    public String getName() {
        return name;
    }

    public String getImdbRating() {
        return imdbRating;
    }


    /**
     * Method to serialize data to JSON file.
     * @return JSONObject
     * */
    public JSONObject toJson() {

        JSONObject episode = new JSONObject();
        episode.put("name", name);
        episode.put("imdbRating", imdbRating);
        return  episode;
    }

    /**
     * Method to serialize data to JSON, but outputs a string.
     * @return String of JSON data
     * */
    @Override
    public String toJSONString() {

        String result = "{}";
        try{
            result = this.toJson().toString(0);
        } catch(Exception ex) {
            System.out.println("Exception in Episode toJSONString(): " + ex.getMessage());
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public String toString() {

        return "Episode Name: " + name +
                "\nIMDB Rating: " + imdbRating;
    }

    public void print(){
        System.out.println("Episode: " + name);
        System.out.println("IMDB Rating: " + imdbRating);
    }

}

