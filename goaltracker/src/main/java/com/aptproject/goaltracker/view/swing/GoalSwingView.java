package com.aptproject.goaltracker.view.swing;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import com.aptproject.goaltracker.model.Goal;
import com.aptproject.goaltracker.model.Habit;
import com.aptproject.goaltracker.view.GoalView;
import com.aptproject.goaltracker.controller.GoalController;

public class GoalSwingView extends JFrame implements GoalView {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtGoal;
	private JTextField txtHabit;
	private JLabel lblError;
	private JList<Goal> listGoals;
	private DefaultListModel<Goal> listGoalsModel;
	private JList<Habit> listHabits;
	private DefaultListModel<Habit> listHabitsModel;

	private GoalController goalController;

	public void setGoalController(GoalController goalController) {
		this.goalController = goalController;
	}

	DefaultListModel<Goal> getListGoalModel() {
		return listGoalsModel;
	}

	DefaultListModel<Habit> getListHabitModel() {
		return listHabitsModel;
	}

	/**
	 * Create the frame.
	 */
	public GoalSwingView() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 100, 0, 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 1.0, 1.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		JScrollPane goalScrollPane = new JScrollPane();
		GridBagConstraints gbc_goalScrollPane = new GridBagConstraints();
		gbc_goalScrollPane.gridwidth = 2;
		gbc_goalScrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_goalScrollPane.fill = GridBagConstraints.BOTH;
		gbc_goalScrollPane.gridx = 0;
		gbc_goalScrollPane.gridy = 1;
		contentPane.add(goalScrollPane, gbc_goalScrollPane);

		JScrollPane habitScrollPane = new JScrollPane();
		GridBagConstraints gbc_habitScrollPane = new GridBagConstraints();
		gbc_habitScrollPane.gridwidth = 2;
		gbc_habitScrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_habitScrollPane.fill = GridBagConstraints.BOTH;
		gbc_habitScrollPane.gridx = 2;
		gbc_habitScrollPane.gridy = 1;
		contentPane.add(habitScrollPane, gbc_habitScrollPane);

		JLabel lblGoal = new JLabel("Goal:");
		GridBagConstraints gbc_lblGoal = new GridBagConstraints();
		gbc_lblGoal.insets = new Insets(0, 0, 5, 5);
		gbc_lblGoal.gridx = 0;
		gbc_lblGoal.gridy = 2;
		contentPane.add(lblGoal, gbc_lblGoal);

		txtGoal = new JTextField();
		GridBagConstraints gbc_txtGoal = new GridBagConstraints();
		gbc_txtGoal.insets = new Insets(0, 0, 5, 5);
		gbc_txtGoal.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtGoal.gridx = 1;
		gbc_txtGoal.gridy = 2;
		contentPane.add(txtGoal, gbc_txtGoal);
		txtGoal.setColumns(10);
		txtGoal.setName("goalTextBox");

		JButton btnAddGoal = new JButton("Add goal");
		btnAddGoal
				.addActionListener(e -> new Thread(() -> goalController.newGoal(new Goal(txtGoal.getText()))).start());
		btnAddGoal.setEnabled(false);
		GridBagConstraints gbc_btnAddGoal = new GridBagConstraints();
		gbc_btnAddGoal.insets = new Insets(0, 0, 5, 5);
		gbc_btnAddGoal.gridx = 2;
		gbc_btnAddGoal.gridy = 2;
		contentPane.add(btnAddGoal, gbc_btnAddGoal);

		JButton btnRemoveGoal = new JButton("Remove goal");
		btnRemoveGoal.addActionListener(
				e -> new Thread(() -> goalController.deleteGoal(listGoalsModel.elementAt(listGoals.getSelectedIndex())))
						.start());
		btnRemoveGoal.setEnabled(false);
		GridBagConstraints gbc_btnRemoveGoal = new GridBagConstraints();
		gbc_btnRemoveGoal.insets = new Insets(0, 0, 5, 0);
		gbc_btnRemoveGoal.gridx = 3;
		gbc_btnRemoveGoal.gridy = 2;
		contentPane.add(btnRemoveGoal, gbc_btnRemoveGoal);

