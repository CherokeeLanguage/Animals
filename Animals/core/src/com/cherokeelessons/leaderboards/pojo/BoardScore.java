package com.cherokeelessons.leaderboards.pojo;

import java.io.Serializable;

public class BoardScore implements Serializable {
	private static final long serialVersionUID = -692343523714247771L;
	public int level=0;
	public long elapsed=0;
	public int correct=0;
	public String nickname="Player";
	public String uuid=null;
	public LeaderBoard board=null;
	public int rank_overall=0;
	public int rank_level=0;
}
