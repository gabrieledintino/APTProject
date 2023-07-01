package com.aptproject.goaltracker.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import java.util.List;
import javax.persistence.EntityExistsException;
import javax.persistence.PersistenceException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.aptproject.goaltracker.model.Goal;
import com.aptproject.goaltracker.model.Habit;
import com.aptproject.goaltracker.repository.ModelRepository;
import com.aptproject.goaltracker.view.GoalView;

public class GoalControllerTest {

	@Mock
	private ModelRepository modelRepository;
	
	@Mock
	private GoalView goalView;
	
	@InjectMocks
	private GoalController goalController;
	
	private AutoCloseable closeable;

	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
	}

	@After
	public void releaseMocks() throws Exception {
		closeable.close();
	}
	
	@Test
	public void testAllGoals() {
		List<Goal> goals = asList(new Goal("first"));
		when(modelRepository.findAllGoals())
			.thenReturn(goals);
		goalController.allGoals();
		verify(goalView).showAllGoals(goals);
	}
	
	@Test
	public void testNewGoal() {
		Goal goal = new Goal("toAdd");
		goalController.newGoal(goal);
		InOrder inOrder = inOrder(modelRepository, goalView);
		inOrder.verify(modelRepository).addGoal(goal);
		inOrder.verify(goalView).goalAdded(goal);
	}
	
	@Test
	public void testDeleteGoal() {
		Goal goalToDelete = new Goal("toDelete");
		goalController.deleteGoal(goalToDelete);
		InOrder inOrder = inOrder(modelRepository, goalView);
		inOrder.verify(modelRepository).deleteGoal(goalToDelete);
		inOrder.verify(goalView).goalRemoved(goalToDelete);
	}
	
	@Test
	public void testAddHabitToGoal() {
		Goal goal = new Goal("goal");
		Habit habit = new Habit("habit");
		goalController.addHabit(goal, habit);
		InOrder inOrder = inOrder(modelRepository, goalView);
		inOrder.verify(modelRepository).addHabitToGoal(goal, habit);
		inOrder.verify(goalView).habitAdded(habit);
		
	}
	
	@Test
	public void testRemoveHabitFromGoal() {
		Goal goal = new Goal("goal");
		Habit habit = new Habit("habit");
		goalController.removeHabit(goal, habit);
		InOrder inOrder = inOrder(modelRepository, goalView);
		inOrder.verify(modelRepository).removeHabitFromGoal(goal, habit);
		inOrder.verify(goalView).habitRemoved(habit);
	}
	
	@Test
	public void testIncrementHabitCounter() {
		Habit habit = new Habit("habit");
		goalController.incrementCounter(habit);
		InOrder inOrder = inOrder(modelRepository, goalView);
		inOrder.verify(modelRepository).incrementCounter(habit);
		inOrder.verify(goalView).counterUpdated(habit);
	}
	
	@Test
	public void testDecrementHabitCounterWhenCounterIsGreaterThanZero() {
		Habit habit = new Habit("habit");
		habit.setCounter(5);
		goalController.decrementCounter(habit);
		InOrder inOrder = inOrder(modelRepository, goalView);
		inOrder.verify(modelRepository).decrementCounter(habit);
		inOrder.verify(goalView).counterUpdated(habit);
	}
	
	@Test
	public void testDecrementHabitCounterWhenCounterIsEqualToZero() {
		Habit habit = new Habit("habit");
		goalController.decrementCounter(habit);
		verify(goalView).showError("You can't decrement a counter equal to zero!");
		verifyNoMoreInteractions(modelRepository);
	}
	
	@Test
	public void testNewDuplicateGoalShouldShowAnErrorAndNotAddToToTheView() {
		Goal goal = new Goal("Goal");
		EntityExistsException exception = new EntityExistsException("The goal " + goal.getName() + " already exists");
		doThrow(exception).when(modelRepository).addGoal(goal);
		goalController.newGoal(goal);
		verify(goalView).showError(exception.getMessage());
		verify(goalView, never()).goalAdded(goal);
	}
	
	@Test
	public void testDeletingNonExistingGoalShouldShowAnError() {
		Goal goal = new Goal("Goal");
		PersistenceException exception = new PersistenceException("The goal " + goal.getName() + " does not exists");
		doThrow(exception).when(modelRepository).deleteGoal(goal);
		goalController.deleteGoal(goal);
		verify(goalView).showError(exception.getMessage());
		verify(goalView, never()).goalRemoved(goal);
	}
	
	@Test
	public void testNewDuplicateHabitShouldShowAnErrorAndNotAddToToTheView() {
		Goal goal = new Goal("Goal");
		Habit habit = new Habit("Habit");
		IllegalStateException exception = new IllegalStateException("The habit " + habit.getName() + " already exists for the current goal");
		doThrow(exception).when(modelRepository).addHabitToGoal(goal, habit);
		goalController.addHabit(goal, habit);
		verify(goalView).showError(exception.getMessage());
		verify(goalView, never()).habitAdded(habit);
	}
	
	@Test
	public void testDeletingNonExistingHabitShouldShowAnError() {
		Goal goal = new Goal("Goal");
		Habit habit = new Habit("Habit");
		PersistenceException exception = new PersistenceException("The habit " + habit.getName() + " does not exists");
		doThrow(exception).when(modelRepository).removeHabitFromGoal(goal, habit);
		goalController.removeHabit(goal, habit);
		verify(goalView).showError(exception.getMessage());
		verify(goalView, never()).habitRemoved(habit);
	}
}