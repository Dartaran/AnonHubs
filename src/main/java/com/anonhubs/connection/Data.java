package com.anonhubs.connection;

public abstract class Data<T>
{
	public abstract void onRecieve(T data);
	public abstract void onFailure(String reason);
}