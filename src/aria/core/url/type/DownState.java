package aria.core.url.type;

public enum DownState{
	InitDown,
	Downloading,
	Pause,
	Complete,
	Failed;

	public static DownState getDownState(String readUTF) {
		return valueOf(readUTF);
	}
	
}
