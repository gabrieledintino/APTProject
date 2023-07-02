package com.aptproject.goaltracker.repository.exception;

import com.aptproject.goaltracker.model.Goal;

public class GoalExistsException extends Exception {

	private static final long serialVersionUID = 1L;

	public GoalExistsException(Goal goal) {
		super("The goal " + goal.getName() + " already exists");
	}
}
