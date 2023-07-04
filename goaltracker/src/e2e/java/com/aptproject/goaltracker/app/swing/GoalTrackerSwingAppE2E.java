package com.aptproject.goaltracker.app.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.*;
import static org.awaitility.Awaitility.await;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.JFrame;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.aptproject.goaltracker.model.Goal;
import com.aptproject.goaltracker.model.Habit;
import com.aptproject.goaltracker.model.HabitId;

@RunWith(GUITestRunner.class)
public class GoalTrackerSwingAppE2E extends AssertJSwingJUnitTestCase {

	private static final String APPLICATION_PERSISTENCE_UNIT = "E2E";
	private static final int TIMEOUT_SECONDS = 2;
	private static final String GOAL_FIXTURE_2_NAME = "Second Goal";
	private static final String GOAL_FIXTURE_1_NAME = "First Goal";
	private static final String EXISTING_HABIT_1_NAME = "First Habit";
	private static final String EXISTING_HABIT_2_NAME = "Second Habit";

	private FrameFixture window;
	private EntityManager entityManager;
	private EntityManagerFactory emf;

	@Override
	protected void onSetUp() throws Exception {
		emf = Persistence.createEntityManagerFactory("PersistenceUnit");
		Goal goal1 = new Goal(GOAL_FIXTURE_1_NAME);
		addGoalToDb(goal1);
		Goal goal2 = new Goal(GOAL_FIXTURE_2_NAME);
		Habit habit1 = new Habit(EXISTING_HABIT_1_NAME);
		Habit habit2 = new Habit(EXISTING_HABIT_2_NAME);
		habit2.setCounter(5);
		goal2.addHabit(habit1);
		habit1.setGoal(goal2);
		goal2.addHabit(habit2);
		habit2.setGoal(goal2);
		addGoalToDb(goal2);
		
		application("com.aptproject.goaltracker.app.swing.GoalTrackerSwingApp")
			.withArgs(
					"--persistence-unit=" + APPLICATION_PERSISTENCE_UNIT)
			.start();
		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return "Goal View".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(robot());
	}

	@Test
	@GUITest
	public void testOnStartAllDatabaseElementsAreShown() {
		assertThat(window.list("goalList").contents()).anySatisfy(e -> assertThat(e).contains(GOAL_FIXTURE_1_NAME))
				.anySatisfy(e -> assertThat(e).contains(GOAL_FIXTURE_2_NAME));
	}

	@Test
	@GUITest
	public void testAddGoalSuccess() {
		window.textBox("goalTextBox").enterText("Added");
		window.button(JButtonMatcher.withText("Add goal")).click();

		await().atMost(5, TimeUnit.SECONDS).untilAsserted(
				() -> assertThat(window.list("goalList").contents()).anySatisfy(e -> assertThat(e).contains("Added")));
	}

