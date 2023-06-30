package com.aptproject.goaltracker.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "goals")
public class Goal {

	@Id
    private String name;

    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Habit> habits;

    public Goal() {
    }
    
    public Goal(String name) {
        this.name = name;
        this.habits = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Habit> getHabits() {
        return habits;
    }

    public void setHabits(List<Habit> habits) {
    	this.habits.clear();
    	if (habits != null) {
    		this.habits.addAll(habits);
    	}
    }
    
    public void addHabit(Habit habit) {
    	this.habits.add(habit);
    }
    
    public void removeHabit(Habit habit) {
    	this.habits.remove(habit);
    }

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Goal other = (Goal) obj;
		return Objects.equals(name, other.name);
	}
}