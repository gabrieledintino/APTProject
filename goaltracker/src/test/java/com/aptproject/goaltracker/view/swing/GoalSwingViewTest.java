package com.aptproject.goaltracker.view.swing;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.Arrays;
import javax.swing.DefaultListModel;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.aptproject.goaltracker.controller.GoalController;
import com.aptproject.goaltracker.model.Goal;
import com.aptproject.goaltracker.model.Habit;

@RunWith(GUITestRunner.class)
public class GoalSwingViewTest extends AssertJSwingJUnitTestCase {

	private FrameFixture window;

	private GoalSwingView goalSwingView;
	
	@Mock
	private GoalController goalController;

	private AutoCloseable closeable;
	
	private static final int TIMEOUT = 5000;

	@Override
	protected void onSetUp() {
		closeable = MockitoAnnotations.openMocks(this);
		GuiActionRunner.execute(() -> {
			goalSwingView = new GoalSwingView();
			goalSwingView.setGoalController(goalController);
			return goalSwingView;
		});
		window = new FrameFixture(robot(), goalSwingView);
		window.show();
	}
	
	@Override
	protected void onTearDown() throws Exception {
		closeable.close();
	}

	@Test
	@GUITest
	public void testControlsInitialStates() {
		window.label(JLabelMatcher.withText("Goal:"));
		window.textBox("goalTextBox").requireEnabled();
		window.button(JButtonMatcher.withText("Add goal")).requireDisabled();
		window.button(JButtonMatcher.withText("Remove goal")).requireDisabled();
		window.label(JLabelMatcher.withText("Habit:"));
		window.textBox("habitTextBox").requireEnabled();
		window.button(JButtonMatcher.withText("Add habit")).requireDisabled();
		window.button(JButtonMatcher.withText("Remove habit")).requireDisabled();
		window.label(JLabelMatcher.withText("Counter:"));
		window.button(JButtonMatcher.withText("Incr. counter")).requireDisabled();
		window.button(JButtonMatcher.withText("Decr. counter")).requireDisabled();
		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	@GUITest
	public void testWhenGoalIsNonEmptyAddButtonShouldBeEnabled() {
		window.textBox("goalTextBox").enterText("a");
		window.button(JButtonMatcher.withText("Add goal")).requireEnabled();
	}

	@Test
	@GUITest
	public void testWhenGoalIsBlankAddButtonShouldBeDisabled() {
		window.textBox("goalTextBox").enterText("  ");
		window.button(JButtonMatcher.withText("Add goal")).requireDisabled();
	}

	@Test
	@GUITest
	public void testDeleteGoalButtonShouldBeEnabledOnlyWhenAGoalIsSelected() {
		GuiActionRunner.execute(() -> goalSwingView.getListGoalModel().addElement(new Goal("test")));
		JButtonFixture deleteButton = window.button(JButtonMatcher.withText("Remove goal"));
		window.list("goalList").selectItem(0);
		deleteButton.requireEnabled();
		window.list("goalList").clearSelection();
		deleteButton.requireDisabled();
	}

	@Test
	@GUITest
	public void testShowAllGoalsShouldAddGoalDescriptionsToTheList() {
		Goal goal1 = new Goal("test1");
		Goal goal2 = new Goal("test2");
		goalSwingView.showAllGoals((Arrays.asList(goal1, goal2)));
		String[] listContents = window.list("goalList").contents();
		assertThat(listContents).containsExactly(goal1.toString(), goal2.toString());
	}

	@Test
	@GUITest
	public void testShowErrorShouldShowTheMessageInTheErrorLabel() {
		//GuiActionRunner.execute(() -> goalSwingView.showError("error message"));
		goalSwingView.showError("error message");
		window.label("errorMessageLabel").requireText("error message");
	}

	@Test
	@GUITest
	public void testGoalAddedShouldAddTheGoalToTheListAndResetTheErrorLabel() {
		Goal goal = new Goal("test");
		goalSwingView.goalAdded(goal);
		String[] contents = window.list("goalList").contents();
		assertThat(contents).containsExactly(goal.toString());
		window.label("errorMessageLabel").requireText(" ");

	}

	@Test
	@GUITest
	public void testGoalRemovedShouldRemoveTheGoalFromTheListAndResetTheErrorLabel() {
		Goal goal1 = new Goal("test1");
		Goal goal2 = new Goal("test2");
		GuiActionRunner.execute(() -> {
			DefaultListModel<Goal> listGoalModel = goalSwingView.getListGoalModel();
			listGoalModel.addElement(goal1);
			listGoalModel.addElement(goal2);
		});
		goalSwingView.goalRemoved(goal1);
		String[] contents = window.list("goalList").contents();
		assertThat(contents).containsExactly(goal2.toString());
		window.label("errorMessageLabel").requireText(" ");
	}
	
	@Test
	@GUITest
	public void testAddHabitButtonShouldBeEnabledOnlyWhenAGoalIsSelectedAndFieldIsNotBlank() throws InterruptedException {
		GuiActionRunner.execute(() -> goalSwingView.getListGoalModel().addElement(new Goal("test")));
		JButtonFixture addHabitButton = window.button(JButtonMatcher.withText("Add habit"));
		addHabitButton.requireDisabled();
		window.list("goalList").selectItem(0);
		addHabitButton.requireDisabled();
		window.textBox("habitTextBox").enterText(" ");
		addHabitButton.requireDisabled();
		window.textBox("habitTextBox").enterText("a");
		addHabitButton.requireEnabled();
		window.list("goalList").clearSelection();
		addHabitButton.requireDisabled();
		window.textBox("habitTextBox").setText("");
		// checking that also entering the text first and selecting the goal after works correctly
		window.textBox("habitTextBox").enterText(" ");
		addHabitButton.requireDisabled();
		window.list("goalList").selectItem(0);
		addHabitButton.requireDisabled();
		window.list("goalList").clearSelection();
		window.textBox("habitTextBox").enterText("a");
		addHabitButton.requireDisabled();
		window.list("goalList").selectItem(0);
		addHabitButton.requireEnabled();
	}
	
	@Test
	@GUITest
	public void testHabitAddedShouldAddTheHabitToTheListAndResetTheErrorLabel() {
		Goal goal = new Goal("goal");
		Habit habit = new Habit("habit");
		GuiActionRunner.execute(() -> {
			DefaultListModel<Goal> listGoalModel = goalSwingView.getListGoalModel();
			listGoalModel.addElement(goal);
		});
		window.list("goalList").selectItem(0);
		//GuiActionRunner.execute(() -> goalSwingView.habitAdded(habit));
		goalSwingView.habitAdded(habit);
		String[] contents = window.list("habitList").contents();
		assertThat(contents).containsExactly(habit.toString());
		window.label("errorMessageLabel").requireText(" ");
	}
	
	@Test
	@GUITest
	public void testRemoveHabitButtonShouldBeEnabledOnlyWhenAnHabitIsSelected() {
		Goal goal = new Goal("goal");
		Habit habit = new Habit("habit");
		goal.addHabit(habit);
		GuiActionRunner.execute(() -> {
			DefaultListModel<Goal> listGoalModel = goalSwingView.getListGoalModel();
			listGoalModel.addElement(goal);
			DefaultListModel<Habit> listHabitModel = goalSwingView.getListHabitModel();
			listHabitModel.addElement(habit);
		});
		JButtonFixture removeHabitButton = window.button(JButtonMatcher.withText("Remove habit"));
		removeHabitButton.requireDisabled();
		window.list("goalList").selectItem(0);
		removeHabitButton.requireDisabled();
		window.list("habitList").selectItem(0);
		removeHabitButton.requireEnabled();
	}
	
	@Test
	@GUITest
	public void testHabitRemovedShouldRemoveTheHabitFromTheListAndResetTheErrorLabel() {
		Goal goal = new Goal("goal");
		Habit habit = new Habit("habit");
		goal.addHabit(habit);
		GuiActionRunner.execute(() -> {
			DefaultListModel<Goal> listGoalModel = goalSwingView.getListGoalModel();
			listGoalModel.addElement(goal);
			DefaultListModel<Habit> listHabitModel = goalSwingView.getListHabitModel();
			listHabitModel.addElement(habit);
		});
		window.list("goalList").selectItem(0);
		window.list("habitList").selectItem(0);
		goalSwingView.habitRemoved(habit);
		String[] contents = window.list("habitList").contents();
		assertThat(contents).isEmpty();
		window.label("errorMessageLabel").requireText(" ");
	}
	
	@Test
	@GUITest
	public void testIncrementAndDecrementCounterButtonsShouldBeEnabledWhenHabitSelected() {
		Goal goal = new Goal("goal");
		Habit habit = new Habit("habit");
		goal.addHabit(habit);
		GuiActionRunner.execute(() -> {
			DefaultListModel<Goal> listGoalModel = goalSwingView.getListGoalModel();
			listGoalModel.addElement(goal);
			DefaultListModel<Habit> listHabitModel = goalSwingView.getListHabitModel();
			listHabitModel.addElement(habit);
		});
		JButtonFixture incrementCounterButton = window.button(JButtonMatcher.withText("Incr. counter"));
		JButtonFixture decrementCounterButton = window.button(JButtonMatcher.withText("Decr. counter"));
		incrementCounterButton.requireDisabled();
		decrementCounterButton.requireDisabled();
		window.list("goalList").selectItem(0);
		window.list("habitList").selectItem(0);
		incrementCounterButton.requireEnabled();
		decrementCounterButton.requireEnabled();
	}
	
	@Test
	@GUITest
	public void testCounterUpdatedShouldUpdateTheHabitListAndResetTheErrorLabel() {
		Goal goal = new Goal("goal");
		Habit habit = new Habit("habit");
		goal.addHabit(habit);
		GuiActionRunner.execute(() -> {
			DefaultListModel<Goal> listGoalModel = goalSwingView.getListGoalModel();
			listGoalModel.addElement(goal);
			DefaultListModel<Habit> listHabitModel = goalSwingView.getListHabitModel();
			listHabitModel.addElement(habit);
		});
		window.list("goalList").selectItem(0);
		window.list("habitList").selectItem(0);
		habit.setCounter(1);
		goalSwingView.counterUpdated(habit);
		String[] contents = window.list("habitList").contents();
		assertThat(contents).containsExactly(habit.toString());
		habit.setCounter(0);
		goalSwingView.counterUpdated(habit);
		contents = window.list("habitList").contents();
		assertThat(contents).containsExactly(habit.toString());
		window.label("errorMessageLabel").requireText(" ");
	}
	
	@Test
	@GUITest
	public void testGoalDeletedShouldRemoveAlsoTheHabitFromTheListAndResetTheErrorLabel() {
		Goal goal = new Goal("goal");
		Habit habit = new Habit("habit");
		goal.addHabit(habit);
		GuiActionRunner.execute(() -> {
			DefaultListModel<Goal> listGoalModel = goalSwingView.getListGoalModel();
			listGoalModel.addElement(goal);
			DefaultListModel<Habit> listHabitModel = goalSwingView.getListHabitModel();
			listHabitModel.addElement(habit);
		});
		window.list("goalList").selectItem(0);
		goalSwingView.goalRemoved(goal);
		String[] contents = window.list("habitList").contents();
		assertThat(contents).isEmpty();
		window.label("errorMessageLabel").requireText(" ");
	}
	
	@Test
	@GUITest
	public void testSelectingAGoalShouldShowItsHabits() {
		Goal goal1 = new Goal("goal1");
		Goal goal2 = new Goal("goal2");
		Habit habit = new Habit("habit");
		goal2.addHabit(habit);
		GuiActionRunner.execute(() -> {
			DefaultListModel<Goal> listGoalModel = goalSwingView.getListGoalModel();
			listGoalModel.addElement(goal1);
			listGoalModel.addElement(goal2);
		});
		window.list("goalList").selectItem(0);
		String[] contents = window.list("habitList").contents();
		assertThat(contents).isEmpty();;
		window.list("goalList").selectItem(1);
		contents = window.list("habitList").contents();
		assertThat(contents).containsExactly(habit.toString());
	}
	
	@Test
	@GUITest
	public void testAddGoalShouldDelegateToGoalControllerNewGoal() {
		window.textBox("goalTextBox").enterText("Goal");
		window.button(JButtonMatcher.withText("Add goal")).click();
		verify(goalController, timeout(TIMEOUT)).newGoal(new Goal("Goal"));
	}
	
	@Test
	@GUITest
	public void testRemoveGoalShouldDelegateToGoalControllerDeleteGoal() {
		Goal goal1 = new Goal("test1");
		Goal goal2 = new Goal("test2");
		GuiActionRunner.execute(() -> {
			DefaultListModel<Goal> listGoalModel = goalSwingView.getListGoalModel();
			listGoalModel.addElement(goal1);
			listGoalModel.addElement(goal2);
		});
		window.list("goalList").selectItem(1);
		window.button(JButtonMatcher.withText("Remove goal")).click();
		verify(goalController, timeout(TIMEOUT)).deleteGoal(goal2);
	}
	
	@Test
	@GUITest
	public void testAddHabitShouldDelegateToGoalControllerAddHabit() {
		Goal goal1 = new Goal("test1");
		Goal goal2 = new Goal("test2");
		GuiActionRunner.execute(() -> {
			DefaultListModel<Goal> listGoalModel = goalSwingView.getListGoalModel();
			listGoalModel.addElement(goal1);
			listGoalModel.addElement(goal2);
		});
		window.list("goalList").selectItem(1);
		window.textBox("habitTextBox").enterText("Habit");
		window.button(JButtonMatcher.withText("Add habit")).click();
		verify(goalController, timeout(TIMEOUT)).addHabit(goal2, new Habit("Habit"));
	}
	
	@Test
	@GUITest
	public void testRemoveHabitShouldDelegateToGoalControllerRemoveHabit() {
		Goal goal = new Goal("goal");
		Habit habit = new Habit("habit");
		goal.addHabit(habit);
		GuiActionRunner.execute(() -> {
			DefaultListModel<Goal> listGoalModel = goalSwingView.getListGoalModel();
			listGoalModel.addElement(goal);
			DefaultListModel<Habit> listHabitModel = goalSwingView.getListHabitModel();
			listHabitModel.addElement(habit);
		});
		window.list("goalList").selectItem(0);
		window.list("habitList").selectItem(0);
		window.button(JButtonMatcher.withText("Remove habit")).click();
		verify(goalController, timeout(TIMEOUT)).removeHabit(goal, habit);
	}
	
	@Test
	@GUITest
	public void testIncrementCounterShouldDelegateToGoalControllerIncrementCounter() {
		Goal goal = new Goal("goal");
		Habit habit = new Habit("habit");
		goal.addHabit(habit);
		GuiActionRunner.execute(() -> {
			DefaultListModel<Goal> listGoalModel = goalSwingView.getListGoalModel();
			listGoalModel.addElement(goal);
			DefaultListModel<Habit> listHabitModel = goalSwingView.getListHabitModel();
			listHabitModel.addElement(habit);
		});
		window.list("goalList").selectItem(0);
		window.list("habitList").selectItem(0);
		window.button(JButtonMatcher.withText("Incr. counter")).click();
		verify(goalController, timeout(TIMEOUT)).incrementCounter(habit);;
	}
	
	@Test
	@GUITest
	public void testDecrementCounterShouldDelegateToGoalControllerDecrementCounter() {
		Goal goal = new Goal("goal");
		Habit habit = new Habit("habit");
		goal.addHabit(habit);
		habit.setCounter(1);
		GuiActionRunner.execute(() -> {
			DefaultListModel<Goal> listGoalModel = goalSwingView.getListGoalModel();
			listGoalModel.addElement(goal);
			DefaultListModel<Habit> listHabitModel = goalSwingView.getListHabitModel();
			listHabitModel.addElement(habit);
		});
		window.list("goalList").selectItem(0);
		window.list("habitList").selectItem(0);
		window.button(JButtonMatcher.withText("Decr. counter")).click();
		verify(goalController, timeout(TIMEOUT)).decrementCounter(habit);;
	}

}