package ser321.assign3.akclifto.server;

import java.util.HashMap;
import java.util.List;

/**
 * Copyright 2020 Adam Clifton (akclifto@asu.edu)
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
 * Purpose: LibraryHelper is an interface with helper methods (get, add, and print)
 * used in conjunction with Library Interface and LibraryServer.
 * <p>
 * Ser321 Principles of Distributed Software Systems
 *
 * @author Tim Lindquist (Tim.Linquist@asu.edu),
 * Software Engineering, CIDSE, IAFSE, ASU Poly
 * @author Adam Clifton akclifto@asu.edu
 * Software Engineering, ASU
 * @version April 2020
 */
public interface LibraryHelper {


    /**
     * Method to retrieve Hashmap library of library server.
     * @return Hashmap of libraryMap
     * */
    HashMap<String, SeriesSeason> getLibraryMap();


    /**
     * Method to get TV show names and seasons available in the library
     * @return String list of names of season series titles
     * */
    String getSeriesSeason();

    /**
     *  Method to get seriesSeason List
     * @return seriesSeasonList
     * */
    List<SeriesSeason> getSeriesSeasonList();

    /**
     * get seriesSeason based on title and season of the series.
     * @param title : title of series to search and retrieve.
     * @param season : season to search if multiple seasons in file.
     * @return seriesSeason object.
     * */
    SeriesSeason getSeriesSeason(String title, String season);

    /**
     * get seriesSeason based on title of the series.
     * @param title : title of series to search and retrieve.
     * @return seriesSeason object.
     * */
    SeriesSeason getSeriesSeason(String title);

    /**
     * Method to return the library class object.
     * @return LibraryServer class object for use in in the client.
     * */
    LibraryServer getLibrary();

     /**
     * Method to add new seriesSeason to the library.
     * @param ss : SeriesSeason object to add to thte library
     * @return void
     * */
    void addSeriesSeason(SeriesSeason ss);

    /**
     * Helper method to print everything in the library. Used for debugging.
     * @return void.
     * */
    void printAll();

}
