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

    //from original interface
    boolean add(SeriesSeason ss);

    boolean remove(String mediaTitle);

    SeriesSeason get(String mediaTitle);

    String[] getTitles();

    /*get TV show names and seasons available in the library*/
    public List<SeriesSeason> getSeriesSeason();

    /*get seriesSeason based on title of the series */
    SeriesSeason getSeriesSeason(String title);

    /*add new seriesSeason*/
    void addSeriesSeason();

    /*remove a series season*/
    void removeSeriesSeason(String title);

    /*serialize and save a JSON file output for the library*/
    void saveLibraryToFile(String filename);

    /*load JSON file and initialize in library*/
    void restoreLibraryFromFile(String filename) throws FileNotFoundException;



}
