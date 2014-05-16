package data.entity;

public class Navigation {

	private Node from;
	private Node to;
	private int payload;
	private double distance;
	private double oppoCost;
	private double supplyOppoCost;
	private double demanderOppoCost;
	private boolean avoidDegen;
	
	public Navigation(Node from, Node to, int payload, double distance) {
		super();
		this.from = from;
		this.to = to;
		this.payload = payload;
		this.distance = distance;
		this.oppoCost = distance;
		this.supplyOppoCost = 0.0;
		this.demanderOppoCost = 0.0;
		this.avoidDegen = false;
	}
	
	public Navigation(int payload){
		this.payload = payload;
	}

	public Node getFrom() {
		return from;
	}

	public void setFrom(Node from) {
		this.from = from;
	}

	public Node getTo() {
		return to;
	}

	public void setTo(Node to) {
		this.to = to;
	}

	public int getPayload() {
		return payload;
	}

	public void setPayload(int payload) {
		this.payload = payload;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getOppoCost() {
		return oppoCost;
	}

	public void setOppoCost(double oppoCost) {
		this.oppoCost = oppoCost;
	}

	public double getSupplyOppoCost() {
		return supplyOppoCost;
	}

	public void setSupplyOppoCost(double supplyOppoCost) {
		this.supplyOppoCost = supplyOppoCost;
		this.oppoCost = this.distance-this.demanderOppoCost-supplyOppoCost;
	}

	public double getDemanderOppoCost() {
		return demanderOppoCost;
	}

	public void setDemanderOppoCost(double demanderOppoCost) {
		this.demanderOppoCost = demanderOppoCost;
		this.oppoCost = this.distance-demanderOppoCost-this.supplyOppoCost;
	}

	public boolean isAvoidDegen() {
		return avoidDegen;
	}

	public void setAvoidDegen(boolean avoidDegen) {
		this.avoidDegen = avoidDegen;
	}
	
	public boolean hasDummy(){
		return "dummy".equals(from.getName()) || "dummy".equals(to.getName());
	}
	
	public void commitNavigation(){
		this.from.commitIncomingStock();
		this.to.commitIncomingStock();
	}
	
	public String getInformation(){
		return "Navigation plan: from"+from.getName()+" to "+to.getName()+" [payload: "+payload+"]";
	}
	
	@Override
	public String toString() {
		return "Navigation [from=" + from + ", to=" + to + ", payload="
				+ payload + ", distance=" + distance + "]";
	}
	
}
