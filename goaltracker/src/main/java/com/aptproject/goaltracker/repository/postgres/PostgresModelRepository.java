package com.aptproject.goaltracker.repository.postgres;

import java.util.List;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import com.aptproject.goaltracker.model.Goal;
import com.aptproject.goaltracker.model.Habit;
import com.aptproject.goaltracker.repository.ModelRepository;
import com.aptproject.goaltracker.repository.exception.GoalExistsException;
import com.aptproject.goaltracker.repository.exception.GoalNotExistsException;
import com.aptproject.goaltracker.repository.exception.HabitExistsException;
import com.aptproject.goaltracker.repository.exception.HabitNotExistsException;
import com.aptproject.goaltracker.model.HabitId;

public class PostgresModelRepository implements ModelRepository {

	private EntityManager entityManager;
	private EntityManagerFactory emf;

	public PostgresModelRepository(String persistenceUnitName) {
		emf = Persistence.createEntityManagerFactory(persistenceUnitName);
		entityManager = emf.createEntityManager();
	}

	@Override
	public List<Goal> findAllGoals() {
		String jpql = "SELECT g FROM Goal g";
		TypedQuery<Goal> query = entityManager.createQuery(jpql, Goal.class);
		return query.getResultList();
	}

	@Override
	public Goal findGoalByName(String name) {
		return entityManager.find(Goal.class, name);
	}

	@Override
	public void addGoal(Goal goal) throws GoalExistsException {
		try {
			entityManager.getTransaction().begin();
			entityManager.persist(goal);
			entityManager.getTransaction().commit();
		} catch (EntityExistsException | RollbackException e) {
			entityManager.getTransaction().rollback();
			throw new GoalExistsException(goal);
		}

	}

	@Override
	public void deleteGoal(Goal goal) throws GoalNotExistsException {
		Goal existing = entityManager.find(Goal.class, goal.getName());
		try {
			entityManager.getTransaction().begin();
			entityManager.remove(existing);
			entityManager.getTransaction().commit();
		} catch (IllegalArgumentException | RollbackException e) {
			entityManager.getTransaction().rollback();
			throw new GoalNotExistsException(goal);
		}
	}

	@Override
	public void addHabitToGoal(Goal goal, Habit habit) throws HabitExistsException {
		try {
			entityManager.getTransaction().begin();
			goal.addHabit(habit);
			habit.setGoal(goal);
			entityManager.merge(goal);
			entityManager.getTransaction().commit();
		} catch (IllegalStateException e) {
			entityManager.getTransaction().rollback();
			throw new HabitExistsException(habit);
		}

	}

	@Override
	public void removeHabitFromGoal(Goal goal, Habit habit) throws HabitNotExistsException {
		Goal existingGoal = entityManager.find(Goal.class, goal.getName());
		HabitId habitId = new HabitId(habit.getName(), habit.getGoal());
		Habit existingHabit = entityManager.find(Habit.class, habitId);
		try {
			entityManager.getTransaction().begin();
			existingGoal.removeHabit(existingHabit);
			entityManager.remove(existingHabit);
			entityManager.merge(existingGoal);
			entityManager.getTransaction().commit();
		} catch (IllegalArgumentException | RollbackException e) {
			entityManager.getTransaction().rollback();
			throw new HabitNotExistsException(habit);
		}
	}

	@Override
	public void incrementCounter(Habit habit) {
		entityManager.getTransaction().begin();
		habit.incrementCounter();
		entityManager.merge(habit);
		entityManager.getTransaction().commit();
	}

	@Override
	public void decrementCounter(Habit habit) {
		entityManager.getTransaction().begin();
		habit.decrementCounter();
		entityManager.merge(habit);
		entityManager.getTransaction().commit();
	}
}
