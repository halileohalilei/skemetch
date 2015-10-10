package model;

import java.awt.Rectangle;

public interface Symbol {
	void detach();
	String generateSchemeCode();
	String getClassName();
	Rectangle getPosition();
}
