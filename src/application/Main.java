package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javafx.animation.Animation;
import javafx.animation.PathTransition;
import javafx.animation.PathTransition.OrientationType;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import controller.AnomalyController;
import controller.NavigationController;
import controller.StockController;
import data.entity.Anomaly;
import data.entity.Map;
import data.entity.Navigation;
import data.entity.Node;

public class Main extends Application {
	Timer t = new Timer();
	Map nodesMap = new Map();
	Group mapBG = new Group();
	Group displayLayer = new Group();
	Group aniLayer = new Group();
	Label informationLabel = new Label();
	HashMap<Navigation, Shape> uavMonitor = new HashMap<>();
	HashMap<Shape, Text> uavPayload = new HashMap<>();
	int count =0;
	double mouseLongConfirm = 0.0;
	double mouseLatConfirm = 0.0;

	@Override
	public void start(Stage primaryStage) {
		try {
			test();
			GridPane grid = new GridPane();
			grid.setAlignment(Pos.TOP_LEFT);
			grid.setHgap(5);
			grid.setVgap(5);
			grid.setAlignment(Pos.CENTER);
			// set scene
			Scene scene = new Scene(grid, 1280, 1024);
			scene.getStylesheets().add(
					getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			// left grid
			grid.add(mapBG, 0, 0);
			setMapToBackGround(mapBG, scene);
			mapBG.getChildren().add(aniLayer);
			mapBG.getChildren().add(displayLayer);
			// right grid
			Group right = new Group();
			grid.add(right, 1, 0);
			addButton(right,0.8311,0.8311);
			//add anomaly
			displayAnomaly(0.8311,0.8311);
			displayNode(0.8311,0.8311);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setMapToBackGround(Group g, Scene scene) {
		Rectangle background = new Rectangle(863, 1024);
		background.setFill(new ImagePattern(new Image("file:img/kenya.jpg")));
		
		g.getChildren().add(background);
	}

	public void addButton(Group right,double heightRatio, double widthRatio) {
		final Button buttonStart = new Button("Start Simulate");
		GridPane gridRight = new GridPane();

		buttonStart.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				t = new Timer();
				t.schedule(new TimerTask() {

					@Override
					public void run() {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								displayLayer.getChildren().clear();
								movementPerTimeFrame();
								displayAnomaly(0.8311,0.8311);
								displayNode(0.8311,0.8311);
							}
						});
					}
				}, 0, 3000);
				buttonStart.setDisable(true);
			}
		});
		
		final Button buttonPause = new Button("Pause");
		buttonPause.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				t.cancel();
				buttonStart.setDisable(false);
			}
		});
		
		final Label note = new Label("Note: click on map to confirm a coordinate");
		final double widthRatiof = widthRatio;
		final double heightRatiof = heightRatio;
		
		final Label labelMouseInfoLong = new Label("Mouse Longitude:");
		final Label labelLongtitude = new Label("");
		final Label labelConfirmInfoLong = new Label("Selected Longitude:");
		final TextField tLongtitude = new TextField ();

		gridRight.add(note,0, 0);
		gridRight.add(labelMouseInfoLong,0, 1);
		gridRight.add(labelLongtitude,1, 1);
		
		gridRight.add(labelConfirmInfoLong,0, 2);
		gridRight.add(tLongtitude,1, 2);
		
		final Label labelMouseInfoLat = new Label("Mouse Latitude:");
		final Label labelLatitude = new Label("");
		final Label labelConfirmInfoLat = new Label("Selected Latitude:");
		final TextField tLatitude = new TextField ();

		gridRight.add(labelMouseInfoLat,0, 3);
		gridRight.add(labelLatitude,1, 3);
		gridRight.add(labelConfirmInfoLat,0, 4);
		gridRight.add(tLatitude,1, 4);
		
		mapBG.setOnMouseMoved(new EventHandler<MouseEvent>() {
		      @Override public void handle(MouseEvent event) {
		    	  double mouseLong = (event.getSceneX())/(widthRatiof*108)+32.935;
		    	  double mouseLat = (event.getSceneY())/(heightRatiof*108)-5.740;
		    	  labelLatitude.setText(String.format("%.2f", Math.abs(mouseLat))+(mouseLat<0?"° N":"° S"));
		    	  labelLongtitude.setText(String.format("%.2f", Math.abs(mouseLong))+"° E");
		      }
		    });

		mapBG.setOnMouseExited(new EventHandler<MouseEvent>() {
		      @Override public void handle(MouseEvent event) {
		    	  labelLatitude.setText(" Out of map");
		    	  labelLongtitude.setText(" Out of map");
		      }
		    });
		
		mapBG.setOnMouseClicked(new EventHandler<MouseEvent>() {
		      @Override public void handle(MouseEvent event) {
		    	  mouseLongConfirm = (event.getSceneX())/(widthRatiof*108)+32.935;
		    	  mouseLatConfirm = (event.getSceneY())/(heightRatiof*108)-5.740;
		    	  tLatitude.setText(String.format("%.2f", Math.abs(mouseLatConfirm))+(mouseLatConfirm<0?"° N":"° S"));
		    	  tLongtitude.setText(String.format("%.2f", Math.abs(mouseLongConfirm))+"° E");
		      }
		    });
		
		Button buttonAddNode = new Button("Add Node");
		final Label nameANL = new Label("Name:");
		final TextField namdANT = new TextField ();
		final Label currStockANL = new Label("Current Stock:");
		final TextField currStockANT = new TextField ();
		final Label minStockANL = new Label("Safety Stock:");
		final TextField minStockANT = new TextField ();
		final Label maxStockANL = new Label("Import Capacity:");
		final TextField maxStockANT = new TextField ();
		
		gridRight.add(nameANL,0, 6);
		gridRight.add(namdANT,1, 6);
		gridRight.add(currStockANL,0, 7);
		gridRight.add(currStockANT,1, 7);
		gridRight.add(minStockANL,0, 8);
		gridRight.add(minStockANT,1, 8);
		gridRight.add(maxStockANL,0, 9);
		gridRight.add(maxStockANT,1, 9);
		gridRight.add(buttonAddNode,0, 10);
		
		buttonAddNode.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				String name = namdANT.getText()==null?"":namdANT.getText();
				int currentStock = currStockANT.getText()==null || !currStockANT.getText().matches("[0-9]+")?0:Integer.valueOf(currStockANT.getText());
				int minStorage = minStockANT.getText()==null || !minStockANT.getText().matches("[0-9]+")?0:Integer.valueOf(minStockANT.getText());
				int maxStorage = maxStockANT.getText()==null || !maxStockANT.getText().matches("[0-9]+")?0:Integer.valueOf(maxStockANT.getText());
				
				Node n = new Node(mouseLongConfirm,mouseLatConfirm,name,currentStock,maxStorage,minStorage);
				nodesMap.addNode(n);
			}
		});
		
		Button buttonAddAnomaly = new Button("Add Anomaly");
		final Label nameAAL = new Label("Name:");
		final TextField namdAAT = new TextField ();
		final Label radiusAAL = new Label("Radius (km):");
		final TextField radiusAAT = new TextField ();
		final Label durationAAL = new Label("Duration:");
		final TextField durationAAT = new TextField ();
		
		buttonAddAnomaly.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				String name = nameAAL.getText()==null?"":nameAAL.getText();
				double radius = radiusAAT.getText()==null || !radiusAAT.getText().matches("[0-9]+")?0.0:Double.valueOf(radiusAAT.getText())*0.8311;
				int duration = durationAAT.getText()==null || !durationAAT.getText().matches("[0-9]+")?0:Integer.valueOf(durationAAT.getText());
				
				
				Anomaly a = new Anomaly(mouseLongConfirm,mouseLatConfirm,name,radius,duration);
				nodesMap.addAnomaly(a);
			}
		});
		
		gridRight.add(nameAAL,0, 11);
		gridRight.add(namdAAT,1, 11);
		gridRight.add(radiusAAL,0, 12);
		gridRight.add(radiusAAT,1, 12);
		gridRight.add(durationAAL,0, 13);
		gridRight.add(durationAAT,1, 13);
		gridRight.add(buttonAddAnomaly,0, 14);
		
		
		gridRight.add(new Label(),0, 15);
		gridRight.add(buttonStart,0, 16);
		gridRight.add(new Label(),0, 17);
		gridRight.add(buttonPause,0, 18);
		gridRight.add(new Label(),0, 19);
		
		gridRight.add(informationLabel,0, 20,2,2);
		
		
		right.getChildren().add(gridRight);
	}
	
	public void updateInformation(){
		
		StringBuilder sb = new StringBuilder("Information:\n");
		for(int i=0;i<nodesMap.getInformations().size();i++){
			String information = nodesMap.getInformations().get(i);
			if(information!=null && !information.equals("")){
				sb.append(information).append("\n");
				
			}
		}
		informationLabel.setText(sb.toString());
	}

	
	public void displayAnomaly(double heightRatio, double widthRatio){
		for(Anomaly anomaly:nodesMap.getAnomalies()){
			int anomalyX = (int) ((anomaly.getLongtitude() - 32.935) * 108 * widthRatio);
			int anomalyY = (int) ((anomaly.getLatitude() + 5.740) * 108 * heightRatio);
			Circle c = new Circle(anomalyX, anomalyY, anomaly.getRadius()*0.75, Color.web("red",0.3));
			displayLayer.getChildren().add(c);
		}
	}
	
	public void displayNode(double heightRatio, double widthRatio){
		for(Node node:nodesMap.getNodes()){
			int nodeX = (int) ((node.getLongtitude() - 32.935) * 108 * widthRatio);
			int nodeY = (int) ((node.getLatitude() + 5.740) * 108 * heightRatio);
			Group g = new Group();
			Circle c = new Circle(nodeX, nodeY, 10, Color.web("green",0.3));
			
			Label name = new Label(node.getName());
			Label currStock = new Label("Current Stock:"+String.valueOf(node.getUnconfirmedStock()));
			currStock.setTextFill(Color.web("blue",1));
			Label avaiStock = new Label("Available Stock:"+String.valueOf(node.getUnconfirmedAvailStock()));
			avaiStock.setTextFill(Color.web("green",1));
			final VBox vbox = new VBox(-5);
			vbox.getChildren().addAll(name,currStock,avaiStock);
			vbox.relocate(nodeX, nodeY);
			if(node.getMaxStorage()>0){
				vbox.setStyle("-fx-background-color: rgba(255, 255, 128, 0.7)");
			}else{
				vbox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7)");
			}
			vbox.setOnMouseEntered(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					vbox.setScaleX(2);
					vbox.setScaleY(2);
				}
			});
			vbox.setOnMouseExited(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					vbox.setScaleX(1);
					vbox.setScaleY(1);
				}
			});
			g.getChildren().add(c);
			g.getChildren().add(vbox);
			displayLayer.getChildren().add(g);
		}
	}

	public void test() {

		Node Nairobi = new Node(36.8, 1.283, "Nairobi", 20, 0, 10);
		Node Kisumu = new Node(34.75, 0.05, "Kisumu", 0, 0, 10);
		Node Mombasa = new Node(39.717, 4.033, "Mombasa", 300, 300, 100);
		Node Wajir = new Node(40.083, -1.7, "Wajir", 13, 0, 5);
		nodesMap.addNode(Nairobi);
		nodesMap.addNode(Kisumu);
		nodesMap.addNode(Mombasa);
		nodesMap.addNode(Wajir);
		Anomaly a1 = new Anomaly(35.99, 0.834, "Typhoon", 50, 30);
		//531,889
		Anomaly a2 = new Anomaly(37.852, 2.491, "Random", 20, 5);
		nodesMap.addAnomaly(a1);
		nodesMap.addAnomaly(a2);

	}

	public void movementPerTimeFrame() {
		//calculate stock
		StockController.calculateStock(nodesMap, 0.9, 5);
		//calculate anomaly
		AnomalyController.controlAnomaly(nodesMap);
		//deploy navigation
		ArrayList<Navigation> newNavigations = NavigationController.deployNavigation(nodesMap);
		//update uav monitor
		for(Navigation navigation:newNavigations){
			updateUavMonitor(navigation,0.8311,0.8311);
//			System.out.println(navigation);
		}
		//calculate uav move;
		for(Navigation navigation:uavMonitor.keySet()){
			calcUavMove(navigation, 0.1, 0.8311, 0.8311, 3000);
		}
		//commit navigations;
		HashMap<Navigation, Shape> uavMonitorClone = (HashMap<Navigation, Shape>) uavMonitor.clone();
		for(Navigation navigation:uavMonitorClone.keySet()){
			commitNavigation(navigation,0.8311, 0.8311);
		}
		//output information
		updateInformation();
		
		
//		calculateStock();
//		calculateAnomaly();
//		arrangeNavigation();
//		assembleAnimation();
	}
	
	public void updateUavMonitor(Navigation navigation,double heightRatio, double widthRatio){
		int startX = (int) ((navigation.getFrom().getLongtitude() - 32.935) * 108 * widthRatio);
		int startY = (int) ((navigation.getFrom().getLatitude() + 5.740) * 108 * heightRatio);
		if (!uavMonitor.keySet().contains(navigation)) {
//			Group uav = new Group();
			Text payload = new Text(String.valueOf(navigation.getPayload()));
			
			Rectangle uavPlane = new Rectangle(startX,startY,25, 25);
			payload.setLayoutX(startX);
			payload.setLayoutY(startY);
//			payload.relocate(startX, startX);
			payload.translateXProperty().bind(uavPlane.translateXProperty());
			payload.translateYProperty().bind(uavPlane.translateYProperty());
			uavPlane.setFill(new ImagePattern(new Image("file:img/uav.png")));
//			uav.getChildren().add(uavPlane);
//			uav.getChildren().add(payload);
			aniLayer.getChildren().add(uavPlane);
			aniLayer.getChildren().add(payload);
			uavMonitor.put(navigation, uavPlane);
			uavPayload.put(uavPlane, payload);
//			System.out.println(uav.getLayoutX());
//			System.out.println(uavPlane.getX());
//			uavPlane.xProperty().bind(uavPlane.xProperty().add(uavPlane.translateXProperty()));
//			uavPlane.yProperty().bind(uavPlane.yProperty().add(uavPlane.translateYProperty()));
//			uav.setLayoutX(startX);
//			uav.setLayoutY(startY);
		}
	}

	public void calcUavMove(Navigation navigation, double pxPerMs,
			double heightRatio, double widthRatio, double ms) {
		int startX = (int) ((navigation.getFrom().getLongtitude() - 32.935) * 108 * widthRatio);
		int startY = (int) ((navigation.getFrom().getLatitude() + 5.740) * 108 * heightRatio);
		int endX = (int) ((navigation.getTo().getLongtitude() - 32.935) * 108 * widthRatio);
		int endY = (int) ((navigation.getTo().getLatitude() + 5.740) * 108 * heightRatio);
		double distFull = Math.sqrt(Math.pow((endX-startX),2)+Math.pow((endY-startY),2));
		
		/*
		Group uav;
		if (!uavMonitor.keySet().contains(navigation)) {
			uav = new Group();
			Text payload = new Text();
			Rectangle uavPlane = new Rectangle(25, 25);
			uavPlane.setFill(new ImagePattern(new Image("file:img/plane.jpg")));
			uav.getChildren().add(uavPlane);
			uav.getChildren().add(payload);
			mapBG.getChildren().add(uav);
			uavMonitor.put(navigation, uav);
			uav.setLayoutX(startX);
			uav.setLayoutY(startY);
		} else {
			uav = uavMonitor.get(navigation);
		}
		*/
		Shape uav = uavMonitor.get(navigation);
		
		int currentX = (int) ( ((Rectangle)uav).getX()+((Rectangle)uav).getTranslateX() );
//		System.out.println(currentX);
		int currentY = (int) ( ((Rectangle)uav).getY()+((Rectangle)uav).getTranslateY() );
//		System.out.println(currentY);
		
		
		double dist = Math.sqrt(Math.pow((endX-currentX),2)+Math.pow((endY-currentY),2));
		
		int targetX;
		int targetY;
		
		if(dist<pxPerMs*ms){
			targetX = endX;
			targetY = endY;
		}else{
//			double ratio = (endX-startX)/(endY-startY);
			targetX = (int) (currentX+(pxPerMs*ms/distFull)*(endX-startX));
			targetY = (int) (currentY+(pxPerMs*ms/distFull)*(endY-startY));
		}
		
//		Path path = new Path();
//		path.getElements().add(new MoveTo(currentX,currentY));
//		path.getElements().add(new LineTo(targetX, targetY));
//		System.out.println("------------");
//		System.out.println("NAV:  "+navigation);
		Path path = drawPath(currentX, currentY, targetX, targetY, heightRatio, widthRatio);
		Animation a = transitionPerTimeFrame(path,uav,ms);
		a.play();		
	}
	
	public Path drawPath(int startX, int startY, int endX, int endY, double heightRatio, double widthRatio){
		Path path = new Path();
//		System.out.println("********");
		double a = ((double)(startY-endY))/((double)(startX-endX));
//		System.out.println("a:"+a+" = ("+startY+"-"+endY+")/("+startX+"-"+endX+")");
		double b = (double)(startY-a*startX);
//		System.out.println("b:"+b+" = "+startY+"-"+a+"*"+startX);
//		boolean isLine = true;
		path.getElements().add(new MoveTo(startX,startY));
		for(Anomaly anomaly:nodesMap.getAnomalies()){
			int anomalyX = (int) ((anomaly.getLongtitude() - 32.935) * 108 * widthRatio);
			int anomalyY = (int) ((anomaly.getLatitude() + 5.740) * 108 * heightRatio);
			double m = (double)(anomalyX+a*anomalyY);
			int crossX = (int) ((m-a*b)/(a*a+1));
			int crossY = (int) (a*crossX+b);
			double distance = Math.sqrt(Math.pow((crossX-anomalyX),2)+Math.pow((crossY-anomalyY),2));
			if(distance>anomaly.getRadius() 
					|| Math.abs(crossX-endX)+Math.abs(crossX-startX)>Math.abs(startX-endX) ){
				continue;
			}else{
				double controlX1;
				double controlY1;
				double controlX2;
				double controlY2;
				double deltaX = anomaly.getRadius()*Math.sqrt(1/(a*a+1))*1.2;
				if(endX<startX){
					deltaX = -deltaX;
				}
				double deltaY = a*deltaX;
				System.err.println(anomaly.getName());
				System.err.println(a);
				System.err.println(deltaX);
				System.err.println(deltaY);
				controlX1 = crossX-deltaX;
				controlX2 = crossX+deltaX;
				controlY1 = crossY-deltaY;
				controlY2 = crossY+deltaY;
				
//				if(endX>startX){
//					controlX1 = crossX-deltaX;
//					controlX2 = crossX+deltaX;
//				}else{
//					controlX1 = crossX+deltaX;
//					controlX2 = crossX-deltaX;
//				}
//				if(endY>startY){
//					controlY1 = crossY-deltaY;
//					controlY2 = crossY+deltaY;
//				}else{
//					controlY1 = crossY+deltaY;
//					controlY2 = crossY-deltaY;
//				}
				
//				path.getElements().add(new MoveTo(startX,startY));
				path.getElements().add(new LineTo(controlX1,controlY1));
				path.getElements().add(new ArcTo(anomaly.getRadius(),anomaly.getRadius(),180,controlX2,controlY2,false,false));
//				path.getElements().add(new LineTo(endX,endY));
//				isLine = false;
//				break;
			}
		}
//		if(isLine){
////			path.getElements().add(new MoveTo(startX,startY));
//			path.getElements().add(new LineTo(endX, endY));
//		}
		path.getElements().add(new LineTo(endX, endY));
		return path;	
	}
	
	public void commitNavigation(Navigation navigation,double heightRatio, double widthRatio){
		Rectangle uav = (Rectangle) uavMonitor.get(navigation);
		int endX = (int) ((navigation.getTo().getLongtitude() - 32.935) * 108 * widthRatio);
		int endY = (int) ((navigation.getTo().getLatitude() + 5.740) * 108 * heightRatio);
		int currentX = (int) ( ((Rectangle)uav).getX()+((Rectangle)uav).getTranslateX() );
		int currentY = (int) ( ((Rectangle)uav).getY()+((Rectangle)uav).getTranslateY() );
		
//		System.out.println("end "+endX+":"+endY);
//		System.out.println("cur "+currentX+":"+currentY);
		double dist = Math.sqrt(Math.pow((endX-currentX),2)+Math.pow((endY-currentY),2));
		if(dist<=20){
//			System.out.println("committed");
			navigation.commitNavigation();
			uavMonitor.remove(navigation);
			aniLayer.getChildren().remove(uavPayload.get(uav));
			uavPayload.remove(uav);
			aniLayer.getChildren().remove(uav);
			return;
		}
	}

	public Animation transitionPerTimeFrame(Path path, Shape uav, double ms) {
		PathTransition pt = new PathTransition();
		pt.setDuration(Duration.millis(ms));
		pt.setPath(path);
		pt.setNode(uav);
		pt.setOrientation(OrientationType.ORTHOGONAL_TO_TANGENT);
		return pt;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
