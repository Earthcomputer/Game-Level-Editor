package net.earthcomputer.stepfish.leveleditor;

import java.io.IOException;

public class WrongFormatException extends IOException {
	private static final long serialVersionUID = -6701136005728261223L;

	public WrongFormatException(String description) {
		super(description);
	}

}
