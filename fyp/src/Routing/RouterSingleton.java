package Routing;

public class RouterSingleton {

	private static Router ref;
	
	public static void setRouter(Router r) {
		ref = r;
	}
	
	public static Router getRouter() {
		if(ref == null) {
			System.out.println("Error - router not initialized");
			System.exit(0);
		}
		return ref;
	}
}
