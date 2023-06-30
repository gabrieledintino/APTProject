package com.aptproject.goaltracker.repository.postgres;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import com.aptproject.goaltracker.model.Goal;
import com.aptproject.goaltracker.model.Habit;
import com.aptproject.goaltracker.repository.ModelRepository;

public class PostgresModelRepository implements ModelRepository {

	private EntityManager entityManager;
	private EntityManagerFactory emf;
	
	EntityManager getEntityManager() {
		return entityManager;
	}

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
	public void addGoal(Goal goal) {
		try {
			entityManager.getTransaction().begin();
			entityManager.persist(goal);
			entityManager.getTransaction().commit();
		} catch (EntityExistsException e) {
			entityManager.getTransaction().rollback();
			throw new EntityExistsException("The goal " + goal.getName() + " already exists");
		}
		
	}

	@Override
	public void deleteGoal(Goal goal) {
		entityManager.getTransaction().begin();
		entityManager.remove(goal);
		entityManager.getTransaction().commit();
	}

	@Override
	public void addHabitToGoal(Goal goal, Habit habit) {
		try {
			entityManager.getTransaction().begin();
			goal.addHabit(habit);
			habit.setGoal(goal);
			entityManager.merge(goal);
			entityManager.getTransaction().commit();	
		} catch (IllegalStateException e) {
			entityManager.getTransaction().rollback();
			throw new IllegalStateException("The habit " + habit.getName() + " already exists for the current goal");
		}
		
	}

	@Override
	public void removeHabitFromGoal(Goal goal, Habit habit) {
		entityManager.getTransaction().begin();
		goal.removeHabit(habit);
		entityManager.merge(goal);
		entityManager.getTransaction().commit();
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
