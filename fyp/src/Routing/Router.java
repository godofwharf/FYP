package Routing;

import java.util.ArrayList;
import java.util.List;

import Tree.GenericTreeNode;

public class Router {
	
	private List<Integer> clients;
	
	private List<Integer> completed;
	
	private int src;
	
	private Treebone treebone; 
	
	private RandomGossiper randomGossiper;
	
	public Router(int src, List<Integer> cli) {
		clients = new ArrayList<Integer>();
		completed = new ArrayList<Integer>();
		treebone = new Treebone(src);
		randomGossiper = new RandomGossiper();
		
		this.src =  src;
		this.clients = cli;
		
	}
	
	
	
	public void addClient(int c) {
		clients.add(c);
	}
	
	public void removeClient(int c) {
		clients.remove(c);
	}
	
	public List<Integer> getClients() {
		return clients;
	}
	
	public synchronized boolean haveToSend() {
		
		if(completed == null || clients.size() > completed.size())
			return true;
		else
			return false;
		
	}
	
	public void setCompleted(List<Integer> completed) {
		this.completed = completed;
	}
	
	public synchronized void addToCompleted(int id) {
		completed.add(id);
	}
	
	public List<Integer> getNeighbours(int u) {
		
		GenericTreeNode<Integer> g;
		
		if((g = treebone.lookup(u)) != null && g.getNumberOfChildren() > 0){
			
			return convert(g.children);
			
		}
		else{
			
			return randomGossiper.getRandomSubset(u); //.getNeighbours(u);
			
		}
		
		
		
	}
	
	public List<Integer> convert(List<GenericTreeNode<Integer>> lg) {
		List<Integer> li = new ArrayList<Integer>();
		
		for(GenericTreeNode<Integer> g: lg) {
			li.add(g.data);
		}
		
		return li;
	}
}
