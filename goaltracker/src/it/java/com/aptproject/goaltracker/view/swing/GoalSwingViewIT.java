package com.aptproject.goaltracker.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.timing.Pause.pause;
import static org.assertj.swing.timing.Timeout.timeout;
import static org.awaitility.Awaitility.*;

import java.util.concurrent.TimeUnit;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.assertj.swing.timing.Condition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.aptproject.goaltracker.controller.GoalController;
import com.aptproject.goaltracker.model.Goal;
import com.aptproject.goaltracker.model.Habit;
import com.aptproject.goaltracker.repository.ModelRepository;
import com.aptproject.goaltracker.repository.exception.GoalExistsException;
import com.aptproject.goaltracker.repository.postgres.PostgresModelRepository;

@RunWith(GUITestRunner.class)
public class GoalSwingViewIT extends AssertJSwingJUnitTestCase {

	private GoalSwingView goalSwingView;
	private FrameFixture window;
	private GoalController goalController;
	private ModelRepository modelRepository;

	private static final long TIMEOUT = 5000;

	@Before
	public void onSetUp() {
		modelRepository = new PostgresModelRepository("PersistenceUnit");
		GuiActionRunner.execute(() -> {
			goalSwingView = new GoalSwingView();
			goalController = new GoalController(goalSwingView, modelRepository);
			goalSwingView.setGoalController(goalController);
			return goalSwingView;
		});
		window = new FrameFixture(robot(), goalSwingView);
		window.show();
	}

	@Test
	@GUITest
	public void testAllGoals() throws GoalExistsException {
		Goal goal1 = new Goal("Goal 1");
		Goal goal2 = new Goal("Goal 2");
		modelRepository.addGoal(goal1);
		modelRepository.addGoal(goal2);

		goalController.allGoals();

		assertThat(window.list("goalList").contents()).containsExactly(goal1.toString(), goal2.toString());
	}

	@Test
	@GUITest
	public void testAddGoalButtonSuccess() {
		window.textBox("goalTextBox").enterText("Goal");
		window.button(JButtonMatcher.withText("Add goal")).click();

		await().atMost(5, TimeUnit.SECONDS).untilAsserted(
				() -> assertThat(window.list("goalList").contents()).containsExactly(new Goal("Goal").toString()));
	}

	@Test
	@GUITest
	public void testAddGoalButtonError() throws GoalExistsException {
		modelRepository.addGoal(new Goal("Goal"));
		window.textBox("goalTextBox").enterText("Goal");
		window.button(JButtonMatcher.withText("Add goal")).click();

		pause(new Condition("Error label to contain text") {
			@Override
			public boolean test() {
				return !window.label("errorMessageLabel").text().trim().isEmpty();
			}
		}, timeout(TIMEOUT));
		assertThat(window.list("goalList").contents()).isEmpty();
		window.label("errorMessageLabel").requireText("The goal Goal already exists");
	}

	@Test
	@GUITest
	public void testDeleteGoalButtonSuccess() {
		Goal goal = new Goal("toDelete");
		goalController.newGoal(goal);
		window.list("goalList").selectItem(0);
		window.button(JButtonMatcher.withText("Remove goal")).click();

		await().atMost(5, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(window.list("goalList").contents()).isEmpty());
	}

	@Test
	@GUITest
	public void testDeleteGoalButtonError() {
		Goal goal = new Goal("nonExisting");
		GuiActionRunner.execute(() -> goalSwingView.getListGoalModel().addElement(goal));
		window.list("goalList").selectItem(0);
		window.button(JButtonMatcher.withText("Remove goal")).click();
		pause(new Condition("Error label to contain text") {
			@Override
			public boolean test() {
				return !window.label("errorMessageLabel").text().trim().isEmpty();
			}
		}, timeout(TIMEOUT));
		assertThat(window.list("goalList").contents()).containsExactly(goal.toString());
		window.label("errorMessageLabel").requireText("The goal nonExisting does not exists");

	}

	@Test
	@GUITest
	public void testAddHabitButtonSuccess() {
		Goal goal = new Goal("Goal");
		GuiActionRunner.execute(() -> goalSwingView.getListGoalModel().addElement(goal));
		window.list("goalList").selectItem(0);
		window.textBox("habitTextBox").enterText("Habit");
		window.button(JButtonMatcher.withText("Add habit")).click();

		await().atMost(5, TimeUnit.SECONDS).untilAsserted(
				() -> assertThat(window.list("habitList").contents()).containsExactly(new Habit("Habit").toString()));
	}

