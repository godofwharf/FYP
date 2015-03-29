package Node;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.xml.transform.Source;

import ch.epfl.arni.ncutils.CodedPacket;
import ch.epfl.arni.ncutils.CodingVectorDecoder;
import ch.epfl.arni.ncutils.NetworkCodeTester;
import ch.epfl.arni.ncutils.NetworkCoder;
import ch.epfl.arni.ncutils.PacketDecoder;
import ch.epfl.arni.ncutils.UncodedPacket;
import ch.epfl.arni.ncutils.Vector;
import Buffer.Buffer;
import Buffer.BufferManager;
import Main.SourceDriver;
import Routing.Router;
import SingletonFactory.SingletonFactory;

public class Node implements Runnable{
	
	private int id;
	
	private String type;
	
	private CodingVectorDecoder cvd;
	
	private PacketDecoder decoder;
	
	private int decoded;
	
	private boolean isDecoded;
	
	private boolean wasAdded;
	
	private int currentSent;	
	
	private List<UncodedPacket> decodedPackets;
	
	private static File f = new File(SourceDriver.PATH + "log.txt");
	
	public Node(int id, String type) {
		this.id = id;
		this.type = type;	
		
		currentSent = 0;
		if(type.equals("sf") || type.equals("source")) {
			cvd = null;
			decoder = null;
		}
		else if(type.equals("nc")) {
			cvd = new CodingVectorDecoder(NetworkCoder.no_of_blocks , NetworkCoder.ff);
			decoder = null;
		}
		else {
			cvd = new CodingVectorDecoder(NetworkCoder.no_of_blocks , NetworkCoder.ff);
			decoder = new PacketDecoder(NetworkCoder.ff, NetworkCoder.no_of_blocks, NetworkCoder.payloadLen);
			decodedPackets = new ArrayList<UncodedPacket>();	
		}
		
		decoded = 0;
		currentSent = 0;
		isDecoded = false;
		wasAdded = false;
	
		
	}	
	
