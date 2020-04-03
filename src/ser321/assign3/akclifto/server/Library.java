package ser321.assign3.akclifto.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
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
public interface Library extends Remote {

    /*From original sample files*/
    String[] getSeriesSeasonTitles() throws RemoteException;

    LibraryServer getLibrary() throws RemoteException;
    int getLibrarySize();
    /**
     * Method toget TV show names and seasons available in the library
     * @return String list of names of season series titles
     * */
    String getSeriesSeason() throws RemoteException;

    /**
     *  Method to get seriesSeason List
     * @return seriesSeasonList
     * */
    List<SeriesSeason> getSeriesSeasonList() throws RemoteException;

    /**
     * get seriesSeason based on title and season of the series.
     * @param title : title of series to search and retrieve.
     * @param season : season to search if multiple seasons in file.
     * @return seriesSeason object.
     * */
    SeriesSeason getSeriesSeason(String title, String season) throws RemoteException;

    /**
     * get seriesSeason based on title of the series.
     * @param title : title of series to search and retrieve.
     * @return seriesSeason object.
     * */
    SeriesSeason getSeriesSeason(String title) throws RemoteException;

    /**
     * Method to add new seriesSeason to the library.
     * @param ss : SeriesSeason object to add to thte library
     * @return void
     * */
    void addSeriesSeason(SeriesSeason ss) throws RemoteException;

    /**
     * Method to remove SeriesSeason from the library by searching its title.
     * @param title : title of series to remove.
     * @return true if SeriesSeason removed, false otherwise
     * */
    boolean removeSeriesSeason(String title) throws RemoteException;

    /**
     * Method to serialize and save library to a JSON file.
     * @param fileName : name of JSON file to be saved. */
    boolean saveLibraryToFile(String fileName) throws RemoteException;

    /*load JSON file and initialize in library*/
    /**
     * Method to load library from a given JSON file.  JSON file structure must match
     * JSON file "saveLibraryToFile structure.
     * @param filename  : name of the file to load the library.
     * @return true if library loaded successfully, false otherwise.
     * */
    boolean restoreLibraryFromFile(String filename) throws RemoteException;

    public void parseURLtoJSON(String jsonSeries, String jsonEpisodes);



}
