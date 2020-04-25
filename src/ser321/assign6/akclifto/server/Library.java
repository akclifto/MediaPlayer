package ser321.assign6.akclifto.server;

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
 * @version April 2020
 */
public interface Library extends Remote {

    /**
     * Method to send serialized series title information to the client from the server.
     * Used to construct JTree on the client-side
     * @return array of series titles for a given library.
     * @throws RemoteException if error during remote method invocation
     * */
    String[] getSeriesSeasonTitles() throws RemoteException;

    /**
     * Method to get the number of seriesSeasons in a library.
     * @return size of seriesSeasonList
     * @throws RemoteException if error during remote method invocation
     * */
    int getLibrarySize() throws RemoteException;



    /**
     * Method to send serialized Episode title information to the client from the server.
     * Used to construct JTree on the client-side
     * @param title : title of the series requested
     * @return array of episode titles for a given series.
     * @throws RemoteException if error during remote method invocation
     * */
    String[] getEpisodeTitles(String title) throws  RemoteException;

    /**
     * Method to send serialized Episode list size information to the client from the server.
     * @param title : title of the series requested
     * @return size of the episode list for the given series.
     * @throws RemoteException if error during remote method invocation
     * */
    int getEpisodeListSize(String title) throws RemoteException;

    /**
     * Method to check if episode is already in the library and send to the client from the server.
     * @param title : title of the series requested
     * @return true if not in the episode list for the selected series, false otherwise.
     * @throws RemoteException if error during remote method invocation
     * */
    boolean checkEpisodes(String title) throws  RemoteException;

    /**
     * Method to send serialized Episode name information to the client from the server.
     * @param parent : title of the series requested
     * @param node : name of the episode node requested
     * @return name of the Episode requested.
     * @throws RemoteException if error during remote method invocation
     * */
    String getEpisodeName(String parent, String node) throws RemoteException;

    /**
     * Method to send serialized IMDB Rating information for an epsidoe to the client from the server.
     * @param parent : title of the series requested
     * @param node : title of episode requested
     * @return IMDB Rating for requested episode.
     * @throws RemoteException if error during remote method invocation
     * */
    String getEpisodeImdbRating(String parent, String node) throws RemoteException;

    /**
     * Method to send serialized Episode Summary information to the client from the server.
     * @param parent : title of the parent series requested
     * @param node  : title of the episode requested
     * @return summary information for episode.
     * @throws RemoteException if error during remote method invocation
     * */
    String getEpisodeSummary(String parent, String node) throws RemoteException;

    /**
     * Method to remove episode from a series, as request by the client.
     * @param series : title of the series requested
     * @param episode : title of the episode to remove
     * @return true if the title is removed, false otherwise.
     * @throws RemoteException if error during remote method invocation
     * */
    boolean removeEpisode(String series, String episode) throws RemoteException;

    /**
     * Method to send serialized series title information to the client from the server.
     * @param title : title of the nodeLabel requested
     * @return title for series.
     * @throws RemoteException if error during remote method invocation
     * */
    String getSeriesTitle(String title) throws RemoteException;

    /**
     * Method to send serialized IMDB Rating information to the client from the server.
     * @param title : title of the series requested
     * @return IMDB Rating for series.
     * @throws RemoteException if error during remote method invocation
     * */
    String getSeriesImdbRating(String title) throws  RemoteException;

    /**
     * Method to send serialized genre information to the client from the server.
     * @param title : title of the series requested
     * @return genre for series.
     * @throws RemoteException if error during remote method invocation
     * */
    String getGenre(String title) throws RemoteException;

    /**
     * Method to send serialized poster url information to the client from the server.
     * @param title : title of the series requested
     * @return url address to poster for series.
     * @throws RemoteException if error during remote method invocation
     * */
    String getPosterLink(String title) throws RemoteException;

    /**
     * Method to send serialized summary information to the client from the server.
     * @param title : title of the series requested
     * @return summary information for series
     * @throws RemoteException if error during remote method invocation
     * */
    String getSummary(String title) throws RemoteException;

    /**
     * Method to remove SeriesSeason from the library by searching its title.
     * @param title : title of series to remove.
     * @return true if SeriesSeason removed, false otherwise
     * @throws RemoteException if error during remote method invocation
     * */
    boolean removeSeriesSeason(String title) throws RemoteException;

    /**
     * Method to serialize and save library to a JSON file.
     * @param fileName : name of JSON file to be saved.
     * @return true if library saved to file correctly, false otherwise.
     * @throws RemoteException if error during remote method invocation
     * */
    boolean saveLibraryToFile(String fileName) throws RemoteException;

    /**
     * Method to load library from a given JSON file.  JSON file structure must match
     * JSON file "saveLibraryToFile structure.
     * @param filename  : name of the file to load the library.
     * @return true if library loaded successfully, false otherwise.
     * @throws RemoteException if error during remote method invocation
     * */
    boolean restoreLibraryFromFile(String filename) throws RemoteException;

    /**
     * Method to parse URL string data into JSON files for SeriesSeason and Episode, then creates new
     * SeriesSeason objects with the data.
     * @param jsonSeries :  string of series json data
     * @param jsonEpisodes : string of episode json data
     * @return void
     * @throws RemoteException if error during remote method invocation
     * */
    void parseURLtoJSON(String jsonSeries, String jsonEpisodes) throws RemoteException;




}
