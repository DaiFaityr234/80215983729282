package spelling;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.SwingWorker;

public class VoiceGenerator extends SwingWorker<Void, Void>{
		//private double pitch;
		//private double utteranceRange;
		private double speed;
		private Voice voice;
		private File schemeFile;
		private String swingWorkerChangedText;
		private String swingWorkerNormalText;
		
		
		public VoiceGenerator(Voice chosenVoice, double chosenSpeed){
			makeSureScmFileIsPresent();
			schemeFile = new File(".spelling_aid_voice_scm");
					
			//this.pitch = pitch;
			//this.utteranceRange = utteranceRange;
			this.speed = chosenSpeed;
			this.voice = chosenVoice;
		}

		public static enum Voice{
			DEFAULT,AUCKLAND;
		}
		
		public void setVoice(Voice chosenVoice){
			voice=chosenVoice;
		}
		
		
		public void sayText(String normalSpeedText,String changedText){
			ClearStatistics.clearFile(schemeFile);
			if(voice == Voice.DEFAULT){
				System.out.println("DEFAULT");
				SpellingList.record(schemeFile, "(voice_rab_diphone)\n" );
			} else {
				System.out.println("AUCKLAND");
				SpellingList.record(schemeFile, "(voice_akl_nz_jdt_diphone)\n");
			}
			SpellingList.record(schemeFile,("(SayText \"" + normalSpeedText + "\" )\n"));
			SpellingList.record(schemeFile, "(Parameter.set 'Duration_Stretch "+speed+")\n");
			SpellingList.record(schemeFile,("(SayText \"" + changedText + "\" )\n"));
			SpellingList.record(schemeFile,"(quit)");
			processStarter("festival -b .spelling_aid_voice_scm");
			
		}
		
		
		// to run BASH commands
		private void processStarter(String command){
			// process builder to run bash commands
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", command);
			Process process;
			try {
				process = builder.start();
				process.waitFor();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private void makeSureScmFileIsPresent() {
			File scmFile = new File(".spelling_aid_voice_scm");
			try{
				if(! scmFile.exists()){
					scmFile.createNewFile();
				}
			} catch (IOException e) {
				e.printStackTrace();

			}
		}
		
		public void setTextForSwingWorker(String normal, String changed){
			swingWorkerChangedText = changed;
			swingWorkerNormalText = normal;
		}

		protected Void doInBackground() throws Exception {
			sayText(swingWorkerNormalText,swingWorkerChangedText);
			return null;
		}
}