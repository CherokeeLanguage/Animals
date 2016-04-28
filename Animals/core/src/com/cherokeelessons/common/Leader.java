package com.cherokeelessons.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.utils.Json;
import com.cherokeelessons.leaderboards.pojo.BoardScore;
import com.cherokeelessons.leaderboards.pojo.GetScores;
import com.cherokeelessons.leaderboards.pojo.GetUUID;
import com.cherokeelessons.leaderboards.pojo.LeaderBoard;

public class Leader {

	private static final int ALL_LEVELS = -1;
	static public final LeaderBoard board = LeaderBoard.EsperantoAnimals;
	static public final String url = "http://www.CherokeeLessons.com/Leaderboards/RPC";
	private HttpRequest request;
	public static String uuid = "";
	public static String nick = "nick";

	public Leader() {
		request = new Net.HttpRequest(Net.HttpMethods.GET);
		request.setTimeOut(1000);
		request.setUrl(url);		
	}

	public void checkUuid(String uuid) {
		checkUuid(uuid, null);
	}
	public void checkUuid(String uuid, final Runnable callback) {
		request.setContent("&cmd=check&uuid="+uuid);
		HttpResponseListener cb_check_uuid=new HttpResponseListener() {
			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {
				Json json = new Json();
				GetUUID check = json.fromJson(GetUUID.class, httpResponse.getResultAsString());
				if (check!=null) {
					Leader.uuid=check.uuid;
				}
				if (callback!=null) {
					callback.run();
				}
			}
			@Override
			public void failed(Throwable t) {
			}
		};
		Gdx.net.sendHttpRequest(request, cb_check_uuid);
	}
	
	public static interface CB_AddScore {
		public void run(BoardScore score);
	}
	
	public void addScore(final int level, final int correct,
			final long ms, final CB_AddScore cb_addscore) {
		System.out.println("addScore");
		final HttpResponseListener cb_scoreposted=new HttpResponseListener() {
			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {
				System.out.println("cb_scoreposted");
				Json json = new Json();
				BoardScore score = json.fromJson(BoardScore.class, httpResponse.getResultAsString());
				if (cb_addscore!=null) {
					System.out.println("cb_addscore");
					cb_addscore.run(score);
				}
			}
			
			@Override
			public void failed(Throwable t) {
				t.printStackTrace();
			}
		};
		final Runnable postScore = new Runnable() {
			public void run() {
				System.out.println("postScore");
				BoardScore score = new BoardScore();
				score.board = board;
				score.correct = correct;
				score.elapsed = ms;
				score.level = level;
				score.nickname = nick;
				score.uuid = uuid;
				StringBuilder query = new StringBuilder();
				try {
					query.append("&cmd=");
					query.append(URLEncoder.encode("addScore", "UTF-8"));
					query.append("&board=");
					query.append(URLEncoder.encode(score.board.name(), "UTF-8"));
					query.append("&correct=");
					query.append(score.correct);
					query.append("&elapsed=");
					query.append(score.elapsed);
					query.append("&level=");
					query.append(score.level);
					query.append("&nick=");
					query.append(URLEncoder.encode(score.nickname, "UTF-8"));
					query.append("&uuid=");
					query.append(URLEncoder.encode(score.uuid, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				request.setContent(query.toString());
				Gdx.net.sendHttpRequest(request, cb_scoreposted);
			}
		};
		if (uuid==null || uuid.length()==0) {
			System.out.println("bad uuid");
			checkUuid(uuid, postScore);
		} else {
			postScore.run();
		}
	}

	public void loadScores(final int levelOn) {
		StringBuilder query = new StringBuilder();
		try {
			query.append("&cmd=getScores");
			query.append("&board=");
			query.append(URLEncoder.encode(Leader.board.name(), "UTF-8"));
			query.append("&level=");
			query.append(levelOn);
			query.append("&uuid=");
			query.append(URLEncoder.encode(Leader.uuid, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		request.setContent(query.toString());
		HttpResponseListener cb_scores=new HttpResponseListener() {
			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {
				Json json = new Json();
				synchronized (scoresBylevel) {
					GetScores scores = json.fromJson(GetScores.class, httpResponse.getResultAsString());
					if (scores!=null) {
						scoresBylevel.put(levelOn, scores);
					}
				}
			}
			
			@Override
			public void failed(Throwable t) {
			}
		};
		Gdx.net.sendHttpRequest(request, cb_scores);
	}
	public void loadScores() {
		StringBuilder query = new StringBuilder();
		try {
			query.append("&cmd=getScores");
			query.append("&board=");
			query.append(URLEncoder.encode(Leader.board.name(), "UTF-8"));
			query.append("&uuid=");
			query.append(URLEncoder.encode(Leader.uuid, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		request.setContent(query.toString());
		HttpResponseListener cb_scores=new HttpResponseListener() {
			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {
				Json json = new Json();
				synchronized (scoresBylevel) {
					GetScores scores = json.fromJson(GetScores.class, httpResponse.getResultAsString());
					if (scores!=null) {
						scoresBylevel.put(ALL_LEVELS, scores);
					}
				}
			}
			
			@Override
			public void failed(Throwable t) {
			}
		};
		Gdx.net.sendHttpRequest(request, cb_scores);
	}
	final private static HashMap<Integer, GetScores> scoresBylevel=new HashMap<Integer, GetScores>();
}
