package data.entity;

import java.util.ArrayList;
import java.util.HashSet;

public class Map {

	private HashSet<Node> nodes = new HashSet<>();
	private HashSet<Anomaly> anomalies = new HashSet<>();
	private ArrayList<String> informations = new ArrayList<String>();
	

	public HashSet<Node> getNodes() {
		return nodes;
	}

	public void setNodes(HashSet<Node> nodes) {
		this.nodes = nodes;
	}

	public void addNode(double longtitude, double latitude, String name,
			int currentStock, int maxStorage, int minStorage) {
		this.nodes.add(new Node(longtitude, latitude, name, currentStock,
				maxStorage, minStorage));
	}

	public void addNode(Node n) {
		this.nodes.add(n);
	}
	
	public void addAnomaly(Map map,double longtitude, double latitude, String name,
			double radius,int durationMs){
		this.anomalies.add(new Anomaly(longtitude, latitude, name, radius, durationMs));
	}
	
	public void addAnomaly(Anomaly a) {
		this.anomalies.add(a);
	}

	public HashSet<Anomaly> getAnomalies() {
		return anomalies;
	}

	public void setAnomalies(HashSet<Anomaly> anomalies) {
		this.anomalies = anomalies;
	}

	public ArrayList<String> getInformations() {
		return informations;
	}

