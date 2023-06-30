package com.aptproject.goaltracker.view;

import java.util.List;

import com.aptproject.goaltracker.model.Goal;
import com.aptproject.goaltracker.model.Habit;

public interface GoalView {
	void showAllGoals(List<Goal> goals);
	
	void showError(String message);
	
	void goalAdded(Goal goal);
	
	void goalRemoved(Goal goal);
	
	void habitAdded(Habit habit);
	
	void habitRemoved(Habit habit);
	
	void counterUpdated(Habit habit);
}
