package com.aptproject.goaltracker.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.concurrent.TimeUnit;

import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.aptproject.goaltracker.controller.GoalController;
import com.aptproject.goaltracker.model.Goal;
import com.aptproject.goaltracker.model.Habit;
import com.aptproject.goaltracker.repository.ModelRepository;
import com.aptproject.goaltracker.repository.exception.GoalExistsException;
import com.aptproject.goaltracker.repository.postgres.PostgresModelRepository;

@RunWith(GUITestRunner.class)
public class ModelViewControllerIT extends AssertJSwingJUnitTestCase {

	private static final int TIMEOUT_SECONDS = 5;
	private GoalSwingView goalSwingView;
	private FrameFixture window;
	private GoalController goalController;
	private ModelRepository modelRepository;

	@Override
	protected void onSetUp() {
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
	public void testAddGoal() {
		window.textBox("goalTextBox").enterText("Goal");
		window.button(JButtonMatcher.withText("Add goal")).click();

		await().atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(modelRepository.findGoalByName("Goal")).isEqualTo(new Goal("Goal")));
	}

	@Test
	public void testDeleteGoal() throws GoalExistsException {
		Goal goal = new Goal("Goal");
		modelRepository.addGoal(goal);
		goalController.allGoals();
		window.list("goalList").selectItem(0);
		window.button(JButtonMatcher.withText("Remove goal")).click();

		await().atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(modelRepository.findGoalByName("Goal")).isNull());
	}

	@Test
	public void testAddHabit() throws InterruptedException, GoalExistsException {
		Goal goal = new Goal("Goal");
		Habit habit = new Habit("Habit");
		habit.setGoal(goal);
		modelRepository.addGoal(goal);
		goalController.allGoals();
		window.list("goalList").selectItem(0);
		window.textBox("habitTextBox").enterText("Habit");
		window.button(JButtonMatcher.withText("Add habit")).click();

		await().atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS).untilAsserted(
				() -> assertThat(modelRepository.findGoalByName("Goal").getHabits().get(0)).isEqualTo(habit));
	}

	@Test
	public void testDeleteHabit() {
		Goal goal = new Goal("Goal");
		Habit habit = new Habit("Habit");
		goalController.newGoal(goal);
		goalController.addHabit(goal, habit);
		window.list("goalList").selectItem(0);
		window.list("habitList").selectItem(0);
		window.button(JButtonMatcher.withText("Remove habit")).click();
		await().atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(modelRepository.findGoalByName("Goal").getHabits()).isEmpty());
	}

	@Test
	public void testIncrementCounter() {
		Goal goal = new Goal("Goal");
		Habit habit = new Habit("Habit");
		goalController.newGoal(goal);
		goalController.addHabit(goal, habit);
		window.list("goalList").selectItem(0);
		window.list("habitList").selectItem(0);
		window.button(JButtonMatcher.withText("Incr. counter")).click();

		await().atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS).untilAsserted(
				() -> assertThat(modelRepository.findGoalByName("Goal").getHabits().get(0).getCounter()).isEqualTo(1));
	}

	@Test
	public void testDecrementCounter() {
		Goal goal = new Goal("Goal");
		Habit habit = new Habit("Habit");
		habit.setCounter(TIMEOUT_SECONDS);
		goalController.newGoal(goal);
		goalController.addHabit(goal, habit);
		window.list("goalList").selectItem(0);
		window.list("habitList").selectItem(0);
		window.button(JButtonMatcher.withText("Decr. counter")).click();

		await().atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS).untilAsserted(
				() -> assertThat(modelRepository.findGoalByName("Goal").getHabits().get(0).getCounter()).isEqualTo(4));
	}
}
