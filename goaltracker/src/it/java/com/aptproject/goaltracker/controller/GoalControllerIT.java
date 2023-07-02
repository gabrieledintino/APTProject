package com.aptproject.goaltracker.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.aptproject.goaltracker.model.Goal;
import com.aptproject.goaltracker.model.Habit;
import com.aptproject.goaltracker.repository.ModelRepository;
import com.aptproject.goaltracker.repository.exception.GoalExistsException;
import com.aptproject.goaltracker.repository.exception.HabitExistsException;
import com.aptproject.goaltracker.repository.postgres.PostgresModelRepository;
import com.aptproject.goaltracker.view.GoalView;

public class GoalControllerIT {
	@Mock
	private GoalView goalView;
	
	private GoalController goalController;

	private ModelRepository modelRepository;

	private AutoCloseable closeable;
	
	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		modelRepository = new PostgresModelRepository("PersistenceUnit");
		goalController = new GoalController(goalView, modelRepository);
	}
	
	@After
	public void releaseMocks() throws Exception {
		closeable.close();
	}
	
	@Test
	public void testAllGoals() throws GoalExistsException {
		Goal goal = new Goal("Goal");
		modelRepository.addGoal(goal);
		goalController.allGoals();
		verify(goalView).showAllGoals(asList(goal));
	}
	
	@Test
	public void testNewGoal() {
		Goal goal = new Goal("Goal");
		goalController.newGoal(goal);
		verify(goalView).goalAdded(goal);
	}
	
	@Test
	public void testDeleteGoal() throws GoalExistsException {
		Goal goalToDelete = new Goal("toDelete");
		modelRepository.addGoal(goalToDelete);
		goalController.deleteGoal(goalToDelete);
		verify(goalView).goalRemoved(goalToDelete);
	}
	
	@Test
	public void testAddHabitToGoal() throws GoalExistsException {
		Goal goal = new Goal("goal");
		Habit habit = new Habit("habit");
		modelRepository.addGoal(goal);
		goalController.addHabit(goal, habit);
		verify(goalView).habitAdded(habit);
		
	}
	
	@Test
	public void testRemoveHabitFromGoal() throws GoalExistsException, HabitExistsException {
		Goal goal = new Goal("goal");
		Habit habit = new Habit("habit");
		modelRepository.addGoal(goal);
		modelRepository.addHabitToGoal(goal, habit);
		goalController.removeHabit(goal, habit);
		verify(goalView).habitRemoved(habit);
	}
	
	@Test
	public void testIncrementHabitCounter() throws GoalExistsException, HabitExistsException {
		Goal goal = new Goal("goal");
		Habit habit = new Habit("habit");
		modelRepository.addGoal(goal);
		modelRepository.addHabitToGoal(goal, habit);
		goalController.incrementCounter(habit);
		verify(goalView).counterUpdated(habit);
	}
	
	@Test
	public void testDecrementHabitCounterWhenCounterIsGreaterThanZero() throws GoalExistsException, HabitExistsException {
		Goal goal = new Goal("goal");
		Habit habit = new Habit("habit");
		modelRepository.addGoal(goal);
		modelRepository.addHabitToGoal(goal, habit);
		habit.setCounter(5);
		goalController.decrementCounter(habit);
		verify(goalView).counterUpdated(habit);
	}	
}
