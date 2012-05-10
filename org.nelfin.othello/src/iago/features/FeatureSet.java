package iago.features;


import iago.Board;
import iago.players.Player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;

public class FeatureSet implements Iterable<Feature>{

	private ArrayList<Feature> Features;
	private String playerID;
	
	public FeatureSet (ArrayList<Feature> Features, String playerID) {
		this.Features = Features;
		this.playerID = playerID;
	}
	public FeatureSet (String playerID) {
		this.Features = new ArrayList<Feature>();
		this.playerID = playerID;
	}
	public FeatureSet () {
		this.Features = new ArrayList<Feature>();
	}
	
	public void add (Feature f) {
		Features.add(f);
	}

	public double score (Board state, Player.PlayerType player){
		double boardscore = 0;
		//Evaluate victory condition (this bit probably unnecessary)
		Boolean victory = false;
		if (victory) return Integer.MAX_VALUE;
		//Loop through features and evaluate each one
		for (Feature f: Features)
			boardscore += f.evaluate(state, player) * f.getWeight();
		return boardscore;
	}
	
	//A game has just ended, evaluate and adjust features based on Win/Loss
	public void update (FeatureSet opponent, Board finalstate) {
		//Learn how to learn
	}
	
	public void save () {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(playerID + ".pl"));
			for (Feature f: Features)
				bw.write(f.name + ":" + Double.toString(f.getWeight()) + "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void load () {
		try {
			BufferedReader br = new BufferedReader(new FileReader(playerID + ".pl"));
			String featureLn;
			String fname;
			double fweight;
			while ((featureLn = br.readLine()) != null)   {
				fname   = featureLn.split(":")[0];
				fweight = Double.parseDouble(featureLn.split(":")[1]);
				Constructor<?> fcons = Class.forName(fname).getConstructor(new Class[]{Double.TYPE});
				Feature f = (Feature) fcons.newInstance(new Object[]{fweight});
				Features.add(f);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	@Override
	public Iterator<Feature> iterator() {
		return Features.iterator();
	}
}
