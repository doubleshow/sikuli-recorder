package org.sikuli.recorder.event;

import org.json.simple.JSONObject;

public class ClickEvent extends Event {
	private int x;
	private int y;
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}	

	public JSONObject toJSON(){
		JSONObject obj=new JSONObject();
		obj.put("x",x);
		obj.put("y",y);
		return obj;
	}
}