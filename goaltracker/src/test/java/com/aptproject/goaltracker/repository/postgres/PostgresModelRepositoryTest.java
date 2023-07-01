package com.aptproject.goaltracker.repository.postgres;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import org.junit.Before;
import org.junit.Test;
import com.aptproject.goaltracker.model.Goal;
import com.aptproject.goaltracker.model.Habit;

public class PostgresModelRepositoryTest {
	
	private EntityManager entityManager;

	private PostgresModelRepository goalRepository;
	
	@Before
	public void setup() throws SQLException {
		goalRepository = new PostgresModelRepository("PersistenceUnit");
		entityManager = goalRepository.getEntityManager();
	}
	
	@Test
	public void testFindAllWhenDatabaseIsEmpty() {
		assertThat(goalRepository.findAllGoals()).isEmpty();			
	}
	
	@Test
	public void testFindAllWhenDatabaseIsNotEmpty() {
		Goal goal1 = new Goal("Test 1");
		Goal goal2 = new Goal("Test 2");
		addGoalToDb(goal1);
		addGoalToDb(goal2);
		
		assertThat(goalRepository.findAllGoals())
			.containsExactly(goal1, goal2);			
	}
	
	@Test
	public void testFindByNameNotFound() {
		Goal goal1 = new Goal("Test 1");
		Goal goal2 = new Goal("Test 2");
		addGoalToDb(goal1);
		addGoalToDb(goal2);
		
		assertThat(goalRepository.findGoalByName("Test"))
			.isNull();			
	}

	private void addGoalToDb(Goal goal) {
		entityManager.getTransaction().begin();
		entityManager.persist(goal);
		entityManager.getTransaction().commit();
	}
	
	@Test
	public void testFindByNameFound() {
		Goal goal1 = new Goal("Test 1");
		Goal goal2 = new Goal("Test 2");
		addGoalToDb(goal1);
		addGoalToDb(goal2);
		
		assertThat(goalRepository.findGoalByName("Test 2"))
			.isEqualTo(goal2);			
	}
	
	@Test
	public void testAddGoal() {
		Goal goal = new Goal("Test");
		
		goalRepository.addGoal(goal);
		
		assertThat(findAllDatabaseSavedGoals())
			.contains(goal);			
	}
	
	@Test
	public void testDeleteGoal() {
		Goal goal = new Goal("Test");
		addGoalToDb(goal);
		
		goalRepository.deleteGoal(goal);
		
		assertThat(findAllDatabaseSavedGoals())
			.isEmpty();			
	}
	
	@Test
	public void testDeleteGoalWhenNotExistingThrowException() {
		Goal goal = new Goal("Test");		
		
		assertThatThrownBy(() -> goalRepository.deleteGoal(goal))
			.isInstanceOf(PersistenceException.class)
			.hasMessage("The goal Test does not exists");
		
		assertThat(findAllDatabaseSavedGoals())
			.isEmpty();			
	}
	
	@Test
	public void testAddHabitToGoalAddHabitAndSetTheLinks() {
		Goal goal = new Goal("Goal");
		Habit habit = new Habit("Habit");
		addGoalToDb(goal);
		
		goalRepository.addHabitToGoal(goal, habit);
		
		Goal retrievedGoal = findAllDatabaseSavedGoals().get(0);
		assertThat(findAllDatabaseSavedHabits()).containsExactly(habit);
		assertThat(retrievedGoal.getHabits()).containsExactly(habit);
		assertThat(findAllDatabaseSavedHabits().get(0).getGoal()).isEqualTo(goal);
	}
	
	@Test
	public void testRemoveHabitFromGoalRemoveHabit() {
		Goal goal = new Goal("Goal");
		Habit habit = new Habit("Habit");
		goal.setHabits(Arrays.asList(habit));
		habit.setGoal(goal);
		addGoalToDb(goal);
		
		goalRepository.removeHabitFromGoal(goal, habit);
		
		Goal retrievedGoal = findAllDatabaseSavedGoals().get(0);
		assertThat(findAllDatabaseSavedHabits()).isEmpty();
		assertThat(retrievedGoal.getHabits()).isEmpty();
	}
	
