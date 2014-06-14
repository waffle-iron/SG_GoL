package de.sydsoft.sg_gol.model;

import java.util.HashSet;

public class GoLPatternList extends HashSet<GoLPattern> {
	private static final long	serialVersionUID	= -4449180697072901071L;

	public boolean contains(boolean[][] pattern) {
		for (GoLPattern goLPB : this) {
			if (goLPB.getPattern().equals(pattern)) return true;
		}
		return false;
	}

	public boolean contains(String name) {
		for (GoLPattern goLPB : this) {
			if (goLPB.getName().equals(name)) return true;
		}
		return false;
	}

	public boolean contains(String[] pattern) {
		for (GoLPattern goLPB : this) {
			if (goLPB.getPattern().equals(GoLPattern.parsePattern(pattern))) return true;
		}
		return false;
	}

	public GoLPattern get(String nameAsKey) {
		for (GoLPattern goLPB : this) {
			if (goLPB.getName().equals(nameAsKey)) return goLPB;
		}
		return null;
	}

	public GoLPattern get(String[] patternAsKey) {
		for (GoLPattern goLPB : this) {
			if (goLPB.getPattern() == GoLPattern.parsePattern(patternAsKey)) return goLPB;
		}
		return null;
	}
}
