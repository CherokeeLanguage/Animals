package com.cherokeelessons.common;

import java.io.Serializable;

public class BoardScore implements Serializable {
	private static final long serialVersionUID = -692343523714247771L;
	public int level=0;
	public long elapsed=0;
	public int correct=0;
	public String nickname="Player";
	public String uuid=null;
	public String board=null;
}