	@Test
	public void testRemoveHabitWhenNotExistingThrowException() {
		Goal goal = new Goal("Test");		
		Habit habit = new Habit("Habit");
		addGoalToDb(goal);
		
		assertThatThrownBy(() -> goalRepository.removeHabitFromGoal(goal, habit))
			.isInstanceOf(PersistenceException.class)
			.hasMessage("The habit Habit does not exists");
		
		assertThat(findAllDatabaseSavedHabits())
			.isEmpty();			
	}
	
	@Test
	public void testIncrementCounter() {
		Goal goal = new Goal("Goal");
		Habit habit = new Habit("Habit");
		goal.setHabits(Arrays.asList(habit));
		habit.setGoal(goal);
		addGoalToDb(goal);
		
		goalRepository.incrementCounter(habit);
		
		Habit retrievedHabit = findAllDatabaseSavedHabits().get(0);
		assertThat(retrievedHabit.getCounter()).isEqualTo(1);
	}
	
	@Test
	public void testDecrementCounter() {
		Goal goal = new Goal("Goal");
		Habit habit = new Habit("Habit");
		goal.setHabits(Arrays.asList(habit));
		habit.setGoal(goal);
		habit.setCounter(5);
		addGoalToDb(goal);
		
		goalRepository.decrementCounter(habit);
		
		Habit retrievedHabit = findAllDatabaseSavedHabits().get(0);
		assertThat(retrievedHabit.getCounter()).isEqualTo(4);
	}
	
	@Test
	public void testCreatingDuplicateGoalShouldThrowAndRollback() {
		Goal goal1 = new Goal("Goal");
		Goal goal2 = new Goal("Goal");
		addGoalToDb(goal1);
		
		assertThatThrownBy(() -> goalRepository.addGoal(goal2))
			.isInstanceOf(EntityExistsException.class)
			.hasMessage("The goal Goal already exists");
		assertThat(findAllDatabaseSavedGoals()).size().isEqualTo(1);
		assertThat(findAllDatabaseSavedGoals()).containsExactly(goal1);
	}
	
	@Test
	public void testHabitWithSameNameButDifferentGoalAreSaved() {
		Goal goal1 = new Goal("Goal 1");
		Goal goal2 = new Goal("Goal 2");
		Habit habit1 = new Habit("Habit");
		Habit habit2 = new Habit("Habit");
		goal1.addHabit(habit1);
		habit1.setGoal(goal1);
		addGoalToDb(goal1);
		addGoalToDb(goal2);
		
		goalRepository.addHabitToGoal(goal2, habit2);
		
		assertThat(findAllDatabaseSavedHabits()).size().isEqualTo(2);
		assertThat(findAllDatabaseSavedHabits()).containsExactly(habit1, habit2);
	}
	
	@Test
	public void testHabitWithSameNameAndSameGoalShouldThrowAndRollback() {
		Goal goal1 = new Goal("Goal 1");
		Goal goal2 = new Goal("Goal 2");
		Habit habit1 = new Habit("Habit");
		Habit habit2 = new Habit("Habit");
		goal1.addHabit(habit1);
		habit1.setGoal(goal1);
		addGoalToDb(goal1);
		addGoalToDb(goal2);
		
		assertThatThrownBy(() -> goalRepository.addHabitToGoal(goal1, habit2))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("The habit Habit already exists for the current goal");
		assertThat(findAllDatabaseSavedHabits()).size().isEqualTo(1);
	}
	
	
	
	private List<Goal> findAllDatabaseSavedGoals() {
		String queryString = "SELECT g FROM Goal g";
        TypedQuery<Goal> query = entityManager.createQuery(queryString, Goal.class);
        return query.getResultList();
	}
	
	private List<Habit> findAllDatabaseSavedHabits() {
		String queryString = "SELECT h FROM Habit h";
        TypedQuery<Habit> query = entityManager.createQuery(queryString, Habit.class);
        return query.getResultList();
	}
}