	public synchronized void log(String msg) {
		
		FileWriter fw;
		try {
			fw = new FileWriter(f.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write(msg);
			bw.newLine();
			bw.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		
	}
	
	public String getType() {
		return type;
	}
	
	public synchronized boolean isLinearlyIndependent(CodedPacket c) {
		Map<Integer, Vector> iv = cvd.addVector(c.getCodingVector());
		
		if(iv != null)
			return true;
		else
			return false;
		
	}
	
	@Override
	public void run() {
		currentSent = 0;
		NetworkCoder nc = SingletonFactory.getNetworkCode();
		
		BufferManager bm = SingletonFactory.getBufferManager();
		
		Router r = SingletonFactory.getRouter();
		
		while(type.equals("source") && r.haveToSend() ) {
			Buffer b = bm.getBufferByNode(id);
			// System.out.println("source");
			if(b.getUncodedPackets() == null)
				continue;
			else {
				List<Integer> neigbours = r.getNeighbours(id);
				
				for(Integer i: neigbours) {
					
						CodedPacket c = nc.getUncodedRandomCombination(b.getUncodedPackets());
					
						sendCodedPacket(c, i.intValue());
						
					
				}
				
			}
			Thread.yield();
			// log(Thread.currentThread().getName() + " " + "no. of coded packets = " + b.getCodedPackets().size());
		}
		
		while(type.equals("sf") && r.haveToSend() ) {
			Buffer b = bm.getBufferByNode(id);
			if(b.getQueueSize() == 0)
				continue;
			else {
				// something to forward
				List<Integer> neigbours = r.getNeighbours(id);
				int k = neigbours.size();
				int cnt = 0;
				CodedPacket c = b.peekQueue();
				
				for(Integer i: neigbours) {			
					
					boolean sent = sendCodedPacket(c, i.intValue());
					if(sent)
						cnt += 1;
				}
				if(cnt > 0)
					b.removeCodedPacketFromQueue();
			}
		}
		
		while( type.equals("nc") && r.haveToSend() ) {
			Buffer b = bm.getBufferByNode(id);
			// System.out.println("nc");
			
			if(b.getCodedPackets() == null)
				continue;
			else {
				List<Integer> neigbours = r.getNeighbours(id);
								
				for(Integer i: neigbours) {
					
						int sz = b.getCodedPackets().size();
						CodedPacket c = nc.getCodedRandomCombination(b.getCodedPackets(), sz);
						sendCodedPacket(c, i.intValue());
					
					
					
				}
			}
			/* if(b.getCodedPackets().size() == NetworkCoder.no_of_blocks) {
				Thread.yield();
			} */
			Thread.yield();
			log(Thread.currentThread().getName() + " " + "no. of coded packets = " + b.getCodedPackets().size());
			
		}
		
		while(type.equals("client")) {
			Buffer b = bm.getBufferByNode(id);
			// System.out.println("client");
			
			int n = NetworkCoder.no_of_blocks;
			List<CodedPacket> c;					
						
			if(!wasAdded && (c = b.getCodedPackets()) != null && c.size() == n) {
				System.out.println("Client with id = " + id + " can decode all packets");	
				// inform other nodes not to send to this client
				r.addToCompleted(id);
				wasAdded = true;
			}
			
			if(b.getCodedPackets() != null && r.haveToSend()) {
				List<Integer> neigbours = r.getNeighbours(id);				
				for(Integer i: neigbours) {			
					int sz = b.getCodedPackets().size();					
					CodedPacket cod = nc.getCodedRandomCombination(b.getCodedPackets(), sz);
					sendCodedPacket(cod, i.intValue());					
				}
			}
			
			if(decodedPackets.size() < n) {
				// try to decode
				List<CodedPacket> codedPackets = b.getCodedPackets(); 
				if(codedPackets != null) {
					int j = decoded;
					int k = codedPackets.size();
					for(int i = 0; i < k; i++) {
						List<UncodedPacket> packets = decoder.addPacket(codedPackets.get(i));
						decodedPackets.addAll(packets);	
						j += packets.size();
						
					}
					decoded = j;
				}
				
			}
			else {
				// decoding is complete
				NetworkCodeTester nct = new NetworkCodeTester();
			    Collections.sort(decodedPackets, nct.new PacketComparator());
				b.setUncodedPackets(decodedPackets);
				isDecoded = true;
			}
			
			if(isDecoded && !r.haveToSend())
				break;
			
			Thread.yield();
			// log(Thread.currentThread().getName() + " " + "no. of coded packets = " + b.getCodedPackets().size());
		}	
		
	}
	
	private static boolean sendCodedPacket(CodedPacket c, int id) {
				
		BufferManager bm = SingletonFactory.getBufferManager();
		
		NodeManager nm = SingletonFactory.getNodeManager();
		
		Buffer b = bm.getBufferByNode(id);		
		
		Node v = nm.getNode(id);
		
		boolean sent = false;
		
		if(v.getType().equals("nc") || v.getType().equals("client")) {
			
			try {
				Thread.currentThread().sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			if(!dropPacket() && v.isLinearlyIndependent(c)) {
				//log("Sending packet from Node " + this.id + " to Node " + id );
			
				b.addCodedPacket(c);
				
				sent = true;
			}
			else {
				//System.out.println("Received coded packet not useful, so dropped");	
				SourceDriver.wasted += 1;
			}
		}
		else if(v.getType().equals("sf")) {
			try {
				Thread.currentThread().sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(!dropPacket()) {
				sent = b.addCodedPacketToQueue(c);
				if(!sent)
					SourceDriver.count[v.id] += 1;
			}
		}
		return sent;
	}
	
	private static boolean dropPacket() {
		Random generator = new Random(System.currentTimeMillis());
		if(generator.nextDouble() > 0.2)
			return false;
		else
			return true;
	}

}
