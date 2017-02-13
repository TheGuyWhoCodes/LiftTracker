package org.usfirst.frc.team5496.networktables;

import java.util.Properties;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class NetworkTablesConfig {
	private Integer team;
	private Integer port;
	private String tableName;
	private NetworkTable table;
	public NetworkTablesConfig(Properties props) {
		super();
		this.setTeam(Integer.parseInt( props.getProperty("team") ));
		this.setTableName(props.getProperty("networktables.tablename"));
		this.setPort(Integer.parseInt(props.getProperty("networktables.port")));
	}
	
	protected String getRoboRioHostName() {
		return "roborio-" + team + "-frc.local";
	}

	protected Integer getTeam() {
		return team;
	}

	private void setTeam(Integer team) {
		this.team = team;
	}

	protected String getTableName() {
		return tableName;
	}

	private void setTableName(String tableName) {
		this.tableName = tableName;
	}

	

	void setTable(NetworkTable table) {
		this.table = table;
	}

	protected Integer getPort() {
		return port;
	}

	private void setPort(Integer port) {
		this.port = port;
	}
	

}
