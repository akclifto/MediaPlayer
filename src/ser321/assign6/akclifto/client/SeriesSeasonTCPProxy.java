package ser321.assign6.akclifto.client;


import org.json.JSONArray;
import org.json.JSONObject;
import ser321.assign6.akclifto.server.Library;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
 * Purpose: SeasonTCPProxy is the client-side TCP/IP stub used to communicate with the
 * server-side TCP/IP skeleton to implement JsonRPC requests and responses.
 * @author Tim Lindquist (Tim.Linquist@asu.edu),
 * Software Engineering, CIDSE, IAFSE, ASU Poly
 * @author Adam Clifton (akclifto@asu.edu)
 * Software Engineering, ASU
 * @version April 2020
 */
public class SeriesSeasonTCPProxy implements Library {

    private static final boolean debugOn = false;
    private static final int buffSize = 8192;
    private static int id = 0;
    private String host;
    private int port;

    /**
     * Constructor
     * @param host : server host
     * @param port : port number used for connection
     * */
    public SeriesSeasonTCPProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Method used for debugging purposes
     * @param message : message to be displayed during debugging
     * @return void
     * */
    private void debug(String message) {
        if(debugOn) {
            System.out.println("debug: " + message);
        }
    }

    /**
     * Method to send/received call information using jsonRPC when communicating
     * with Library TCP JsonRPC server.
     * @param method : method request invoked on server
     * @param params : object containing information related to method call
     * @return string styled for json objects.
     * */
    public String callMethod(String method, Object[] params){
        JSONObject theCall = new JSONObject();
        String ret = "{}";

        try{
            debug("Request is: " + theCall.toString());
            theCall.put("method", method);
            theCall.put("id", id);
            theCall.put("jsonrpc", "2.0");
            List<Object> list = new ArrayList<>();
            Collections.addAll(list, params);  //TODO: may need to change.

            JSONArray paramJson = new JSONArray(list);
            theCall.put("params", paramJson);

            Socket sock = new Socket(host, port);
            OutputStream out = sock.getOutputStream();
            InputStream in = sock.getInputStream();
            int bufLen = 4096;
            String strToSend = theCall.toString();
            byte[] byteRecd = new byte[buffSize];
            byte[] byteSend = strToSend.getBytes();
            out.write(byteSend, 0,byteSend.length);

            int numBytesRecd = in.read(byteRecd, 0, bufLen);
            ret = new String(byteRecd, 0, numBytesRecd);
            debug("CallMethod received from Server: " + ret);
            out.close();
            in.close();
            sock.close();
        }catch (Exception ex){
            System.out.println("Exception in the Client Proxy callMethod: " + ex.getMessage());
            ex.printStackTrace();
        }
        return ret;
    }

    @Override
    public String[] getSeriesSeasonTitles() {

        String result = callMethod("getLibraryTitles", new Object[]{});
        JSONObject res = new JSONObject(result);
        JSONArray arr = res.getJSONArray("result");
        String[] ret = new String[(arr.length())];
        for(int i = 0; i < arr.length(); i++){
            ret[i] = arr.getString(i);
        }
        return ret;
    }

    @Override
    public int getLibrarySize() {

        String result = callMethod("getLibrarySize", new Object[]{});
        JSONObject res = new JSONObject(result);
        return res.getInt("result");
    }

    @Override
    public String jsonGetSeries(String seriesTitle) {

        String result = callMethod("getSeries", new Object[]{seriesTitle});
        JSONObject res = new JSONObject(result);
        return res.optString("result");
    }

    @Override
    public String jsonGetEpisode(String seriesTitle, String episodeName) {

        String result = callMethod("getEpisode", new Object[]{seriesTitle, episodeName});
        JSONObject res = new JSONObject(result);
        return res.optString("result");
    }

