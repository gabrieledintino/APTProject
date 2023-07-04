package com.aptproject.goaltracker.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.*;

@Entity
@IdClass(HabitId.class)
@Table(name = "habit")
public class Habit implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
    private String name;
    private int counter;
    
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id")
    private Goal goal;

    public Habit() {
    }

    public Habit(String name) {
        this.name = name;
        this.counter = 0;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}
	
	public void incrementCounter() {
		this.counter++;
	}
	
	public void decrementCounter() {
		this.counter--;
	}

	public Goal getGoal() {
		return goal;
	}

	public void setGoal(Goal goal) {
		this.goal = goal;
	}
	
	@Override
	public String toString() {
		return name + " " + counter;
	}

	@Override
	public int hashCode() {
		return Objects.hash(counter, goal, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Habit other = (Habit) obj;
		return counter == other.counter && Objects.equals(goal, other.goal) && Objects.equals(name, other.name);
	}    
}
