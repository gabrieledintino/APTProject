package com.aptproject.goaltracker.controller;

import javax.persistence.EntityExistsException;
import javax.persistence.PersistenceException;
import com.aptproject.goaltracker.model.Goal;
import com.aptproject.goaltracker.model.Habit;
import com.aptproject.goaltracker.repository.ModelRepository;
import com.aptproject.goaltracker.view.GoalView;

public class GoalController {
	private GoalView goalView;
	private ModelRepository modelRepository;
	
	public GoalController(GoalView goalView, ModelRepository modelRepository) {
		this.goalView = goalView;
		this.modelRepository = modelRepository;
	}
	
	public void allGoals() {
		goalView.showAllGoals(modelRepository.findAllGoals());
	}
	
	public void newGoal(Goal goal) {
		try {
			modelRepository.addGoal(goal);
		} catch (EntityExistsException e) {
			goalView.showError(e.getMessage());
			return;
		}
		goalView.goalAdded(goal);
	}

	public void deleteGoal(Goal goal) {
		try {
			modelRepository.deleteGoal(goal);
		} catch (PersistenceException e) {
			goalView.showError(e.getMessage());
			return;
		}
		goalView.goalRemoved(goal);
	}

	public void addHabit(Goal goal, Habit habit) {
		try {
			modelRepository.addHabitToGoal(goal, habit);
		} catch (IllegalStateException e) {
			goalView.showError(e.getMessage());
			return;
		}
		goalView.habitAdded(habit);
	}

	public void removeHabit(Goal goal, Habit habit) {
		try {
			modelRepository.removeHabitFromGoal(goal, habit);
		} catch (PersistenceException e) {
			goalView.showError(e.getMessage());
			return;
		}
		goalView.habitRemoved(habit);
	}

	public void incrementCounter(Habit habit) {
		modelRepository.incrementCounter(habit);
		goalView.counterUpdated(habit);
	}

	public void decrementCounter(Habit habit) {
		if (habit.getCounter() == 0) {
			goalView.showError("You can't decrement a counter equal to zero!");
			return;
		}
		modelRepository.decrementCounter(habit);
		goalView.counterUpdated(habit);
	}
}