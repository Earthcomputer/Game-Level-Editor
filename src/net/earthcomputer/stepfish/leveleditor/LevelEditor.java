package net.earthcomputer.stepfish.leveleditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

public class LevelEditor {

	public static final String GAME_NAME = "Stepfish";
	public static final String TITLE = GAME_NAME + " Level Editor";
	public static final int CURRENT_LEVEL_VERSION = 0;

	public static Level editingLevel;
	public static File editingFile;

	public static int scrollX;
	public static int scrollY;

	public static boolean movingUp = false;
	public static boolean movingDown = false;
	public static boolean movingLeft = false;
	public static boolean movingRight = false;

	public static EnumObjectType objectType = EnumObjectType.PLAYER;
	public static boolean gridSnap = true;
	public static boolean replace = true;

	public static JFrame window;

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

		int answer = JOptionPane.showOptionDialog(null, "What do you want to do?", TITLE, JOptionPane.DEFAULT_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, new Object[] { "Create New", "Open" }, "Create New");
		if (answer == 0) {
			String name = JOptionPane.showInputDialog(null, "Enter a name for this level:", TITLE,
					JOptionPane.QUESTION_MESSAGE);
			if (name == null)
				return;
			String widthString = JOptionPane.showInputDialog(null, "Enter the width of the level:", TITLE,
					JOptionPane.QUESTION_MESSAGE);
			int width;
			try {
				width = Integer.parseInt(widthString);
			} catch (Exception e) {
				return;
			}
			if (width <= 0)
				return;
			String heightString = JOptionPane.showInputDialog(null, "Enter the height of the level:", TITLE,
					JOptionPane.QUESTION_MESSAGE);
			int height;
			try {
				height = Integer.parseInt(heightString);
			} catch (Exception e) {
				return;
			}
			if (height <= 0)
				return;
			editingLevel = new Level(name, width, height, new ArrayList<GameObject>());
		} else if (answer == 1) {
			if (!userPressedOpen(false))
				return;
		} else if (answer == JOptionPane.CLOSED_OPTION) {
			return;
		}

		window = new JFrame();
		refreshWindowTitle();
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
						userPressedOpen(true);
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

				inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "startMovingUp");
				actions.put("startMovingUp", new AbstractAction() {
					private static final long serialVersionUID = -1751067873506742527L;

					@Override
					public void actionPerformed(ActionEvent e) {
						movingUp = true;
					}

				});

				inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true), "stopMovingUp");
				actions.put("stopMovingUp", new AbstractAction() {
					private static final long serialVersionUID = 8647983038034346409L;

					@Override
					public void actionPerformed(ActionEvent e) {
						movingUp = false;
					}

				});

				inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "startMovingDown");
				actions.put("startMovingDown", new AbstractAction() {
					private static final long serialVersionUID = 5266684050071092599L;

					@Override
					public void actionPerformed(ActionEvent e) {
						movingDown = true;
					}

				});

				inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "stopMovingDown");
				actions.put("stopMovingDown", new AbstractAction() {
					private static final long serialVersionUID = 2950726600093856612L;

					@Override
					public void actionPerformed(ActionEvent e) {
						movingDown = false;
					}

				});

				inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "startMovingLeft");
				actions.put("startMovingLeft", new AbstractAction() {
					private static final long serialVersionUID = 8390214617395646929L;

					@Override
					public void actionPerformed(ActionEvent e) {
						movingLeft = true;
					}

				});

				inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "stopMovingLeft");
				actions.put("stopMovingLeft", new AbstractAction() {
					private static final long serialVersionUID = 7776088795169513514L;

					@Override
					public void actionPerformed(ActionEvent e) {
						movingLeft = false;
					}

				});

				inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "startMovingRight");
				actions.put("startMovingRight", new AbstractAction() {
					private static final long serialVersionUID = -7116884664214470557L;

					@Override
					public void actionPerformed(ActionEvent e) {
						movingRight = true;
					}

				});

				inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "stopMovingRight");
				actions.put("stopMovingRight", new AbstractAction() {
					private static final long serialVersionUID = 605736615454879227L;

					@Override
					public void actionPerformed(ActionEvent e) {
						movingRight = false;
					}

				});

				inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_G, 0), "goto");
				actions.put("goto", new AbstractAction() {
					private static final long serialVersionUID = -3131112887323094335L;

					@Override
					public void actionPerformed(ActionEvent e) {
						String xString = JOptionPane.showInputDialog(null, "X-position:", "Go to location",
								JOptionPane.QUESTION_MESSAGE);
						int x;
						try {
							x = Integer.parseInt(xString);
						} catch (Exception e1) {
							return;
						}
						String yString = JOptionPane.showInputDialog(null, "Y-position:", "Go to location",
								JOptionPane.QUESTION_MESSAGE);
						int y;
						try {
							y = Integer.parseInt(yString);
						} catch (Exception e1) {
							return;
						}
						scrollX = x;
						scrollY = y;
					}

				});

				inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, 0), "changeType");
				actions.put("changeType", new AbstractAction() {
					private static final long serialVersionUID = -9202074778625548790L;

					@Override
					public void actionPerformed(ActionEvent e) {
						Object selectedType = JOptionPane.showInputDialog(null, "Choose new type:",
								"Change placed type", JOptionPane.QUESTION_MESSAGE, null, EnumObjectType.getAllNames(),
								EnumObjectType.PLAYER.getName());
						if (selectedType != null) {
							objectType = EnumObjectType.byName((String) selectedType);
						}
					}

				});

				inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "toggleSnap");
				actions.put("toggleSnap", new AbstractAction() {
					private static final long serialVersionUID = -4964306376060016641L;

					@Override
					public void actionPerformed(ActionEvent e) {
						gridSnap = !gridSnap;
					}

				});

				inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "toggleReplace");
				actions.put("toggleReplace", new AbstractAction() {
					private static final long serialVersionUID = 5969855475162882059L;

					@Override
					public void actionPerformed(ActionEvent e) {
						replace = !replace;
					}

				});

				inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, 0), "changeName");
				actions.put("changeName", new AbstractAction() {
					private static final long serialVersionUID = -9150378420939592943L;

					@Override
					public void actionPerformed(ActionEvent e) {
						String newName = JOptionPane.showInputDialog(null, "Enter the new name of this level:",
								"Enter New Name", JOptionPane.QUESTION_MESSAGE);
						if (newName != null) {
							editingLevel.name = newName;
							refreshWindowTitle();
						}
					}

				});

				inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), "changeDimensions");
				actions.put("changeDimensions", new AbstractAction() {
					private static final long serialVersionUID = 860734003639836461L;

					@Override
					public void actionPerformed(ActionEvent e) {
						String newWidthString = JOptionPane.showInputDialog(null, "Enter the new width:",
								"Enter New Dimensions", JOptionPane.QUESTION_MESSAGE);
						int newWidth;
						try {
							newWidth = Integer.parseInt(newWidthString);
						} catch (Exception e1) {
							return;
						}
						if (newWidth <= 0)
							return;
						String newHeightString = JOptionPane.showInputDialog(null, "Enter the new height:",
								"Enter New Dimensions", JOptionPane.QUESTION_MESSAGE);
						int newHeight;
						try {
							newHeight = Integer.parseInt(newHeightString);
						} catch (Exception e1) {
							return;
						}
						if (newHeight <= 0)
							return;
						editingLevel.levelWidth = newWidth;
						editingLevel.levelHeight = newHeight;
					}

				});

				addMouseListener(new MouseAdapter() {

					@Override
					public void mousePressed(MouseEvent e) {
						Point hoveredLocation = getHoveredLocation();
						if (hoveredLocation != null) {
							if ((e.getButton() == MouseEvent.BUTTON1 && replace)
									|| e.getButton() == MouseEvent.BUTTON3) {
								synchronized (editingLevel.objects) {
									for (int i = editingLevel.objects.size() - 1; i >= 0; i--) {
										GameObject object = editingLevel.objects.get(i);
										if (object.x <= hoveredLocation.x && object.y <= hoveredLocation.y
												&& object.x + 16 > hoveredLocation.x
												&& object.y + 16 > hoveredLocation.y) {
											editingLevel.objects.remove(i);
											if (e.getButton() == MouseEvent.BUTTON3)
												break;
										}
									}
								}
							}
							if (e.getButton() == MouseEvent.BUTTON1) {
								if (gridSnap) {
									hoveredLocation.x = hoveredLocation.x / 16 * 16;
									hoveredLocation.y = hoveredLocation.y / 16 * 16;
								}
								editingLevel.objects
										.add(new GameObject(hoveredLocation.x, hoveredLocation.y, objectType));
							}
						}
					}

					@Override
					public void mouseWheelMoved(MouseWheelEvent e) {
						scrollY += e.getWheelRotation() * 20;
					}

				});
			}

			@Override
			public void paintComponent(Graphics g) {
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, 640, 480);
				synchronized (editingLevel.objects) {
					for (GameObject object : editingLevel.objects) {
						object.render(scrollX, scrollY, g);
					}
				}
				g.setColor(Color.GRAY);
				for (int i = (scrollX < 0 ? 0 : 16) - (scrollX % 16); i < 640; i += 16) {
					g.drawLine(i, 0, i, 480);
				}
				for (int i = (scrollY < 0 ? 0 : 16) - (scrollY % 16); i < 480; i += 16) {
					g.drawLine(0, i, 640, i);
				}
				g.setColor(Color.RED);
				if (scrollX < 0 && scrollX >= -640 && scrollY >= -480 && scrollY < 480)
					g.drawLine(-scrollX, Math.max(0, -scrollY), -scrollX, Math.min(480, 480 - scrollY));
				if (scrollX >= 0 && scrollX < 640 && scrollY >= -480 && scrollY < 480)
					g.drawLine(640 - scrollX, Math.max(0, -scrollY), 640 - scrollX, Math.min(480, 480 - scrollY));
				if (scrollX >= -640 && scrollX < 640 && scrollY < 0 && scrollY >= -480)
					g.drawLine(Math.max(0, -scrollX), -scrollY, Math.min(640, 640 - scrollX), -scrollY);
				if (scrollX >= -640 && scrollX < 640 && scrollY >= 0 && scrollY < 480)
					g.drawLine(Math.max(0, -scrollX), 480 - scrollY, Math.min(640, 640 - scrollX), 480 - scrollY);
				g.setColor(Color.WHITE);
				Point hoveredLocation = getHoveredLocation();
				if (hoveredLocation != null)
					g.drawString(String.format("Hovered Location: (%d, %d)", hoveredLocation.x, hoveredLocation.y), 2,
							10);
				g.drawString(String.format("Placing type: %s", objectType.getName()), 2, 20);
				g.drawString(String.format("Grid snap: %s", gridSnap ? "On" : "Off"), 2, 30);
				g.drawString(String.format("Auto-replace: %s", replace ? "On" : "Off"), 2, 40);
			}
		});

		final Timer timer = new Timer(50, new ActionListener() {

			private boolean ticking = false;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (ticking)
					return;
				ticking = true;
				if (movingUp)
					scrollY -= 20;
				if (movingDown)
					scrollY += 20;
				if (movingLeft)
					scrollX -= 20;
				if (movingRight)
					scrollX += 20;
				window.repaint();
				ticking = false;
			}

		});

		window.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				timer.stop();
				System.exit(0);
			}

			@Override
			public void windowLostFocus(WindowEvent e) {
				movingUp = movingDown = movingLeft = movingRight = false;
			}

		});
		window.setResizable(false);
		window.getContentPane().setPreferredSize(new Dimension(640, 480));
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		timer.start();
	}

	public static boolean userPressedOpen(boolean showWarningMessage) {
		if (showWarningMessage) {
			if (JOptionPane.showConfirmDialog(null, "Do you want to continue? Make sure you have saved your work.",
					"Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION)
				return false;
		}

		File openedFile = showOpenDialog();
		if (openedFile == null)
			return false;

		try {
			open(new BufferedInputStream(new FileInputStream(openedFile)));
		} catch (WrongFormatException e) {
			JOptionPane.showMessageDialog(null, "Input file has invalid format:\n" + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error reading file:\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		editingFile = openedFile;

		refreshWindowTitle();

		return true;
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
		File savingFile = showSaveDialog();
		if (savingFile == null)
			return;
		editingFile = savingFile;
		refreshWindowTitle();
		userPressedSave();
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
				int x = dataInput.readInt();
				int y = dataInput.readInt();
				int id = dataInput.readUnsignedShort();
				EnumObjectType objectType = EnumObjectType.byID(id);
				if (objectType == null)
					throw new WrongFormatException("Unrecognized object type with ID " + id);
				objects.add(new GameObject(x, y, objectType));
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
			dataOutput.writeInt(object.x);
			dataOutput.writeInt(object.y);
			dataOutput.writeChar(object.objectType.getID());
		}
	}

	public static File showOpenDialog() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter(GAME_NAME + " Levels", "gglevel"));
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
		fileChooser.setFileFilter(new FileNameExtensionFilter(GAME_NAME + " Levels", "gglevel"));
		fileChooser.setDialogTitle("Set location to save level");

		if (fileChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION)
			return null;
		File selectedFile = fileChooser.getSelectedFile();
		if (selectedFile == null)
			return null;
		if (!selectedFile.getName().endsWith(".gglevel"))
			selectedFile = new File(selectedFile.getParentFile(), selectedFile.getName() + ".gglevel");

		if (selectedFile.exists()) {
			int result = JOptionPane.showConfirmDialog(null,
					"This file will be overwritten. Are you sure you want to do this?", "Warning",
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (result != JOptionPane.YES_OPTION)
				return null;
		}
		return selectedFile;
	}

	public static Point getHoveredLocation() {
		Point mouseLocation = new Point(MouseInfo.getPointerInfo().getLocation());
		Point compLocation = window.getContentPane().getLocationOnScreen();
		mouseLocation.x -= compLocation.x;
		mouseLocation.y -= compLocation.y;
		if (mouseLocation.x < 0 || mouseLocation.x >= 640 || mouseLocation.y < 0 || mouseLocation.y >= 480)
			return null;
		mouseLocation.x += scrollX;
		mouseLocation.y += scrollY;
		return mouseLocation;
	}

	public static void refreshWindowTitle() {
		if (window != null)
			window.setTitle(String.format("%s (in %s) - " + TITLE, editingLevel.name,
					editingFile == null ? "no file" : editingFile.getName()));
	}

}
