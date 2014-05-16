package data.entity;

public class Anomaly {
	
	private double longtitude;
	private double latitude;
	private String name;
	private double radius;
	private int durationMs;
	
	public Anomaly(double longtitude, double latitude, String name,
			double radius,int durationMs) {
		super();
		this.longtitude = longtitude;
		this.latitude = latitude;
		this.name = name;
		this.radius = radius;
		this.durationMs = durationMs;
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

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public int getDurationMs() {
		return durationMs;
	}

	public void setDurationMs(int durationMs) {
		this.durationMs = durationMs;
	}
	
	

}
