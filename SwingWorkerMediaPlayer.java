package spelling;

import javax.swing.SwingWorker;

public class SwingWorkerMediaPlayer extends SwingWorker<Void,Void> {
	private int player;
	private SpellingAid spellingAid;

	public SwingWorkerMediaPlayer(int mediaPlayer,SpellingAid spellAid){
		player = mediaPlayer;
		spellingAid = spellAid;
	}
	@Override
	protected Void doInBackground() throws Exception {
		//new MediaPlayer(player,spellingAid);
		return null;
	}
	
	protected void done(){
		//spellingAid.nextQuizOptions();
	}
}
