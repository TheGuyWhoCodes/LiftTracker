package org.usfirst.frc.team5496.networktables;

import java.util.Properties;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class NetworkTablesServer extends NetworkTablesConfig implements NetworkTables{
	
	NetworkTable table ;
    public NetworkTablesServer(Properties props) {
		// TODO Auto-generated constructor stub
    	super(props);
    	
	}
	@Override
	public void init(Properties props) {
		
		
		
	}

	@Override
	public NetworkTable getTable() {
		// TODO Auto-generated method stub
		return table;
	}

}
