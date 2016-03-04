package com.example.oracle;

import java.io.Serializable;

/**
 *
 * @author Stephane Nicoll
 */
public class MyFakeAdtMessage implements Serializable {

	private final ORAData oraData;

	public MyFakeAdtMessage(ORAData oraData) {
		this.oraData = oraData;
	}


	public Object getAdtPayload() {
		return oraData;
	}
}
