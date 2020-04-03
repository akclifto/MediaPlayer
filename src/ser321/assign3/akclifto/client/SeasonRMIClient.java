package ser321.assign3.akclifto.client;

import ser321.assign2.lindquis.MediaLibraryGui;
import ser321.assign3.akclifto.server.Library;

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
import java.rmi.Naming;
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
 * Purpose: SeasonRMIClient is the client-side application controller for the tv series
 * media player.  functionality uses the MediaLibraryGUI to display/search/manage tv show
 * series library content.
 *
 * @author Tim Lindquist (Tim.Linquist@asu.edu),
 * Software Engineering, CIDSE, IAFSE, ASU Poly
 * @author Adam Clifton (akclifto@asu.edu)
 * Software Engineering, ASU
 * @version April 2020
 */
public class SeasonRMIClient extends MediaLibraryGui implements
		TreeWillExpandListener,
		ActionListener,
		TreeSelectionListener {

	private static final boolean debugOn = false;
	private static final String pre = "https://www.omdbapi.com/?apikey=";
	private static String urlOMBD;
	private Library libraryServer;
	private String omdbKey;
	private static String posterImg =
			"http://2.bp.blogspot.com/-tE3fN3JVM-c/TjtR1B_o9tI/AAAAAAAAAXo/vZN2fWNVgF4/s1600/movie_reel.jpg";

	public SeasonRMIClient(String author, String authorKey, Library server) {
		// sets the value of 'author' on the title window of the GUI.
		super(author);
		this.omdbKey = authorKey;
		urlOMBD = pre + omdbKey + "&t=";
		libraryServer = server;
		try {
	//		libraryServer.printAll();
			//libraryServer.initializeServer();
		} catch (Exception ex){
			System.out.println("Exception in SeasonRMIClient library: " + ex.getMessage());
		}

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
			// set poster image here-----default is setAlbumImage.  This is re-done to resize image.
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

		try {
			tree.removeTreeSelectionListener(this);

			DefaultTreeModel model = (DefaultTreeModel) tree.getModel();  //bases structure
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot(); //root tree node (named as "author")
			clearTree(root, model); //clear the tree

			// put nodes in the tree for all registered with the library
			DefaultMutableTreeNode libraryNode = new DefaultMutableTreeNode("Library");
			model.insertNodeInto(libraryNode, root, model.getChildCount(root));

			String[] seriesTitles = libraryServer.getSeriesSeasonTitles();  //array of all titles in the library

			for (String title : seriesTitles) {

				setTreeSeriesNodes(model, libraryNode, title);
			}

			// expand all the nodes in the JTree
			for (int r = 0; r < tree.getRowCount(); r++) {
				tree.expandRow(r);
			}
		} catch(Exception ex){
			System.out.println("Exception in rebuilTree: " + ex.getMessage());
			ex.printStackTrace();
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

		try{

//			String seriesName = libraryServer.getSeriesSeason(title).getTitle();
//			String[] epTitles = libraryServer.getSeriesSeason(title).getEpisodeTitles();
			String seriesName = libraryServer.getSeriesSeasonsTitle(title);
			String[] epTitles = libraryServer.getEpisodeTitles(title);


			DefaultMutableTreeNode seriesToAdd = new DefaultMutableTreeNode(seriesName);  // series node to add to tree
//			DefaultMutableTreeNode subNode = getSubLabelled(root, libraryServer.getSeriesSeason(title).getTitle());  // sub nodes to seriesToAdd
			DefaultMutableTreeNode subNode = getSubLabelled(root, libraryServer.getSeriesSeasonsTitle(title));  // sub nodes to seriesToAdd

			if(subNode != null) { //if series exists.

				debug("seriesSeason exists: " + libraryServer.getSeriesSeasonsTitle(title));
				model.insertNodeInto(seriesToAdd, subNode, model.getChildCount(subNode));

//				if(libraryServer.getSeriesSeason(title).checkEpisodes()){
				if(libraryServer.checkEpisodes(title)){

					setTreeEpisodeNodes(model, subNode, epTitles);
				}

			} else {  // if series does not exist.

				DefaultMutableTreeNode seriesNode = new DefaultMutableTreeNode(seriesName);
				debug("No series, so adding one with name: " + seriesName);
				model.insertNodeInto(seriesNode, root, model.getChildCount(root));

//				if(libraryServer.getSeriesSeason(title).checkEpisodes())
				if(libraryServer.checkEpisodes(title))
				{
					setTreeEpisodeNodes(model, seriesNode, epTitles);
				}
			}
//			System.out.println("From server, Tree Series Nodes Set for " + libraryServer.getSeriesSeason(title).getTitle());
			System.out.println("From server, Tree Series Nodes Set for " + libraryServer.getSeriesSeasonsTitle(title));
		} catch(Exception ex){
			System.out.println("Exception in setTreeSeriesNodes: " + ex.getMessage());
			ex.printStackTrace();
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


				//DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot(); // get the root
				// First (and only) child of the root (username) node is 'My Series' node.
				//DefaultMutableTreeNode rootLibrary = (DefaultMutableTreeNode) root.getNextNode(); // Library node
				DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();

				//System.out.println("node level is ---------------: " + node.getLevel());

				if(node.getLevel() == series){

					int episodeCount = libraryServer.getSeriesSeason(nodeLabel).getEpisodeList().size();

					// Change to season name
					// change to rating of the episode
					if(episodeCount == 1){
						episodeJTF.setText(" " + episodeCount + " Episode in library");            // name of the episode
					} else {
						//set text to panels to displayed selected node information
						episodeJTF.setText(" " + episodeCount + " Episodes in library");            // name of the episode
					}
					ratingJTF.setText(libraryServer.getSeriesSeason(nodeLabel).getImdbRating());        // change to rating of the episode
					genreJTF.setText(libraryServer.getSeriesSeason(nodeLabel).getGenre());
					setPosterImage(libraryServer.getSeriesSeason(nodeLabel).getPosterLink());
					summaryJTA.setText(libraryServer.getSeriesSeason(nodeLabel).getPlotSummary());
					seriesSeasonJTF.setText(libraryServer.getSeriesSeason(nodeLabel).getTitle());      // Change to season name
				} else if (node.getLevel() == episode){

					String parentLabel = (String) parent.getUserObject();

					//set text to panels to displayed selected node information
					episodeJTF.setText(libraryServer.getSeriesSeason(parentLabel).getEpisode(nodeLabel).getName());    // name of the episode
					ratingJTF.setText(libraryServer.getSeriesSeason(parentLabel).getEpisode(nodeLabel).getImdbRating());  // change to rating of the episode
					genreJTF.setText(libraryServer.getSeriesSeason(parentLabel).getGenre());
					setPosterImage(libraryServer.getSeriesSeason(parentLabel).getPosterLink());
					summaryJTA.setText(libraryServer.getSeriesSeason(parentLabel).getEpisode(nodeLabel).getEpSummary());
					seriesSeasonJTF.setText(libraryServer.getSeriesSeason(parentLabel).getTitle());      // Change to season name

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

			actionAddSeries();

		} else if (e.getActionCommand().equals("Series-SeasonRemove")) {

			actionRemoveSeries();

		} else if(e.getActionCommand().equalsIgnoreCase("EpisodeAdd")) {

			actionAddEpisode();

		} else if (e.getActionCommand().equalsIgnoreCase("EpisodeRemove")){

			actionRemoveEpisode();

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
			boolean flag = libraryServer.saveLibraryToFile("series.json");

			if(flag){
				System.out.println("From server, file saved to library");
			}
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
			 flag = libraryServer.restoreLibraryFromFile("series.json");

			 if (!flag) {
				 flag = libraryServer.restoreLibraryFromFile("seriesDefault.json");
			 }
			 int size = libraryServer.getLibrarySize();
			 System.out.println("From server, library restored with " + size + " series entries.");
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
	  * Helper method to remove series from library.  This will remove any episdoes associated with it.
	  * @return void
	  * */
	 private void actionRemoveSeries(){

	 	try {
			String series = libraryServer.getSeriesSeason(seriesSeasonJTF.getText()).getTitle();
			int option = JOptionPane.showConfirmDialog(null,
					"Remove Selected Series? \n" + seriesSeasonJTF.getText(),
					"Remove Series-Season",
					JOptionPane.YES_NO_OPTION);

			if (option == JOptionPane.YES_OPTION) {

				try {
					libraryServer.removeSeriesSeason(seriesSeasonJTF.getText());
					refreshTree();
				} catch (Exception ex) {
					System.out.println("Exception removing Series-Season: " + ex.getMessage());
					ex.printStackTrace();
				}
				System.out.println("From server, removes " + series + " from the library.");
			}
		} catch(Exception ex){
			System.out.println("Exception in actionRemoveSeries: " + ex.getMessage());
		}
	 }


	/**
	 * Helper Method to removeEpisode from a episode list of a series.
	 * @return void.
	 * */
	 private void actionRemoveEpisode(){

		 int option = JOptionPane.showConfirmDialog(null,
				 "Remove Selected Episode? \n" + episodeJTF.getText(),
				 "Remove Episode",
				 JOptionPane.YES_NO_OPTION);

		 if(option == JOptionPane.YES_OPTION) {

			 try {
				 libraryServer.getSeriesSeason(seriesSeasonJTF.getText()).
						 removeEpisode(episodeJTF.getText());
				 refreshTree();
			 } catch (Exception ex) {
				 System.out.println("Exception removing Episode: " + ex.getMessage());
				 ex.printStackTrace();
			 }
			 System.out.println("From server, removes " + episodeJTF.getText() + " from the library.");
		 }
	 }


	 /**
	  * Helper method to add an episode to a series season
	  * @return void
	  * */
	 private void actionAddEpisode(){

		 int option = JOptionPane.showConfirmDialog(null,
				 "Add Episode? \n" + episodeJTF.getText(),
				 "Add Episode",
				 JOptionPane.YES_NO_OPTION);

		 if(option == JOptionPane.YES_OPTION) {
			 try {
				 System.out.println("Unsure how to implement.");
//				 library.getLibraryServer().getSeriesSeason(seriesSeasonJTF.getText()).
//						 removeEpisode(episodeJTF.getText());
				 refreshTree();
				 System.out.println("From server, " + libraryServer.getSeriesSeason(episodeJTF.getText()) +
						 " removed from library.");
			 } catch (Exception ex) {
				 System.out.println("Exception removing Episode: " + ex.getMessage());
				 ex.printStackTrace();
			 }
		 }
	 }


	 /**
	  * Help method to add series to Library based on user season and search input text fields.
	  * @return void.
	  * */
	 private void actionAddSeries(){

		 int option = JOptionPane.showConfirmDialog(null,
				 "Add Series? \n" + seriesSearchJTF.getText() + " - Season " + seasonSearchJTF.getText(),
				 "Add Series",
				 JOptionPane.YES_NO_OPTION);

		 if(option == JOptionPane.YES_OPTION) {

			 try {

			 	 // fetch series info
				 String searchReqURL = urlOMBD + seriesSearchJTF.getText().replace(" ", "%20");
				 String jsonSeries = fetchURL(searchReqURL);
				 // fetch season info
				 String searchReqURL2 = urlOMBD + seriesSearchJTF.getText().replace(" ", "%20")
						 + "&season=" + seasonSearchJTF.getText();
				 String jsonEpisodes = fetchURL(searchReqURL2);

				 actionFetchResults(jsonSeries, jsonEpisodes);
				 refreshTree();

			 } catch (Exception ex) {
				 System.out.println("Exception removing Episode: " + ex.getMessage());
				 ex.printStackTrace();
			 }
			 System.out.println("From server, " + seriesSearchJTF.getText() + "added to library.");
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

			libraryServer.parseURLtoJSON(jsonSeries, jsonEpisodes);

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

		try {
			String hostId = "localhost";
			String regPort = "8888";
			String name = "first.last";
			String key = "use-your-last.ombd-key";

			if (args.length >= 4) {
				hostId = args[0];
				regPort = args[1];
				name = args[2];
				key = args[3];
			}

			Library libraryServer;
			libraryServer = (Library)Naming.lookup(
					"rmi://"+hostId+":"+regPort+"/LibraryServer");
			System.out.println("Client obtained remote object reference to" +
					" the LibraryServer");
			new SeasonRMIClient(name, key, libraryServer);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