		JLabel lblHabit = new JLabel("Habit:");
		GridBagConstraints gbc_lblHabit = new GridBagConstraints();
		gbc_lblHabit.insets = new Insets(0, 0, 5, 5);
		gbc_lblHabit.gridx = 0;
		gbc_lblHabit.gridy = 3;
		contentPane.add(lblHabit, gbc_lblHabit);

		txtHabit = new JTextField();
		GridBagConstraints gbc_txtHabit = new GridBagConstraints();
		gbc_txtHabit.insets = new Insets(0, 0, 5, 5);
		gbc_txtHabit.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtHabit.gridx = 1;
		gbc_txtHabit.gridy = 3;
		contentPane.add(txtHabit, gbc_txtHabit);
		txtHabit.setColumns(10);
		txtHabit.setName("habitTextBox");

		JButton btnAddHabit = new JButton("Add habit");
		btnAddHabit.addActionListener(
				e -> new Thread(() -> goalController.addHabit(listGoalsModel.elementAt(listGoals.getSelectedIndex()),
						new Habit(txtHabit.getText()))).start());
		btnAddHabit.setEnabled(false);
		GridBagConstraints gbc_btnAddHabit = new GridBagConstraints();
		gbc_btnAddHabit.insets = new Insets(0, 0, 5, 5);
		gbc_btnAddHabit.gridx = 2;
		gbc_btnAddHabit.gridy = 3;
		contentPane.add(btnAddHabit, gbc_btnAddHabit);

		JButton btnRemoveHabit = new JButton("Remove habit");
		btnRemoveHabit.addActionListener(
				e -> new Thread(() -> goalController.removeHabit(listGoalsModel.elementAt(listGoals.getSelectedIndex()),
						listHabitsModel.elementAt(listHabits.getSelectedIndex()))).start());
		btnRemoveHabit.setEnabled(false);
		GridBagConstraints gbc_btnRemoveHabit = new GridBagConstraints();
		gbc_btnRemoveHabit.insets = new Insets(0, 0, 5, 0);
		gbc_btnRemoveHabit.gridx = 3;
		gbc_btnRemoveHabit.gridy = 3;
		contentPane.add(btnRemoveHabit, gbc_btnRemoveHabit);

		JLabel lblCounter = new JLabel("Counter:");
		GridBagConstraints gbc_lblCounter = new GridBagConstraints();
		gbc_lblCounter.gridwidth = 2;
		gbc_lblCounter.insets = new Insets(0, 0, 5, 5);
		gbc_lblCounter.gridx = 0;
		gbc_lblCounter.gridy = 4;
		contentPane.add(lblCounter, gbc_lblCounter);

		JButton btnIncreaseCounter = new JButton("Incr. counter");
		btnIncreaseCounter.addActionListener(e -> new Thread(
				() -> goalController.incrementCounter(listHabitsModel.getElementAt(listHabits.getSelectedIndex())))
				.start());
		btnIncreaseCounter.setEnabled(false);
		GridBagConstraints gbc_btnIncreaseCounter = new GridBagConstraints();
		gbc_btnIncreaseCounter.insets = new Insets(0, 0, 5, 5);
		gbc_btnIncreaseCounter.gridx = 2;
		gbc_btnIncreaseCounter.gridy = 4;
		contentPane.add(btnIncreaseCounter, gbc_btnIncreaseCounter);

		JButton btnDecreaseCounter = new JButton("Decr. counter");
		btnDecreaseCounter.addActionListener(e -> new Thread(
				() -> goalController.decrementCounter(listHabitsModel.getElementAt(listHabits.getSelectedIndex())))
				.start());
		btnDecreaseCounter.setEnabled(false);
		GridBagConstraints gbc_btnDecreaseCounter = new GridBagConstraints();
		gbc_btnDecreaseCounter.insets = new Insets(0, 0, 5, 5);
		gbc_btnDecreaseCounter.gridx = 3;
		gbc_btnDecreaseCounter.gridy = 4;
		contentPane.add(btnDecreaseCounter, gbc_btnDecreaseCounter);

