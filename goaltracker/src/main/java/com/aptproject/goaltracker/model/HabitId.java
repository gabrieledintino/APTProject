package com.aptproject.goaltracker.model;

import java.io.Serializable;
import java.util.Objects;

public class HabitId implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	
	private Goal goal;
	
	public HabitId() {
	}
	
	public HabitId(String name, Goal goal) {
		this.name = name;
		this.goal = goal;
	}

	@Override
	public int hashCode() {
		return Objects.hash(goal, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HabitId other = (HabitId) obj;
		return Objects.equals(goal, other.goal) && Objects.equals(name, other.name);
	}
}