	@Test
	@GUITest
	public void testAddHabitButtonError() {
		Goal goal = new Goal("Goal");
		Habit habit = new Habit("Habit");
		goalController.newGoal(goal);
		goalController.addHabit(goal, habit);
		window.list("goalList").selectItem(0);
		window.textBox("habitTextBox").enterText("Habit");
		window.button(JButtonMatcher.withText("Add habit")).click();

		pause(new Condition("Error label to contain text") {
			@Override
			public boolean test() {
				return !window.label("errorMessageLabel").text().trim().isEmpty();
			}
		}, timeout(TIMEOUT));
		assertThat(window.list("habitList").contents()).containsExactly(habit.toString());
		window.label("errorMessageLabel").requireText("The habit Habit already exists for the current goal");
	}

	@Test
	@GUITest
	public void testDeleteHabitButtonSuccess() {
		Goal goal = new Goal("Goal");
		Habit habit = new Habit("Habit");
		goalController.newGoal(goal);
		goalController.addHabit(goal, habit);
		window.list("goalList").selectItem(0);
		window.list("habitList").selectItem(0);
		window.button(JButtonMatcher.withText("Remove habit")).click();

		await().atMost(5, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(window.list("habitList").contents()).isEmpty());
	}

	@Test
	@GUITest
	public void testDeleteHabitButtonError() {
		Goal goal = new Goal("Goal");
		Habit habit = new Habit("nonExisting");
		goalController.newGoal(goal);
		window.list("goalList").selectItem(0);
		// splitted the GuiActionRunner to add a non existing Habit in the list related
		// to the existing Goal
		GuiActionRunner.execute(() -> {
			goalSwingView.getListHabitModel().addElement(habit);
		});
		window.list("habitList").selectItem(0);
		window.button(JButtonMatcher.withText("Remove habit")).click();
		pause(new Condition("Error label to contain text") {
			@Override
			public boolean test() {
				return !window.label("errorMessageLabel").text().trim().isEmpty();
			}
		}, timeout(TIMEOUT));
		assertThat(window.list("habitList").contents()).containsExactly(habit.toString());
		window.label("errorMessageLabel").requireText("The habit nonExisting does not exists");
	}

	@Test
	@GUITest
	public void testIncrementCounterButtonSuccess() {
		Goal goal = new Goal("Goal");
		Habit habit = new Habit("Habit");
		goalController.newGoal(goal);
		goalController.addHabit(goal, habit);
		window.list("goalList").selectItem(0);
		window.list("habitList").selectItem(0);
		window.button(JButtonMatcher.withText("Incr. counter")).click();

		await().atMost(5, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(window.list("habitList").contents()).containsExactly("Habit - 1"));

	}

	@Test
	@GUITest
	public void testDecrementCounterButtonSuccess() {
		Goal goal = new Goal("Goal");
		Habit habit = new Habit("Habit");
		habit.setCounter(5);
		goalController.newGoal(goal);
		goalController.addHabit(goal, habit);
		window.list("goalList").selectItem(0);
		window.list("habitList").selectItem(0);
		window.button(JButtonMatcher.withText("Decr. counter")).click();

		await().atMost(5, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(window.list("habitList").contents()).containsExactly("Habit - 4"));
	}

	@Test
	@GUITest
	public void testDecrementCounterButtonError() {
		Goal goal = new Goal("Goal");
		Habit habit = new Habit("Habit");
		goalController.newGoal(goal);
		goalController.addHabit(goal, habit);
		window.list("goalList").selectItem(0);
		window.list("habitList").selectItem(0);
		window.button(JButtonMatcher.withText("Decr. counter")).click();

		pause(new Condition("Error label to contain text") {
			@Override
			public boolean test() {
				return !window.label("errorMessageLabel").text().trim().isEmpty();
			}
		}, timeout(TIMEOUT));
		assertThat(window.list("habitList").contents()).containsExactly("Habit - 0");
		window.label("errorMessageLabel").requireText("You can't decrement a counter equal to zero!");
	}

}