	public void setInformations(ArrayList<String> informations) {
		this.informations = informations;
	}
	
	
//
//	public void deployNavigation(int minUnit) {
//		int sumSupply = 0;
//		int sumDemand = 0;
//		Random r = new Random();
//		ArrayList<Node> suppliers = new ArrayList<>();
//		ArrayList<Node> demanders = new ArrayList<>();
//
//		for (Node node : nodes) {
//			// reset all present navigation;
//			node.reset();
//			// seperate demanders and suppliers;
//			if (node.getCurrentStock() < node.getMinStorage()) {
//				demanders.add(node);
//				sumDemand -= node.getCurrentStock();
//			} else {
//				suppliers.add(node);
//				sumSupply += node.getCurrentStock();
//			}
//			// add dummy nodes to absorbe surplus demand or supply;
//			if (sumSupply > sumDemand) {
//				Node dummy = new Node(0.0, 0.0, "dummy", sumDemand - sumSupply,
//						0, 0);
//				demanders.add(dummy);
//				sumDemand += sumSupply - sumDemand;
//			} else if (sumDemand > sumSupply) {
//				Node dummy = new Node(0.0, 0.0, "dummy", sumDemand - sumSupply,
//						0, 0);
//				suppliers.add(dummy);
//				sumSupply += sumDemand - sumSupply;
//			}
//			// construct a priority queue for oppoCost
//			PriorityQueue<Navigation> minDistQueue = new PriorityQueue<>(1,
//					new Comparator<Navigation>() {
//
//						@Override
//						public int compare(Navigation o1, Navigation o2) {
//							if (o1.getDistance() > o2.getDistance())
//								return 1;
//							else if (o1.getDistance() < o2.getDistance())
//								return -1;
//							else
//								return 0;
//						}
//					});
//			// construct a list for all oppocosts
//			ArrayList<Navigation> sortOppoCost = new ArrayList<>();
//			// set all cost and navigation
//			for (int i = 0; i < suppliers.size(); i++) {
//				for (int j = 0; j < demanders.size(); j++) {
//					Node supplier = suppliers.get(i);
//					Node demander = demanders.get(j);
//					double k = supplier.distance(demander);
//					if (supplier.getCurrentStock() - supplier.getMinStorage()
//							- demander.getCurrentStock()
//							+ demander.getMinStorage() > 0) {
//						k = 65536;
//					}
//					Navigation n = new Navigation(supplier, demander, 0, k);
//					// add navigation to 2 list for later use
//					minDistQueue.add(n);
//					sortOppoCost.add(n);
//					// add navigation to supplier and demander for back
//					// reference
//					supplier.getRelatedNavs().add(n);
//					demander.getRelatedNavs().add(n);
//				}
//			}			
//			//setup a initial solution
//			while (sumSupply + sumDemand > 0) {
//				// get the min cost navigation
//				Navigation n = minDistQueue.poll();
//				int maxPayload = Math.min(n.getFrom().getCurrentStock()	- n.getFrom().getMinStorage(), 
//						n.getTo().getMinStorage() - n.getTo().getCurrentStock());
//				n.setPayload(maxPayload);
//				
//				//-maxPayload means transport from supplier
//				n.getFrom().setIncomingStock(-maxPayload);
//				n.getTo().setIncomingStock(maxPayload);
//
//				sumSupply -= maxPayload;
//				sumDemand -= maxPayload;
//			}			
//			//iteration
//			int count2 = 0;
//			while (count2++ < 1500) {				
//				//check navigation with payload
//				int actualNavCount = 0;
//				for (Node n : suppliers) {
//					n.setVisited(false);
//					actualNavCount += n.getSupplyCount();
//				}
//				for (Node n : demanders) {
//					n.setVisited(false);
//				}				
//				//check the tableu is degenerate or not
//				if (actualNavCount < suppliers.size() + demanders.size() - 1) {
//					int remain = suppliers.size() + demanders.size() - 1
//							- actualNavCount;
//					while (remain > 0) {
//						Navigation nav = suppliers.get( r.nextInt(suppliers.size()) )
//								.getRelatedNavs().get( r.nextInt(demanders.size()) );
//						if (nav.getPayload() <= 0) {
//							nav.setAvoidDegen(true);
//							remain--;
//						}
//					}
//				}				
//				//get the max supplied supplier
//				Collections.sort(suppliers, new Comparator<Node>() {
//
//					@Override
//					public int compare(Node o1, Node o2) {
//						// TODO Auto-generated method stub
//						return o1.getSupplyCount() - o2.getSupplyCount();
//					}
//
//				});				
//				//update all cost
//				updateOppoCost(suppliers.get(suppliers.size() - 1), null, 0.0);
//				//sort by oppocost of navigation
//				Collections.sort(sortOppoCost, new Comparator<Navigation>() {
//
//					@Override
//					public int compare(Navigation o1, Navigation o2) {
//						// TODO Auto-generated method stub
//						if (o1.getOppoCost() > o2.getOppoCost())
//							return 1;
//						else if (o1.getOppoCost() < o2.getOppoCost())
//							return -1;
//						else
//							return 0;
//					}
//					
//				});
//				//set a var to check optium
//				boolean isOptium = false;
//				//traverse sortOppoCost
//				Iterator<Navigation> iterator = sortOppoCost.iterator();
//				while (iterator.hasNext()) {
//					Navigation selectedN = iterator.next();
//					//must be a empty navigation
//					if (selectedN.getPayload() > 0) {
//						continue;
//					}
//					//if all oppocost is larger than 0 then is optium
//					if (selectedN.getOppoCost() >= 0) {
//						isOptium = true;
//						break;
//					}
//					//find least payload to swap
//					Navigation swapNavSameSupplier = selectedN.getFrom()
//							.getLeastPayloadNav();
//					Navigation swapNavSameDemander = selectedN.getTo()
//							.getLeastPayloadNav();
//					Navigation swapNavOpposite = swapNavSameSupplier.getTo()
//							.findNavBySupplier(swapNavSameDemander.getFrom());
//					//swap max swap-able amount
//					int swapPayload = Math.min(swapNavSameSupplier.getPayload(),
//							swapNavSameDemander.getPayload());
//					//exec swap
//					selectedN.setPayload(selectedN.getPayload() + swapPayload);
//					swapNavSameSupplier.setPayload(swapNavSameSupplier.getPayload()
//							- swapPayload);
//					swapNavSameDemander.setPayload(swapNavSameDemander.getPayload()
//							- swapPayload);
//					swapNavOpposite.setPayload(swapNavOpposite.getPayload()
//							+ swapPayload);
//					break;
//				}				 
//				if (isOptium || !iterator.hasNext())
//					break;				
//			}
//		}
//	}
//	
//	public void updateOppoCost(Node supplier, Node demander, Double cost) {
//		//recursive solve the graph
//		if (supplier == null) {
//			demander.setVisited(true);
//			for (Navigation n : demander.getRelatedNavs()) {
//
//				if (n.getPayload() > 0 || n.isAvoidDegen()) {
//					if (!n.getFrom().isVisited()) {
//						updateOppoCost(n.getFrom(), null, n.getDistance() - cost);
//					}
//				} else {
//					n.setDemanderOppoCost(cost);
//				}
//			}
//		}
//		if (demander == null) {
//			supplier.setVisited(true);
//			for (Navigation n : supplier.getRelatedNavs()) {
//
//				if (n.getPayload() > 0 || n.isAvoidDegen()) {
//					if (!n.getTo().isVisited()) {
//						updateOppoCost(null, n.getTo(), n.getDistance() - cost);
//					}
//				} else {
//					n.setSupplyOppoCost(cost);
//				}
//			}
//		}
//	}
}
