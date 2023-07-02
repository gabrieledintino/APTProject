package com.aptproject.goaltracker.repository.exception;

import com.aptproject.goaltracker.model.Habit;

public class HabitNotExistsException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public HabitNotExistsException(Habit habit) {
		super("The habit " + habit.getName() + " does not exists");
	}
}
