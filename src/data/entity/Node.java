package data.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Node {

	private double longtitude;
	private double latitude;
	private String name;
	private int currentStock;
	private int incomingStock;
	private int maxStorage;
	private int minStorage;
	private ArrayList<Navigation> relatedNavs;
	private boolean isVisited;
	private Node previous;
	private double previousDist;

	public Node(double longtitude, double latitude, String name,
			int currentStock, int maxStorage, int minStorage) {
		super();
		this.longtitude = longtitude;
		this.latitude = latitude;
		this.name = name;
		this.currentStock = currentStock;
		this.maxStorage = maxStorage;
		this.minStorage = minStorage;
		this.relatedNavs = new ArrayList<Navigation>();
		this.isVisited = false;
	}

	public double getLongtitude() {
		return longtitude;
	}

	public void setLongtitude(double longtitude) {
		this.longtitude = longtitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCurrentStock() {
		return currentStock + this.incomingStock;
	}
	
	public void consumeStock(int consumeStock){
		this.currentStock-=consumeStock;
//		System.out.println("updated:"+currentStock+" - "+consumeStock);
	}

	public int getIncomingStock() {
		return incomingStock;
	}

	public void setIncomingStock(int incomingStock) {
		this.incomingStock = incomingStock;
	}

	public void commitIncomingStock() {
		this.currentStock += this.incomingStock;
		this.incomingStock = 0;
//		System.out.println("commited: "+currentStock+"-"+incomingStock);
	}
	
	public void resupplyMaxStock(){
		this.currentStock = this.maxStorage;
	}
	
	public int getAvailableStock(){
		return getCurrentStock()-getMinStorage();
	}

	public int getMaxStorage() {
		return maxStorage;
	}

	public void setMaxStorage(int maxStorage) {
		this.maxStorage = maxStorage;
	}

	public int getMinStorage() {
		return minStorage;
	}

	public void setMinStorage(int minStorage) {
		this.minStorage = minStorage;
	}

	public ArrayList<Navigation> getRelatedNavs() {
		return relatedNavs;
	}

	public void setRelatedNavs(ArrayList<Navigation> relatedNavs) {
		this.relatedNavs = relatedNavs;
	}

	public boolean isVisited() {
		return isVisited;
	}

	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}

	public Node getPrevious() {
		return previous;
	}

	public void setPrevious(Node previous) {
		this.previous = previous;
	}

	public double getPreviousDist() {
		return previousDist;
	}

	public void setPreviousDist(double previousDist) {
		this.previousDist = previousDist;
	}

	public int getPredictStock() {
		return this.currentStock + this.incomingStock;
	}
	
	public int getUnconfirmedStock() {
		return this.currentStock;
	}
	
	public int getUnconfirmedAvailStock() {
		return this.currentStock-this.minStorage;
	}

	public Navigation getLeastPayloadNav() {
		Collections.sort(relatedNavs, new Comparator<Navigation>() {

			@Override
			public int compare(Navigation o1, Navigation o2) {
				if (o1.getPayload() > o2.getPayload())
					return 1;
				else if (o1.getPayload() < o2.getPayload())
					return -1;
				else
					return 0;
			}
		});
		for (Navigation n : relatedNavs) {
			if (n.getPayload() == 0) {
				continue;
			} else {
				return n;
			}
		}
		return null;
	}

	public Navigation findNavByDemander(Node d) {
		for (Navigation n : relatedNavs) {
			if (n.getTo() == d) {
				return n;
			}
		}
		return null;
	}

	public Navigation findNavBySupplier(Node s) {
		for (Navigation n : relatedNavs) {
			if (n.getFrom() == s) {
				return n;
			}
		}
		return null;
	}

	public void reset() {
		this.relatedNavs.clear();
	}

	public int getSupplyCount() {
		int i = 0;
		for (Navigation navi : relatedNavs) {
			if (navi.getPayload() > 0 || navi.isAvoidDegen())
				i++;
			navi.setAvoidDegen(false);
		}
		return i;
	}

	public double distance(Node n) {
		if ("dummy".equals(name) || "dummy".equals(n.getName())) {
			return 0.0;
		} else {
			return Math.sqrt(Math.pow((latitude - n.getLatitude()), 2)
					+ Math.pow((longtitude - n.getLongtitude()), 2));
		}
	}

	@Override
	public String toString() {
		return "Node [currstock=" + getCurrentStock() + ", incstock=" + getIncomingStock()
				+ ", name=" + name + ", minstorage=" + minStorage + "]";
	}

}