		lblError = new JLabel(" ");
		GridBagConstraints gbc_lblError = new GridBagConstraints();
		gbc_lblError.gridwidth = 4;
		gbc_lblError.insets = new Insets(0, 0, 0, 5);
		gbc_lblError.gridx = 0;
		gbc_lblError.gridy = 5;
		contentPane.add(lblError, gbc_lblError);
		lblError.setName("errorMessageLabel");
		lblError.setForeground(Color.RED);

		KeyAdapter addGoalButtonEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				btnAddGoal.setEnabled(!txtGoal.getText().trim().isEmpty());
			}
		};
		txtGoal.addKeyListener(addGoalButtonEnabler);

		KeyAdapter addHabitButtonEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				btnAddHabit.setEnabled(!txtHabit.getText().trim().isEmpty() && listGoals.getSelectedIndex() != -1);
			}
		};
		txtHabit.addKeyListener(addHabitButtonEnabler);

		listGoalsModel = new DefaultListModel<>();
		listGoals = new JList<>(listGoalsModel);
		ListSelectionListener listGoalSelectionListener = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int selectedIndex = listGoals.getSelectedIndex();
				btnRemoveGoal.setEnabled(listGoals.getSelectedIndex() != -1);
				btnAddHabit.setEnabled(!txtHabit.getText().trim().isEmpty() && selectedIndex != -1);
				if (selectedIndex != -1) {
					listHabitsModel.clear();
					Goal selectedGoal = listGoalsModel.get(selectedIndex);
					listHabitsModel.addAll(selectedGoal.getHabits());
				}
			}
		};
		listGoals.addListSelectionListener(listGoalSelectionListener);
		listGoals.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listGoals.setName("goalList");
		goalScrollPane.setViewportView(listGoals);

		listHabitsModel = new DefaultListModel<>();
		listHabits = new JList<>(listHabitsModel);
		ListSelectionListener listHabitSelectionListener = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				btnRemoveHabit.setEnabled(listHabits.getSelectedIndex() != -1);
				btnIncreaseCounter.setEnabled(listHabits.getSelectedIndex() != -1);
				btnDecreaseCounter.setEnabled(listHabits.getSelectedIndex() != -1);
			}
		};
		listHabits.addListSelectionListener(listHabitSelectionListener);
		listHabits.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listHabits.setName("habitList");
		habitScrollPane.setViewportView(listHabits);
	}

	@Override
	public void showAllGoals(List<Goal> goals) {
		SwingUtilities.invokeLater(() -> goals.stream().forEach(listGoalsModel::addElement));
	}

	@Override
	public void showError(String message) {
		SwingUtilities.invokeLater(() -> lblError.setText(message));
	}

	@Override
	public void goalAdded(Goal goal) {
		SwingUtilities.invokeLater(() -> {
			listGoalsModel.addElement(goal);
			resetErrorLabel();
		});
	}

	@Override
	public void goalRemoved(Goal goal) {
		SwingUtilities.invokeLater(() -> {
			listGoalsModel.removeElement(goal);
			listHabitsModel.clear();
			resetErrorLabel();
		});
	}

	private void resetErrorLabel() {
		lblError.setText(" ");
	}

	@Override
	public void habitAdded(Habit habit) {
		SwingUtilities.invokeLater(() -> {
			listHabitsModel.addElement(habit);
			resetErrorLabel();
		});
	}

	@Override
	public void habitRemoved(Habit habit) {
		SwingUtilities.invokeLater(() -> {
			listHabitsModel.removeElement(habit);
			resetErrorLabel();
		});
	}

	@Override
	public void counterUpdated(Habit habit) {
		SwingUtilities.invokeLater(() -> {
			listHabitsModel.clear();
			Goal selectedGoal = listGoalsModel.get(listGoals.getSelectedIndex());
			listHabitsModel.addAll(selectedGoal.getHabits());
			resetErrorLabel();
		});
	}

}