    @Override
    public String[] getEpisodeTitles(String title) {

        String result = callMethod("getEpisodeTitles", new Object[]{title});
        JSONObject res = new JSONObject(result);
        JSONArray arr = res.getJSONArray("result");
        String[] ret = new String[arr.length()];
        for(int i = 0; i < arr.length(); i++){
            ret[i] = arr.getString(i);
//            System.out.println(ret[i]);
        }
        return  ret;
    }

    @Override
    public int getEpisodeListSize(String title) {

        String result = callMethod("getEpisodeListSize", new Object[]{title});
        JSONObject res = new JSONObject(result);
        return res.getInt("result");
    }

    @Override
    public boolean checkSeries(String title) {

        String result = callMethod("checkSeriesExists", new Object[]{title});
        JSONObject res = new JSONObject(result);
        return res.optBoolean("result", false);
    }

    @Override
    public boolean checkEpisodes(String title) {

        String result = callMethod("checkEpisodes", new Object[]{title});
        JSONObject res = new JSONObject(result);
        return res.optBoolean("result", false);
    }

    @Override
    public String getEpisodeName(String parent, String node) {

        String result = callMethod("getEpisodeName", new Object[]{parent, node});
        JSONObject res = new JSONObject(result);
        return res.optString("result");
    }

    @Override
    public String getEpisodeImdb(String parent, String node) {

        String result = callMethod("getEpisodeImdb", new Object[]{parent, node});
        JSONObject res = new JSONObject(result);
        return  res.optString("result");
    }

    @Override
    public String getEpisodeSummary(String parent, String node) {

        String result = callMethod("getEpisodeSummary", new Object[]{parent, node});
        JSONObject res = new JSONObject(result);
        return res.optString("result");
    }

    @Override
    public boolean addEpisode(String series, String episode) {

        String result = callMethod("addEpisode5", new Object[]{series, episode});
        JSONObject res = new JSONObject(result);
        return res.optBoolean("result", false);
    }

    @Override
    public boolean removeEpisode(String series, String episode) {

        String result = callMethod("removeEpisode", new Object[]{series, episode});
        JSONObject res = new JSONObject(result);
        return res.optBoolean("result", false);
    }

    @Override
    public String getSeriesTitle(String title) {

        String result = callMethod("getSeriesTitle", new Object[]{title});
        JSONObject res = new JSONObject(result);
        return res.optString("result");
    }

    @Override
    public String getSeriesImdbRating(String title) {

        String result = callMethod("getSeriesImdbRating", new Object[]{title});
        JSONObject res = new JSONObject(result);
        return res.optString("result");
    }

    @Override
    public String getGenre(String title) {

        String result = callMethod("getSeriesGenre", new Object[]{title});
        JSONObject res = new JSONObject(result);
        return res.optString("result");
    }

    @Override
    public String getPosterLink(String title) {

        String result = callMethod("getSeriesPoster", new Object[]{title});
        JSONObject res = new JSONObject(result);
        return res.optString("result");
    }

    @Override
    public String getSummary(String title) {

        String result = callMethod("getSeriesSummary", new Object[]{title});
        JSONObject res = new JSONObject(result);
        return res.optString("result");

    }

    @Override
    public boolean removeSeriesSeason(String title) {

        String result = callMethod("removeSeries", new Object[]{title});
        JSONObject res = new JSONObject(result);
        return res.optBoolean("result", false);
    }

    @Override
    public boolean saveLibraryToFile(String fileName) {

        String result = callMethod("toJsonFile", new Object[]{fileName});
        JSONObject res = new JSONObject(result);
        return res.optBoolean("result", false);
    }

    @Override
    public boolean restoreLibraryFromFile(String filename) {

        String result = callMethod("initLibraryFromJsonFile", new Object[]{filename});
        JSONObject res = new JSONObject(result);
        return res.optBoolean("result", false);
    }

    @Override
    public boolean parseURLtoJSON(String jsonSeries, String jsonEpisodes) {

        String result = callMethod("parseURLtoJSON", new Object[]{jsonSeries, jsonEpisodes});
        JSONObject res = new JSONObject(result);
        return res.optBoolean("result", false);
    }

}
