package io.cronox.delta.exceptions;

public class ConnectionFailedException extends RuntimeException {

	private static final long serialVersionUID = -510461213539701888L;

	public ConnectionFailedException(String message){
		super("Failed to Connect to : " + message);
	}
}
