package ser321.assign2.akclifto.client;

import com.sun.javafx.collections.SetAdapterChange;
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
    private List<SeriesSeason> seriesSeasonList = new LinkedList<>(); // List of SeriesSeason objects
    private static SeasonLibrary sLibrary = null;

//    public SeasonLibrary(){
//        library = new HashMap<>();
//    }

    public SeasonLibrary() {
        this.library = new HashMap<>();
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
                }
            }
        } catch (Exception ex) {
            System.out.println("Exception reading " + fileName + ": " + ex.getMessage());
        }
    }

    /**
     * Construct Season Library, Singleton
     * @return SeasonLibrary
     * */
    public static SeasonLibrary getInstance() {

        if (sLibrary == null) {
            sLibrary = new SeasonLibrary();
        }
        return sLibrary;
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

    @Override
    public void removeSeriesSeason(String title) {

        if(seriesSeasonList.isEmpty()){
            System.out.println("SeriesSeason List is empty.");
            return;
        }

        for (SeriesSeason series : seriesSeasonList) {
            if (series.getTitle().equalsIgnoreCase(title)) {
                System.out.println(title + "was found and removed from the list.");
                seriesSeasonList.remove(series);
                break;
            }
        }
        System.out.println(title + "was not found in the list!");
    }

    @Override
    public void saveLibraryToFile(String filename) {
        //TODO

    }



    @Override
    public void restoreLibraryFromFile(String filename) {

        this.library = new HashMap<>();

        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream(filename);
            if (is == null) {
                is = new FileInputStream(new File(filename));
            }
            JSONObject fileJSON = new JSONObject(new JSONTokener(is));
            Iterator<String> it = fileJSON.keys();

            while (it.hasNext()) {
                String mediaTitle = it.next();
                JSONObject seriesJSON = fileJSON.optJSONObject(mediaTitle);
                if (seriesJSON != null) {
                    SeriesSeason ss = new SeriesSeason(seriesJSON);
                    library.put(mediaTitle, ss);
                }
            }
        } catch (Exception ex) {
            System.out.println("Exception reading " + filename + ": " + ex.getMessage());
        }
    }


}
