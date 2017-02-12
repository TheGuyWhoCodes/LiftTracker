package org.usfirst.frc.team5496.networktables;

import java.util.Properties;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class NetworkTablesClient extends NetworkTablesConfig implements NetworkTables {
	private NetworkTable table;
	
	public NetworkTablesClient(Properties props) {
		super(props);
		// TODO Auto-generated constructor stub
	}

	
	

	private void setupNetworkTables(Integer teamNumber, String  networkTableName)  {
    	NetworkTable.setClientMode();
		NetworkTable.setTeam( getTeam() );
		NetworkTable.setIPAddress( getRoboRioHostName() );
		NetworkTable.initialize();
		setTable(NetworkTable.getTable(getTableName()));
			
    }

	@Override
	public void init(Properties props) {
		setupNetworkTables(Integer.parseInt(props.getProperty("teamnumber")), props.getProperty("tablename"));
		
	}

	@Override
	public NetworkTable getTable() {
		// TODO Auto-generated method stub
		return table;
	}
	

}
