package com.aptproject.goaltracker.repository.exception;

import com.aptproject.goaltracker.model.Habit;

public class HabitExistsException extends Exception {

	private static final long serialVersionUID = 1L;

	public HabitExistsException(Habit habit) {
		super("The habit " + habit.getName() + " already exists for the current goal");
	}
}
