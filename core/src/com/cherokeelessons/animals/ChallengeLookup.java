package com.cherokeelessons.animals;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ChallengeLookup {
	public Set<String> challenges = new TreeSet<String>();
	public Map<String, String> pronounce = new HashMap<String, String>();
	public Map<String, String> latin = new HashMap<String, String>();
	public Map<String, Set<String>> audio = new HashMap<String, Set<String>>();
	public Map<String, Set<String>> images = new HashMap<String, Set<String>>();
}