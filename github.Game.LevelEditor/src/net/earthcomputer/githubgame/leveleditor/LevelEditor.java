package net.earthcomputer.githubgame.leveleditor;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

public class LevelEditor {

	public static final int CURRENT_LEVEL_VERSION = 0;

	public static Level editingLevel;
	public static File editingFile;

	public static int scrollX;
	public static int scrollY;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// Non-fatal error
			e.printStackTrace();
		}

		if (args.length != 0) {
			editingFile = new File(args[0]);
		}

		JFrame window = new JFrame("Github Game Level Editor");
		window.setContentPane(new JPanel() {
			private static final long serialVersionUID = -4406165240495985709L;

			{
				InputMap inputs = getInputMap();
				ActionMap actions = getActionMap();

				inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK), "open");
				actions.put("open", new AbstractAction() {
					private static final long serialVersionUID = -4607026652303892477L;

					@Override
					public void actionPerformed(ActionEvent e) {
						userPressedOpen();
					}

				});

				inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "save");
				actions.put("save", new AbstractAction() {
					private static final long serialVersionUID = 7439340686206466441L;

					@Override
					public void actionPerformed(ActionEvent e) {
						userPressedSave();
					}

				});

				inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.SHIFT_DOWN_MASK), "saveAs");
				actions.put("saveAs", new AbstractAction() {
					private static final long serialVersionUID = -4986446487972490592L;

					@Override
					public void actionPerformed(ActionEvent e) {
						userPressedSaveAs();
					}

				});
			}

			@Override
			public void paintComponent(Graphics g) {
				synchronized (editingLevel.objects) {
					for (GameObject object : editingLevel.objects) {
						object.render(scrollX, scrollY, g);
					}
				}
			}
		});
	}

	public static void userPressedOpen() {
		if (JOptionPane.showConfirmDialog(null, "Do you want to continue? Make sure you have saved your work.",
				"Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION)
			return;

		File openedFile = showOpenDialog();
		if (openedFile == null)
			return;

		try {
			open(new BufferedInputStream(new FileInputStream(openedFile)));
		} catch (WrongFormatException e) {
			JOptionPane.showMessageDialog(null, "Input file has invalid format:\n" + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error reading file:\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void userPressedSave() {
		if (editingFile == null) {
			userPressedSaveAs();
			return;
		}

		try {
			if (editingFile.getParentFile() != null)
				editingFile.getParentFile().mkdirs();
			editingFile.createNewFile();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Unable to create new file:\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			BufferedOutputStream bufferedOutput = new BufferedOutputStream(new FileOutputStream(editingFile));
			save(bufferedOutput);
			bufferedOutput.flush();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error saving to file:\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void userPressedSaveAs() {

	}

	public static void open(InputStream input) throws WrongFormatException, IOException {
		DataInputStream dataInput = new DataInputStream(input);
		try {
			if (dataInput.readInt() != 0x4748474D) {
				throw new WrongFormatException("Wrong magic number");
			}

			int version = dataInput.readUnsignedByte();
			if (version > CURRENT_LEVEL_VERSION)
				throw new WrongFormatException("Cannot open a future level format version");

			String levelName = dataInput.readUTF();
			if (levelName.length() == 0)
				throw new WrongFormatException("Cannot have a level name of length 0");

			int levelWidth = dataInput.readUnsignedShort();
			if (levelWidth == 0)
				throw new WrongFormatException("Cannot have a level width of 0");

			int levelHeight = dataInput.readUnsignedShort();
			if (levelHeight == 0)
				throw new WrongFormatException("Cannot have a level height of 0");

			int objectCount = dataInput.readUnsignedShort();
			List<GameObject> objects = new ArrayList<GameObject>(objectCount);
			for (int i = 0; i < objectCount; i++) {
				double x = dataInput.readDouble();
				double y = dataInput.readDouble();
				int id = dataInput.readUnsignedShort();
				objects.add(new GameObject(x, y, id));
			}

			editingLevel = new Level(levelName, levelWidth, levelHeight, objects);
		} catch (EOFException e) {
			throw new WrongFormatException("The end of the file has been reached unexpectedly");
		}
	}

	public static void save(OutputStream output) throws IOException {
		DataOutputStream dataOutput = new DataOutputStream(output);

		dataOutput.writeInt(0x4748474D);
		dataOutput.write(CURRENT_LEVEL_VERSION);

		dataOutput.writeUTF(editingLevel.name);
		dataOutput.writeChar(editingLevel.levelWidth);
		dataOutput.writeChar(editingLevel.levelHeight);

		dataOutput.writeChar(editingLevel.objects.size());
		for (GameObject object : editingLevel.objects) {
			dataOutput.writeDouble(object.x);
			dataOutput.writeDouble(object.y);
			dataOutput.writeChar(object.id);
		}
	}

	public static File showOpenDialog() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("Github Game Levels", "gglevel"));
		fileChooser.setDialogTitle("Select level to edit");

		if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
			return null;
		File selectedFile = fileChooser.getSelectedFile();
		if (selectedFile == null || !selectedFile.isFile())
			return null;
		else
			return selectedFile;
	}

	public static File showSaveDialog() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("Github Game Levels", "gglevel"));
		fileChooser.setDialogTitle("Set location to save level");

		if (fileChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION)
			return null;
		File selectedFile = fileChooser.getSelectedFile();
		if (selectedFile == null)
			return null;

		if (selectedFile.exists()) {
			int result = JOptionPane.showConfirmDialog(null,
					"This file will be overwritten. Are you sure you want to do this?", "Warning",
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (result != JOptionPane.YES_OPTION)
				return null;
		}
		return selectedFile;
	}

}
