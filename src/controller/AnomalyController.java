package controller;

import java.util.Iterator;

import data.entity.Anomaly;
import data.entity.Map;

public class AnomalyController {
	
//	public void detectAnomaly(Map map){
//		
//	}
	
	public static void controlAnomaly(Map map){
		Iterator<Anomaly> i =map.getAnomalies().iterator();
		while(i.hasNext()){
			Anomaly a = i.next();
			a.setDurationMs(a.getDurationMs()-1);
			if(a.getDurationMs()<=0){
				i.remove();
			}
		}
	}
	
	
}
