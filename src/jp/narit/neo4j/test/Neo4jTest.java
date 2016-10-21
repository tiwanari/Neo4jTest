package jp.narit.neo4j.test;

import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Expander;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.Traversal;

public class Neo4jTest {
	
	private static final String NAME = "NAME";
	
	enum SSSPRelationshipType implements RelationshipType {
		Link
	}
	
	public static void main(String[] args) {
		GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase("ssp");
		Node tokyo, osaka;
		try (Transaction tx = graphDb.beginTx()) {
			tokyo = graphDb.createNode();
			tokyo.setProperty(NAME, "東京");
			osaka = graphDb.createNode();
			osaka.setProperty(NAME, "大阪");
			tx.success();
		}
		try (Transaction tx = graphDb.beginTx()) {
			Relationship rel = tokyo.createRelationshipTo(osaka, SSSPRelationshipType.Link); // "東京"と"大阪"のLinkを作成
			rel.setProperty("Length", 9);
			tx.success();
		}
		try (Transaction tx = graphDb.beginTx()) {
			Expander OUTGOING_LINKS = Traversal.expanderForTypes(SSSPRelationshipType.Link, Direction.OUTGOING);
			PathFinder<WeightedPath> finder = GraphAlgoFactory.dijkstra(OUTGOING_LINKS, "Length");
		
			long t0 = System.currentTimeMillis();
			Path path = finder.findSinglePath(tokyo, osaka);
			long t1 = System.currentTimeMillis();
			System.out.println("path: " + path.length() + ", time: " + (t1-t0) + " ms");
			tx.success();
		}
		
	}
}
