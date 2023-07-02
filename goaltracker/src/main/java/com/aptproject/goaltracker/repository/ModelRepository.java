package com.aptproject.goaltracker.repository;

import java.util.List;

import com.aptproject.goaltracker.model.Goal;
import com.aptproject.goaltracker.model.Habit;
import com.aptproject.goaltracker.repository.exception.GoalExistsException;
import com.aptproject.goaltracker.repository.exception.GoalNotExistsException;
import com.aptproject.goaltracker.repository.exception.HabitExistsException;
import com.aptproject.goaltracker.repository.exception.HabitNotExistsException;

public interface ModelRepository {
	public List<Goal> findAllGoals();
	
	public Goal findGoalByName(String name);
	
	public void addGoal(Goal goal) throws GoalExistsException;
	
	public void deleteGoal(Goal goal) throws GoalNotExistsException;
	
	public void addHabitToGoal(Goal goal, Habit habit) throws HabitExistsException;
	
	public void removeHabitFromGoal(Goal goal, Habit habit) throws HabitNotExistsException;
	
	public void incrementCounter(Habit habit);
	
	public void decrementCounter(Habit habit);
}