	@Test
	@GUITest
	public void testAddGoalError() {
		window.textBox("goalTextBox").enterText(GOAL_FIXTURE_1_NAME);
		window.button(JButtonMatcher.withText("Add goal")).click();

		await().atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS).untilAsserted(
				() -> assertThat(window.label("errorMessageLabel").text()).contains(GOAL_FIXTURE_1_NAME));
	}

	@Test
	@GUITest
	public void testDeleteGoalSuccess() {
		window.list("goalList").selectItem(Pattern.compile(".*" + GOAL_FIXTURE_1_NAME + ".*"));
		window.button(JButtonMatcher.withText("Remove goal")).click();

		await().atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS).untilAsserted(
				() -> assertThat(window.list("goalList").contents()).noneMatch(e -> e.contains(GOAL_FIXTURE_1_NAME)));
	}

	@Test
	@GUITest
	public void testDeleteGoalError() {
		window.list("goalList").selectItem(Pattern.compile(".*" + GOAL_FIXTURE_1_NAME + ".*"));
		removeGoalFromDb(new Goal(GOAL_FIXTURE_1_NAME));
		window.button(JButtonMatcher.withText("Remove goal")).click();

		await().atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS).untilAsserted(
				() -> assertThat(window.label("errorMessageLabel").text()).contains(GOAL_FIXTURE_1_NAME));
	}

	@Test
	@GUITest
	public void testAddHabitSuccess() {
		window.list("goalList").selectItem(Pattern.compile(".*" + GOAL_FIXTURE_1_NAME + ".*"));
		window.textBox("habitTextBox").enterText("Habit 1");
		window.button(JButtonMatcher.withText("Add habit")).click();

		await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> assertThat(window.list("habitList").contents())
				.anySatisfy(e -> assertThat(e).contains("Habit 1")));
	}

	@Test
	@GUITest
	public void testAddHabitError() {
		window.list("goalList").selectItem(Pattern.compile(".*" + GOAL_FIXTURE_2_NAME + ".*"));
		// window.textBox("goalTextBox").enterText("Added");
		window.textBox("habitTextBox").enterText(EXISTING_HABIT_1_NAME);
		window.button(JButtonMatcher.withText("Add habit")).click();

		await().atMost(5, TimeUnit.SECONDS).untilAsserted(
				() -> assertThat(window.label("errorMessageLabel").text()).contains(EXISTING_HABIT_1_NAME));
	}

	@Test
	@GUITest
	public void testDeleteHabitSuccess() {
		window.list("goalList").selectItem(Pattern.compile(".*" + GOAL_FIXTURE_2_NAME + ".*"));
		window.list("habitList").selectItem(Pattern.compile(".*" + EXISTING_HABIT_1_NAME + ".*"));
		window.button(JButtonMatcher.withText("Remove habit")).click();

		await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> assertThat(window.list("habitList").contents())
				.anySatisfy(e -> assertThat(e).contains("Second Habit")));
	}

	@Test
	@GUITest
	public void testDeleteHabitError() {
		window.list("goalList").selectItem(Pattern.compile(".*" + GOAL_FIXTURE_2_NAME + ".*"));
		window.list("habitList").selectItem(Pattern.compile(".*" + EXISTING_HABIT_1_NAME + ".*"));
		Habit habit = new Habit(EXISTING_HABIT_1_NAME);
		habit.setGoal(new Goal(GOAL_FIXTURE_2_NAME));
		removeHabitFromDb(new Goal(GOAL_FIXTURE_2_NAME), habit);
		window.button(JButtonMatcher.withText("Remove habit")).click();

		await().atMost(5, TimeUnit.SECONDS).untilAsserted(
				() -> assertThat(window.label("errorMessageLabel").text()).contains(EXISTING_HABIT_1_NAME));
	}

	@Test
	@GUITest
	public void testIncreaseCounterSuccess() {
		window.list("goalList").selectItem(Pattern.compile(".*" + GOAL_FIXTURE_2_NAME + ".*"));
		window.list("habitList").selectItem(Pattern.compile(".*" + EXISTING_HABIT_1_NAME + ".*"));
		window.button(JButtonMatcher.withText("Incr. counter")).click();

		await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> assertThat(window.list("habitList").contents())
				.anySatisfy(e -> assertThat(e).contains(EXISTING_HABIT_1_NAME + " 1")));
	}

	@Test
	@GUITest
	public void testDecreaseCounterSuccess() {
		window.list("goalList").selectItem(Pattern.compile(".*" + GOAL_FIXTURE_2_NAME + ".*"));
		window.list("goalList").selectItem(1);
		window.list("habitList").selectItem(Pattern.compile(".*" + EXISTING_HABIT_2_NAME + ".*"));
		window.button(JButtonMatcher.withText("Decr. counter")).click();

		await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> assertThat(window.list("habitList").contents())
				.anySatisfy(e -> assertThat(e).contains(EXISTING_HABIT_2_NAME + " 4")));
	}

	@Test
	@GUITest
	public void testDecreaseCounterError() {
		window.list("goalList").selectItem(Pattern.compile(".*" + GOAL_FIXTURE_2_NAME + ".*"));
		window.list("habitList").selectItem(Pattern.compile(".*" + EXISTING_HABIT_1_NAME + ".*"));
		window.button(JButtonMatcher.withText("Decr. counter")).click();

		await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> assertThat(window.label("errorMessageLabel").text())
				.contains("You can't decrement a counter equal to zero!"));
	}

	private void addGoalToDb(Goal goal) {
		entityManager = emf.createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.persist(goal);
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	private void removeGoalFromDb(Goal goal) {
		entityManager = emf.createEntityManager();
		Goal existing = entityManager.find(Goal.class, goal.getName());
		entityManager.getTransaction().begin();
		entityManager.remove(existing);
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	private void removeHabitFromDb(Goal goal, Habit habit) {
		entityManager = emf.createEntityManager();
		HabitId habitId = new HabitId(habit.getName(), habit.getGoal());
		Goal existingGoal = entityManager.find(Goal.class, goal.getName());
		Habit existingHabit = entityManager.find(Habit.class, habitId);
		entityManager.getTransaction().begin();
		existingGoal.removeHabit(existingHabit);
		entityManager.merge(existingGoal);
		entityManager.flush();
		entityManager.getTransaction().commit();
		entityManager.close();
	}
}
