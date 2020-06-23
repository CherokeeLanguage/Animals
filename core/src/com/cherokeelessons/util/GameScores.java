package com.cherokeelessons.util;

import java.util.ArrayList;
import java.util.List;

public class GameScores {
	public static class GameScore {
		public String pctCorrect;
		public String imgUrl;
		public String rank;
		public String score;
		public String tag;
		public String user;
		public int levelOn;
		public boolean isMe = false;
	}

	public List<GameScores.GameScore> list = new ArrayList<>();
}