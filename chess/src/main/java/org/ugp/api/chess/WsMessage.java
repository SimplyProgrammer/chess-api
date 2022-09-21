package org.ugp.api.chess;

import java.util.Map;

import org.ugp.serialx.Scope;

public class WsMessage extends Scope {

	private static final long serialVersionUID = 1L;

	protected String type;
	
	public WsMessage(Map<String, ?> variablesMap, Object... values) 
	{
		super(variablesMap, values);
		setType(getString("type"));
	}
	
	public WsMessage(String type, Object source)
	{
		super();
		setType(type);
		put("type", type);
		put("data", source);
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
