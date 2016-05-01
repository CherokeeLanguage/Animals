package com.cherokeelessons.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.Preferences;
import com.cherokeelessons.util.GameScores.GameScore;

public class DreamLo {

	private static final String PRIVATE_CODE = "Vm-nbYP_PESG2jZIvqx9fAju9H0-_iz0qj7oDcgpbNgw";
	private static final String PUBLIC_CODE = "572424bd6e51b60bc08ed5a8";

	private static final String DREAMLO_USERID = "dreamlo-userid";

	private static final String readUrl = "http://dreamlo.com/lb/" + PUBLIC_CODE;

	private static boolean registeredListenerPending = false;

	private static final String writeUrl = "http://dreamlo.com/lb/" + PRIVATE_CODE;

	/**
	 * Correctly percent encode for passing as URL component
	 * {@link http://stackoverflow.com/questions/724043/http-url-address-encoding-in-java}
	 * 
	 * @param s
	 * @return
	 */
	public static String encode(final String s) {
		if (s == null) {
			return "";
		}

		final StringBuilder sb = new StringBuilder();

		try {
			for (final char c : s.toCharArray()) {
				if (((c >= 'A') && (c <= 'Z')) //
						|| ((c >= 'a') && (c <= 'z')) //
						|| ((c >= '0') && (c <= '9')) //
						|| (c == '-') //
						|| (c == '.') //
						|| (c == '_') //
						|| (c == '~')) {
					sb.append(c);
					continue;
				}

				final byte[] bytes = ("" + c).getBytes("UTF-8");

				for (byte b : bytes) {
					sb.append('%');

					int upper = (((int) b) >> 4) & 0xf;
					sb.append(Integer.toHexString(upper).toUpperCase());

					int lower = ((int) b) & 0xf;
					sb.append(Integer.toHexString(lower).toUpperCase());
				}
			}
			return sb.toString();
		} catch (UnsupportedEncodingException uee) {
			throw new RuntimeException("UTF-8 unsupported!?", uee);
		}
	}

	/**
	 * boardId = "animal-slot#-timstamp-random";
	 */
	private final Preferences prefs;
	private HttpResponseListener registeredListener = new HttpResponseListener() {
		@Override
		public void cancelled() {
			registeredListenerPending = false;
		}

		@Override
		public void failed(Throwable t) {
			registeredListenerPending = false;
		}

		@Override
		public void handleHttpResponse(HttpResponse httpResponse) {
			registeredListenerPending = false;
		}
	};

	private Runnable registerWithBoard = new Runnable() {
		@Override
		public void run() {
			Gdx.app.log(this.getClass().getName(), "DreamLo: registerWithBoard#run");
			HttpRequest httpRequest = new HttpRequest("GET");
			httpRequest.setUrl(readUrl + "/pipe");
			Gdx.app.log(this.getClass().getName(), "DreamLo: '" + httpRequest.getUrl() + "'");
			HttpResponseListener httpResponseListener = new HttpResponseListener() {
				@Override
				public void cancelled() {
					Gdx.app.log(this.getClass().getName(), "DreamLo: registerWithBoard: TIMED OUT");
				}

				@Override
				public void failed(Throwable t) {
					Gdx.app.log(this.getClass().getName(), "DreamLo: registerWithBoard: ", t);
				}

				@Override
				public void handleHttpResponse(HttpResponse httpResponse) {
					String resultAsString = httpResponse.getResultAsString();
					Gdx.app.log(this.getClass().getName(), "DreamLo: registerWithBoard: " + resultAsString);
					String str_scores = resultAsString;
					String[] scores = str_scores.split("\n");
					Random r = new Random();
					int id = 0;
					tryagain: while (true) {
						id = r.nextInt(Integer.MAX_VALUE) + 1;
						for (String score : scores) {
							if (score.contains(id + "-")) {
								continue tryagain;
							}
						}
						break tryagain;
					}
					HttpRequest httpRequest = new HttpRequest("GET");
					httpRequest.setTimeOut(10000);
					httpRequest.setUrl(writeUrl + "/add/" + id + "-0/0/0/" + encode("ᎢᏤ ᏴᏫ!!!ᎩᎶ ᎢᏤ"));
					Gdx.net.sendHttpRequest(httpRequest, registeredListener);
					prefs.putString(DREAMLO_USERID, id + "");
					prefs.flush();
				}
			};
			Gdx.net.sendHttpRequest(httpRequest, httpResponseListener);
		}
	};

	public DreamLo(Preferences prefs) {
		this.prefs = prefs;
	}

