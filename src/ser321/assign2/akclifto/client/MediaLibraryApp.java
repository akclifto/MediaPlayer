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
		urlOMBD = pre + authorKey + "&t=";
		library = new SeasonLibrary();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// register this object as an action listener for menu item clicks. This will cause
		// my actionPerformed method to be called every time the user selects a menuitem.
		for (JMenuItem[] userMenuItem : userMenuItems) {
			for (int j = 0; j < userMenuItem.length; j++) {
				userMenuItem[j].addActionListener(this);
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
//			setAlbumImage(posterImg);
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
	private void setPosterImage(String posterLink) throws IOException {

		try {

			BufferedImage image = ImageIO.read(new URL(posterLink));
			// resize width
			int imgWidth = 290;
			// resize height
			int imgHeight = 366;
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
	 * TODO--------------------
	 *
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
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		clearTree(root, model);
		// put nodes in the tree for all registered with the library


		String[] titleList = library.getTitles();
		for (String s : titleList) {
			SeriesSeason aSS = library.get(s);
			String aMTitle = aSS.getTitle();
			debug("Adding episode with title: " + aSS.getTitle());
			DefaultMutableTreeNode toAdd = new DefaultMutableTreeNode(aMTitle);
			DefaultMutableTreeNode subNode = getSubLabelled(root, aSS.getSeason());
			if (subNode != null) { // if seriesSeason subnode already exists
				debug("seriesSeason exists: " + aSS.getSeason());
				model.insertNodeInto(toAdd, subNode,
						model.getChildCount(subNode));
			} else { // album node does not exist
				DefaultMutableTreeNode anAlbumNode =
						new DefaultMutableTreeNode(aSS.getSeason());
				debug("No Series, so add one with name: " + aSS.getSeason());
				model.insertNodeInto(anAlbumNode, root,
						model.getChildCount(root));
				DefaultMutableTreeNode aSubCatNode =
						new DefaultMutableTreeNode("aSubCat");
				debug("Adding subcat labelled: " + aSubCatNode);
				model.insertNodeInto(toAdd, anAlbumNode,
						model.getChildCount(anAlbumNode));
			}
		}
		// expand all the nodes in the JTree
		for (int r = 0; r < tree.getRowCount(); r++) {
			tree.expandRow(r);
		}
		tree.addTreeSelectionListener(this);
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
			DefaultMutableTreeNode next = null;
			int subs = model.getChildCount(root);
			for (int k = subs - 1; k >= 0; k--) {
				next = (DefaultMutableTreeNode) model.getChild(root, k);
				debug("removing node labelled:" + (String) next.getUserObject());
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

	// TODO:
	// this will be called when you click on a node.
	// It will update the node based on the information stored in the library
	// this will need to change since your library will be of course totally different
	// extremely simplified! E.g. make sure that you display sensible content when the root,
	// the My Series, the Series/Season, and Episode nodes are selected
	public void valueChanged(TreeSelectionEvent e) {

		try {

			tree.removeTreeSelectionListener(this);
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)
					tree.getLastSelectedPathComponent();

			if (node != null) {

				String nodeLabel = (String) node.getUserObject();
				debug("In valueChanged. Selected node labelled: " + nodeLabel);
				// is this a terminal node?

				// All fields empty to start with
				seriesSeasonJTF.setText("");
				genreJTF.setText("");
				setPosterImage(posterImg);
				ratingJTF.setText("");
				episodeJTF.setText("");
				summaryJTA.setText("");


				DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot(); // get the root
				// First (and only) child of the root (username) node is 'My Series' node.
				DefaultMutableTreeNode mySeries = (DefaultMutableTreeNode) root.getChildAt(0); // mySeries node
				DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();

				// TODO when it is an episode change the episode to something and set the rating to the episode rating
				if (node.getChildCount() == 0 &&
						(node != (DefaultMutableTreeNode) tree.getModel().getRoot())) {

					SeriesSeason ss = library.get(nodeLabel);
					//set text to panels to displayed selected node information
					episodeJTF.setText(nodeLabel);                // name of the episode
					ratingJTF.setText(ss.getImdbRating());    // change to rating of the episode
					String parentLabel = (String) parent.getUserObject();
					genreJTF.setText(ss.getGenre());
					setPosterImage(ss.getPosterLink());
					summaryJTA.setText(ss.getPlotSummary());
					seriesSeasonJTF.setText(parentLabel);        // Change to season name

				} else if (parent == root) {                    // should be the series/season


					seriesSeasonJTF.setText(nodeLabel);            // season name
					genreJTF.setText("Genre");                    // genre of the series from library
					ratingJTF.setText("IMDB Rating");            // rating of the season get from library
					episodeJTF.setText("Episode Name");            // nothing in here since not an episode
					summaryJTA.setText("Plot Summary");         // plot summary
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		tree.addTreeSelectionListener(this);
	}

	// TODO: this is where you will need to implement a bunch. So when some action is called the correct thing happens
	public void actionPerformed(ActionEvent e) {
		tree.removeTreeSelectionListener(this);
		if (e.getActionCommand().equals("Exit")) {
			System.exit(0);
		} else if (e.getActionCommand().equals("Save")) {
			boolean savRes = false;
			System.out.println("Save " + ((savRes) ? "successful" : "not implemented")); //TODO implement that current library is saved to JSON file
		} else if (e.getActionCommand().equals("Restore")) {
			boolean resRes = false;
			rebuildTree();
			System.out.println("Restore " + ((resRes) ? "successful" : "not implemented")); // TODO: implement that tree is restored to library
		} else if (e.getActionCommand().equals("Series-SeasonAdd")) {
			System.out.println("Series-SeasonAdd not implemented"); // TODO: implement that the whole season with all episodes currently in tree will be added to library
		} else if (e.getActionCommand().equals("Search")) {
			// TODO: implement that the search result is used to create new series/season object
			/*
			 * In the below API(s) the error response should be appropriately handled
			 */

			// with all episodes only display this new series/season with the episodes in tree

			// Doing a fetch two times so that we only get the full series info (with poster, summary, rating) once
			// fetch series info
			String searchReqURL = urlOMBD + seriesSearchJTF.getText().replace(" ", "%20");
			System.out.println("calling fetch with url: " + searchReqURL);
			String json = fetchURL(searchReqURL);
			System.out.println("Fetch result just season: " + json);

			// fetch season info
			String searchReqURL2 = urlOMBD + seriesSearchJTF.getText().replace(" ", "%20") + "&season=" + seasonSearchJTF.getText();
			String jsonEpisodes = fetchURL(searchReqURL2);
			System.out.println("Fetch result episodes: " + jsonEpisodes);

			/* TODO: implement here that this json will be used to create a Season object with the episodes included
			 * This should also then build the tree and display the info in the left side bar (so the new tree with its episodes)
			 * right hand should display the Series information
			 */

		} else if (e.getActionCommand().equals("Tree Refresh")) {
			rebuildTree();
		} else if (e.getActionCommand().equals("Series-SeasonRemove")) {
			System.out.println("Series-SeasonRemove not implemented"); //TODO: remove the season from library

		}
		tree.addTreeSelectionListener(this);
	}

	/**
	 * A method to do asynchronous url request printing the result to System.out
	 *
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

	public static void main(String[] args) {




//		String name = "first.last";
//		String key = "use-your-last.ombd-key";
//		if (args.length >= 2) {
//			//System.out.println("java -cp classes:lib/json.lib ser321.assign2.lindquist."+
//			//                   "MediaLibraryApp \"Lindquist Music Library\" lastFM-Key");
//			name = args[0];
//			key = args[1];
//		}
//		try {
//			//System.out.println("calling constructor name "+name);
//			MediaLibraryApp mla = new MediaLibraryApp(name, key);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
	}

}
