package client.com;

import server.com.User;

public class CurrentUser extends User {
	private boolean loggedIn;
	
	public CurrentUser() {
		super();
		this.loggedIn = false;
	}
	
	public CurrentUser(String user,String password) {
		super(user,password);
		this.loggedIn = true;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}
	
	public void login() {
		this.loggedIn = true;
	}

	public String toString() {
		return super.toString()+","+loggedIn;
	}
}
