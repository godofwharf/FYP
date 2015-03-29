package Graph;

public class GraphSingleton {
	
	private static Graph ref;
	
	public static void setGraph(Graph g) {
		ref = g;
	}
	
	public static Graph getGraph() {
		if(ref == null) {
			System.out.println("Error - graph not created yet");
			System.exit(0);
		}
		return ref;
		
	}

}
