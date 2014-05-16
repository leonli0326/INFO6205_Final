package controller;

import java.util.Random;

import data.entity.Map;
import data.entity.Node;

public class StockController {
	
	private static int count;
	
	public static void calculateStock(Map map, double prob, int range){
		count++;
		Random r = new Random();
		for(Node node:map.getNodes()){
			if(node.getCurrentStock()>0){
				double d = r.nextDouble();
				if(d<prob){
					int s = r.nextInt(range)+1;
					node.consumeStock(s);
				}
			}
			if(node.getMaxStorage()>0){
				if(count%10==0){
					node.resupplyMaxStock();
				}
			}
		}
	}

}
