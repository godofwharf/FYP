package ch.epfl.arni.ncutils;



public class NetworkCoderSingleton {
	
	private static NetworkCoder ref;
	
	public static void setNetworkCode(NetworkCoder nc) {
		ref = nc;
	}
	
	public static NetworkCoder getNetworkCode() {
		if(ref == null) {
			System.out.println("Error: Network code not initialized");
			System.exit(0);
		}
		return ref;
	}

}

