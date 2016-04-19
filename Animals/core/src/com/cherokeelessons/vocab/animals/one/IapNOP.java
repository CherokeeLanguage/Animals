package com.cherokeelessons.vocab.animals.one;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class IapNOP implements IAP {

	private PublicKey mPublicKey;

	@Override
	public boolean isPurchased() {
		return false;
	}

	@Override
	public void loadItems(Callback callback) {
		if (callback!=null) {
			callback.onSuccess("");
		}		
	}

	@Override
	public String getDevId() {
		return null;
	}

	@Override
	public PublicKey getPublicKey() {
		// Create a PublicKey object from the key data downloaded from the
		// developer portal.
		FileHandle key_der = Gdx.files.internal("data/key.der");
		byte[] applicationKey = key_der.readBytes();
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(applicationKey);
		try {
			// Create a public key
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			mPublicKey = keyFactory.generatePublic(keySpec);
		} catch (Exception e) {
			Gdx.app.log(getClass().getCanonicalName(),
					"Unable to create encryption key", e);
			throw new RuntimeException(e);
		}
		return mPublicKey;
	}

	@Override
	public void loadReceipts(final Callback callback) {
		if (callback!=null) {
			callback.onSuccess("");
		}
		
	}

	@Override
	public void loadUUID(Callback callback) {
		if (callback!=null) {
			callback.onSuccess("");
		}		
	}

	@Override
	public void purchaseGame(Callback callback) {
		if (callback!=null) {
			callback.onSuccess("");
		}		
	}

}
