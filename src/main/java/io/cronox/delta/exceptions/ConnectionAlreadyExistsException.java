package io.cronox.delta.exceptions;

public class ConnectionAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = -1090025064404951825L;

	public ConnectionAlreadyExistsException(String message){
		super("Two or more connections with same id found. make sure all connection id's are unique :"+message);
	}
	@Override
	public String getMessage() {
		return "Connection You are trying to add already exists!!";
	}
}
