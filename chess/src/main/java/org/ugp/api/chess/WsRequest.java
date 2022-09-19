package org.ugp.api.chess;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.ugp.serialx.GenericScope;
import org.ugp.serialx.Scope;

public class WsRequest extends Scope {

	private static final long serialVersionUID = 1L;

	protected String type;
	
	public WsRequest(Object... values) 
	{
		this(null, values);
	}
	
	public WsRequest(Map<String, ?> variablesMap, Object... values) 
	{
		super(variablesMap, values);
		setType((String) remove("type"));
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
