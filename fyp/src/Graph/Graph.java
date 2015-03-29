package Graph;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class Graph {
	
	public int N;
	
	
	public List<GraphNode> nodes;
	public List<List<Integer> > adj; 

	public Graph(int N) {
		// TODO Auto-generated constructor stub
		this.N = N;
		
		nodes = new ArrayList<GraphNode>();
		adj = new ArrayList<List<Integer> >();
		
		for(int i=0;i<N;i++) {
			adj.add(new ArrayList<Integer>());
		}
	}
	
	public GraphNode getNode(int id) {
		return nodes.get(id);
	}
	
	public void addNode(GraphNode a) {
		nodes.add(a);
	}
	
	public void addEdge(int u, int v) {
		adj.get(u).add(v);
	}
	
	public List<Integer> getNeighbourList(int u) {
		return adj.get(u);
	}

	public static Graph read(String NodeFile, String EdgeFile) throws NumberFormatException, IOException {
		
		//read Nodes
		BufferedReader br = new BufferedReader(new FileReader(NodeFile));
		
		int N = Integer.valueOf(br.readLine());
		String line;
		
		Graph g = new Graph(N);
		
		while((line = br.readLine()) != null) {
			
			String[] tokens = line.trim().split(" ");
			int id = Integer.valueOf(tokens[0]);
			String type = tokens[1];			
			
			g.addNode(new GraphNode(id, type));
			
		}		
		
		//read Edges
		br = new BufferedReader(new FileReader(EdgeFile));
		
		while((line = br.readLine()) != null) {
			
			String[] parts = line.split(" ");
			
			int u = Integer.valueOf(parts[0]);
			int v = Integer.valueOf(parts[1]);
			
			g.addEdge(u, v);
			
		}
		
		return g;
		
	}
}
