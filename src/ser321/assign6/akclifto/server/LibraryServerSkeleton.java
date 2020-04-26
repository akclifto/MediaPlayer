package ser321.assign6.akclifto.server;


import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Copyright 2020 Adam Clifton, akclifto@asu.edu
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
 * This module acts as the View layer for your application.
 * The 'MediaLibraryGui' class actually builds the Gui with all
 * the components - buttons, text fields, text areas, panels etc.
 * This class should be used to write the logic to add functionality
 * to the Gui components.
 * You are free add more files and further modularize this class's
 * functionality.
 * <p>
 * Purpose: LibraryServerSkeleton is the server-side TCP/IP skeleton used to communicate with the
 * client-side TCP/IP stub to implement JsonRPC requests and responses.
 * <p>
 * @author Tim Lindquist (Tim.Linquist@asu.edu),
 * Software Engineering, CIDSE, IAFSE, ASU Poly
 * @author Adam Clifton (akclifto@asu.edu)
 * Software Engineering, ASU
 * @version April 2020
 */
public class LibraryServerSkeleton {

    private static final boolean debugOn = false;
    Library library;

    public LibraryServerSkeleton(Library sLibrary) {
        this.library = sLibrary;
    }

    /**
     * Method used for debugging purposes
     * @param message : message to be displayed during debugging
     * @return void
     * */
    private void debug(String message){
        if(debugOn){
            System.out.println("debug: " + message);
        }
    }

    /**
     * Method to send/received call information using jsonRPC when communicating
     * with Library client proxy.
     * @param request : method request invoked on server
     * @return string styled for json objects in response to request received.
     * */
    public String callMethod(String request) {
        JSONObject result = new JSONObject();
        try {

            JSONObject theCall = new JSONObject(request);
            debug("Request is: " + theCall.toString());

            String method = theCall.getString("method");
            int id = theCall.getInt("id");
            JSONArray params = null;
            if(!theCall.isNull("params")){
                params = theCall.getJSONArray("params");
            }
            result.put("id", id);
            result.put("jsonrpc", "2.0");

            if(method.equalsIgnoreCase("toJsonFile")){
                assert params != null;
                String filename = params.getString(0);
                debug("Saving library to file: " + filename);
                result.put("result", library.saveLibraryToFile(filename));

            } else if(method.equalsIgnoreCase("initLibraryFromJsonFile")){
                assert params != null;
                String filename = params.getString(0);
                debug("Restoring library from file: " + filename);
                result.put("result", library.restoreLibraryFromFile(filename));

            } else if (method.equalsIgnoreCase("parseURLtoJSON")) {
                assert params != null;
                String jsonSeries = params.getString(0);
                String jsonEpisodes = params.getString(1);
                debug("Parsing URL to Json Objects:\nseries: \n" + jsonSeries +
                        "\nepisodes: \n" + jsonEpisodes);
                result.put("result", library.parseURLtoJSON(jsonSeries, jsonEpisodes));

            } else if (method.equalsIgnoreCase("removeSeries")) {
                assert params != null;
                String seriesTitle = params.getString(0);
                debug("Removing series: " + seriesTitle);
                result.put("result", library.removeSeriesSeason(seriesTitle));

            } else if (method.equalsIgnoreCase("addEpisode")) {
                assert params != null;
                String seriesTitle = params.getString(0);
                String episodeName = params.getString(1);
                debug("Adding episode: " + episodeName + " from " + seriesTitle);
                result.put("result", library.addEpisode(seriesTitle,episodeName));

            } else if (method.equalsIgnoreCase("removeEpisode")) {
                assert params != null;
                String seriesTitle = params.getString(0);
                String episodeName = params.getString(1);
                debug("Removing episode: " + episodeName + " from " + seriesTitle);
                result.put("result", library.removeEpisode(seriesTitle,episodeName));

            } else if (method.equalsIgnoreCase("getSeries")) {
                assert params != null;
                String seriesName = params.getString (0);
                debug("Getting series: " + seriesName);
                //TODO:  need to make a json object method for this.

            } else if (method.equalsIgnoreCase("getEpisode")) {
                assert params != null;
                String seriesTitle = params.getString(0);
                String episodeName = params.getString(1);
                debug("Getting episode: " + episodeName + " from " + seriesTitle);
                //TODO:  need to make a json object method for this.

            } else if (method.equalsIgnoreCase("getEpisodeListSize")) {
                assert params != null;
                String seriesTitle = params.getString(0);
                debug("Getting episode list size for: " + seriesTitle);
                result.put("result", library.getEpisodeListSize(seriesTitle));

            } else if (method.equalsIgnoreCase("getLibraryTitles")) {
                debug("Getting library titles");
                result.put("result", library.getSeriesSeasonTitles());

            } else if (method.equalsIgnoreCase("getEpisodeTitles")) {
                assert params != null;
                String seriesTitle = params.getString(0);
                debug("Getting episode titles for: " + seriesTitle);
                result.put("result", library.getEpisodeTitles(seriesTitle));

            } else if (method.equalsIgnoreCase("getSeriesTitle")) {
                assert params != null;
                String seriesLabel = params.getString(0);
                debug("Getting SeriesTitle for label: " + seriesLabel);
                result.put("result", library.getSeriesTitle(seriesLabel));

            }else if (method.equalsIgnoreCase("getSeriesPoster")) {
                assert params != null;
                String seriesName = params.getString(0);
                debug("Getting Series poster for series: " + seriesName);
                result.put("result", library.getPosterLink(seriesName));

            }else if (method.equalsIgnoreCase("getSeriesGenre")) {
                assert params != null;
                String seriesName = params.getString(0);
                debug("Getting series genre for: " + seriesName);
                result.put("result", library.getGenre(seriesName));

            }else if (method.equalsIgnoreCase("checkSeriesExists")) {
                assert params != null;
                String seriesName = params.getString(0);
                debug("Checking if Series: " + seriesName + " is in library...");
                result.put("result", library.checkSeries(seriesName));

            }else if (method.equalsIgnoreCase("getSeriesImdbRating")) {
                assert params != null;
                String seriesName = params.getString(0);
                debug("Geting series imbd Rating for: " + seriesName);
                result.put("result", library.getSeriesImdbRating(seriesName));

            }else if (method.equalsIgnoreCase("getSeriesSummary")) {
                assert params != null;
                String seriesName = params.getString(0);
                debug("Getting summary for: " +seriesName);
                result.put("result", library.getSummary(seriesName));

            }else if (method.equalsIgnoreCase("getEpisodeName")) {
                assert params != null;
                String parent = params.getString(0);
                String node = params.getString(1);
                debug("Getting episode name for series label: "+ parent + " and node label: " + node);
                result.put("result", library.getEpisodeName(parent, node));

            }else if (method.equalsIgnoreCase("getEpisodeImdb")) {
                assert params != null;
                String parent = params.getString(0);
                String node = params.getString(1);
                debug("Getting episode imdb rating for: " + node + " in " + parent);
                result.put("result", library.getEpisodeImdb(parent, node));

            }else if (method.equalsIgnoreCase("getEpisodeSummary")) {
                assert params != null;
                String parent= params.getString(0);
                String node = params.getString(1);
                debug("Getting episode summary for: " + node + " in " + parent);
                result.put("result", library.getEpisodeSummary(parent,node));

            } else {
                debug("Unable to match method: " + method + ". Returning 0.");
                result.put("result", 0.0);
            }
        } catch (Exception ex) {
            System.out.println("Exception in Skeleton CallMethod: " + ex.getMessage());
            ex.printStackTrace();
        }
        return result.toString();
    }
}