	public void lb_getScores(final Callback<GameScores> callback) {
		if (!registerWithDreamLoBoard()) {
			Gdx.app.postRunnable(new Runnable() {
				public void run() {
					DreamLo.this.lb_getScores(callback);
				}
			});
			return;
		}
		HttpRequest httpRequest = new HttpRequest("GET");
		httpRequest.setTimeOut(10000);
		httpRequest.setUrl(readUrl + "/pipe/100");
		Gdx.net.sendHttpRequest(httpRequest, new HttpResponseListener() {
			@Override
			public void cancelled() {
				Gdx.app.log(this.getClass().getName(), "DreamLo: lb_getScoresFor: timed out");
				Gdx.app.postRunnable(callback.with(new RuntimeException("TIMED OUT")));
			}

			@Override
			public void failed(Throwable t) {
				Gdx.app.log(this.getClass().getName(), "DreamLo: lb_getScoresFor:", t);
				Gdx.app.postRunnable(callback.with(new RuntimeException(t)));
			}

			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {
				String myId = prefs.getString(DREAMLO_USERID, "") + "-";
				List<String> records = new ArrayList<String>(
						Arrays.asList(httpResponse.getResultAsString().split("\n")));
				final GameScores gss = new GameScores();
				gss.list = new ArrayList<GameScore>();
				for (String score_record : records) {
					if (score_record == null || score_record.length() == 0) {
						continue;
					}
					
					String[] s = score_record.split("\\|");
					if (s == null || s.length < 4) {
						continue;
					}
					/*
					 * 0: username 1: score 2: time 3: tag 4: date 5: index
					 */
					GameScore gs = new GameScore();
					gs.score = StringUtils.defaultString(s[2]).trim();
					gs.score = StringUtils.reverse(gs.score).replaceAll("(\\d{3})", "$1,");
					gs.score = StringUtils.reverse(gs.score);
					gs.score = StringUtils.strip(gs.score, ",");
					String label = StringUtils.defaultString(s[3]).trim();
					gs.tag = StringUtils.substringBefore(label, "!!!");
					String decoded_other_name = StringUtils.substringAfter(label, "!!!");
					String dreamLoId = StringUtils.defaultString(s[0]).trim();
					try {
						gs.levelOn=Integer.valueOf(StringUtils.substringAfter(dreamLoId, "-"));
					} catch (NumberFormatException e) {
						continue;
					}
					if (gs.levelOn==0){
						continue;
					}
					gs.user = dreamLoId;
					if (gs.user.startsWith(myId)) {
						gs.user = decoded_other_name;
						gs.isMe=true;
					} else {
						if (!decoded_other_name.matches(".*?[a-zA-Z].*?")) {
							gs.user = StringUtils.left(decoded_other_name, 14) + " #" + dreamLoId;
						}
						gs.isMe=false;
					}
					gs.user = StringUtils.left(gs.user, 17);
					gs.pctCorrect = StringUtils.defaultString(s[1]).trim();
					gs.pctCorrect = StringUtils.reverse(gs.pctCorrect).replaceAll("(\\d{3})", "$1,");
					gs.pctCorrect = StringUtils.reverse(gs.pctCorrect);
					gs.pctCorrect = StringUtils.strip(gs.pctCorrect, ",");
					gss.list.add(gs);
				}
				Comparator<GameScore> descending = new Comparator<GameScore>() {
					@Override
					public int compare(GameScore o1, GameScore o2) {
						if (o1 == o2) {
							return 0;
						}
						if (o2 == null) {
							return -1;
						}
						if (o1 == null) {
							return 1;
						}
						int v1;
						int v2;
						try {
							v1 = Integer.valueOf(o1.pctCorrect.replace(",", ""));
						} catch (NumberFormatException e) {
							v1 = 0;
						}
						try {
							v2 = Integer.valueOf(o2.pctCorrect.replace(",", ""));
						} catch (NumberFormatException e) {
							v2 = 0;
						}
						if (v1 != v2) {
							return v2-v1;
						}
						try {
							v1 = Integer.valueOf(o1.score.replace(",", ""));
						} catch (NumberFormatException e) {
							v1 = 0;
						}
						try {
							v2 = Integer.valueOf(o2.score.replace(",", ""));
						} catch (NumberFormatException e) {
							v2 = 0;
						}
						return v2-v1;
					}
				};
				Collections.sort(gss.list, descending);
				for (int ix = 0; ix < gss.list.size(); ix++) {
					GameScore tmp = gss.list.get(ix);
					switch (ix + 1) {
					case 1:
						tmp.rank = "1st";
						break;
					case 2:
						tmp.rank = "2nd";
						break;
					case 3:
						tmp.rank = "3rd";
						break;
					default:
						tmp.rank = (ix + 1) + "th";
						break;
					}
				}
				Gdx.app.postRunnable(callback.with(gss));
			}
		});
	}

	public void lb_submit(final String boardId, final long cards, final long score, final String label,
			final Callback<Void> callback) {
		if (!registerWithDreamLoBoard()) {
			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					DreamLo.this.lb_submit(boardId, cards, score, label, callback);
				}
			});
			return;
		}
		HttpRequest httpRequest = new HttpRequest("GET");
		httpRequest.setTimeOut(10000);
		String url = writeUrl + "/add/" + prefs.getString(DREAMLO_USERID, "") + "-" + boardId + "/" + cards + "/"
				+ score + "/" + encode(label);
		httpRequest.setUrl(url);
		Gdx.net.sendHttpRequest(httpRequest, new HttpResponseListener() {
			@Override
			public void cancelled() {
				Gdx.app.log(this.getClass().getName(), "DreamLo: lb_submit: timed out");
				Gdx.app.postRunnable(callback.with(new RuntimeException("TIMED OUT")));
			}

			@Override
			public void failed(Throwable t) {
				Gdx.app.log(this.getClass().getName(), "DreamLo: lb_submit", t);
				Gdx.app.postRunnable(callback.with(new RuntimeException(t)));
			}

			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {
				Gdx.app.postRunnable(callback.withNull());
			}
		});
	}

	public boolean registerWithDreamLoBoard() {
		if (prefs.getString(DREAMLO_USERID, "").length() == 0) {
			if (!registeredListenerPending) {
				Gdx.app.log(this.getClass().getName(), "DreamLo: registeredWithBoard: do");
				registeredListenerPending = true;
				Gdx.app.postRunnable(registerWithBoard);
			}
			return false;
		}
		return true;
	}
}