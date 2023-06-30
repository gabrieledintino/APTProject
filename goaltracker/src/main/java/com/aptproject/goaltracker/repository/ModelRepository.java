package com.aptproject.goaltracker.repository;

import java.util.List;

import com.aptproject.goaltracker.model.Goal;
import com.aptproject.goaltracker.model.Habit;

public interface ModelRepository {
	public List<Goal> findAllGoals();
	
	public Goal findGoalByName(String name);
	
	public void addGoal(Goal goal);
	
	public void deleteGoal(Goal goal);
	
	public void addHabitToGoal(Goal goal, Habit habit);
	
	public void removeHabitFromGoal(Goal goal, Habit habit);
	
	public void incrementCounter(Habit habit);
	
	public void decrementCounter(Habit habit);
}