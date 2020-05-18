package io.cronox.delta.exceptions;

public class ConnectionNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -6476082017443257429L;

	public ConnectionNotFoundException(String message){
		super("Connection with given id could not be found. :"+message);
	}
	@Override
	public String getMessage() {
		return "Connection with given id Could not be found!!";
	}

	
}
