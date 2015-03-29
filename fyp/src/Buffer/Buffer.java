package Buffer;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import Node.NodeManager;
import SingletonFactory.SingletonFactory;
import ch.epfl.arni.ncutils.CodedPacket;
import ch.epfl.arni.ncutils.CodingVectorDecoder;
import ch.epfl.arni.ncutils.NetworkCoder;
import ch.epfl.arni.ncutils.NetworkCoderSingleton;
import ch.epfl.arni.ncutils.PacketDecoder;
import ch.epfl.arni.ncutils.UncodedPacket;

/**
 * Buffer class for each node in the network
 * @author Guru
 * 
 *
 */
public class Buffer {
	private List<UncodedPacket> uncodedPackets;
	private List<CodedPacket> codedPackets;
	
	private Queue<CodedPacket> queue;
	
	private int maxThreshold;
	private int capacity;
	
	private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
	private final ReentrantReadWriteLock readWriteLockQueue = new ReentrantReadWriteLock(true);
	private final ReentrantReadWriteLock readWriteLockThreshold = new ReentrantReadWriteLock(true);
	
	private final Lock read  = readWriteLock.readLock();
	private final Lock write = readWriteLock.writeLock();
	
	private final Lock readQueue = readWriteLockQueue.readLock();
	private final Lock writeQueue = readWriteLockQueue.writeLock();
	
	private final Lock readThreshold = readWriteLockThreshold.readLock();
	private final Lock writeThreshold = readWriteLockThreshold.writeLock();
	
	
	
	public Buffer(int id, List<UncodedPacket> uncodedPackets, List<CodedPacket> codedPackets) {
		this.uncodedPackets = uncodedPackets;
		this.codedPackets = codedPackets;
		
		NetworkCoder nc = SingletonFactory.getNetworkCode();
		NodeManager nm = SingletonFactory.getNodeManager();
		
		String type = nm.getNodeType(id);
		
		maxThreshold = -1;
		capacity = -1;
		
		if(type.equals("sf")) {
			queue = new LinkedList<CodedPacket>();
			capacity = 10;
			maxThreshold = 3;
		}
		else if(type.equals("nc")) {
			queue = null;
			this.codedPackets = new ArrayList<CodedPacket>();			
		}		
		else if(type.equals("client")) {
			queue = null;
			
			this.codedPackets = new ArrayList<CodedPacket>();
			this.uncodedPackets = new ArrayList<UncodedPacket>();
		}
		else {
			queue = null;
			this.codedPackets = new ArrayList<CodedPacket>();
			
		}
	}		
	
	public int getMaxThreshold() {
		readThreshold.lock();
		int ret = 0;
		try {
			ret = maxThreshold;
		}
		finally {
			readThreshold.unlock();
			
		}
		return ret;
	}
	
	public void setMaxThreshold(int mT) {
		writeThreshold.lock();
		try 
		{
			maxThreshold = mT;
		}
		finally {
			writeThreshold.unlock();
		}
	}
	
	
	public void addCodedPacket(CodedPacket c) {
		write.lock();
		try {			
			codedPackets.add(c);			
		}
		finally {
			write.unlock();
		}
	}
	
	public int getQueueSize() {
		readQueue.lock();
		int sz = 0;
		try {
			sz = queue.size();
		}
		finally {
			readQueue.unlock();
		}
		return sz;
	}
	
	public boolean addCodedPacketToQueue(CodedPacket c) {
		writeQueue.lock();
		boolean ret = false;
		try {
			if(queue.size() < maxThreshold) {
				queue.add(c);
				ret = true;
			}
		}
		finally {
			writeQueue.unlock();
		}
		return ret;
	}
	
	public CodedPacket peekQueue() {
		readQueue.lock();
		CodedPacket ret;
		try {
			ret = queue.peek();
		}
		finally {
			readQueue.unlock();
		}
		return ret;
	}
	
	public CodedPacket removeCodedPacketFromQueue() {
		writeQueue.lock();
		CodedPacket ret;
		try {
			// removes and returns first element - dequeue
			ret = queue.remove();
		
		}
		finally {
			writeQueue.unlock();
		}
		return ret;
	}
	
	// setters
	public void setUncodedPackets(List<UncodedPacket> u) {
		uncodedPackets = u;
	}
	
	public void setCodedPackets(List<CodedPacket> c) {
		codedPackets = c;
	}
	
	// getters
	public List<UncodedPacket> getUncodedPackets() {
		return uncodedPackets;
	}
	
	public List<CodedPacket> getCodedPackets() {
		read.lock();
		try {
			return codedPackets;
		}
		finally {
			read.unlock();
		}		
	}
	
}
