package org.usfirst.frc.team5496.networktables;

import java.util.Properties;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public interface NetworkTables {
	public NetworkTable getTable();
	public void init(Properties props);

}
