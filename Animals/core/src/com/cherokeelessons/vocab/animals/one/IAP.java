package com.cherokeelessons.vocab.animals.one;

import java.security.PublicKey;

public interface IAP {
	public static interface Callback {
		public void onCancel();
		public void onFailure(int errorCode, String errorMessage);
		public void onSuccess(String success);
	}
	public boolean isPurchased();
	public String getDevId();
	public PublicKey getPublicKey();
	public void loadReceipts(Callback callback);
	public void loadItems(Callback callback);
	public void loadUUID(Callback callback);
	public void purchaseGame(Callback callback);
}
