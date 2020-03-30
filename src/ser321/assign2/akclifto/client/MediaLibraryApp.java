package ser321.assign2.akclifto.client;

import ser321.assign2.lindquis.MediaLibraryGui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;
import java.time.Duration;

/**
 * Copyright 2020 Tim Lindquist,
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
 * Purpose: MediaLibraryApp instructor sample for solving Spring 2020 ser321 assignments.
 * This problem provides a sample for browsing and managing information about
 * tv series. It uses a Swing JTree, and JTextField controls to
 * realize a GUI with a split pane. The left pane contains an expandable
 * JTree of the media library.
 * This program is a sample controller for the non-distributed version of
 * the system.
 * The right pane contains components that allow viewing, modifying and adding
 * albums, tracks, and corresponding files.
 *
 * @author Tim Lindquist (Tim.Linquist@asu.edu),
 * Software Engineering, CIDSE, IAFSE, ASU Poly
 * @version January 2020
 */
public class MediaLibraryApp extends MediaLibraryGui implements
		TreeWillExpandListener,
		ActionListener,
		TreeSelectionListener {

	private static final boolean debugOn = true;
	private static final String pre = "https://www.omdbapi.com/?apikey=";
	private static String urlOMBD;
	private SeasonLibrary library;
	private String omdbKey;
	private static String posterImg =
			"http://getdrawings.com/img/black-and-white-tree-silhouette-9.jpg";

	public MediaLibraryApp(String author, String authorKey) {
		// sets the value of 'author' on the title window of the GUI.
		super(author);
		this.omdbKey = authorKey;
		urlOMBD = pre + omdbKey + "&t=";
		library = SeasonLibrary.getInstance();	//initialize library
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// register this object as an action listener for menu item clicks. This will cause
		// my actionPerformed method to be called every time the user selects a menuitem.
		for (JMenuItem[] userMenuItem : userMenuItems) {
			for (JMenuItem jMenuItem : userMenuItem) {
				jMenuItem.addActionListener(this);
			}
		}
		// register this object as an action listener for the Search button. This will cause
		// my actionPerformed method to be called every time the user clicks the Search button
		searchJButt.addActionListener(this);
		try {
			//tree.addTreeWillExpandListener(this);  // add if you want to get called with expansion/contract
			tree.addTreeSelectionListener(this);
			rebuildTree();

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Handling " +
					" constructor exception: " + ex.getMessage());
		}
		try {
			/*
			 * display an image just to show how the album or artist image can be displayed in the
			 * app's window. setAlbumImage is implemented by MediaLibraryGui class. Call it with a
			 * string url to a png file as obtained from an album search.
			 */

			// set poster image here-----default poster image
			setPosterImage(posterImg);

		} catch (Exception ex) {
			System.out.println("unable to open image");
		}
		setVisible(true);
	}

	/**
	 * Set poster image helper method to resize and set image, alternative to MediaLibraryGIU impl.
	 *
	 * @param posterLink : a url link to the given image
	 * @return void
	 */
	private void setPosterImage(String posterLink) {

		try {
			BufferedImage image = ImageIO.read(new URL(posterLink));
			// resize width
			int imgWidth = 260;
			// resize height
			int imgHeight = 360;
			BufferedImage resized = resize(image, imgHeight, imgWidth);
			ImageIcon poster = new ImageIcon(resized);
			displayPane.setIcon(poster);

		} catch (IOException ex) {
			System.out.println("Exception setting image: " + ex.getMessage());
			ex.printStackTrace();
		}
	}


	/**
	 * Scale down the URL image to better fit the GUI window.
	 *
	 * @param img    : buffered image input to resize
	 * @param height : height resize specification
	 * @param width  : width resize specification
	 * @return resized BufferedImage
	 */
	private static BufferedImage resize(BufferedImage img, int height, int width) {

		Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = resized.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return resized;
	}

	/**
	 * A method to facilitate printing debugging messages during development, but which can be
	 * turned off as desired.
	 *
	 * @param message Is the message that should be printed.
	 * @return void
	 */
	private void debug(String message) {
		if (debugOn)
			System.out.println("debug: " + message);
	}

	/**
	 * Create and initialize nodes in the JTree of the left pane.
	 * buildInitialTree is called by MediaLibraryGui to initialize the JTree.
	 * Classes that extend MediaLibraryGui should override this method to
	 * perform initialization actions specific to the extended class.
	 * The default functionality is to set base as the label of root.
	 * In your solution, you will probably want to initialize by deserializing
	 * your library and displaying the categories and subcategories in the
	 * tree.
	 *
	 * @param root Is the root node of the tree to be initialized.
	 * @param base Is the string that is the root node of the tree.
	 */
	public void buildInitialTree(DefaultMutableTreeNode root, String base) {
		//set up the context and base name
		try {
			root.setUserObject(base);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "exception initial tree:" + ex);
			ex.printStackTrace();
		}
	}

	/**
	 * method to build the JTree of media shown in the left panel of the UI. The
	 * field tree is a JTree as defined and initialized by MediaLibraryGui class.
	 * It is defined to be protected so it can be accessed by extending classes.
	 * This version of the method uses the music library to get the names of
	 * tracks. Your solutions will need to replace this structure with one that
	 * you need for the series/season and Episode. These two classes should store your information.
	 * Your library (so a changes - or newly implemented MediaLibraryImpl) will store
	 * and provide access to Series/Seasons and Episodes.
	 * This method is provided to demonstrate one way to add nodes to a JTree based
	 * on an underlying storage structure.
	 * See also the methods clearTree, valueChanged defined in this class, and
	 * getSubLabelled which is defined in the GUI/view class.
	 **/
	public void rebuildTree() {

		tree.removeTreeSelectionListener(this);

		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();  //bases structure
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot(); //root tree node (named as "author")
		clearTree(root, model); //clear the tree

		// put nodes in the tree for all registered with the library
		DefaultMutableTreeNode libraryNode = new DefaultMutableTreeNode("Library");
		model.insertNodeInto(libraryNode, root, model.getChildCount(root));

		String[] seriesTitles = library.getSeriesSeasonTitles();  //array of all titles in the library

		for(String title : seriesTitles){

			setTreeSeriesNodes(model, libraryNode, title);
		}

		// expand all the nodes in the JTree
		for (int r = 0; r < tree.getRowCount(); r++) {
			tree.expandRow(r);
		}
		tree.addTreeSelectionListener(this);
	}


	/**
	 * Helper method to set Series Nodes within the DefaultTreeModel structure.
	 * @param model : DefaultTreeModel used in GUI
	 * @param root : root tree where series node will be linked.
	 * @param title : title of series to add to the tree.
	 * */
	private void setTreeSeriesNodes(DefaultTreeModel model, DefaultMutableTreeNode root, String title) {

		SeriesSeason ss = library.getSeriesSeason(title);
		String seriesName = ss.getTitle();
		String[] epTitles = ss.getEpisodeTitles();

		DefaultMutableTreeNode seriesToAdd = new DefaultMutableTreeNode(seriesName);  // series node to add to tree
		DefaultMutableTreeNode subNode = getSubLabelled(root, ss.getTitle());  // sub nodes to seriesToAdd

		if(subNode != null) { //if series exists.

			debug("seriesSeason exists: " + ss.getTitle());
			model.insertNodeInto(seriesToAdd, subNode, model.getChildCount(subNode));

			if(ss.checkEpisodes()){

				setTreeEpisodeNodes(model, subNode, epTitles);
			}

		} else {  // if series does not exist.

			DefaultMutableTreeNode seriesNode = new DefaultMutableTreeNode(seriesName);
			debug("No series, so adding one with name: " + seriesName);
			model.insertNodeInto(seriesNode, root, model.getChildCount(root));

			if(ss.checkEpisodes())
			{
				setTreeEpisodeNodes(model, seriesNode, epTitles);
			}
		}
	}


	/**
	 * Helper method to set Episode Nodes in the Tree structure under their proper series.
	 * @param model : DefaultTreeModel used in GUI
	 * @param root : root tree node where episode will be linked.
	 * @param epTitles : array of episode titles to add labels for each episode sub-node
	 * @return void.
	 * */
	private void setTreeEpisodeNodes(DefaultTreeModel model, DefaultMutableTreeNode root,
									 String[] epTitles) {

		for (String name : epTitles) {

			DefaultMutableTreeNode episodeNode = new DefaultMutableTreeNode(name);
			model.insertNodeInto(episodeNode, root, model.getChildCount(root));
		}
	}

	/**
	 * Remove all nodes in the left pane tree view.
	 *
	 * @param root  Is the root node of the tree.
	 * @param model Is a model that uses TreeNodes.
	 * @return void
	 */
	private void clearTree(DefaultMutableTreeNode root, DefaultTreeModel model) {

		try {
			DefaultMutableTreeNode next;
			int subs = model.getChildCount(root);
			for (int k = subs - 1; k >= 0; k--) {
				next = (DefaultMutableTreeNode) model.getChild(root, k);
				debug("removing node labelled:" + next.getUserObject());
				model.removeNodeFromParent(next);
			}
		} catch (Exception ex) {
			System.out.println("Exception while trying to clear tree:");
			ex.printStackTrace();
		}
	}

	public void treeWillCollapse(TreeExpansionEvent tee) {
		debug("In treeWillCollapse with path: " + tee.getPath());
		tree.setSelectionPath(tee.getPath());
	}

	public void treeWillExpand(TreeExpansionEvent tee) {
		debug("In treeWillExpand with path: " + tee.getPath());
	}


	// this will be called when you click on a node.
	// It will update the node based on the information stored in the library
	// this will need to change since your library will be of course totally different
	// extremely simplified! E.g. make sure that you display sensible content when the root,
	// the My Series, the Series/Season, and Episode nodes are selected
	public void valueChanged(TreeSelectionEvent e) {

		int series = 2;
		int episode = 3;

		try {

			tree.removeTreeSelectionListener(this);

			DefaultMutableTreeNode node = (DefaultMutableTreeNode)
					tree.getLastSelectedPathComponent();

			if (node != null) {

				String nodeLabel = (String) node.getUserObject();
				debug("In valueChanged. Selected node labelled: " + nodeLabel);

				// All fields empty to start with
				seriesSeasonJTF.setText("");
				genreJTF.setText("");
				setPosterImage(posterImg);
				ratingJTF.setText("");
				episodeJTF.setText("");
				summaryJTA.setText("");


				SeriesSeason ssCurrent = library.getSeriesSeason(nodeLabel);

				DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot(); // get the root
				// First (and only) child of the root (username) node is 'My Series' node.
				DefaultMutableTreeNode rootLibrary = (DefaultMutableTreeNode) root.getNextNode(); // Library node
				DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();

				//System.out.println("node level is ---------------: " + node.getLevel());

				if(node.getLevel() == series){

					int episodeCount = ssCurrent.getEpisodeList().size();

					// Change to season name
					// change to rating of the episode
					if(episodeCount == 1){
						episodeJTF.setText(" " + episodeCount + " Episode in library");            // name of the episode
					} else {
						//set text to panels to displayed selected node information
						episodeJTF.setText(" " + episodeCount + " Episodes in library");            // name of the episode
					}
					ratingJTF.setText(ssCurrent.getImdbRating());        // change to rating of the episode
					genreJTF.setText(ssCurrent.getGenre());
					setPosterImage(ssCurrent.getPosterLink());
					summaryJTA.setText(ssCurrent.getPlotSummary());
					seriesSeasonJTF.setText(ssCurrent.getTitle());      // Change to season name
				} else if (node.getLevel() == episode){

					String parentLabel = (String) parent.getUserObject();
					SeriesSeason ssParent = library.getSeriesSeason(parentLabel);
					Episode epi = ssParent.getEpisode(nodeLabel);

					//set text to panels to displayed selected node information
					episodeJTF.setText(epi.getName());            // name of the episode
					ratingJTF.setText(epi.getImdbRating());        // change to rating of the episode
					genreJTF.setText(ssParent.getGenre());
					setPosterImage(ssParent.getPosterLink());
					summaryJTA.setText(epi.getEpSummary());
					seriesSeasonJTF.setText(ssParent.getTitle());      // Change to season name

				} else if (node.getLevel() == 0 || node.getLevel() == 1) {                     // root directory "Library"

					seriesSeasonJTF.setText("Season Name and Number");    // season name
					genreJTF.setText("Genre");                   // genre of the series from library
					ratingJTF.setText("IMDB Rating");       	 // rating of the season get from library
					episodeJTF.setText("Episode Name");          // nothing in here since not an episode
					summaryJTA.setText("Plot Summary");			 // Plot Summary
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		tree.addTreeSelectionListener(this);
	}



	public void actionPerformed(ActionEvent e) {

		tree.removeTreeSelectionListener(this);

		if (e.getActionCommand().equals("Exit")) {
			System.exit(0);

		} else if (e.getActionCommand().equals("Save")) {
			boolean savRes = actionSaveTree();
			System.out.println("Save " + ((savRes) ? "successful" : "not implemented"));

		} else if (e.getActionCommand().equals("Restore")) {

			boolean resRes = actionRestoreTree();
			if(resRes) {
				refreshTree();
			}
			System.out.println("Restore " + ((resRes) ? "successful" : "not implemented"));

		} else if (e.getActionCommand().equals("Series-SeasonAdd")) {
			System.out.println("Series-SeasonAdd not implemented"); // TODO: implement that the whole season with all episodes currently in tree will be added to library

		} else if (e.getActionCommand().equals("Series-SeasonRemove")) {

			actionRemoveSeries();
			refreshTree();
		} else if(e.getActionCommand().equalsIgnoreCase("EpisodeAdd")) {
			System.out.println("NOT IMPLEMENTED");
		} else if (e.getActionCommand().equalsIgnoreCase("EpisodeRemove")){

			actionRemoveEpisode();
			refreshTree();

		} else if (e.getActionCommand().equals("Search")) {
			/*
			 * In the below API(s) the error response should be appropriately handled
			 */
			// with all episodes only display this new series/season with the episodes in tree

			// Doing a fetch two times so that we only get the full series info (with poster, summary, rating) once
			// fetch series info
			String searchReqURL = urlOMBD + seriesSearchJTF.getText().replace(" ", "%20");
			System.out.println("calling fetch with url: " + searchReqURL);
			String jsonSeries = fetchURL(searchReqURL);
			System.out.println("Fetch result just season: " + jsonSeries);

			// fetch season info
			String searchReqURL2 = urlOMBD + seriesSearchJTF.getText().replace(" ", "%20")
					+ "&season=" + seasonSearchJTF.getText();
			String jsonEpisodes = fetchURL(searchReqURL2);
			System.out.println("Fetch result episodes: " + jsonEpisodes);

			/*
			 * This should also then build the tree and display the info in the left side bar (so the new tree with its episodes)
			 * right hand should display the Series information
			 */
			actionFetchResults(jsonSeries, jsonEpisodes);
			refreshTree();

		} else if (e.getActionCommand().equals("Tree Refresh")) {

			refreshTree();
		}
		tree.addTreeSelectionListener(this);
	}


	/**
	 * Helper method to save library tree bases on user actionPerformed Selection.
	 * @return true of library displayed in tree saved correctly, false otherwise.
	 * */
	private boolean actionSaveTree(){

		try{
			library.saveLibraryToFile("SavedLibrary.json");
		} catch(Exception ex){
			System.out.println("Exception saving library to file : " + ex.getMessage());
			return false;
		}
		return true;
	}


	/**
	 * Helper method to restore Library tree from JSON file based on user actionPerformed selection.
	 * @return true if library successfully restored, false otherwise.
	 * */
	 private boolean actionRestoreTree() {

	 	boolean flag;

		 try {
		 	flag = library.restoreLibraryFromFile("SavedLibrary.json");

			 if (!flag) {
				 flag = library.restoreLibraryFromFile("series.json");
			 }
		 } catch (Exception defaultRestore) {
			 System.out.println("Exception restoring library: " + defaultRestore.getMessage());
			 defaultRestore.printStackTrace();
			 return false;
		 }
		 return flag;
	 }



	 /**
	  * Helper method to refresh, rebuild tree.
	  * @return void.
	  * */
	 private void refreshTree(){
		 rebuildTree();
		 revalidate(); 	  // recalculate the layout(which is necessary when adding components)
		 repaint();       // repaint the image of the swing window
	 }


	 /**
	  * Helper method to remove series from library.  Will remove any episdoes associated with it.
	  * @return void
	  * */
	 private void actionRemoveSeries(){

		 int option = JOptionPane.showConfirmDialog(null,
				 "Remove Selected Series? \n" + seriesSeasonJTF.getText(),
				 "Remove Series-Season",
				 JOptionPane.YES_NO_OPTION);

		 if(option == JOptionPane.YES_OPTION) {

			 try {
				 library.getSeasonLibrary().removeSeriesSeason(seriesSeasonJTF.getText());
			 } catch (Exception ex) {
				 System.out.println("Exception removing Series-Season: " + ex.getMessage());
				 ex.printStackTrace();
			 }
		 }
	 }



	 private void actionRemoveEpisode(){

		 int option = JOptionPane.showConfirmDialog(null,
				 "Remove Selected Episode? \n" + episodeJTF.getText(),
				 "Remove Episode",
				 JOptionPane.YES_NO_OPTION);

		 if(option == JOptionPane.YES_OPTION) {

			 try {
				 library.getSeasonLibrary().getSeriesSeason(seriesSeasonJTF.getText()).
						 removeEpisode(episodeJTF.getText());
			 } catch (Exception ex) {
				 System.out.println("Exception removing Episode: " + ex.getMessage());
				 ex.printStackTrace();
			 }
		 }
	 }


	 /**
	  * Helper method to Fetch URL Results and parse to JSON Object files so they
	  * can be added as SeriesSeason objects to the library.  Tree will be updated
	  * after successful library addition.
	  * @param jsonSeries :  string of series json data
	  * @param jsonEpisodes : string of episode json data
	  * @return void
	  * */
	 private void actionFetchResults(String jsonSeries, String jsonEpisodes) {

	 	try{

			library.parseURLtoJSON(jsonSeries, jsonEpisodes);
			refreshTree();

		} catch(Exception ex){
			System.out.println("Exception in actionFetchResults: " + ex.getMessage());
		}
	 }


	/**
	 * A method to do asynchronous url request printing the result to System.out
	 * @param aUrl the String indicating the query url for the OMDb api search
	 **/
	public void fetchAsyncURL(String aUrl) {
		try {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(aUrl))
					.timeout(Duration.ofMinutes(1))
					.build();
			client.sendAsync(request, BodyHandlers.ofString())
					.thenApply(HttpResponse::body)
					.thenAccept(System.out::println)
					.join();
		} catch (Exception ex) {
			System.out.println("Exception in fetchAsyncUrl request: " + ex.getMessage());
		}
	}


	/**
	 * a method to make a web request. Note that this method will block execution
	 * for up to 20 seconds while the request is being satisfied. Better to use a
	 * non-blocking request.
	 *
	 * @param aUrl the String indicating the query url for the OMDb api search
	 * @return the String result of the http request.
	 **/
	public String fetchURL(String aUrl) {
		StringBuilder sb = new StringBuilder();
		URLConnection conn = null;
		InputStreamReader in = null;
		try {
			URL url = new URL(aUrl);
			conn = url.openConnection();
			if (conn != null)
				conn.setReadTimeout(20 * 1000); // timeout in 20 seconds
			if (conn != null && conn.getInputStream() != null) {
				in = new InputStreamReader(conn.getInputStream(),
						Charset.defaultCharset());
				BufferedReader br = new BufferedReader(in);
				if (br != null) {
					int ch;
					// read the next character until end of reader
					while ((ch = br.read()) != -1) {
						sb.append((char) ch);
					}
					br.close();
				}
			}
			assert in != null;
			in.close();
		} catch (Exception ex) {
			System.out.println("Exception in url request:" + ex.getMessage());
		}
		return sb.toString();
	}


	/**
	 * main method: Program entry point.
	 * @param args :  taken default args or CLI user input.
	 * @return void
	 * */
	public static void main(String[] args) {

		String name = "first.last";
		String key = "use-your-last.ombd-key";
		if (args.length >= 2) {
			//System.out.println("java -cp classes:lib/json.lib ser321.assign2.lindquist."+
			//                   "MediaLibraryApp \"Lindquist Music Library\" lastFM-Key");
			name = args[0];
			key = args[1];
		}
		try {
			//System.out.println("calling constructor name "+name);
			new MediaLibraryApp(name, key);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	/**
	 * Method testCase() used for debugging.
	 * @return void
	 * */
	private static void testCase() {

		System.out.println("EPISODE TESTS: ");
		Episode epi = new Episode("Adam", "10.0", "Things happen");
		Episode ep2 = new Episode("Adam redux v.2", "11/10", "more things happen");
		System.out.println("SERIES TESTS: ");
		SeriesSeason series = new SeriesSeason("The GreatShow", "season 1", "8.5",
				"Action", "https://fakelink.com", "The plot thickens...");
		series.addToEpisodeList(epi);
		series.addToEpisodeList(ep2);
		System.out.println();
		SeriesSeason series2 = new SeriesSeason("Shows", "season 4", "5.5",
				"comedy", "weblinke.co", "some plot lines");
		Episode ep3 = new Episode("Epi 1", "8.0", "epSummary1");
		Episode ep4 = new Episode("Epi 2", "5/10", "epSummary2");
		Episode ep5 = new Episode("Epi 3", "1/10", "epSummary3");
		series2.addToEpisodeList(ep3);
		series2.addToEpisodeList(ep4);
		series2.addToEpisodeList(ep5);
		series2.printEpisodes();

		SeasonLibrary sl  = SeasonLibrary.getInstance();
		sl.addSeriesSeason(series);
		sl.addSeriesSeason(series2);
		sl.addSeriesSeason(series);
		sl.addSeriesSeason(series2);
		sl.printAll();
		sl.saveLibraryToFile("test.json");

		boolean flag = sl.restoreLibraryFromFile("test.json");
		System.out.println(flag);
		System.out.println("libraryMap size: " + sl.getlibrarySize());
		System.out.println("seasonlist size: " + sl.getSeriesSeasonList().size());
		sl.saveLibraryToFile("test_output.json");
	}
}
