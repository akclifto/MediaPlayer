package ser321.assign3.akclifto.server;

import java.util.List;

public interface LibraryHelper {

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

    LibraryServer getLibrary();

     /**
     * Method to add new seriesSeason to the library.
     * @param ss : SeriesSeason object to add to thte library
     * @return void
     * */
    void addSeriesSeason(SeriesSeason ss);

}
