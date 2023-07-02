package com.aptproject.goaltracker.repository.exception;

import com.aptproject.goaltracker.model.Goal;

public class GoalNotExistsException extends Exception {

	private static final long serialVersionUID = 1L;

	public GoalNotExistsException(Goal goal) {
		super("The goal " + goal.getName() + " does not exists");
	}
}
