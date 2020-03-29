package ser321.assign2.akclifto.client;

import java.io.FileNotFoundException;
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
 * Purpose: Series defines interface for SeriesSeason that pulls information from
 * SeasonLibrary and Episode to display to the user.
 * <p>
 * Ser321 Principles of Distributed Software Systems
 *
 * @author Tim Lindquist (Tim.Linquist@asu.edu),
 * Software Engineering, CIDSE, IAFSE, ASU Poly
 * @author Adam Clifton akclifto@asu.edu
 * Software Engineering, ASU
 * @version March 2020
 */
public interface Library {


    boolean remove(String mediaTitle);

    SeriesSeason get(String mediaTitle);

    String[] getTitles();

    /**
     * Method toget TV show names and seasons available in the library
     * @return String list of names of season series titles
     * */
    public String getSeriesSeason();

    /**
     *  Method to get seriesSeason List
     * @return seriesSeasonList
     * */
    public List<SeriesSeason> getSeriesSeasonList();


    /**
     * get seriesSeason based on title of the series.
     * @param title : title of series to search and retrieve.
     * @return seriesSeason object.
     * */
    SeriesSeason getSeriesSeason(String title);

    /**Methods to add new seriesSeason*/
    void addSeriesSeason(SeriesSeason ss);

    /*remove a series season*/
    void removeSeriesSeason(String title);

    /*serialize and save a JSON file output for the library*/
    boolean saveLibraryToFile(String fileName);

    /*load JSON file and initialize in library*/
    boolean restoreLibraryFromFile(String filename) throws FileNotFoundException;



}
