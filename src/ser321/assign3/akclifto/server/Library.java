package ser321.assign3.akclifto.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

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

    int getLibrarySize() throws RemoteException;

    String[] getEpisodeTitles(String title) throws  RemoteException;

    int getEpisodeListSize(String title) throws RemoteException;

    boolean checkEpisodes(String title) throws  RemoteException;

    String getEpisodeName(String parent, String node) throws RemoteException;

    String getEpisodeImdbRating(String parent, String node) throws RemoteException;

    String getEpisodeSummary(String parent, String node) throws RemoteException;

    boolean removeEpisode(String series, String episode) throws RemoteException;

    String getSeriesTitle(String title) throws RemoteException;

    String getSeriesImdbRating(String title) throws  RemoteException;

    String getGenre(String title) throws RemoteException;

    String getPosterLink(String title) throws RemoteException;

    String getSummary(String title) throws RemoteException;

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

    /**
     * Method to load library from a given JSON file.  JSON file structure must match
     * JSON file "saveLibraryToFile structure.
     * @param filename  : name of the file to load the library.
     * @return true if library loaded successfully, false otherwise.
     * */
    boolean restoreLibraryFromFile(String filename) throws RemoteException;

    /**
     * Method to parse URL string data into JSON files for SeriesSeason and Episode, then creates new
     * SeriesSeason objects with the data.
     * @param jsonSeries :  string of series json data
     * @param jsonEpisodes : string of episode json data
     * @return void
     * */
    void parseURLtoJSON(String jsonSeries, String jsonEpisodes) throws RemoteException;




}
