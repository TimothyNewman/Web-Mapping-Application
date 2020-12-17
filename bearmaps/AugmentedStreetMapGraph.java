package bearmaps;
import bearmaps.utils.graph.streetmap.StreetMapGraph;
import java.util.*;
import bearmaps.utils.graph.streetmap.Node;
import bearmaps.utils.ps.KDTree;
import bearmaps.utils.ps.MyTrieSet;
import bearmaps.utils.ps.Point;

/**
 * An augmented graph that is more powerful that a standard StreetMapGraph.
 * Specifically, it supports the following additional operations:
 *
 *
 * @author Alan Yao, Josh Hug, ________
 */
public class AugmentedStreetMapGraph extends StreetMapGraph {
    private HashMap<Point, Node> streetMapping;
    private KDTree myTree;
    private List<Point> allPoints;
    private MyTrieSet trieSet;
    private HashMap<String, LinkedList<Node>> stringMapper;

    public AugmentedStreetMapGraph(String dbPath) {
        super(dbPath);
        List<Node> nodes = this.getNodes();
        streetMapping = new HashMap<>();
        allPoints = new LinkedList<>();
        trieSet = new MyTrieSet();
        stringMapper = new HashMap<>();
        for (Node element : nodes) {
            if (element.name() != null) {
                String cleanedString = cleanString(element.name());
                if (!stringMapper.containsKey(cleanedString)) {
                    LinkedList<Node> newLL = new LinkedList<>();
                    newLL.add(element);
                    stringMapper.put(cleanedString, newLL);
                    trieSet.add(cleanedString);
                } else {
                    LinkedList<Node> retrieved = stringMapper.get(cleanedString);
                    retrieved.add(element);
                }
            }
            if (neighbors(element.id()).size() > 0) {
                Point newPoint = new Point(element.lon(), element.lat());
                streetMapping.put(newPoint, element);
                allPoints.add(newPoint);
            }
        }
        myTree = new KDTree(allPoints);
    }

    /**
     * Returns the vertex closest to the given longitude and latitude.
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    public long closest(double lon, double lat) {
        Point closestPoint = myTree.nearest(lon, lat);
        return streetMapping.get(closestPoint).id();
    }

    /**
     * In linear time, collect all the names of OSM locations that prefix-match the query string.
     * @param prefix Prefix string to be searched for. Could be any case, with our without
     *               punctuation.
     * @return A <code>List</code> of the full names of locations whose cleaned name matches the
     * cleaned <code>prefix</code>.
     */
    public List<String> getLocationsByPrefix(String prefix) {
        ArrayList<String> locations = new ArrayList<>();
        List<String> trieList = trieSet.keysWithPrefix(cleanString(prefix));
        for (String cleanedString: trieList) {
            LinkedList<Node> nodeList = stringMapper.get(cleanedString);
            for (Node n : nodeList) {
                locations.add(n.name());
            }
        }
        return locations;
    }

    /**
     * Collect all locations that match a cleaned <code>locationName</code>, and return
     * information about each node that matches.
     * @param locationName A full name of a location searched for.
     * @return A list of locations whose cleaned name matches the
     * cleaned <code>locationName</code>, and each location is a map of parameters for the Json
     * response as specified: <br>
     * "lat" -> Number, The latitude of the node. <br>
     * "lon" -> Number, The longitude of the node. <br>
     * "name" -> String, The actual name of the node. <br>
     * "id" -> Number, The id of the node. <br>
     */
    public List<Map<String, Object>> getLocations(String locationName) {
        List<Map<String, Object>> returnList = new ArrayList<>();
        String cleaned = cleanString(locationName);
        for (Node n : stringMapper.get(cleaned)) {
            HashMap<String, Object> contents = new HashMap<>();
            contents.put("lat", n.lat());
            contents.put("lon", n.lon());
            contents.put("name", n.name());
            contents.put("id", n.id());
            returnList.add(contents);
        }

        return returnList;
    }


    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    private static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    public static void main (String[] args) {
        AugmentedStreetMapGraph test = new AugmentedStreetMapGraph("../library-su19/data/proj3_xml/berkeley-2019.osm.xml");
        List<String> locations = test.getLocationsByPrefix("g");
    }
}
