package com.chalcodes.weaponm.database;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Transient;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.neo4j.ogm.typeconversion.CompositeAttributeConverter;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Constants for the names of game stats.
 *
 * @author Kevin Krumwiede
 */
@NodeEntity
public class GameStats {
	@GraphId
	private Long mGraphId;

	/**
	 * Required by OGM.
	 */
	public GameStats() {}

	public GameStats(@Nonnull final List<String> stats) {
		for(final String s : stats) {
			if(s.charAt(0) == '[') continue;
			final int i = s.indexOf('=');
			if(i != -1) {
				final String k = s.substring(0, i);
				final String v = s.substring(i + 1, s.length());
				mStats.put(k, v);
			}
		}
	}

	@Convert(Converter.class)
	private final Map<String, String> mStats = new HashMap<>();

	private static class Converter implements CompositeAttributeConverter<Map<String, String>> {
		@Override
		public Map<String, ?> toGraphProperties(final Map<String, String> value) {
			return new HashMap<String, Object>(value);
		}

		@Override
		public Map<String, String> toEntityAttribute(final Map<String, ?> value) {
			final Map<String, String> result = new HashMap<>();
			for(final Map.Entry<String, ?> entry : value.entrySet()) {
				result.put(entry.getKey(), entry.getValue().toString());
			}
			return result;
		}
	}

	// TODO all the constants!

/*
	Major Version=3
	Minor Version=35
	Range 1=All day: Multiplayer Access
	Gold Enabled=True
	MBBS Compatibility=True
	Bubbles=False
	Start Day=03/12/45
	Game Age=43 Days
	Last Extern Day=04/24/45
	Internal Aliens=False
	Internal Ferrengi=False
	Closed Game=False
	Show Stardock=True
	Turn Base=1,000 Turns
	Time Online=360 Min
	Inactive Time=300 Sec
	Last Bust Clear Day=04/24/45
	Initial Fighters=30
	Initial Credits=300
	Initial Holds=20
	New Player Planets=True
	Days Til Deletion=30 Days
	Colonist Regen Rate=12000
	Max Planet Sector=5
	Max Corp Members=6
	FedSpace Ship Limit=5
	Photon Missile Duration=2 Sec
	FedSpace Photons=False
	Photons Disable Players=True
	Cloak Fail Percent=3%
	Debris Loss Percent=3%
	Trade Percent=100%
	Steal Buy=True
	Production Rate=50
	Max Production Regen=100
	Multiple Photons=False
	Clear Bust Days=1 Days
	Steal Factor=70%
	Rob Factor=50%
	Port Production Max=32760
	Radiation Lifetime=14 Days
	Fighter Lock Decay=1,440 Min
	Invincible Ferengal=False
	MBBS Combat=True
	Death Delay=True
	Deaths Per Day=0
	Startup Asset Dropoff=No dropoff
	Show Whos Online=True
	Interactive Sub-prompts=True
	Allow Aliases=True
	Alien Sleep Mode=Active
	Allow MBBS MegaRob Bug=True
	Max Terra Colonists=100000
	Minimum Login Time=None
	Turn Accumulation Days=7
	Podless Captures=Always
	Capture Fail Percent=0%
	Max Bank Credits=500,000 cr
	High Score Mode=On demand
	High Score Type=Values
	Rankings Mode=On demand
	Rankings Type=Values + Titles
	Entry Log Blackout=None
	Game Log Blackout=None
	Port Report Delay=No Delay
	Input Bandwidth=1 Mps Broadband
	Output Bandwidth=1 Mps Broadband
	Latency=150 ms
	Ship Delay=Constant (250 ms)
	Planet Delay=None
	Other Attacks Delay=None
	EProbe Delay=None
	Crime Delay=None
	Photon Launch Delay=None
	Photon Wave Delay=None
	Genesis Launch Delay=None
	IC Powerup Delay=None
	PIG Powerup Delay=None
	Planet Landing/Takeoff Delay=None
	Port Dock/Depart Delay=None
	Ship Transporter Delay=None
	Planet Transporter Delay=None
	Take/Drop Fighters Delay=None
	Drop/Take Mines Delay=None
	Tavern Announcement=100 cr
	Limpet Removal=5,000 cr
	Reregister Ship=5,000 cr
	Citadel Transport Unit=50,000 cr
	Citadel Transport Upgrade=25,000 cr
	Genesis Torpedo=20,000 cr
	Armid Mine=1,000 cr
	Limpet Mine=10,000 cr
	Beacon=100 cr
	Type I TWarp=50,000 cr
	Type II TWarp=80,000 cr
	TWarp Upgrade=10,000 cr
	Psychic Probe=10,000 cr
	Planet Scanner=30,000 cr
	Atomic Detonator=15,000 cr
	Corbomite=1,000 cr
	Ether Probe=3,000 cr
	Photon Missile=40,000 cr
	Cloaking Device=25,000 cr
	Mine Disruptor=6,000 cr
	Holographic Scanner=25,000 cr
	Density Scanner=2,000 cr
	Sectors=5000
	Users=200
	Aliens=0
	Ships=800
	Ports=3899
	Planets=1000
	Max Course Length=90
	Tournament Mode=0
	Days To Enter=N/A
	Lockout Mode=N/A
	Max Times Blown Up=N/A
	Local Game Time=04/24/45 04:38:02 AM
	Active Players=9
	Percent Players Good=44
	Active Aliens=0
	Percent Aliens Good=N/A
	Active Ports=3691
	Port Value=20681071
	Active Planets=58
	Percent Planet Citadels=37
	Active Ships=23
	Active Corps=5
	Active Figs=569947
	Active Mines=391
*/

}
