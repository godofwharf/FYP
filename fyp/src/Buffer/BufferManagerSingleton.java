package Buffer;

public class BufferManagerSingleton {
	
	private static BufferManager ref;
	
	public static void setBufferManager(BufferManager bm) {
		ref = bm;
	}
	
	public static BufferManager getBufferManager() {
		if(ref == null) {
			System.out.println("Error: Buffer manager not initialized");
			System.exit(0);
		}
		return ref;
	}

}
