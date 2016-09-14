package spelling;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class VoiceGenerator {
		private String command;
		private String text;
		private double pitch;
		private double utteranceRange;
		private double speed;
		private Voice voice;
		

		public VoiceGenerator(String textToSay) {
			this(textToSay, Voice.JOHN3, 110, 20, 1);
		}

		public VoiceGenerator(String textToSay, Voice voice, double pitch, double utteranceRange, double speed){
			this.text = textToSay;
			this.pitch = pitch;
			this.utteranceRange = utteranceRange;
			this.speed = speed;
			this.voice = voice;
		}

		public static enum Voice{
			JOHN1, JOHN2, JOHN3;
		}


		public void getVoiceFromName(String name){

			name = name.toLowerCase();

			switch(name){
				case "john1":
					command = "festival –b temp1.scm";
				case "john2":
					command = "festival –b temp2.scm";
				case "john3":
					command = "festival –b temp3.scm";
			}
			
			bashCmd(command);
		}
		

		// to run BASH commands
		private void bashCmd(String command){
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
		
		
		/*
		
		 * Begin speaking the text that was entered when creating this object
		 
		public void speak() {
			try {
				File file = new File("temp.scm");

				switch (this.voice){
					case JOHN1:
						file = new File("temp1.scm");

						PrintWriter writer = new PrintWriter(file, "UTF-8");
						writer.println("(voice_kal_diphone)");
						writer.close();
						break;
					case JOHN2:
						file = new File("temp2.scm");

						writer = new PrintWriter(file, "UTF-8");
						writer.println("(voice_rab_diphone)");
						writer.close();
						break;
					case JOHN3:
						file = new File("temp3.scm");

						writer = new PrintWriter(file, "UTF-8");
						writer.println("(voice_akl_nz_jdt_diphone)");
						writer.close();
						break;
				}

				
			} catch (IOException e){
				e.printStackTrace();
			}
		
		}
		*/
}