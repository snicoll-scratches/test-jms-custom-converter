package com.example.oracle;

import java.io.Serializable;

/**
 *
 * @author Stephane Nicoll
 */
public class ORAData implements Serializable {

	private final String name;

	public ORAData(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
