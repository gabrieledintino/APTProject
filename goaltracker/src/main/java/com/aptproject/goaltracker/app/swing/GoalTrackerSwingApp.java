package com.aptproject.goaltracker.app.swing;

import java.awt.EventQueue;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.aptproject.goaltracker.controller.GoalController;
import com.aptproject.goaltracker.repository.postgres.PostgresModelRepository;
import com.aptproject.goaltracker.view.swing.GoalSwingView;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(mixinStandardHelpOptions = true)
public class GoalTrackerSwingApp implements Callable<Void> {

	@Option(names = { "--persistence-unit" }, description = "The name of the persistence unit to use")
	private static String persistenceUnit = "PersistenceUnit";

	/**
	 * Launch the application. The database must be started first. Docker command is
	 * the following: docker run --name postgres-docker --rm -p 5455:5432 -e POSTGRES_USER=user -e POSTGRES_PASSWORD=password -e POSTGRES_DB=postgres -d postgres:15.3
	 */
	public static void main(String[] args) {
		new CommandLine(new GoalTrackerSwingApp()).execute(args);
	}

	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(() -> {
			try {
				PostgresModelRepository modelRepository = new PostgresModelRepository(persistenceUnit);
				GoalSwingView goalView = new GoalSwingView();
				GoalController goalController = new GoalController(goalView, modelRepository);
				goalView.setGoalController(goalController);
				goalView.setVisible(true);
				goalController.allGoals();
			} catch (Exception e) {
				
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Exception", e);
			}
		});
		return null;
	}
}
